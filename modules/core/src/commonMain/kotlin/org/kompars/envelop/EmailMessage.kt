package org.kompars.envelop

import kotlinx.datetime.*
import org.kompars.envelop.common.*

public data class EmailMessage(
    val id: EmailMessageId? = null,
    val references: List<EmailMessageId> = emptyList(),
    val recipients: List<EmailRecipient> = emptyList(),
    val headers: Map<String, String> = emptyMap(),
    val subject: String? = null,
    val textBody: String? = null,
    val htmlBody: String? = null,
    val attachments: List<EmailAttachment> = emptyList(),
    val sentAt: Instant? = null,
)

public enum class EmailRecipientType {
    From,
    ReplyTo,
    To,
    Cc,
    Bcc,
}

public data class EmailRecipient(
    val type: EmailRecipientType,
    val emailAddress: EmailAddress,
)

public enum class EmailAttachmentType {
    Inline,
    Attachment,
}

public data class EmailAttachment(
    val type: EmailAttachmentType,
    val name: String? = null,
    val contentType: String,
    val contentId: String? = null,
    val contentProvider: EmailAttachmentContentProvider,
)

public fun interface EmailAttachmentContentProvider {
    public suspend fun getContent(): ByteArray
}

public class ByteArrayContentProvider(private val content: ByteArray) : EmailAttachmentContentProvider {
    override suspend fun getContent(): ByteArray {
        return content
    }
}
