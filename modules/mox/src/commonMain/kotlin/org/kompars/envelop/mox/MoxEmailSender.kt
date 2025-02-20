package org.kompars.envelop.mox

import io.ktor.http.*
import kotlinx.datetime.*
import org.kompars.envelop.*
import org.kompars.envelop.Submission
import org.kompars.envelop.mox.model.*

public class MoxEmailSender(
    private val moxApi: MoxApi,
    private val requireTls: Boolean = false,
    private val saveSent: Boolean = true,
    public val outgoingWebhooks: MoxOutgoingWebhooks = MoxOutgoingWebhooks(),
) : EmailSender {
    override suspend fun send(message: EmailMessage): EmailSent {
        val sendRequest = SendRequest(
            messageId = message.id,
            from = message.recipients.toNameAddresses(EmailRecipientType.From),
            replyTo = message.recipients.toNameAddresses(EmailRecipientType.ReplyTo),
            to = message.recipients.toNameAddresses(EmailRecipientType.To),
            cc = message.recipients.toNameAddresses(EmailRecipientType.Cc),
            bcc = message.recipients.toNameAddresses(EmailRecipientType.Bcc),
            subject = message.subject,
            text = message.textBody,
            html = message.htmlBody,
            references = message.references,
            headers = message.headers.map { (key, value) -> listOf(key, value) },
            requireTls = requireTls,
            saveSent = saveSent,
        )

        val parts = message.attachments.map { attachment ->
            Part(
                partType = when (attachment.type) {
                    EmailAttachmentType.Attachment -> PartType.AttachedFile
                    EmailAttachmentType.Inline -> PartType.InlineFile
                },
                content = attachment.contentProvider.getContent(),
                name = attachment.name,
                contentId = attachment.contentId,
                contentType = ContentType.parse(attachment.contentType),
            )
        }

        val response = moxApi.messageSend(sendRequest, parts)

        return EmailSent(
            id = response.messageId,
            sentAt = Clock.System.now(),
            submissions = response.submissions.map {
                Submission(
                    id = it.queueMessageId.toString(),
                    recipient = it.address,
                )
            },
        )
    }

    override fun onDelivery(block: suspend (Delivery) -> Unit) {
        outgoingWebhooks.registerCallback { outgoing ->
            val delivery = Delivery(
                submissionId = outgoing.queueMessageId.toString(),
                error = outgoing.error?.ifEmpty { null },
                status = when (outgoing.event) {
                    OutgoingEvent.Delivered -> DeliveryStatus.Delivered
                    OutgoingEvent.Delayed -> DeliveryStatus.Delayed
                    OutgoingEvent.Failed, OutgoingEvent.Canceled, OutgoingEvent.Suppressed -> DeliveryStatus.Failed
                    OutgoingEvent.Unrecognized, OutgoingEvent.Expanded, OutgoingEvent.Relayed -> DeliveryStatus.Unknown
                },
            )

            block(delivery)
        }
    }

    private fun List<EmailRecipient>.toNameAddresses(type: EmailRecipientType): List<NameAddress> {
        return filter { it.type == type }.map {
            NameAddress(name = it.emailAddress.identifier, address = it.emailAddress.withIdentifier(null))
        }
    }
}
