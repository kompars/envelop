package org.kompars.envelop

import kotlin.time.*
import org.kompars.envelop.blob.*
import org.kompars.envelop.common.*

public class EmailMessageBuilder {
    private var messageId: EmailMessageId? = null
    private var date: Instant? = null
    private val recipients: MutableList<EmailRecipient> = mutableListOf()
    private val headers: MutableMap<String, String> = mutableMapOf()
    private val references: MutableList<EmailMessageId> = mutableListOf()
    private var subject: String? = null
    private var textBody: String? = null
    private var htmlBody: String? = null
    private val attachments: MutableList<EmailAttachment> = mutableListOf()

    public fun messageId(messageId: EmailMessageId) {
        this.messageId = messageId
    }

    public fun date(date: Instant) {
        this.date = date
    }

    public fun recipient(type: EmailRecipientType, address: EmailAddress) {
        recipients += EmailRecipient(type, address)
    }

    public fun from(address: EmailAddress) {
        recipient(EmailRecipientType.From, address)
    }

    public fun to(address: EmailAddress) {
        recipient(EmailRecipientType.To, address)
    }

    public fun cc(address: EmailAddress) {
        recipient(EmailRecipientType.Cc, address)
    }

    public fun bcc(address: EmailAddress) {
        recipient(EmailRecipientType.Bcc, address)
    }

    public fun replyTo(address: EmailAddress) {
        recipient(EmailRecipientType.ReplyTo, address)
    }

    public fun references(messageIds: List<EmailMessageId>) {
        references += messageIds
    }

    public fun header(name: String, value: String) {
        headers[name] = value
    }

    public fun subject(subject: String) {
        this.subject = subject
    }

    public fun textBody(textBody: String) {
        this.textBody = textBody
    }

    public fun htmlBody(htmlBody: String) {
        this.htmlBody = htmlBody
    }

    public fun attachment(type: EmailAttachmentType, blob: Blob, name: String, contentType: String? = null, contentId: String? = null) {
        val attachment = EmailAttachment(
            type = type,
            name = name,
            contentId = contentId,
            contentType = contentType ?: "application/octet-stream",
            blob = blob,
        )

        attachments.add(attachment)
    }

    public fun attachment(blob: Blob, name: String, contentType: String? = null) {
        attachment(EmailAttachmentType.Attachment, blob, name, contentType)
    }

    public fun inlineFile(blob: Blob, name: String, contentId: String, contentType: String? = null) {
        attachment(EmailAttachmentType.Inline, blob, name, contentType, contentId)
    }

    public fun build(): EmailMessage {
        return EmailMessage(
            messageId = messageId,
            date = date,
            recipients = recipients,
            references = references,
            headers = headers,
            subject = subject ?: "",
            textBody = textBody,
            htmlBody = htmlBody,
            attachments = attachments,
        )
    }
}
