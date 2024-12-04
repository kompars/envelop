package org.kompars.envelop

public suspend fun MailSender.send(block: MailMessageBuilder.() -> Unit) {
    send(MailMessageBuilder().apply(block).build())
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
        this.from.add(principal)
    }

    public fun from(name: String, address: String) {
        this.from.add(EmailPrincipal(name, EmailAddress(address)))
    }

    public fun from(address: String) {
        this.from.add(EmailPrincipal(address = EmailAddress(address)))
    }

    public fun from(address: EmailAddress) {
        this.from.add(EmailPrincipal(address = address))
    }

    public fun to(principal: EmailPrincipal) {
        this.to.add(principal)
    }

    public fun to(name: String, address: String) {
        this.to.add(EmailPrincipal(name, EmailAddress(address)))
    }

    public fun to(address: String) {
        this.to.add(EmailPrincipal(address = EmailAddress(address)))
    }

    public fun to(address: EmailAddress) {
        this.to.add(EmailPrincipal(address = address))
    }

    public fun cc(principal: EmailPrincipal) {
        this.cc.add(principal)
    }

    public fun cc(name: String, address: String) {
        this.cc.add(EmailPrincipal(name, EmailAddress(address)))
    }

    public fun cc(address: String) {
        this.cc.add(EmailPrincipal(address = EmailAddress(address)))
    }

    public fun cc(address: EmailAddress) {
        this.cc.add(EmailPrincipal(address = address))
    }

    public fun bcc(principal: EmailPrincipal) {
        this.bcc.add(principal)
    }

    public fun bcc(name: String, address: String) {
        this.bcc.add(EmailPrincipal(name, EmailAddress(address)))
    }

    public fun bcc(address: String) {
        this.bcc.add(EmailPrincipal(address = EmailAddress(address)))
    }

    public fun bcc(address: EmailAddress) {
        this.bcc.add(EmailPrincipal(address = address))
    }

    public fun header(name: String, value: String) {
        this.headers[name] = value
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

    public fun attachment(name: String, contentType: String? = null, contentProvider: MailFileContentProvider) {
        val attachment = MailFile(
            name = name,
            contentType = contentType,
            contentProvider = contentProvider
        )

        this.attachments.add(attachment)
    }

    public fun inlineFile(name: String, contentType: String? = null, contentId: String, contentProvider: MailFileContentProvider) {
        val attachment = MailFile(
            name = name,
            contentType = contentType,
            contentId = contentId,
            contentProvider = contentProvider
        )

        this.inlineFiles.add(attachment)
    }

    internal fun build(): MailMessage {
        if (this.from.isEmpty()) throw IllegalArgumentException("Sender address can not be empty")
        if (this.to.isEmpty() && this.cc.isEmpty() && this.bcc.isEmpty()) throw IllegalArgumentException("Receiver address can not be empty")
        if (this.subject == null) throw IllegalArgumentException("Subject can not be empty")

        return MailMessage(
            from = this.from,
            to = this.to,
            cc = this.cc,
            bcc = this.bcc,
            headers = this.headers,
            subject = this.subject!!,
            textBody = this.textBody,
            htmlBody = this.htmlBody,
            attachments = this.attachments,
            inlineFiles = this.inlineFiles
        )
    }
}
