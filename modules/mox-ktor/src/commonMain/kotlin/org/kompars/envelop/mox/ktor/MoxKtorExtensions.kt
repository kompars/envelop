package org.kompars.envelop.mox.ktor

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import org.kompars.envelop.mox.*

public fun <T> Route.moxWebhooks(path: String = "", webhooks: MoxWebhooks<T>) {
    post(path) {
        val webhookId = call.request.header("X-Mox-Webhook-ID")
        val webhookAttempt = call.request.header("X-Mox-Webhook-Attempt")

        call.application.log.debug("Processing mox webhook $webhookId, attempt $webhookAttempt")

        val body = call.receiveText()

        val status = try {
            webhooks.process(body)
            HttpStatusCode.OK
        } catch (e: TimeoutCancellationException) {
            HttpStatusCode.RequestTimeout
        } catch (e: Throwable) {
            HttpStatusCode.InternalServerError
        }

        call.respond(status, status.description)
    }
}
