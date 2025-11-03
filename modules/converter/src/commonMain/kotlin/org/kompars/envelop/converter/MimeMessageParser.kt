package org.kompars.envelop.converter

import io.ktor.http.ContentType
import io.ktor.http.charset
import jakarta.mail.Session
import jakarta.mail.internet.MimeMessage
import java.util.Properties
import org.slf4j.LoggerFactory

public object MimeMessageParser {
    private val session = Session.getInstance(Properties())
    private val logger = LoggerFactory.getLogger("org.kompars.envelop.converter.MimeMessageParser")

    private val multilineEncodingRegex = "=\\?utf-8\\?B\\?(.*)\\?=\r?\n *=\\?utf-8\\?B\\?(.*)\\?=".toRegex()

    public fun parse(eml: ByteArray): MimeMessage {
        var mimeMessage = MimeMessage(session, eml.inputStream())

        val subject = mimeMessage.subject
        val charset = ContentType.parse(mimeMessage.contentType).charset()

        if (charset == Charsets.UTF_8 && subject.contains('�')) {
            logger.warn("Fixing invalid EML - $subject")

            try {
                mimeMessage = MimeMessage(session, fixMultilineEncoding(eml).inputStream())
            } catch (e: Exception) {
                logger.error("Failed to parse fixed EML", e)
            }
        }

        return mimeMessage
    }

    private fun fixMultilineEncoding(eml: ByteArray): ByteArray {
        var last = eml.decodeToString()
        var previous = last

        while (true) {
            last = previous.replace(multilineEncodingRegex) { "=?utf-8?B?${it.groupValues[1]}${it.groupValues[2]}?=" }
            if (previous == last) {
                break
            } else {
                previous = last
            }
        }

        return last.encodeToByteArray()
    }
}
