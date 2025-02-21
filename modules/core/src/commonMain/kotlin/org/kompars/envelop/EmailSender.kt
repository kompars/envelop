package org.kompars.envelop

import org.kompars.envelop.common.*

public interface EmailSender {
    public suspend fun send(message: EmailMessage): EmailSent
    public fun onDelivery(block: suspend (Delivery) -> Unit)
}

public data class EmailSent(
    val messageId: EmailMessageId,
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
