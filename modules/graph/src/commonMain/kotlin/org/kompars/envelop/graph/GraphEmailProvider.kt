package org.kompars.envelop.graph

import org.kompars.envelop.*
import org.kompars.envelop.blob.*

public class GraphEmailProvider internal constructor(
    private val sender: GraphEmailSender,
    private val receiver: GraphEmailReceiver,
) : EmailProvider, EmailSender by sender, EmailReceiver by receiver

public fun GraphEmailProvider(graphApi: GraphApi, blobStorage: BlobStorage = InMemoryBlobStorage): GraphEmailProvider {
    val sender = GraphEmailSender(graphApi, blobStorage)
    val receiver = GraphEmailReceiver(graphApi, blobStorage)

    return GraphEmailProvider(sender, receiver)
}
