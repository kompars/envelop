package org.kompars.envelop.common.serialization

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import org.kompars.envelop.common.*

public object EmailAddressWithIdentifierSerializer : KSerializer<EmailAddress> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("org.kompars.envelop.common.EmailAddress", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: EmailAddress) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): EmailAddress {
        return EmailAddress.parse(decoder.decodeString(), allowIdentifier = true)
    }
}
