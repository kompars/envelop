package org.kompars.envelop

import kotlinx.datetime.*

public data class MailMessage(
    val id: String? = null,
    val from: List<EmailPrincipal> = emptyList(),
    val to: List<EmailPrincipal> = emptyList(),
    val cc: List<EmailPrincipal> = emptyList(),
    val bcc: List<EmailPrincipal> = emptyList(),
    val references: List<String> = emptyList(),
    val headers: Map<String, String> = emptyMap(),
    val subject: String? = null,
    val textBody: String? = null,
    val htmlBody: String? = null,
    val sentAt: Instant? = null,
    val attachments: List<MailFile> = emptyList(),
    val inlineFiles: List<MailFile> = emptyList(),
)

public data class MailFile(
    val name: String? = null,
    val contentType: String? = null,
    val contentId: String? = null,
    val contentProvider: MailFileContentProvider,
)

public fun interface MailFileContentProvider {
    public suspend fun getContent(): ByteArray
}

public class ByteArrayContentProvider(private val content: ByteArray) : MailFileContentProvider {
    override suspend fun getContent(): ByteArray {
        return content
    }
}
