package org.kompars.envelop

import kotlinx.datetime.*
import org.kompars.envelop.common.*

public data class EmailMessage(
    val messageId: EmailMessageId? = null,
    val references: List<EmailMessageId> = emptyList(),
    val recipients: List<EmailRecipient> = emptyList(),
    val headers: Map<String, String> = emptyMap(),
    val date: Instant? = null,
    val subject: String = "",
    val textBody: String? = null,
    val htmlBody: String? = null,
    val attachments: List<EmailAttachment> = emptyList(),
) {
    public companion object {
        public const val REPLY_PREFIX: String = "RE: "
        public const val FORWARD_PREFIX: String = "FWD: "
    }
}

public inline fun EmailMessage.Companion.build(block: EmailMessageBuilder.() -> Unit): EmailMessage {
    return EmailMessageBuilder().apply(block).build()
}

public fun EmailMessage.prepareReply(all: Boolean = true): EmailMessage {
    val to = recipients.filter { it.type == EmailRecipientType.ReplyTo }
        .ifEmpty { recipients.filter { it.type == EmailRecipientType.From } }
        .map { EmailRecipient(EmailRecipientType.To, it.address) }

    val recipients = when (all) {
        true -> to + recipients.filter { it.type == EmailRecipientType.Cc }
        false -> to
    }

    return EmailMessage.build {
        subject(EmailMessage.REPLY_PREFIX + subject)
        references(references + listOfNotNull(messageId))
        recipients.forEach {
            recipient(it.type, it.address)
        }
    }
}

public fun EmailMessage.prepareForward(): EmailMessage {
    return EmailMessage.build {
        subject(EmailMessage.FORWARD_PREFIX + subject)
        references(references + listOfNotNull(messageId))
    }
}
