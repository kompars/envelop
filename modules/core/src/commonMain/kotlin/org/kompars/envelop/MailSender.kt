package org.kompars.envelop

import kotlinx.datetime.*

public interface MailSender {
    public suspend fun send(message: MailMessage): EmailSent
    public fun onDelivery(block: suspend (Delivery) -> Unit)
}

public data class EmailSent(
    val id: String,
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
