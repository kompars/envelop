package org.kompars.envelop.parser

import jakarta.mail.internet.*
import kotlinx.datetime.*
import org.kompars.envelop.*
import org.kompars.envelop.blob.*
import org.kompars.envelop.common.*
import org.simplejavamail.api.email.*
import org.simplejavamail.converter.*

public class EmailParser(
    private val blobStorage: BlobStorage = InMemoryBlobStorage,
) {
    public suspend fun fromMimeMessage(message: MimeMessage): EmailMessage {
        return parse(EmailConverter.mimeMessageToEmail(message))
    }

    public suspend fun fromEml(content: ByteArray): EmailMessage {
        return parse(EmailConverter.emlToEmail(content.inputStream()))
    }

    public suspend fun fromMsg(content: ByteArray): EmailMessage {
        return parse(EmailConverter.outlookMsgToEmail(content.inputStream()))
    }

    private suspend fun parse(email: Email): EmailMessage {
        return EmailMessage.build {
            email.subject?.let { subject(it) }
            email.plainText?.let { textBody(it) }
            email.htmlText?.let { htmlBody(it) }
            email.id?.let { messageId(EmailMessageId.parse(it)) }

            email.headers["References"]
                ?.filterNotNull()
                ?.map { EmailMessageId.parse(it) }
                ?.let { references(it) }

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
