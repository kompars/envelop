package org.kompars.envelop.graph

import org.kompars.envelop.*
import org.kompars.envelop.blob.*
import org.kompars.envelop.converter.*

public class GraphEmailProvider internal constructor(
    private val sender: GraphEmailSender,
    private val receiver: GraphEmailReceiver,
) : EmailProvider, EmailSender by sender, EmailReceiver by receiver {
    public suspend fun receive() {
        receiver.receive()
    }
}

public fun GraphEmailProvider(graphApi: GraphApi, blobStorage: BlobStorage = InMemoryBlobStorage): GraphEmailProvider {
    val converter = EmailConverter(blobStorage)
    val sender = GraphEmailSender(graphApi, converter)
    val receiver = GraphEmailReceiver(graphApi, converter)

    return GraphEmailProvider(sender, receiver)
}
