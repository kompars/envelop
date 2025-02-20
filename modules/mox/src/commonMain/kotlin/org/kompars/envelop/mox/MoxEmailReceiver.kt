package org.kompars.envelop.mox

import io.ktor.utils.io.*
import org.kompars.envelop.*
import org.kompars.envelop.mox.model.*

public class MoxEmailReceiver(
    private val moxApi: MoxApi,
    public val incomingWebhooks: MoxIncomingWebhooks = MoxIncomingWebhooks(),
) : EmailReceiver {
    override fun onMessage(block: suspend (EmailMessage) -> Unit) {
        incomingWebhooks.registerCallback { incoming ->
            val files = incoming.structure.flatten()

            val recipients =
                incoming.from.toRecipients(EmailRecipientType.From) +
                incoming.replyTo.toRecipients(EmailRecipientType.ReplyTo) +
                incoming.to.toRecipients(EmailRecipientType.To) +
                incoming.cc.toRecipients(EmailRecipientType.Cc) +
                incoming.bcc.toRecipients(EmailRecipientType.Bcc)

            val message = EmailMessage(
                id = incoming.messageId,
                recipients = recipients,
                subject = incoming.subject,
                textBody = incoming.text?.ifEmpty { null },
                htmlBody = incoming.html?.ifEmpty { null },
                sentAt = incoming.meta.received,
                references = incoming.references,
                attachments = files.map { (partPath, structure) ->
                    structure.toEmailAttachment(incoming.meta.messageId, partPath, structure.contentDisposition)
                },
            )

            block(message)
        }
    }

    private fun List<NameAddress>.toRecipients(type: EmailRecipientType): List<EmailRecipient> {
        return map {
            EmailRecipient(type, it.address.withIdentifier(it.name?.ifEmpty { null }))
        }
    }

    private fun Structure.flatten(partPath: List<Int> = listOf()): List<Pair<List<Int>, Structure>> {
        return listOf(partPath to this) + parts.flatMapIndexed { index, structure ->
            structure.flatten(partPath + index)
        }
    }

    private fun Structure.toEmailAttachment(messageId: Int, partPath: List<Int>, contentDisposition: String): EmailAttachment {
        return EmailAttachment(
            name = fileName.ifEmpty { null },
            contentType = contentType.ifEmpty { "application/octet-stream" },
            contentId = contentId.ifEmpty { null },
            type = when (contentDisposition) {
                "inline" -> EmailAttachmentType.Inline
                else -> EmailAttachmentType.Attachment
            },
            contentProvider = MoxContentProvider(
                moxApi = moxApi,
                messageId = messageId,
                partPath = partPath,
            ),
        )
    }
}

public class MoxContentProvider internal constructor(
    private val moxApi: MoxApi,
    private val messageId: Int,
    private val partPath: List<Int>,
) : EmailAttachmentContentProvider {
    override suspend fun getContent(): ByteArray {
        val request = MessagePartGetRequest(
            messageId = messageId,
            partPath = partPath,
        )

        return moxApi.messagePartGet(request).toByteArray()
    }
}
