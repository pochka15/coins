package pw.coins.security.oauth

import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuth1RequestToken
import org.springframework.stereotype.Service
import pw.coins.db.generated.tables.daos.UsosTokensDao
import pw.coins.db.generated.tables.pojos.UsosToken
import pw.coins.security.UuidSource
import java.time.OffsetDateTime
import java.util.concurrent.ConcurrentHashMap

@Service
class UsosTokenService(
    val dao: UsosTokensDao,
    val uuidSource: UuidSource,
) {
    val cache: ConcurrentHashMap<String, OAuth1RequestToken> = ConcurrentHashMap()

    fun getTokenByKey(key: String): UsosToken? {
        return dao.fetchByKey(key).getOrNull(0)
    }

    fun cacheRequestToken(token: OAuth1RequestToken) {
        cache[token.token] = token
    }

    fun getCachedToken(key: String): OAuth1RequestToken? {
        return cache[key]
    }

    fun storeAccessToken(token: OAuth1AccessToken): Boolean {
        val model = UsosToken(
            uuidSource.genUuid(),
            token.token,
            token.tokenSecret,
            OffsetDateTime.now()
        )

        return try {
            dao.insert(model)
            true
        } catch (e: Exception) {
            return false
        }
    }
}