package org.kompars.envelop.converter

import jakarta.mail.Session
import jakarta.mail.internet.MimeMessage
import java.util.Properties
import org.slf4j.LoggerFactory

public object MimeMessageParser {
    private val session = Session.getInstance(Properties())
    private val logger = LoggerFactory.getLogger("org.kompars.envelop.converter.MimeMessageParser")

    private val multilineEncodingRegex = "=\\?utf-8\\?B\\?(.*)\\?=\r?\n *=\\?utf-8\\?B\\?(.*)\\?=".toRegex()

    public fun parse(eml: ByteArray): MimeMessage {
        val mimeMessage = MimeMessage(session, eml.inputStream())
        val subject = mimeMessage.subject

        if (subject.any { it.code == 65533 }) {
            logger.info("Fixing invalid subject - $subject")

            try {
                val fixedMimeMessage = MimeMessage(session, fixMultilineEncoding(eml).inputStream())
                mimeMessage.subject = fixedMimeMessage.subject
            } catch (e: Exception) {
                logger.warn("Failed to fix invalid subject", e)
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
