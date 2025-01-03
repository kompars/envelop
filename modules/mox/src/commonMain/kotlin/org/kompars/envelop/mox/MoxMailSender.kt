package org.kompars.envelop.mox

import io.ktor.http.*
import org.kompars.envelop.*
import org.kompars.envelop.mox.model.*

public class MoxMailSender(
    private val moxApi: MoxApi,
    private val requireTls: Boolean = false,
    private val saveSent: Boolean = true,
    public val outgoingWebhooks: MoxOutgoingWebhooks = MoxOutgoingWebhooks(),
) : MailSender {
    override suspend fun send(message: MailMessage) {
        val sendRequest = SendRequest(
            from = message.from.map { it.toNameAddress() },
            to = message.to.map { it.toNameAddress() },
            cc = message.cc.map { it.toNameAddress() },
            bcc = message.bcc.map { it.toNameAddress() },
            subject = message.subject,
            text = message.textBody,
            html = message.htmlBody,
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

        moxApi.messageSend(sendRequest, parts)
    }

    override fun onDelivery(block: suspend (DeliveryStatus) -> Unit) {
        outgoingWebhooks.registerCallback { outgoing ->
            val status = when (outgoing.event) {
                OutgoingEvent.Delivered -> DeliveryStatus.Delivered
                OutgoingEvent.Delayed -> DeliveryStatus.Delayed
                OutgoingEvent.Failed, OutgoingEvent.Canceled, OutgoingEvent.Suppressed -> DeliveryStatus.Failed
                OutgoingEvent.Unrecognized, OutgoingEvent.Expanded, OutgoingEvent.Relayed -> DeliveryStatus.Unknown
            }

            block(status)
        }
    }

    private fun EmailPrincipal.toNameAddress(): NameAddress {
        return NameAddress(name = name, address = address.toString())
    }
}
