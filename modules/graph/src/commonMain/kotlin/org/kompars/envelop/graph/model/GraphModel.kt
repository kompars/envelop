package org.kompars.envelop.graph.model

import kotlinx.datetime.*
import kotlinx.serialization.*

@Serializable
public data class GraphResponse<T>(
    @SerialName("value")
    val value: T,
)

@Serializable
public data class MessagePreview(
    @SerialName("id")
    val id: String,

    @SerialName("receivedDateTime")
    val receivedDateTime: Instant,
)

@Serializable
public data class MoveMessage(
    @SerialName("destinationId")
    val destinationId: String,
)
