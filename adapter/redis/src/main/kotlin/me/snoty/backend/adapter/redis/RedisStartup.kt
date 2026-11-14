package me.snoty.backend.adapter.redis

import com.sksamuel.hoplite.Masked
import io.lettuce.core.ClientOptions
import io.lettuce.core.MaintNotificationsConfig
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import me.snoty.backend.config.ConfigLoader
import me.snoty.backend.config.load
import org.koin.core.annotation.Single

data class RedisConfig(
    val url: String,
    val username: String? = null,
    val password: Masked? = null,
    val sentinel: RedisSentinelAuthentication? = null,
)

data class RedisSentinelAuthentication(
    val username: String? = null,
    val password: Masked? = null,
)

@Single
fun provideRedisConfig(configLoader: ConfigLoader): RedisConfig =
    configLoader.load(REDIS_ADAPTER_TYPE)

@Single
fun provideRedisClient(redisConfig: RedisConfig): RedisClient =
    RedisClient
        .create(redisConfig.toRedisUri())
        .apply {
            options = ClientOptions.builder()
                .maintNotificationsConfig(MaintNotificationsConfig.disabled())
                .build()
        }

internal fun RedisConfig.toRedisUri(): RedisURI {
    val redisUri = RedisURI.create(url)

    if (username != null || password != null) {
        require(username != null && password != null) {
            "Redis credentials require username and password to be set together"
        }

        redisUri.setAuthentication(
            username,
            password.value,
        )
    }

    if (redisUri.sentinels.isEmpty()) {
        if (sentinel != null) {
            throw IllegalArgumentException("Redis Sentinel credentials can only be used with a Redis Sentinel URL")
        }

        return redisUri
    }

    if (sentinel?.username != null || sentinel?.password != null) {
        require(sentinel.username != null && sentinel.password != null) {
            "Redis Sentinel credentials require username and password to be set together"
        }

        redisUri.sentinels.forEach { sentinelUri ->
            sentinelUri.setAuthentication(
                sentinel.username,
                sentinel.password.value,
            )
        }
	}

    return redisUri
}
