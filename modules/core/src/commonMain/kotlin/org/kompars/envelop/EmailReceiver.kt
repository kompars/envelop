package org.kompars.envelop

import kotlinx.datetime.*

public interface EmailReceiver {
    public fun onMessage(block: suspend (EmailMessage, Instant) -> Unit)
}
