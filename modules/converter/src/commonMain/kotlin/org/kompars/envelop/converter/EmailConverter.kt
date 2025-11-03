package org.kompars.envelop.converter

import jakarta.mail.internet.MimeMessage
import java.util.Date
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant
import org.kompars.envelop.EmailAttachmentType
import org.kompars.envelop.EmailMessage
import org.kompars.envelop.EmailRecipientType
import org.kompars.envelop.blob.Blob
import org.kompars.envelop.blob.BlobStorage
import org.kompars.envelop.blob.InMemoryBlobStorage
import org.kompars.envelop.build
import org.kompars.envelop.common.EmailAddress
import org.kompars.envelop.common.EmailMessageId
import org.simplejavamail.api.email.Email
import org.simplejavamail.converter.EmailConverter
import org.simplejavamail.email.EmailBuilder

public class EmailConverter(
    private val blobStorage: BlobStorage = InMemoryBlobStorage,
) {
    public suspend fun fromMimeMessage(message: MimeMessage): EmailMessage {
        return fromEmail(EmailConverter.mimeMessageToEmail(message))
    }

    public suspend fun toMimeMessage(message: EmailMessage): MimeMessage {
        return EmailConverter.emailToMimeMessage(toEmail(message))
    }

    public suspend fun fromEml(content: ByteArray): EmailMessage {
        return fromEmail(EmailConverter.mimeMessageToEmail(MimeMessageParser.parse(content)))
    }

    public suspend fun toEml(message: EmailMessage): ByteArray {
        return EmailConverter.emailToEML(toEmail(message)).encodeToByteArray()
    }

    private suspend fun toEmail(message: EmailMessage): Email {
        val from = message.recipients.single { it.type == EmailRecipientType.From }
        val to = message.recipients.filter { it.type == EmailRecipientType.To }
        val cc = message.recipients.filter { it.type == EmailRecipientType.Cc }
        val bcc = message.recipients.filter { it.type == EmailRecipientType.Bcc }
        val replyTo = message.recipients.filter { it.type == EmailRecipientType.ReplyTo }
        val attachments = message.attachments.filter { it.type == EmailAttachmentType.Attachment }
        val inlineAttachments = message.attachments.filter { it.type == EmailAttachmentType.Inline }

        val builder = EmailBuilder.startingBlank()
            .from(from.address.identifier, from.address.withIdentifier(null).toString())
            .withSubject(message.subject)
            .withPlainText(message.textBody)
            .withHTMLText(message.htmlBody)

        if (message.date != null) {
            builder.fixingSentDate(Date.from(message.date?.toJavaInstant()))
        }

        if (message.messageId != null) {
            builder.fixingMessageId(message.messageId?.toString())
        }

        if (message.references.isNotEmpty()) {
            builder.withHeader("References", message.references.joinToString(" ") { it.toString() })
            builder.withHeader("In-Reply-To", message.references.last().toString())
        }

        for (recipient in replyTo) {
            builder.withReplyTo(recipient.address.identifier, recipient.address.withIdentifier(null).toString())
        }

        for (recipient in to) {
            builder.to(recipient.address.identifier, recipient.address.withIdentifier(null).toString())
        }

        for (recipient in cc) {
            builder.cc(recipient.address.identifier, recipient.address.withIdentifier(null).toString())
        }

        for (recipient in bcc) {
            builder.bcc(recipient.address.identifier, recipient.address.withIdentifier(null).toString())
        }

        for (attachment in attachments) {
            builder.withAttachment(
                attachment.name,
                blobStorage.read(attachment.blob),
                attachment.contentType,
            )
        }

        for (attachment in inlineAttachments) {
            builder.withEmbeddedImage(
                attachment.contentId!!,
                blobStorage.read(attachment.blob),
                attachment.contentType,
            )
        }

        return builder.buildEmail()
    }

    private suspend fun fromEmail(email: Email): EmailMessage {
        return EmailMessage.build {
            email.subject?.let { subject(it) }
            email.plainText?.let { textBody(it) }
            email.htmlText?.let { htmlBody(it) }
            email.id?.let { messageId(EmailMessageId.parse(it)) }

            val references = (email.headers["References"] ?: email.headers["In-Reply-To"])
                ?.single()
                ?.trim()
                ?.split(Regex("[\\s,]+"))
                ?.filter { it.isNotBlank() }
                ?.map { EmailMessageId.parse(it) }

            references?.let { references(it) }

            email.sentDate?.let {
                date(it.toInstant().toKotlinInstant())
            }

            email.fromRecipient?.let {
                from(EmailAddress.parse(it.address).withIdentifier(it.name))
            }

            email.replyToRecipients.map {
                replyTo(EmailAddress.parse(it.address).withIdentifier(it.name))
            }

            email.toRecipients.map {
                to(EmailAddress.parse(it.address).withIdentifier(it.name))
            }

            email.ccRecipients.map {
                cc(EmailAddress.parse(it.address).withIdentifier(it.name))
            }

            email.bccRecipients.map {
                bcc(EmailAddress.parse(it.address).withIdentifier(it.name))
            }

            val blobs = mutableListOf<Blob>()

            email.embeddedImages.forEach { resource ->
                val blob = blobStorage.write(resource.dataSource.inputStream.readAllBytes())

                inlineFile(
                    blob = blob.also { blobs.add(it) },
                    name = resource.dataSource.name,
                    contentType = resource.dataSource.contentType,
                    contentId = resource.name!!,
                )
            }

            email.attachments.forEach { resource ->
                val blob = blobStorage.write(resource.dataSource.inputStream.readAllBytes())

                if (blob !in blobs) {
                    attachment(
                        blob = blob,
                        name = resource.dataSource.name,
                        contentType = resource.dataSource.contentType,
                    )
                }
            }
        }
    }
}
