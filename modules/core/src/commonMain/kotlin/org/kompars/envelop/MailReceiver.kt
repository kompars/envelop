package org.kompars.envelop

public interface MailReceiver {
    public fun onMessage(block: suspend (MailMessage) -> Unit)
}
