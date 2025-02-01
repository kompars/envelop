package org.kompars.envelop

public suspend fun MailSender.send(block: MailMessageBuilder.() -> Unit): EmailSent {
    return send(MailMessageBuilder().apply(block).build())
}

public class MailMessageBuilder internal constructor() {
    private val from: MutableList<EmailPrincipal> = mutableListOf()
    private val to: MutableList<EmailPrincipal> = mutableListOf()
    private val cc: MutableList<EmailPrincipal> = mutableListOf()
    private val bcc: MutableList<EmailPrincipal> = mutableListOf()
    private val headers: MutableMap<String, String> = mutableMapOf()
    private var subject: String? = null
    private var textBody: String? = null
    private var htmlBody: String? = null
    private val attachments: MutableList<MailFile> = mutableListOf()
    private val inlineFiles: MutableList<MailFile> = mutableListOf()

    public fun from(principal: EmailPrincipal) {
        from.add(principal)
    }

    public fun from(name: String, address: String) {
        from.add(EmailPrincipal(name, EmailAddress(address)))
    }

    public fun from(address: String) {
        from.add(EmailPrincipal(address = EmailAddress(address)))
    }

    public fun from(address: EmailAddress) {
        from.add(EmailPrincipal(address = address))
    }

    public fun to(principal: EmailPrincipal) {
        to.add(principal)
    }

    public fun to(name: String, address: String) {
        to.add(EmailPrincipal(name, EmailAddress(address)))
    }

    public fun to(address: String) {
        to.add(EmailPrincipal(address = EmailAddress(address)))
    }

    public fun to(address: EmailAddress) {
        to.add(EmailPrincipal(address = address))
    }

    public fun cc(principal: EmailPrincipal) {
        cc.add(principal)
    }

    public fun cc(name: String, address: String) {
        cc.add(EmailPrincipal(name, EmailAddress(address)))
    }

    public fun cc(address: String) {
        cc.add(EmailPrincipal(address = EmailAddress(address)))
    }

    public fun cc(address: EmailAddress) {
        cc.add(EmailPrincipal(address = address))
    }

    public fun bcc(principal: EmailPrincipal) {
        bcc.add(principal)
    }

    public fun bcc(name: String, address: String) {
        bcc.add(EmailPrincipal(name, EmailAddress(address)))
    }

    public fun bcc(address: String) {
        bcc.add(EmailPrincipal(address = EmailAddress(address)))
    }

    public fun bcc(address: EmailAddress) {
        bcc.add(EmailPrincipal(address = address))
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
            headers = headers,
            subject = subject,
            textBody = textBody,
            htmlBody = htmlBody,
            attachments = attachments,
            inlineFiles = inlineFiles,
        )
    }
}
