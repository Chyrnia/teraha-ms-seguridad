package com.teraha.security.config;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

import com.teraha.commons.dtos.UserDTO;

import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper,
            @Value("${app.cache.ttl.default-seconds}") long defaultTtl,
            @Value("${app.cache.ttl.security-users-seconds}") long usersTtl) {

			RedisCacheConfiguration defaults =
				RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofSeconds(defaultTtl))
				.disableCachingNullValues()
				.serializeValuesWith(
						SerializationPair.fromSerializer(
							new JacksonJsonRedisSerializer<>(
								objectMapper, UserDTO.class)));

			RedisCacheConfiguration usersCache =
				defaults.entryTtl(Duration.ofSeconds(usersTtl));

			RedisCacheManager redisManager = 
				RedisCacheManager.builder(connectionFactory)
				.cacheDefaults(defaults)
				.withInitialCacheConfigurations(
						Map.of("security:users", usersCache))
				.transactionAware()
				.build();

			return new LoggingCacheManager(redisManager);

			}
}
