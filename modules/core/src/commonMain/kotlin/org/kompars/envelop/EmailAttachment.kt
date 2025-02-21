package org.kompars.envelop

import org.kompars.envelop.blob.*

public enum class EmailAttachmentType {
    Inline,
    Attachment,
}

public data class EmailAttachment(
    val type: EmailAttachmentType,
    val name: String,
    val contentType: String,
    val contentId: String? = null,
    val blob: Blob,
)
