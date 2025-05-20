package org.kompars.envelop.graph

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*

public class GraphException(public val error: String) : RuntimeException("Error using Graph API - $error")

public data class GraphConfig(
    val clientId: String,
    val clientSecret: String,
    val tenantId: String,
)

public class GraphApi(
    private val config: GraphConfig,
    private val tokenStorage: TokenStorage,
) {
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = true
    }

    private val httpClient = HttpClient {
        expectSuccess = false
        defaultRequest {
            url("https://graph.microsoft.com/v1.0/")
        }
        install(ContentNegotiation) {
            json(json)
        }
        install(Auth) {
            bearer {
                loadTokens {
                    val tokens = tokenStorage.get() ?: throw GraphException("No tokens provided")
                    BearerTokens(tokens.accessToken, tokens.refreshToken)
                }
                refreshTokens {
                    val refreshToken = oldTokens?.refreshToken ?: throw GraphException("No refresh token provided")
                    val bearerTokens = client.refreshTokens(refreshToken)
                    val tokens = Tokens(bearerTokens.accessToken, bearerTokens.refreshToken ?: throw GraphException("No refresh token provided"))

                    tokenStorage.update(tokens)
                    bearerTokens
                }
            }
        }
    }

    public suspend fun request(block: HttpRequestBuilder.() -> Unit): HttpResponse {
        return httpClient.request(block)
    }

    public suspend inline fun <reified T, reified U> callWithBody(
        httpMethod: HttpMethod,
        path: String,
        request: T,
        contentType: ContentType = ContentType.Application.Json,
    ): U {
        val response = request {
            method = httpMethod
            url(path)
            contentType(contentType)
            setBody(request)
        }

        return when (response.status.isSuccess()) {
            true -> response.body()
            false -> throw GraphException(response.bodyAsText())
        }
    }

    public suspend inline fun <reified U> call(
        path: String,
        httpMethod: HttpMethod = HttpMethod.Get,
        crossinline block: HttpRequestBuilder.() -> Unit = {},
    ): U {
        val response = request {
            method = httpMethod
            url(path)
            contentType(ContentType.Application.Json)
            block()
        }

        return when (response.status.isSuccess()) {
            true -> response.body()
            false -> throw GraphException(response.bodyAsText())
        }
    }

    private suspend fun HttpClient.refreshTokens(refreshToken: String): BearerTokens {
        val response = submitForm(
            url = "https://login.microsoftonline.com/${config.tenantId}/oauth2/v2.0/token",
            formParameters = parameters {
                append("client_id", config.clientId)
                append("client_secret", config.clientSecret)
                append("grant_type", "refresh_token")
                append("refresh_token", refreshToken)
            }
        )

        return response.body<JsonObject>().let {
            BearerTokens(
                accessToken = it.getValue("access_token").jsonPrimitive.content,
                refreshToken = it.getValue("refresh_token").jsonPrimitive.content,
            )
        }
    }
}
