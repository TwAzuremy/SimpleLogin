package com.framework.simpleLogin.config;

import com.framework.simpleLogin.utils.CONSTANT;
import org.springframework.data.redis.cache.*;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

public class RedisManager extends RedisCacheManager {
    private static final RedisSerializationContext.SerializationPair<Object> SERIALIZATION_PAIR = RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer());
    private static final CacheKeyPrefix PREFIX = cacheName -> cacheName + ":";

    public RedisManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
    }

    @Override
    protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfiguration) {
        int hashtagLastIndexOf = name.lastIndexOf("#");
        int atSymbolLastIndexOf = name.lastIndexOf("@");

        if (hashtagLastIndexOf > -1) {
            String ttl = name.substring(hashtagLastIndexOf + 1);

            return this.configureCache(
                    cacheConfiguration,
                    name.substring(0, hashtagLastIndexOf),
                    Long.parseLong(ttl)
            );
        } else if (atSymbolLastIndexOf > -1) {
            String field = name.substring(atSymbolLastIndexOf + 1);
            long ttl = CONSTANT.CACHE_EXPIRATION_TIME.get(field);

            return this.configureCache(
                    cacheConfiguration,
                    name.substring(0, atSymbolLastIndexOf),
                    ttl
            );
        } else {
            cacheConfiguration = cacheConfiguration
                    .computePrefixWith(PREFIX)
                    .serializeValuesWith(SERIALIZATION_PAIR);

            return super.createRedisCache(name, cacheConfiguration);
        }
    }

    private RedisCache configureCache(RedisCacheConfiguration configuration, String cacheName, long ttl) {
        Duration duration = Duration.ofMillis(ttl);

        configuration = configuration
                .entryTtl(duration)
                .computePrefixWith(PREFIX)
                .serializeValuesWith(SERIALIZATION_PAIR);

        return super.createRedisCache(cacheName, configuration);
    }
}
