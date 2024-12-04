package org.kompars.envelop

public interface MailSender {
    public suspend fun send(message: MailMessage)
    public fun onDelivery(block: suspend (DeliveryStatus) -> Unit)
}

public enum class DeliveryStatus {
    Delivered,
    Delayed,
    Failed,
    Unknown,
}
