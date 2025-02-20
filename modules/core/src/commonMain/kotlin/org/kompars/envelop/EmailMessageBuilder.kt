package org.kompars.envelop

import kotlinx.datetime.*
import org.kompars.envelop.common.*

public class EmailMessageBuilder internal constructor() {
    private var id: EmailMessageId? = null
    private var date: Instant? = null
    private val recipients: MutableList<EmailRecipient> = mutableListOf()
    private val headers: MutableMap<String, String> = mutableMapOf()
    private val references: MutableList<EmailMessageId> = mutableListOf()
    private var subject: String? = null
    private var textBody: String? = null
    private var htmlBody: String? = null
    private val attachments: MutableList<EmailAttachment> = mutableListOf()

    public fun id(id: EmailMessageId) {
        this.id = id
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

    public fun references(ids: List<EmailMessageId>) {
        references += ids
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

    public fun attachment(type: EmailAttachmentType, name: String, contentType: String? = null, contentId: String? = null, contentProvider: EmailAttachmentContentProvider) {
        val attachment = EmailAttachment(
            type = type,
            name = name,
            contentId = contentId,
            contentType = contentType ?: "application/octet-stream",
            contentProvider = contentProvider,
        )

        attachments.add(attachment)
    }

    public fun attachment(name: String, contentType: String? = null, contentProvider: EmailAttachmentContentProvider) {
        attachment(EmailAttachmentType.Attachment, name, contentType, null, contentProvider)
    }

    public fun inlineFile(name: String, contentId: String, contentType: String? = null, contentProvider: EmailAttachmentContentProvider) {
        attachment(EmailAttachmentType.Inline, name, contentType, contentId, contentProvider)
    }

    internal fun build(): EmailMessage {
        return EmailMessage(
            id = id,
            recipients = recipients,
            references = references,
            headers = headers,
            subject = subject,
            textBody = textBody,
            htmlBody = htmlBody,
            attachments = attachments,
        )
    }
}
