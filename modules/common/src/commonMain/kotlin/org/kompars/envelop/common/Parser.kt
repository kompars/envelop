package org.kompars.envelop.common

internal object Parser {
    fun parse(input: String, allowIdentifier: Boolean, lowerCase: Boolean): Result<EmailAddress> {
        return parseInternal(input.trim(), allowIdentifier, lowerCase)
    }

    private fun parseInternal(input: String, allowIdentifier: Boolean, lowerCase: Boolean): Result<EmailAddress> {
        // email cannot be less than 3 chars (local-part, @, domain)
        if (input.length < 3) {
            return errorResult(ParseError.ADDRESS_TOO_SHORT)
        }

        // check for source-routing
        if (input[0] == '@') {
            return errorResult(ParseError.BEGINS_WITH_AT_SYMBOL)
        }

        val size = input.length

        // email cannot be more than 320 chars
        if (size > 320) {
            return errorResult(ParseError.ADDRESS_TOO_LONG)
        }

        // email cannot start with '.'
        if (input[0] == '.') {
            return errorResult(ParseError.STARTS_WITH_DOT)
        }

        // email cannot end with '.'
        if (input[size - 1] == '.') {
            return errorResult(ParseError.ENDS_WITH_DOT)
        }

        // email cannot end with '-'
        if (input[size - 1] == '-') {
            return errorResult(ParseError.DOMAIN_PART_ENDS_WITH_DASH)
        }

        var atFound = false // set to true when the '@' character is found
        var inQuotes = false // set to true if we are currently within quotes
        var previousDot = false // set to true if the previous character is '.'
        var previousBackslash = false // set to true if the previous character is '\'
        var firstDomainChar = true // set to false after beginning parsing the domain
        var isIpAddress = false // set to true if encountered an IP address domain
        var requireAtOrDot = false // set to true if the next character should be @ or .
        var requireAtDotOrComment = false // set to true if the next character should be @ . or (
        var whitespace = false // set to true if we are currently within whitespace
        var previousComment = false // set to true if the last character was the end comment
        var requireAngledBracket = false // set to true if we need an angled bracket before the @

        var removableQuotePair = true // set to false if the current quote could not be removed
        var previousQuotedDot = false // set to true if the previous character is '.' in quotes
        var requireQuotedAtOrDot = false // set to true if we need an @ or . for a removable quote

        val localPart = StringBuilder(size)
        var currentQuote = StringBuilder()
        val domain = StringBuilder(size)
        var currentDomainPart = StringBuilder()

        var charactersOnLine = 1 // sine we can have 0 chars on the first line, start at 1

        var i = 0
        while (i < size) {
            val c = input[i]

            if (c == '<' && !inQuotes && !previousBackslash) {
                // could be "phrase <address>" format. If not, it's not allowed
                if (input[size - 1] != '>' || !allowIdentifier) {
                    return errorResult(ParseError.UNQUOTED_ANGLED_BRACKET)
                }

                val innerResult = parseInternal(
                    input = input.substring(i + 1, size - 1),
                    allowIdentifier = false,
                    lowerCase = lowerCase,
                )

                // If the address passed validation, return success with the identifier included.
                // Otherwise, just return the failed internal result
                return when (val inner = innerResult.getOrNull()) {
                    null -> innerResult
                    else -> {
                        val result = EmailAddress(
                            localPart = inner.localPart,
                            domain = inner.domain,
                            identifier = input.substring(0, i).trim().ifEmpty { null },
                        )

                        Result.success(result)
                    }
                }
            }

            if (c == '@' && !inQuotes && !previousBackslash) {
                // If we already found an @ outside of quotes, fail
                if (atFound) {
                    return errorResult(ParseError.MULTIPLE_AT_SYMBOLS)
                }

                // If we need an angled bracket we should fail, it's too late
                if (requireAngledBracket) {
                    return errorResult(ParseError.INVALID_WHITESPACE)
                }

                // Otherwise
                atFound = true
                requireAtDotOrComment = false
                requireAtOrDot = requireAtDotOrComment
                whitespace = false
                previousDot = true // '@' acts like a '.' separator
                i++
                continue
            }

            if (c == '\n') {
                // Ensure there are no empty lines
                if (charactersOnLine <= 0) {
                    return errorResult(ParseError.INVALID_WHITESPACE)
                }

                charactersOnLine = 0
            } else if (c != '\r') {
                // Increment for everything other than \r\n
                charactersOnLine++
            }

            if (requireAtOrDot) {
                // If we needed to find the @ or . and we didn't, we have to fail
                if (!isWhitespace(c) && c != '.') {
                    return errorResult(ParseError.INVALID_COMMENT_LOCATION)
                } else {
                    requireAtOrDot = false
                }
            }

            if (requireAtDotOrComment) {
                // If we needed to find the @ or . ( and we didn't, we have to fail
                if (!isWhitespace(c) && c != '.' && c != '(') {
                    return errorResult(ParseError.INVALID_QUOTE_LOCATION)
                } else {
                    requireAtDotOrComment = false
                }
            }

            if (whitespace) {
                // Whitespace is allowed if it is between parts
                if (!previousDot && !previousComment) {
                    if (c != '.' && c != '@' && c != '(' && !isWhitespace(c)) {
                        if (!atFound) {
                            requireAngledBracket = true // or in phrase <addr> format
                        } else {
                            return errorResult(ParseError.INVALID_WHITESPACE)
                        }
                    }
                }
            }

            // Additional logic to check if the current quote could be removable
            if (requireQuotedAtOrDot && inQuotes) {
                if (c != '.' && c != '@' && !isWhitespace(c) && c != '"') {
                    removableQuotePair = false
                } else if (!isWhitespace(c) && c != '"') {
                    requireQuotedAtOrDot = false
                }
            }

            // If we tried to remove a quote with a comment it would change the
            // meaning of the address
            if (c == '(' && inQuotes && !previousBackslash) {
                removableQuotePair = false
            }

            if (c == '(' && !inQuotes) {
                // validate comment
                val comment = parseComment(input.substring(i))

                if (comment == null) {
                    return errorResult(ParseError.INVALID_COMMENT)
                }

                val commentStr = comment
                val commentStrLen = commentStr.length

                // Now, what do we need surrounding the comment to make it valid?
                if (!atFound && (i != 0 && !previousDot)) {
                    // if at beginning of local part, or we had a dot, ok.
                    // if not, we need to be at the end of the local part '@' or get a dot
                    requireAtOrDot = true
                } else if (atFound && !firstDomainChar && !previousDot) {
                    // if at beginning of domain, or we had a dot, ok.
                    // if not, we need to be at the end of the domain or get a dot
                    if (i + commentStrLen != size) {
                        requireAtOrDot = true
                    }
                }

                i += (commentStrLen - 1)

                previousComment = true
                i++
                continue
            }

            // If we find two dots outside of quotes, fail
            if (c == '.' && previousDot) {
                if (!inQuotes) {
                    return errorResult(ParseError.MULTIPLE_DOT_SEPARATORS)
                } else {
                    removableQuotePair = false
                }
            }

            if (!atFound) {
                // No @ found, we're in the local-part
                // If we are at a new quote: it must be preceded by a dot or at the beginning
                if (c == '"' && i > 0 && !previousDot && !inQuotes) {
                    return errorResult(ParseError.INVALID_QUOTE_LOCATION)
                }

                // If we are not in quotes, and this character is not the quote, make sure the
                // character is allowed
                val mustBeQuoted = DISALLOWED_UNQUOTED_CHARACTERS.contains(c)

                if (c != '"' && !inQuotes && !previousBackslash && mustBeQuoted) {
                    return errorResult(ParseError.DISALLOWED_UNQUOTED_CHARACTER)
                }

                // If we are in quotes and the character requires quotes, mark the pair as not removable
                if (mustBeQuoted && inQuotes && !previousBackslash && c != '"') {
                    removableQuotePair = false
                }

                // If we previously saw a backslash, we must make sure it is being used to quote something
                if (!inQuotes && previousBackslash && !mustBeQuoted && c != ' ' && c != '\\') {
                    return errorResult(ParseError.UNUSED_BACKSLASH_ESCAPE)
                }

                if (inQuotes) {
                    // if we are in quotes, we need to make sure that if the character requires
                    // a backlash escape, that it is there
                    if (ALLOWED_QUOTED_WITH_ESCAPE.contains(c)) {
                        if (!previousBackslash) {
                            return errorResult(ParseError.MISSING_BACKSLASH_ESCAPE)
                        }

                        removableQuotePair = false
                    }
                }

                if (c != '"') {
                    if (inQuotes) {
                        currentQuote.append(c)
                    } else if (!isWhitespace(c)) {
                        localPart.append(c)
                    }
                }
            } else {
                // We're in the domain
                if (firstDomainChar && c == '[') {
                    // validate IP address and be done
                    val ipDomain = input.substring(i)

                    if (!ipDomain.startsWith("[") || !ipDomain.endsWith("]") || ipDomain.length < 3) {
                        return errorResult(ParseError.INVALID_IP_DOMAIN)
                    }

                    val ip = ipDomain.substring(1, ipDomain.length - 1)

                    // If it starts with the IPv6 prefix, validate with IPv6, otherwise it must be IPv4
                    val validatedIp = if (ip.startsWith(IPV6_PREFIX)) {
                        validateIpv6(ip.substring(IPV6_PREFIX.length))?.let { "$IPV6_PREFIX$it" }
                    } else {
                        validateIpv4(ip)
                    }

                    if (validatedIp == null) {
                        return errorResult(ParseError.INVALID_IP_DOMAIN)
                    }

                    currentDomainPart.append(validatedIp)
                    domain.append(validatedIp)

                    isIpAddress = true
                    break
                }

                if (c == '.') {
                    if (currentDomainPart.length > 63) {
                        return errorResult(ParseError.DOMAIN_PART_TOO_LONG)
                    }

                    if (currentDomainPart[0] == '-') {
                        return errorResult(ParseError.DOMAIN_PART_STARTS_WITH_DASH)
                    }

                    if (currentDomainPart[currentDomainPart.length - 1] == '-') {
                        return errorResult(ParseError.DOMAIN_PART_ENDS_WITH_DASH)
                    }

                    currentDomainPart = StringBuilder()
                } else {
                    if (!isWhitespace(c)) {
                        currentDomainPart.append(c)
                    }
                }

                domain.append(c)
                firstDomainChar = false
            }

            val quotedWhitespace = isWhitespace(c) && inQuotes

            if (c == '"' && !previousBackslash) {
                if (inQuotes) {
                    requireAtDotOrComment = true // closing quote, make sure next char is . or @

                    if (currentQuote.isEmpty()) {
                        removableQuotePair = false
                    }

                    if (removableQuotePair) {
                        localPart.append(currentQuote)
                    } else {
                        localPart.append('"')
                        localPart.append(currentQuote)
                        localPart.append('"')
                    }
                } else { // opening quote
                    removableQuotePair = true
                    currentQuote = StringBuilder()
                }

                inQuotes = !inQuotes
            }

            whitespace = isWhitespace(c) && !inQuotes && !previousBackslash

            if (!whitespace) {
                previousDot = c == '.'
            }

            if (!quotedWhitespace) {
                previousQuotedDot = c == '.'
            }

            // For whitespace within quotes we need some special checks to see
            // if this quote would be removable
            if (quotedWhitespace) {
                if (!previousQuotedDot && !previousBackslash) {
                    requireQuotedAtOrDot = true
                }
            }

            // if we already had a prev backslash, this backslash is escaped
            previousBackslash = (c == '\\' && !previousBackslash)
            i++
        }

        if (!atFound) {
            return errorResult(ParseError.MISSING_AT_SYMBOL)
        }

        // Check length
        val localPartLen = localPart.length

        if (localPartLen == 0) {
            return errorResult(ParseError.LOCAL_PART_MISSING)
        }

        if (localPartLen > 64) {
            return errorResult(ParseError.LOCAL_PART_TOO_LONG)
        }

        val domainLen = domain.length

        if (domainLen == 0) {
            return errorResult(ParseError.DOMAIN_MISSING)
        }

        if (domainLen > 255) {
            return errorResult(ParseError.DOMAIN_TOO_LONG)
        }

        // Check that local-part does not end with '.'
        if (localPart[localPart.length - 1] == '.') {
            return errorResult(ParseError.LOCAL_PART_ENDS_WITH_DOT)
        }

        // Ensure the TLD is not empty or greater than 63 chars
        if (currentDomainPart.isEmpty()) {
            return errorResult(ParseError.MISSING_TOP_LEVEL_DOMAIN)
        }

        if (currentDomainPart.length > 63) {
            return errorResult(ParseError.TOP_LEVEL_DOMAIN_TOO_LONG)
        }

        // Check that the final domain part does not start with '-'
        // We already checked to make sure it doesn't end with '-'
        if (currentDomainPart[0] == '-') {
            return errorResult(ParseError.DOMAIN_PART_STARTS_WITH_DASH)
        }

        // Ensure the last domain part (TLD) is not all numeric
        if (currentDomainPart.toString().toCharArray().all { it.isDigit() }) {
            return errorResult(ParseError.NUMERIC_TLD)
        }

        // Validate the characters in the domain if it is not an IP address
        if (!isIpAddress && !isValidIdn(domain.toString())) {
            return errorResult(ParseError.INVALID_DOMAIN_CHARACTER)
        }

        val finalLocalPart = when (lowerCase) {
            true -> localPart.toString().lowercase()
            false -> localPart.toString()
        }

        var finalDomain = when (lowerCase) {
            true -> domain.toString().lowercase()
            false -> domain.toString()
        }

        if (isIpAddress) {
            finalDomain = "[$finalDomain]"
        }

        val result = EmailAddress(
            localPart = finalLocalPart,
            domain = finalDomain,
        )

        return Result.success(result)
    }

    private fun parseComment(s: String): String? {
        if (s.length < 2) return null

        val builder = StringBuilder(s.length)

        var previousBackslash = false
        var foundClosingParenthesis = false

        var i = 0
        val size = s.length
        while (i < size) {
            val c = s[i]

            if (c == '(' && !previousBackslash && i != 0) {
                // comment within a comment??
                val inner = parseComment(s.substring(i))

                if (inner == null) {
                    return null
                }

                i += inner.length - 1
                builder.append(inner)
                i++
                continue
            }

            builder.append(c)

            if (c == ')' && !previousBackslash) {
                foundClosingParenthesis = true
                break
            }

            previousBackslash = c == '\\'
            i++
        }

        if (!foundClosingParenthesis) {
            return null
        }

        return builder.toString()
    }


    public fun validateIpv4(ip: String): String? {
        var currentPart = StringBuilder()
        var partCount = 0

        var i = 0
        val size = ip.length
        while (i < size) {
            val c = ip[i]

            if (c == '.') {
                // End of IPv4 part. Validate the current part and continue if valid
                if (isInvalidIpv4Part(currentPart.toString())) {
                    return null
                }

                partCount++
                currentPart = StringBuilder()
                i++
                continue
            }

            if (c < '0' || c > '9') {
                return null
            }

            currentPart.append(c)
            i++
        }

        if (isInvalidIpv4Part(currentPart.toString())) {
            return null
        }

        partCount++

        // IPv4 must have 4 parts
        if (partCount != 4) {
            return null
        }

        return ip
    }

    public fun validateIpv6(ip: String): String? {
        val len = ip.length

        // Shortest IPv6 is "::"
        if (len < 2) {
            return null
        }

        // IPv6 cannot start with single colon, only double colon
        if (ip[0] == ':' && ip[1] != ':') {
            return null
        }

        // IPv6 cannot end with single colon, only double colon
        if (ip[len - 1] == ':' && ip[len - 2] != ':') {
            return null
        }

        var currentPart = StringBuilder()
        var partCount = 0

        var previousColon = false
        var doubleColon = false
        var isDual = false

        var i = 0
        val size = ip.length
        while (i < size) {
            val c = ip[i]

            if (c == '.') {
                // A dot - we need to check if this is a dual IPv6/IPv4
                isDual = true

                // In a dual address, the IPv6 part can only have up to 6 segments
                if (partCount > 6) {
                    return null
                }

                // Validate the IPv4 address
                val remainingSubstring = ip.substring(i)
                val ipv4 = validateIpv4(currentPart.toString() + remainingSubstring)

                if (ipv4 == null) {
                    return null
                }

                break
            }

            if (c == ':') {
                // We already saw a double colon, we can't see another one
                if (previousColon && doubleColon) {
                    return null
                }

                if (previousColon) {
                    // two colons in a row, we skip
                    doubleColon = true
                    i++
                    continue
                }

                if (currentPart.isNotEmpty()) {
                    if (isInvalidIpv6Part(currentPart.toString())) {
                        return null
                    } else {
                        partCount++
                    }
                }

                currentPart = StringBuilder()
                previousColon = true
                i++
                continue
            } else {
                previousColon = false
            }

            if (!ALLOWED_HEX_CHARACTERS.contains(c)) {
                return null
            }

            currentPart.append(c)
            i++
        }

        if (currentPart.isNotEmpty()) {
            if (isInvalidIpv6Part(currentPart.toString())) {
                return null
            } else {
                partCount++
            }
        }

        if (isDual) {
            // Dual - without a double colon there must be 7 parts exactly.
            // With a double colon there can be no more than 6.
            if ((!doubleColon && partCount != 7) || (doubleColon && partCount > 6)) {
                return null
            }
        } else {
            // Regular - without a double colon there must be 8 parts exactly.
            // With a double colon there can be no more than 7.
            if ((!doubleColon && partCount != 8) || (doubleColon && partCount > 7)) {
                return null
            }
        }

        return ip
    }

    private fun isInvalidIpv4Part(part: String): Boolean {
        // IPv4 can only have 3 digits in a single part
        if (part.length > 3) {
            return true
        }

        try {
            // The int cannot be less than zero since we check isDigit earlier on
            if (part.toInt() > 255) {
                return true
            }
        } catch (e: NumberFormatException) {
            return true
        }

        return false
    }

    private fun isInvalidIpv6Part(part: String): Boolean {
        return part.length > 4
    }

    private fun idnToAscii(test: String): String? {
        return test

        // TODO: replace java usage
        //return try {
        //    java.net.IDN.toASCII(test, java.net.IDN.ALLOW_UNASSIGNED)
        //} catch (e: java.lang.IllegalArgumentException) {
        //    // If IDN.toASCII fails, it's not valid
        //    null
        //}
    }

    private fun isValidIdn(test: String): Boolean {
        val domain = idnToAscii(test) ?: return false

        var i = 0
        val size = domain.length

        while (i < size) {
            val c = domain[i]

            if (!ALLOWED_DOMAIN_CHARACTERS.contains(c)) {
                return false
            }

            i++
        }

        return true
    }

    private fun isWhitespace(c: Char): Boolean {
        return (c == ' ' || c == '\n' || c == '\r')
    }

    private fun errorResult(reason: ParseError): Result<EmailAddress> {
        return Result.failure(InvalidEmailAddressException(reason))
    }

    private const val IPV6_PREFIX = "IPv6:"

    // Set of characters that are not allowed in the local-part outside of quotes
    private val DISALLOWED_UNQUOTED_CHARACTERS: Set<Char> = setOf(
        '\t', '(', ')', ',', ':', ';', '<', '>', '@', '[', ']', '"',
        // Control characters 1-8, 11, 12, 14-31
        '␁', '␂', '␃', '␄', '␅', '␆', '␇', '␈', '␋', '␌', '␎', '␏', '␐', '␑',
        '␒', '␓', '␔', '␕', '␖', '␗', '␘', '␙', '␚', '␛', '␜', '␝', '␟', '␁'
    )

    // Set of characters that are allowed in the domain
    private val ALLOWED_DOMAIN_CHARACTERS: Set<Char> = setOf(
        // A - Z
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
        'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        // a - z
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
        's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        // 0 - 9
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        // Hyphen and dot (also allow whitespace between parts)
        '-', '.', ' '
    )

    // Set of characters within local-part quotes that require an escape
    private val ALLOWED_QUOTED_WITH_ESCAPE: Set<Char> = setOf('\r', '␀', '\n')

    // Set of allowed characters in a HEX number
    private val ALLOWED_HEX_CHARACTERS: Set<Char> = setOf(
        'A', 'B', 'C', 'D', 'E', 'F', 'a', 'b', 'c', 'd', 'e', 'f',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    )
}
