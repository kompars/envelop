package org.kompars.envelop.common.test

//import kotlin.jvm.*
//
//
///**
// * The `EmailValidator` class provides a way to validate email addresses
// * beyond what the basic validation of [JMail.tryParse] provides.
// *
// *
// * Custom rules can be added to an `EmailValidator` that will perform any
// * additional required validations.
// *
// *
// * Example usage:
// *
// * <pre>
// * Optional&#60;Email&#62; parsedEmail = JMail.validator()
// * .disallowIpDomain()
// * .requireTopLevelDomain()
// * .withRule(email -> email.domain().startsWith("test"))
// * .tryParse("test@test.com");
//</pre> *
// */
//class EmailValidator @JvmOverloads internal constructor(validationPredicates: MutableSet<java.util.function.Predicate<Email?>?> = java.util.HashSet<java.util.function.Predicate<Email?>?>()) {
//    private val validationPredicates: MutableSet<java.util.function.Predicate<Email?>?>
//
//    init {
//        this.validationPredicates = java.util.Collections.unmodifiableSet<java.util.function.Predicate<Email?>?>(validationPredicates)
//    }
//
//    /**
//     * Create a new `EmailValidator` with all rules from the current instance and the
//     * additional provided custom validation rules.
//     *
//     *
//     * Example usage:
//     *
//     * <pre>
//     * validator.withRules(List.of(
//     * email -> email.domain().startsWith("test"),
//     * email -> email.localPart.contains("hello")));
//    </pre> *
//     *
//     * @param rules a collection of requirements that make a valid email address
//     * @return the new `EmailValidator` instance
//     */
//    fun withRules(rules: MutableCollection<java.util.function.Predicate<Email?>?>?): EmailValidator {
//        val ruleSet: MutableSet<java.util.function.Predicate<Email?>?> = java.util.HashSet<java.util.function.Predicate<Email?>?>(validationPredicates)
//        ruleSet.addAll(rules)
//
//        return EmailValidator(ruleSet)
//    }
//
//    /**
//     * Create a new `EmailValidator` with all rules from the current instance and an
//     * additional provided custom validation rule.
//     *
//     *
//     * Example usage:
//     *
//     * <pre>
//     * validator.withRule(email -> email.domain().startsWith("test"));
//    </pre> *
//     *
//     * @param rule the requirement for a valid email address. This must be a [Predicate] that
//     * accepts an [Email] object.
//     * @return the new `EmailValidator` instance
//     */
//    fun withRule(rule: java.util.function.Predicate<Email?>?): EmailValidator {
//        return withRules(mutableListOf<java.util.function.Predicate<Email?>?>(rule))
//    }
//
//    /**
//     * Create a new `EmailValidator` with all rules from the current instance and the
//     * [ValidationRules.disallowIpDomain] rule.
//     * Email addresses that have an IP address for a domain will fail validation.
//     *
//     *
//     * For example, `"sample@[1.2.3.4]"` would be invalid.
//     *
//     * @return the new `EmailValidator` instance
//     */
//    fun disallowIpDomain(): EmailValidator {
//        return withRule(DISALLOW_IP_DOMAIN_PREDICATE)
//    }
//
//    /**
//     * Create a new `EmailValidator` with all rules from the current instance and the
//     * [ValidationRules.requireTopLevelDomain] rule.
//     * Email addresses that do not have a top level domain will fail validation.
//     *
//     *
//     * For example, `"sample@mailserver"` would be invalid.
//     *
//     * @return the new `EmailValidator` instance
//     */
//    fun requireTopLevelDomain(): EmailValidator {
//        return withRule(REQUIRE_TOP_LEVEL_DOMAIN_PREDICATE)
//    }
//
//    /**
//     * Create a new `EmailValidator` with all rules from the current instance and the
//     * [ValidationRules.disallowExplicitSourceRouting] rule.
//     * Email addresses that have explicit source routing will fail validation.
//     *
//     *
//     * For example, `"@1st.relay,@2nd.relay:user@final.domain"` would be invalid.
//     *
//     * @return the new `EmailValidator` instance
//     */
//    fun disallowExplicitSourceRouting(): EmailValidator {
//        return withRule(DISALLOW_EXPLICIT_SOURCE_ROUTING)
//    }
//
//    /**
//     * Create a new `EmailValidator` with all rules from the current instance and the
//     * [ValidationRules.disallowQuotedIdentifiers] rule.
//     * Email addresses that have quoted identifiers will fail validation.
//     *
//     *
//     * For example, `"John Smith <test@server.com>"` would be invalid.
//     *
//     * @return the new `EmailValidator` instance
//     */
//    fun disallowQuotedIdentifiers(): EmailValidator {
//        return withRule(DISALLOW_QUOTED_IDENTIFIERS)
//    }
//
//    /**
//     * Create a new `EmailValidator` with all rules from the current instance and the
//     * [ValidationRules.disallowReservedDomains] rule.
//     * Email addresses that have a reserved domain according to RFC 2606 will fail validation.
//     *
//     *
//     * For example, `"name@example.com"` would be invalid.
//     *
//     * @return the new `EmailValidator` instance
//     */
//    fun disallowReservedDomains(): EmailValidator {
//        return withRule(DISALLOW_RESERVED_DOMAINS_PREDICATE)
//    }
//
//    /**
//     * Create a new `EmailValidator` with all rules from the current instance and the
//     * [ValidationRules.requireOnlyTopLevelDomains] rule.
//     * Email addresses that have top level domains other than those provided will
//     * fail validation.
//     *
//     *
//     * For example, if you require only [TopLevelDomain.DOT_COM], the email address
//     * `"name@host.net"` would be invalid.
//     *
//     * @param allowed the set of allowed [TopLevelDomain]
//     * @return the new `EmailValidator` instance
//     */
//    fun requireOnlyTopLevelDomains(vararg allowed: TopLevelDomain?): EmailValidator {
//        return withRule(java.util.function.Predicate { email: Email? ->
//            ValidationRules.requireOnlyTopLevelDomains(
//                email, java.util.Arrays.stream<TopLevelDomain?>(allowed).collect(java.util.stream.Collectors.toSet())
//            )
//        })
//    }
//
//    /**
//     * Create a new `EmailValidator` with all rules from the current instance and the
//     * [ValidationRules.disallowObsoleteWhitespace] rule.
//     * Email addresses that have obsolete folding whitespace according to RFC 2822 will fail
//     * validation.
//     *
//     *
//     * For example, `"1234   @   local(blah)  .com"` would be invalid.
//     *
//     * @return the new `EmailValidator` instance
//     */
//    fun disallowObsoleteWhitespace(): EmailValidator {
//        return withRule(DISALLOW_OBSOLETE_WHITESPACE_PREDICATE)
//    }
//
//    /**
//     * Create a new `EmailValidator` with all rules from the current instance and the
//     * [ValidationRules.requireValidMXRecord] rule.
//     * Email addresses that have a domain without a valid MX record will fail validation.
//     *
//     *
//     * **NOTE: Adding this rule to your EmailValidator may increase
//     * the amount of time it takes to validate email addresses, as the default initial timeout is
//     * 100ms and the number of retries using exponential backoff is 2.
//     * Use [.requireValidMXRecord] to customize the timeout and retries.**
//     *
//     * @return the new `EmailValidator` instance
//     */
//    fun requireValidMXRecord(): EmailValidator {
//        return withRule(REQUIRE_VALID_MX_RECORD_PREDICATE)
//    }
//
//    /**
//     * Create a new `EmailValidator` with all rules from the current instance and the
//     * [ValidationRules.requireValidMXRecord] rule.
//     * Email addresses that have a domain without a valid MX record will fail validation.
//     *
//     *
//     * This method allows you to customize the timeout and retries for performing DNS lookups.
//     * The initial timeout is supplied in milliseconds, and the number of retries indicate how many
//     * times to retry the lookup using exponential backoff. Each successive retry will use a
//     * timeout that is twice as long as the previous try.
//     *
//     * @param initialTimeout the timeout in milliseconds for the initial DNS lookup
//     * @param numRetries the number of retries to perform using exponential backoff
//     * @return the new `EmailValidator` instance
//     */
//    fun requireValidMXRecord(initialTimeout: Int, numRetries: Int): EmailValidator {
//        return withRule(java.util.function.Predicate { email: Email? -> ValidationRules.requireValidMXRecord(email, initialTimeout, numRetries) })
//    }
//
//    /**
//     * Create a new `EmailValidator` with all rules from the current instance and the
//     * [ValidationRules.requireAscii] rule.
//     * Email addresses that contain characters other than those in the ASCII charset will fail
//     * validation.
//     *
//     *
//     * For example, `"jørn@test.com"` would be invalid.
//     *
//     * @return the new `EmailValidator` instance
//     */
//    fun requireAscii(): EmailValidator {
//        return withRule(REQUIRE_ASCII_PREDICATE)
//    }
//
//    /**
//     * Return true if the given email address is valid according to all registered validation rules,
//     * or false otherwise. See [JMail.tryParse] for details on the basic
//     * validation that is always performed.
//     *
//     * @param email the email address to validate
//     * @return the result of the validation
//     */
//    fun isValid(email: String?): Boolean {
//        return JMail.tryParse(email)
//            .filter({ email: Email? -> this.passesPredicates(email) })
//            .isPresent()
//    }
//
//    /**
//     * Return true if the given email address is **NOT** valid according to all
//     * registered validation rules, or false otherwise. See [JMail.tryParse] for
//     * details on the basic validation that is always performed.
//     *
//     * @param email the email address to validate
//     * @return the result of the validation
//     */
//    fun isInvalid(email: String?): Boolean {
//        return !isValid(email)
//    }
//
//    /**
//     * Require that the given email address is valid according to all registered validation rules,
//     * throwing [InvalidEmailException] if the email is invalid. See
//     * [JMail.tryParse] for details on the basic validation that is always performed.
//     *
//     * @param email the email address to validate
//     * @throws InvalidEmailException if the validation fails
//     */
//    @Throws(InvalidEmailException::class)
//    fun enforceValid(email: String?) {
//        if (!isValid(email)) {
//            throw InvalidEmailException()
//        }
//    }
//
//    /**
//     * Determine if the given email address is valid, returning a new [EmailValidationResult]
//     * object that contains details on the result of the validation. Use this method if you need to
//     * see the [FailureReason] upon validation failure. See [JMail.tryParse]
//     * for details on what is required of an email address within basic validation.
//     *
//     * @param email the email address to validate
//     * @return a [EmailValidationResult] containing success or failure, along with the parsed
//     * [Email] object if successful, or the [FailureReason] if not
//     */
//    fun validate(email: String?): EmailValidationResult {
//        val result: EmailValidationResult = JMail.validate(email)
//
//        // If failed basic validation, just return it
//        if (!result.getEmail().isPresent()) return result
//
//        // If the address fails custom validation, return failure
//        if (!passesPredicates(result.getEmail().get())) {
//            return EmailValidationResult.failure(FailureReason.FAILED_CUSTOM_VALIDATION)
//        }
//
//        return result
//    }
//
//    /**
//     * Attempts to parse the given email address string, only succeeding if the given address is
//     * valid according to all registered validation rules. See [JMail.tryParse]
//     * for details on the basic validation that is always performed.
//     *
//     * @param email the email address to parse
//     * @return an [Optional] containing the parsed [Email], or empty if the email
//     * is invalid according to all registered validation rules
//     */
//    fun tryParse(email: String?): java.util.Optional<Email?> {
//        return JMail.tryParse(email).filter({ email: Email? -> this.passesPredicates(email) })
//    }
//
//    /**
//     * Test the given email address against all configured validation predicates.
//     *
//     * @param email the email address to test
//     * @return true if it passes the predicates, false otherwise
//     */
//    private fun passesPredicates(email: Email?): Boolean {
//        return validationPredicates.stream()
//            .allMatch { rule: java.util.function.Predicate<Email?>? -> rule.test(email) }
//    }
//
//    override fun toString(): String {
//        return java.util.StringJoiner(", ", EmailValidator::class.java.getSimpleName() + "[", "]")
//            .add("validationRuleCount=" + validationPredicates.size)
//            .toString()
//    }
//
//    companion object {
//        // Define some predicates here so that when adding them to the set of validation
//        // predicates we protect against adding them multiple times.
//        private val DISALLOW_IP_DOMAIN_PREDICATE
//        : java.util.function.Predicate<Email?> = ValidationRules::disallowIpDomain
//        private val REQUIRE_TOP_LEVEL_DOMAIN_PREDICATE
//        : java.util.function.Predicate<Email?> = ValidationRules::requireTopLevelDomain
//        private val DISALLOW_EXPLICIT_SOURCE_ROUTING
//        : java.util.function.Predicate<Email?> = ValidationRules::disallowExplicitSourceRouting
//        private val DISALLOW_QUOTED_IDENTIFIERS
//        : java.util.function.Predicate<Email?> = ValidationRules::disallowQuotedIdentifiers
//        private val DISALLOW_RESERVED_DOMAINS_PREDICATE
//        : java.util.function.Predicate<Email?> = ValidationRules::disallowReservedDomains
//        private val DISALLOW_OBSOLETE_WHITESPACE_PREDICATE
//        : java.util.function.Predicate<Email?> = ValidationRules::disallowObsoleteWhitespace
//        private val REQUIRE_VALID_MX_RECORD_PREDICATE
//        : java.util.function.Predicate<Email?> = ValidationRules::requireValidMXRecord
//        private val REQUIRE_ASCII_PREDICATE
//        : java.util.function.Predicate<Email?> = ValidationRules::requireAscii
//    }
//}
