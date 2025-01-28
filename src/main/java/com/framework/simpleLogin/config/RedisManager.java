package com.framework.simpleLogin.config;

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
        int lastIndexOf = name.lastIndexOf("#");

        if (lastIndexOf > -1) {
            String ttl = name.substring(lastIndexOf + 1);
            Duration duration = Duration.ofMillis(Long.parseLong(ttl));

            cacheConfiguration = cacheConfiguration.entryTtl(duration)
                    .computePrefixWith(PREFIX)
                    .serializeValuesWith(SERIALIZATION_PAIR);

            String cacheName = name.substring(0, lastIndexOf);

            return super.createRedisCache(cacheName, cacheConfiguration);
        } else {
            cacheConfiguration = cacheConfiguration
                    .computePrefixWith(PREFIX)
                    .serializeValuesWith(SERIALIZATION_PAIR);

            return super.createRedisCache(name, cacheConfiguration);
        }
    }
}
