package org.kompars.envelop.mox

import io.ktor.utils.io.*
import kotlinx.datetime.*
import org.kompars.envelop.*
import org.kompars.envelop.blob.*
import org.kompars.envelop.mox.model.*

public class MoxEmailReceiver(
    private val moxApi: MoxApi,
    private val blobStorage: BlobStorage = InMemoryBlobStorage,
) : EmailReceiver {
    public val incomingWebhooks: MoxIncomingWebhooks = MoxIncomingWebhooks()

    override fun onMessage(block: suspend (EmailMessage, Instant) -> Unit) {
        incomingWebhooks.registerCallback { incoming ->
            val files = incoming.structure.flatten()

            val recipients =
                incoming.from.toRecipients(EmailRecipientType.From) +
                incoming.replyTo.toRecipients(EmailRecipientType.ReplyTo) +
                incoming.to.toRecipients(EmailRecipientType.To) +
                incoming.cc.toRecipients(EmailRecipientType.Cc) +
                incoming.bcc.toRecipients(EmailRecipientType.Bcc)

            val message = EmailMessage(
                messageId = incoming.messageId,
                references = incoming.references,
                recipients = recipients,
                date = incoming.date ?: incoming.meta.received,
                subject = incoming.subject,
                textBody = incoming.text?.ifEmpty { null },
                htmlBody = incoming.html?.ifEmpty { null },
                attachments = files.mapNotNull { (partPath, structure) ->
                    structure.toEmailAttachment(incoming.meta.messageId, partPath, structure.contentDisposition)
                },
            )

            block(message, incoming.meta.received)
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

    private suspend fun Structure.toEmailAttachment(messageId: Int, partPath: List<Int>, contentDisposition: String): EmailAttachment? {
        val type = when (contentDisposition) {
            "inline" -> EmailAttachmentType.Inline
            "attachment" -> EmailAttachmentType.Attachment
            else -> return null
        }

        val request = MessagePartGetRequest(
            messageId = messageId,
            partPath = partPath,
        )

        return EmailAttachment(
            name = fileName.ifEmpty { null } ?: contentId.ifEmpty { null } ?: "",
            contentType = contentType.ifEmpty { "application/octet-stream" },
            contentId = contentId.ifEmpty { null },
            type = type,
            blob = blobStorage.write(moxApi.messagePartGet(request).toByteArray()),
        )
    }
}
