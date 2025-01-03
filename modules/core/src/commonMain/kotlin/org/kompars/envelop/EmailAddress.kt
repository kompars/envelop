package org.kompars.envelop

import kotlin.jvm.*

@JvmInline
public value class EmailAddress(public val email: String) {
    init {
        require(isValid(email)) { "Invalid email address format" }
    }

    public companion object {
        private val emailRegex = Regex(
            "^(?<user>[a-zA-Z0-9.%+-]+)(?:\\+(?<tag>[a-zA-Z0-9.%+-]+))?@(?<host>[a-zA-Z0-9.-]+(?:\\.[a-zA-Z]{2,})?)$"
        )

        public fun isValid(email: String): Boolean {
            return emailRegex.matches(email)
        }
    }

    public val user: String get() = matchGroups("user")!!
    public val tag: String? get() = matchGroups("tag")
    public val host: String get() = matchGroups("host")!!

    private fun matchGroups(groupName: String): String? {
        return emailRegex.matchEntire(email)?.groups[groupName]?.value
    }

    override fun toString(): String {
        return email
    }
}
