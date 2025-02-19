package org.kompars.envelop.mox

import io.ktor.utils.io.*
import org.kompars.envelop.*
import org.kompars.envelop.common.*
import org.kompars.envelop.mox.model.*

public class MoxMailReceiver(
    private val moxApi: MoxApi,
    public val incomingWebhooks: MoxIncomingWebhooks = MoxIncomingWebhooks(),
) : MailReceiver {
    override fun onMessage(block: suspend (MailMessage) -> Unit) {
        incomingWebhooks.registerCallback { incoming ->
            val files = incoming.structure.flatten()

            val message = MailMessage(
                id = incoming.messageId,
                from = incoming.from.map { it.toEmailAddress() },
                to = incoming.to.map { it.toEmailAddress() },
                cc = incoming.cc.map { it.toEmailAddress() },
                bcc = incoming.bcc.map { it.toEmailAddress() },
                subject = incoming.subject,
                textBody = incoming.text?.ifEmpty { null },
                htmlBody = incoming.html?.ifEmpty { null },
                sentAt = incoming.meta.received,
                references = incoming.references,
                attachments = files
                    .filter { it.second.contentDisposition == "attachment" }
                    .map { it.second.toMailFile(incoming.meta.messageId, it.first) },
                inlineFiles = files
                    .filter { it.second.contentDisposition == "inline" }
                    .map { it.second.toMailFile(incoming.meta.messageId, it.first) },
            )

            block(message)
        }
    }

    private fun NameAddress.toEmailAddress(): EmailAddress {
        return EmailAddress.parse(address).withIdentifier(name?.ifEmpty { null })
    }

    private fun Structure.flatten(partPath: List<Int> = listOf()): List<Pair<List<Int>, Structure>> {
        return listOf(partPath to this) + parts.flatMapIndexed { index, structure ->
            structure.flatten(partPath + index)
        }
    }

    private fun Structure.toMailFile(messageId: Int, partPath: List<Int>): MailFile {
        return MailFile(
            name = fileName.ifEmpty { null },
            contentType = contentType.ifEmpty { null },
            contentId = contentId.ifEmpty { null },
            contentProvider = MoxFileContentProvider(
                moxApi = moxApi,
                messageId = messageId,
                partPath = partPath,
            )
        )
    }
}

public class MoxFileContentProvider internal constructor(
    private val moxApi: MoxApi,
    private val messageId: Int,
    private val partPath: List<Int>,
) : MailFileContentProvider {
    override suspend fun getContent(): ByteArray {
        val request = MessagePartGetRequest(
            messageId = messageId,
            partPath = partPath,
        )

        return moxApi.messagePartGet(request).toByteArray()
    }
}
