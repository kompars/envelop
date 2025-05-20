package org.kompars.envelop.graph

public data class Tokens(
    public val accessToken: String,
    public val refreshToken: String,
)

public interface TokenStorage {
    public suspend fun get(): Tokens?
    public suspend fun update(tokens: Tokens?)
}

public class InMemoryTokenStorage : TokenStorage {
    private var tokens: Tokens? = null

    override suspend fun get(): Tokens? {
        return tokens
    }

    override suspend fun update(tokens: Tokens?) {
        this.tokens = tokens
    }
}
