package com.albanote.memberservice.redis

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisClusterConfiguration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.StringRedisSerializer


@EnableRedisRepositories
@Configuration
class RedisConfiguration {
    @Value("\${spring.redis.host}")
    lateinit var redisHost: String

    @Value("\${spring.redis.port}")
    var redisPort: Int = 0

    @Bean
    fun redisClusterConfiguration(): RedisClusterConfiguration? {
        val clusterConfiguration = RedisClusterConfiguration()
        clusterConfiguration.clusterNode(redisHost, redisPort)
        LettuceConnectionFactory(clusterConfiguration)
        return clusterConfiguration
    }

    @Bean
    fun redisMessageListener(): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(redisConnectionFactory()!!)
        return container
    }

//    @Bean
//    fun redisClusterConfiguration(): RedisClusterConfiguration? {
//        return RedisClusterConfiguration()
//            .clusterNode("hitup-redis-0001-001.go5u2n.0001.apn2.cache.amazonaws.com", 6379)
//            .clusterNode("hitup-redis-0001-002.go5u2n.0001.apn2.cache.amazonaws.com", 6379)
//            .clusterNode("hitup-redis-0001-003.go5u2n.0001.apn2.cache.amazonaws.com", 6379)
//            .clusterNode("hitup-redis-0002-001.go5u2n.0001.apn2.cache.amazonaws.com", 6379)
//            .clusterNode("hitup-redis-0002-002.go5u2n.0001.apn2.cache.amazonaws.com", 6379)
//            .clusterNode("hitup-redis-0002-003.go5u2n.0001.apn2.cache.amazonaws.com", 6379)
//            .clusterNode("hitup-redis-0003-001.go5u2n.0001.apn2.cache.amazonaws.com", 6379)
//            .clusterNode("hitup-redis-0003-002.go5u2n.0001.apn2.cache.amazonaws.com", 6379)
//            .clusterNode("hitup-redis-0003-003.go5u2n.0001.apn2.cache.amazonaws.com", 6379)
//    }

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory? {
        return LettuceConnectionFactory(redisHost, redisPort)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<*, *>? {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.setConnectionFactory(redisConnectionFactory()!!)
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = StringRedisSerializer()
        redisTemplate.hashKeySerializer = StringRedisSerializer()
        redisTemplate.hashValueSerializer = StringRedisSerializer()
        return redisTemplate
    }
}