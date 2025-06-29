package org.kompars.envelop.graph

import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.time.*
import org.kompars.envelop.*
import org.kompars.envelop.converter.*
import org.kompars.envelop.graph.model.*

public class GraphEmailReceiver(
    private val graphApi: GraphApi,
    private val converter: EmailConverter,
) : EmailReceiver {
    private val onMessageHooks = mutableListOf<suspend (EmailMessage, Instant) -> Unit>()
    private val archiveMessage = MoveMessage(destinationId = "archive")

    public suspend fun receive() {
        val previewResponse = graphApi.call<GraphResponse<List<MessagePreview>>>("me/mailFolders/inbox/messages") {
            parameter("\$orderby", "receivedDateTime asc")
            parameter("\$top", "100")
            parameter("\$select", "id,receivedDateTime")
        }

        for (item in previewResponse.value) {
            val content = graphApi.call<String>("me/messages/${item.id}/\$value").encodeToByteArray()
            val message = converter.fromEml(content)

            for (hook in onMessageHooks) {
                hook(message, item.receivedDateTime)
            }

            graphApi.callWithBody<MoveMessage, MessagePreview>(HttpMethod.Post, "me/messages/${item.id}/move", archiveMessage)
        }
    }

    override fun onMessage(block: suspend (EmailMessage, Instant) -> Unit) {
        onMessageHooks += block
    }
}
