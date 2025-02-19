package org.kompars.envelop.mox

import io.ktor.http.*
import kotlinx.datetime.*
import org.kompars.envelop.*
import org.kompars.envelop.Submission
import org.kompars.envelop.common.*
import org.kompars.envelop.mox.model.*

public class MoxMailSender(
    private val moxApi: MoxApi,
    private val requireTls: Boolean = false,
    private val saveSent: Boolean = true,
    public val outgoingWebhooks: MoxOutgoingWebhooks = MoxOutgoingWebhooks(),
) : MailSender {
    override suspend fun send(message: MailMessage): EmailSent {
        val sendRequest = SendRequest(
            from = message.from.map { it.toNameAddress() },
            to = message.to.map { it.toNameAddress() },
            cc = message.cc.map { it.toNameAddress() },
            bcc = message.bcc.map { it.toNameAddress() },
            subject = message.subject,
            text = message.textBody,
            html = message.htmlBody,
            references = message.references,
            headers = message.headers.map { (key, value) -> listOf(key, value) },
            requireTls = requireTls,
            saveSent = saveSent,
        )

        val attachments = message.attachments.map { it to PartType.AttachedFile }
        val inlineFiles = message.inlineFiles.map { it to PartType.InlineFile }

        val parts = (attachments + inlineFiles).map { (file, type) ->
            Part(
                partType = type,
                content = file.contentProvider.getContent(),
                name = file.name,
                contentId = file.contentId,
                contentType = when {
                    file.contentType != null -> ContentType.parse(file.contentType!!)
                    file.name != null -> ContentType.defaultForFilePath(file.name!!)
                    else -> ContentType.Application.OctetStream
                },
            )
        }

        val response = moxApi.messageSend(sendRequest, parts)

        return EmailSent(
            id = response.messageId, sentAt = Clock.System.now(), submissions = response.submissions.map {
                Submission(
                    id = it.queueMessageId.toString(),
                    recipient = EmailAddress.parse(it.address),
                )
            })
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

    private fun EmailAddress.toNameAddress(): NameAddress {
        return NameAddress(name = identifier, address = withIdentifier(null).toString())
    }
}
