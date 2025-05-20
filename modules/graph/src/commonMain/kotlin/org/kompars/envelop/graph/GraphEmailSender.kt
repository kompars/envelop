package org.kompars.envelop.graph

import org.kompars.envelop.*
import org.kompars.envelop.blob.*

public class GraphEmailSender(
    private val graphApi: GraphApi,
    private val blobStorage: BlobStorage = InMemoryBlobStorage,
    private val saveSent: Boolean = true,
) : EmailSender {
    override suspend fun send(message: EmailMessage): EmailSent {
        TODO()
    }

    override fun onDelivery(block: suspend (Delivery) -> Unit) {
        TODO()
    }
}
