package org.kompars.envelop.common

import kotlin.jvm.*
import kotlin.uuid.*
import kotlinx.serialization.*
import org.kompars.envelop.common.serialization.*

@JvmInline
@Serializable(with = EmailMessageIdSerializer::class)
public value class EmailMessageId private constructor(public val id: String) {
    init {
        require(id.isNotBlank()) { "Message ID cannot be blank - ID: '$id'" }
        require(id.contains('@')) { "Message ID must contain '@' - ID: '$id'" }
        require(id.startsWith('<')) { "Message ID must start with '<' - ID: '$id'" }
        require(id.endsWith('>')) { "Message ID must end with '>' - ID: '$id'" }
    }

    override fun toString(): String {
        return id
    }

    public companion object {
        public fun parse(value: String, lowerCase: Boolean = true): EmailMessageId {
            val normalized = when (lowerCase) {
                true -> value.lowercase()
                false -> value
            }

            return EmailMessageId(normalized.trim())
        }

        @OptIn(ExperimentalUuidApi::class)
        public fun random(domain: String? = null): EmailMessageId {
            val id = Uuid.random().toHexString()
            val domain = domain ?: Uuid.random().toHexString().plus(".local")

            return EmailMessageId("<$id@$domain>")
        }
    }
}
