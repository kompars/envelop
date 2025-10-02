package org.kompars.envelop.converter

import jakarta.mail.internet.*
import java.util.*
import kotlin.time.*
import org.kompars.envelop.*
import org.kompars.envelop.blob.*
import org.kompars.envelop.common.*
import org.simplejavamail.api.email.*
import org.simplejavamail.converter.EmailConverter
import org.simplejavamail.email.*

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
        return fromEmail(EmailConverter.emlToEmail(content.inputStream()))
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

            email.attachments.forEach {
                attachment(
                    blob = blobStorage.write(it.dataSource.inputStream.readAllBytes()),
                    name = it.dataSource.name,
                    contentType = it.dataSource.contentType,
                )
            }

            email.embeddedImages.forEach {
                inlineFile(
                    blob = blobStorage.write(it.dataSource.inputStream.readAllBytes()),
                    name = it.dataSource.name,
                    contentType = it.dataSource.contentType,
                    contentId = it.name!!,
                )
            }
        }
    }
}
