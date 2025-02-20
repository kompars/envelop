package org.kompars.envelop.common.serialization

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import org.kompars.envelop.common.*

public object EmailMessageIdSerializer : KSerializer<EmailMessageId> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("org.kompars.envelop.common.EmailMessageId", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: EmailMessageId) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): EmailMessageId {
        return EmailMessageId.parse(decoder.decodeString())
    }
}
