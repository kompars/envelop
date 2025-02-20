package org.kompars.envelop.common

import kotlinx.serialization.*
import org.kompars.envelop.common.serialization.*

public class InvalidEmailAddressException(public val error: EmailAddressParseError) : RuntimeException("Error parsing email address: $error")

@ConsistentCopyVisibility
@Serializable(with = EmailAddressSerializer::class)
public data class EmailAddress internal constructor(
    val localPart: String,
    val domain: String,
    val identifier: String? = null,
) {
    public override fun toString(): String {
        return when (identifier) {
            null -> "$localPart@$domain"
            else -> "$identifier <$localPart@$domain>"
        }
    }

    public fun withIdentifier(identifier: String?): EmailAddress {
        return when {
            identifier == this.identifier -> this
            else -> EmailAddress(localPart, domain, identifier)
        }
    }

    public companion object {
        public const val SIMPLE_PATTERN: String = "^[a-z0-9\\.\\+_\\-]+@[a-z0-9](?:[a-z0-9\\-]{0,61}[a-z0-9])?(?:\\.[a-z0-9](?:[a-z0-9\\-]{0,61}[a-z0-9])?)+$"

        public fun parse(input: String, allowIdentifier: Boolean = false, lowerCase: Boolean = true): EmailAddress {
            return EmailAddressParser.parse(input, allowIdentifier, lowerCase).getOrThrow()
        }

        public fun parseOrNull(input: String, allowIdentifier: Boolean = false, lowerCase: Boolean = true): EmailAddress? {
            return EmailAddressParser.parse(input, allowIdentifier, lowerCase).getOrNull()
        }

        public fun tryParse(input: String, allowIdentifier: Boolean = false, lowerCase: Boolean = true): Result<EmailAddress> {
            return EmailAddressParser.parse(input, allowIdentifier, lowerCase)
        }

        public fun isValid(input: String, allowIdentifier: Boolean = false): Boolean {
            return EmailAddressParser.parse(input, allowIdentifier, false).isSuccess
        }
    }
}

public fun String.toEmailAddress(): EmailAddress {
    return EmailAddress.parse(this)
}
