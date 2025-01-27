package org.kompars.envelop.mox

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.kompars.envelop.mox.model.*

public class ErrorException(public val code: Int, public val error: Error) : Exception("$code: $error")

public class MoxApi(baseUrl: String, email: String, password: String) {
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = true
    }

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
        defaultRequest {
            url(baseUrl)
            basicAuth(email, password)
        }
    }

    public suspend fun messageGet(request: MessageGetRequest): MessageGetResult {
        return callApi("MessageGet", request)
    }

    public suspend fun messageRawGet(request: MessageRawGetRequest): ByteReadChannel {
        return callApi("MessageRawGet", request)
    }

    public suspend fun messagePartGet(request: MessagePartGetRequest): ByteReadChannel {
        return callApi("MessagePartGet", request)
    }

    public suspend fun messageDelete(request: MessageDeleteRequest): MessageDeleteResult {
        return callApi("MessageDelete", request)
    }

    public suspend fun messageFlagsAdd(request: MessageFlagsAddRequest): MessageFlagsAddResult {
        return callApi("MessageFlagsAdd", request)
    }

    public suspend fun messageFlagsRemove(request: MessageFlagsRemoveRequest): MessageFlagsRemoveResult {
        return callApi("MessageFlagsRemove", request)
    }

    public suspend fun messageMove(request: MessageMoveRequest): MessageMoveResult {
        return callApi("MessageMove", request)
    }

    public suspend fun messageSend(request: SendRequest, parts: List<Part> = emptyList()): SendResult {
        return callApi("Send", request) {
            parts.forEach { part ->
                append(part.partType.key, part.content, headers {
                    part.contentType?.let { set("Content-Type", it.toString()) }
                    part.contentId?.let { set("Content-ID", it) }
                    part.name?.let { set("Content-Disposition", "filename=\"$it\"") }
                })
            }
        }
    }

    private suspend inline fun <reified T, reified U> callApi(
        url: String,
        request: T,
        noinline block: FormBuilder.() -> Unit = {},
    ): U {
        val formData = formData {
            append("request", json.encodeToString(request))
            block()
        }

        val response = httpClient.submitFormWithBinaryData(url, formData)

        return when (response.status.isSuccess()) {
            true -> response.body()
            false -> throw ErrorException(response.status.value, response.body<Error>())
        }
    }
}
