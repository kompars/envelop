package org.kompars.envelop.mox

import org.kompars.envelop.*
import org.kompars.envelop.blob.*

public class MoxEmailProvider internal constructor(
    private val sender: MoxEmailSender,
    private val receiver: MoxEmailReceiver,
) : EmailProvider, EmailSender by sender, EmailReceiver by receiver {
    public val outgoingWebhooks: MoxOutgoingWebhooks = sender.outgoingWebhooks
    public val incomingWebhooks: MoxIncomingWebhooks = receiver.incomingWebhooks
}

public fun MoxEmailProvider(moxApi: MoxApi, blobStorage: BlobStorage = InMemoryBlobStorage): MoxEmailProvider {
    val sender = MoxEmailSender(moxApi, blobStorage)
    val receiver = MoxEmailReceiver(moxApi, blobStorage)

    return MoxEmailProvider(sender, receiver)
}
