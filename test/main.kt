package org.kompars.envelop.common.test

import kotlin.time.*
import kotlin.uuid.*
import org.kompars.envelop.common.*

@OptIn(ExperimentalUuidApi::class)
public fun main() {
    //val result = EmailParser.parse("Morki San    <\"Morki    \"  . \"Navratil-]\"@(s)[192.168.0.1]>")
    //val result = EmailParser.parse("Morki San    <Morki      .  \"Navratil-]\"   @(s)[192.168.0.1]>")
    for (i in 0..1000000) {
        val random = Uuid.random()
        val (result, time) = measureTimedValue {
            EmailAddress.parse("$random Morki San    <\"Morki    \"  . \"Navratil-]\"@(s)[192.168.0.1]>", allowIdentifier = true)
        }

        println("$time - $result")

        //when (result) {
        //    is EmailAddressParsingResult.Success -> println(result.emailAddress)
        //    is EmailAddressParsingResult.Failure -> println(result.failureReason)
        //}
    }
}
