package org.kompars.envelop.mox

import io.ktor.utils.io.*
import org.kompars.envelop.*
import org.kompars.envelop.mox.model.*

public class MoxMailReceiver(
    private val moxApi: MoxApi,
    public val incomingWebhooks: MoxIncomingWebhooks = MoxIncomingWebhooks(),
) : MailReceiver {
    override fun onMessage(block: suspend (MailMessage) -> Unit) {
        incomingWebhooks.registerCallback { incoming ->
            val files = incoming.structure.flatten()

            val message = MailMessage(
                from = incoming.from.map { it.toEmailPrincipal() },
                to = incoming.to.map { it.toEmailPrincipal() },
                cc = incoming.cc.map { it.toEmailPrincipal() },
                bcc = incoming.bcc.map { it.toEmailPrincipal() },
                subject = incoming.subject,
                textBody = incoming.text?.ifEmpty { null },
                htmlBody = incoming.html?.ifEmpty { null },
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

    private fun NameAddress.toEmailPrincipal(): EmailPrincipal {
        return EmailPrincipal(name = name?.ifEmpty { null }, address = EmailAddress(address))
    }

    private fun Structure.flatten(partPath: List<Int> = listOf(0)): List<Pair<List<Int>, Structure>> {
        return listOf(partPath to this) + parts.flatMapIndexed { index, structure ->
            structure.flatten(partPath + index)
        }
    }

    private fun Structure.toMailFile(messageId: Int, partPath: List<Int>): MailFile {
        return MailFile(
            name = fileName.ifEmpty { null },
            contentType = contentType,
            contentId = contentId,
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
