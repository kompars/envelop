package org.kompars.envelop.mox

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.kompars.envelop.mox.model.*

public class MoxIncomingWebhooks : MoxWebhooks<Incoming>(serializer())
public class MoxOutgoingWebhooks : MoxWebhooks<Outgoing>(serializer())

public sealed class MoxWebhooks<T> protected constructor(private val serializer: KSerializer<T>) {
    private val callbacks = mutableListOf<suspend (T) -> Unit>()

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = true
    }

    public suspend fun process(body: String) {
        val data = json.decodeFromString(serializer, body)
        callbacks.forEach { callback ->
            callback(data)
        }
    }

    public fun registerCallback(block: suspend (T) -> Unit) {
        callbacks += block
    }
}
