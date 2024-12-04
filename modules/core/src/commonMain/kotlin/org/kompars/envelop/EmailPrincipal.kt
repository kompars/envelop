package org.kompars.envelop

public data class EmailPrincipal(
    val name: String? = null,
    val address: EmailAddress,
) {
    public companion object {
        private val pattern = Regex("""(?:"?(?<name>[^"]*)"?\s+)?<?(?<email>.+@[^>]+)>?""")

        public fun parse(input: String): EmailPrincipal {
            val match = pattern.matchEntire(input)
                ?: throw IllegalArgumentException("Invalid input format")

            val name = match.groups["name"]?.value?.trim()
            val email = match.groups["email"]!!.value.trim()

            return EmailPrincipal(name, EmailAddress(email))
        }
    }

    override fun toString(): String {
        return when (name) {
            null -> address.email
            else -> buildString(name.length + address.email.length + 3) {
                append(name)
                append(" ")
                append("<")
                append(address.email)
                append(">")
            }
        }
    }
}
