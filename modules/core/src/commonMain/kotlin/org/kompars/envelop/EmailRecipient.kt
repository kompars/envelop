package org.kompars.envelop

import org.kompars.envelop.common.*

public enum class EmailRecipientType {
    From,
    ReplyTo,
    To,
    Cc,
    Bcc,
}

public data class EmailRecipient(
    val type: EmailRecipientType,
    val address: EmailAddress,
)
