package org.kompars.envelop.graph

import kotlinx.datetime.*
import org.kompars.envelop.*
import org.kompars.envelop.blob.*

public class GraphEmailReceiver(
    private val graphApi: GraphApi,
    private val blobStorage: BlobStorage = InMemoryBlobStorage,
) : EmailReceiver {
    override fun onMessage(block: suspend (EmailMessage, Instant) -> Unit) {
        TODO()
    }
}
