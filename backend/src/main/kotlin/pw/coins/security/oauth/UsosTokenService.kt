package pw.coins.security.oauth

import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuth1RequestToken
import org.springframework.stereotype.Service
import pw.coins.db.generated.tables.daos.UsosTokensDao
import pw.coins.db.generated.tables.pojos.UsosToken
import pw.coins.security.UuidSource
import java.time.OffsetDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class UsosTokenService(
    val dao: UsosTokensDao,
    val uuidSource: UuidSource,
) {
    val cache: ConcurrentHashMap<String, OAuth1RequestToken> = ConcurrentHashMap()

    fun getTokenByUserId(id: UUID): UsosToken? {
        return dao.fetchByUserId(id).getOrNull(0)
    }

    fun cacheRequestToken(token: OAuth1RequestToken) {
        cache[token.token] = token
    }

    fun getCachedToken(key: String): OAuth1RequestToken? {
        return cache[key]
    }

    fun storeAccessToken(token: OAuth1AccessToken, userId: UUID) {
        getTokenByUserId(userId)?.let { dao.delete(it) }
        dao.insert(
            UsosToken(
                uuidSource.genUuid(),
                token.token,
                token.tokenSecret,
                OffsetDateTime.now(),
                userId
            )
        )
    }
}