package org.kompars.envelop.graph

import io.ktor.http.*
import kotlin.io.encoding.*
import org.kompars.envelop.*
import org.kompars.envelop.converter.*

public class GraphEmailSender(
    private val graphApi: GraphApi,
    private val converter: EmailConverter,
) : EmailSender {
    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun send(message: EmailMessage): EmailSent {
        val content = Base64.encode(converter.toEml(message))

        graphApi.callWithBody<String, String>(HttpMethod.Post, "me/sendMail", content, ContentType.Text.Plain)

        return EmailSent(message.messageId!!, emptyList())
    }

    override fun onDelivery(block: suspend (Delivery) -> Unit) {}
}
