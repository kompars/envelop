package org.kompars.envelop.common.test

///**
// * Provides common email address validation rules that can be added to an
// * [EmailValidator].
// */
//public object ValidationRules {
//    // Set of reserved TLDs according to RFC 2606, section 2
//    // https://datatracker.ietf.org/doc/html/rfc2606
//    private val reservedTopLevelDomains: Set<String> = setOf("test", "invalid", "example", "localhost")
//
//    // Set of reserved second level domains according to RFC 2606, section 3
//    // Reserved second level domains are all "example.*" The values for * are defined here.
//    private val reservedExampleTlds: Set<TopLevelDomain> = setOf(TopLevelDomain.DOT_COM, TopLevelDomain.DOT_NET, TopLevelDomain.DOT_ORG)
//
//    /**
//     * Rejects an email address that has an IP address as the domain. For example, the address
//     * `"test@[12.34.56.78]"` would be rejected.
//     *
//     * @param email the email address to validate
//     * @return true if this email address does not have an IP address domain, or false if it does
//     */
//    public fun disallowIpDomain(email: Email): Boolean {
//        return !email.isIpAddress()
//    }
//
//    /**
//     * Rejects an email address that does not have a top-level domain. For example, the address
//     * `"admin@mailserver"` would be rejected.
//     *
//     * @param email the email address to validate
//     * @return true if this email address has a top-level domain, or false if it does not
//     */
//    public fun requireTopLevelDomain(email: Email): Boolean {
//        return email.topLevelDomain() != TopLevelDomain.NONE
//    }
//
//    /**
//     * Rejects an email address that uses explicit source routing. Explicit source routing has been
//     * [deprecated](https://datatracker.ietf.org/doc/html/rfc5321#section-3.6.1)
//     * as of RFC 5321 and you SHOULD NOT use explicit source routing except under unusual
//     * circumstances.
//     *
//     * For example, the address `"@1st.relay,@2nd.relay:user@final.domain"` would be
//     * rejected.
//     *
//     * @param email the email address to validate
//     * @return true if the email address does not contain explicit source routing, or false if it does
//     */
//    public fun disallowExplicitSourceRouting(email: Email): Boolean {
//        return email.explicitSourceRoutes().size() <= 0
//    }
//
//    /**
//     * Rejects an email address that has quoted identifiers. For example, the address
//     * `"John Smith <test@server.com>"` would be rejected.
//     *
//     * @param email the email address to validate
//     * @return true if this email address does not have a quoted identifier, or false if it does
//     */
//    public fun disallowQuotedIdentifiers(email: Email): Boolean {
//        return !email.hasIdentifier()
//    }
//
//    /**
//     * Rejects an email address that has a top-level domain other than the ones in the allowed set.
//     * For example, if `allowed` is `[DOT_COM, DOT_ORG]`, then the address
//     * `"test@example.net"` would be rejected.
//     *
//     * @param email the email address to validate
//     * @param allowed the set of allowed [TopLevelDomain]
//     * @return true if this email address has an allowed top-level domain, or false if it does not
//     */
//    public fun requireOnlyTopLevelDomains(email: Email, allowed: Set<TopLevelDomain>): Boolean {
//        return email.topLevelDomain() in allowed
//    }
//
//    /**
//     * Rejects an email address that has obsolete whitespace within the local-part or domain.
//     * For example, the address `"1234   @   local(blah)  .com"` would be rejected.
//     *
//     * @param email the email address to validate
//     * @return true if this email does not contain obsolete whitespace, or false if it does
//     */
//    public fun disallowObsoleteWhitespace(email: Email): Boolean {
//        return !email.containsWhitespace()
//    }
//
//    /**
//     * Rejects an email address that has a reserved domain according to
//     * [RFC 2606](https://datatracker.ietf.org/doc/html/rfc2606). The reserved domains
//     * are:
//     *
//     *  * `.test`
//     *  * `.example`
//     *  * `.invalid`
//     *  * `.localhost`
//     *  * `example.com`
//     *  * `example.org`
//     *  * `example.net`
//     *
//     * @param email the email address to validate
//     * @return true if this email address does not have a reserved domain, or false if it does
//     */
//    public fun disallowReservedDomains(email: Email): Boolean {
//        val domainParts = email.domainParts()
//
//        // Check the top level domain to see if it is reserved, if so return false
//        if (reservedTopLevelDomains.contains(domainParts[domainParts.size - 1])) {
//            return false
//        }
//
//        // Check the second level domain to see if it is example.*, where * is contained in
//        // the reservedExampleTlds set
//        if (domainParts.size > 1 && "example" == domainParts[domainParts.size - 2] && email.topLevelDomain() in reservedExampleTlds) {
//            return false
//        }
//
//        return true
//    }
//
//    /**
//     * Rejects an email address that contains characters other than those in the ASCII set.
//     *
//     * @param email the email address to validate
//     * @return true if this email address only contains ASCII characters, or false if it does not
//     */
//    public fun requireAscii(email: Email): Boolean {
//        return email.isAscii()
//    }
//}
