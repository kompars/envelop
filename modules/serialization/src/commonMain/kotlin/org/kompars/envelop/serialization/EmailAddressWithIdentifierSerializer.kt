package org.kompars.envelop.serialization

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import org.kompars.envelop.common.*

public typealias SerializableEmailAddressWithIdentifier = @Serializable(with = EmailAddressWithIdentifierSerializer::class) EmailAddress

public object EmailAddressWithIdentifierSerializer : KSerializer<EmailAddress> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("org.kompars.envelop.common.EmailAddress", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: EmailAddress) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): EmailAddress {
        return EmailAddress.Companion.parse(decoder.decodeString(), allowIdentifier = true)
    }
}
