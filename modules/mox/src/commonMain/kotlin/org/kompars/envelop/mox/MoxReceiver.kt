package org.kompars.envelop.mox

import io.ktor.utils.io.*
import org.kompars.envelop.*
import org.kompars.envelop.mox.model.*

public class MoxReceiver(private val moxApi: MoxApi, private val incomingWebhooks: MoxIncomingWebhooks) : MailReceiver {
    override fun onMessage(block: suspend (MailMessage) -> Unit) {
        incomingWebhooks.registerCallback { incoming ->
            val files = incoming.structure.flatten().map { (partPath, structure) ->
                MailFile(
                    name = structure.contentTypeParams["name"],
                    contentType = structure.contentType,
                    contentId = structure.contentId,
                    contentProvider = MoxFileContentProvider(
                        moxApi = moxApi,
                        messageId = incoming.meta.messageId,
                        partPath = partPath,
                    )
                )
            }

            val message = MailMessage(
                from = incoming.from.map { it.toEmailPrincipal() },
                to = incoming.to.map { it.toEmailPrincipal() },
                cc = incoming.cc.map { it.toEmailPrincipal() },
                bcc = incoming.bcc.map { it.toEmailPrincipal() },
                subject = incoming.subject,
                textBody = incoming.text,
                htmlBody = incoming.html,
                attachments = files.filter { it.name != null },
                inlineFiles = files.filter { it.contentId != null },
            )

            block(message)
        }
    }

    private fun NameAddress.toEmailPrincipal(): EmailPrincipal {
        return EmailPrincipal(name = name, address = EmailAddress(address))
    }

    private fun Structure.flatten(partPath: List<Int> = listOf(0)): List<Pair<List<Int>, Structure>> {
        return listOf(partPath to this) + parts.flatMapIndexed { index, structure ->
            structure.flatten(partPath + index)
        }
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

        return moxApi.messagePartGet(request).readByteArray(Int.MAX_VALUE)
    }
}
