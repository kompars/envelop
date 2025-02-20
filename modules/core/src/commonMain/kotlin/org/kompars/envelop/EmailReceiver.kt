package org.kompars.envelop

public interface EmailReceiver {
    public fun onMessage(block: suspend (EmailMessage) -> Unit)
}
