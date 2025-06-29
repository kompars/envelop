package org.kompars.envelop

import kotlin.time.*

public interface EmailReceiver {
    public fun onMessage(block: suspend (EmailMessage, Instant) -> Unit)
}
