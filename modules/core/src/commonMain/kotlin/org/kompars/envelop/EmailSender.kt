package org.kompars.envelop

import kotlinx.datetime.*
import org.kompars.envelop.common.*

public interface EmailSender {
    public suspend fun send(message: EmailMessage): EmailSent
    public fun onDelivery(block: suspend (Delivery) -> Unit)
}

public suspend fun EmailSender.send(block: EmailMessageBuilder.() -> Unit): EmailSent {
    return send(EmailMessage.build(block))
}

public data class EmailSent(
    val id: EmailMessageId,
    val sentAt: Instant,
    val submissions: List<Submission>,
)

public data class Submission(
    val id: String?,
    val recipient: EmailAddress,
)

public data class Delivery(
    val submissionId: String,
    val status: DeliveryStatus,
    val error: String? = null,
)

public enum class DeliveryStatus {
    Delivered,
    Delayed,
    Failed,
    Unknown,
}
