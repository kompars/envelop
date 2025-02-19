package org.kompars.envelop

import org.kompars.envelop.common.*

public suspend fun MailSender.send(block: MailMessageBuilder.() -> Unit): EmailSent {
    return send(MailMessageBuilder().apply(block).build())
}

public class MailMessageBuilder internal constructor() {
    private val from: MutableList<EmailAddress> = mutableListOf()
    private val to: MutableList<EmailAddress> = mutableListOf()
    private val cc: MutableList<EmailAddress> = mutableListOf()
    private val bcc: MutableList<EmailAddress> = mutableListOf()
    private val headers: MutableMap<String, String> = mutableMapOf()
    private val references: MutableList<String> = mutableListOf()
    private var subject: String? = null
    private var textBody: String? = null
    private var htmlBody: String? = null
    private val attachments: MutableList<MailFile> = mutableListOf()
    private val inlineFiles: MutableList<MailFile> = mutableListOf()

    public fun from(address: EmailAddress) {
        from.add(address)
    }

    public fun to(address: EmailAddress) {
        to.add(address)
    }

    public fun cc(address: EmailAddress) {
        cc.add(address)
    }

    public fun bcc(address: EmailAddress) {
        bcc.add(address)
    }

    public fun references(id: String) {
        references += id
    }

    public fun references(ids: List<String>) {
        references += ids
    }

    public fun header(name: String, value: String) {
        headers[name] = value
    }

    public fun subject(subject: String) {
        this.subject = subject
    }

    public fun textBody(text: String) {
        textBody = text
    }

    public fun htmlBody(html: String) {
        htmlBody = html
    }

    public fun attachment(name: String, contentType: String? = null, contentProvider: MailFileContentProvider) {
        val attachment = MailFile(
            name = name,
            contentType = contentType,
            contentProvider = contentProvider,
        )

        attachments.add(attachment)
    }

    public fun inlineFile(name: String, contentId: String, contentType: String? = null, contentProvider: MailFileContentProvider) {
        val attachment = MailFile(
            name = name,
            contentId = contentId,
            contentType = contentType,
            contentProvider = contentProvider,
        )

        inlineFiles.add(attachment)
    }

    internal fun build(): MailMessage {
        require(to.isNotEmpty() || cc.isNotEmpty() || bcc.isNotEmpty()) { "Receiver address can not be empty" }

        return MailMessage(
            from = from,
            to = to,
            cc = cc,
            bcc = bcc,
            references = references,
            headers = headers,
            subject = subject,
            textBody = textBody,
            htmlBody = htmlBody,
            attachments = attachments,
            inlineFiles = inlineFiles,
        )
    }
}
