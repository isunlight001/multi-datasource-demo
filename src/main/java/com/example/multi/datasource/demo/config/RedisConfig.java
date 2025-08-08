package com.example.multi.datasource.demo.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisConfig {
    
    // 默认Redis连接工厂
    @Bean
    public LettuceConnectionFactory defaultRedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6379);
        return new LettuceConnectionFactory(config);
    }
    
    // 默认Redis模板
    @Bean
    @Qualifier("defaultRedisTemplate")
    public RedisTemplate<String, Object> defaultRedisTemplate(
            @Qualifier("defaultRedisConnectionFactory") RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
        template.setHashValueSerializer(new GenericToStringSerializer<>(Object.class));
        return template;
    }
    
    /**
     * 动态Redis连接工厂Map
     * @return Redis连接工厂Map
     */
    @Bean
    public Map<String, LettuceConnectionFactory> dynamicRedisConnectionFactoryMap() {
        return new HashMap<>();
    }
    
    /**
     * 动态Redis模板Map
     * @return Redis模板Map
     */
    @Bean
    public Map<String, RedisTemplate<String, Object>> dynamicRedisTemplateMap() {
        return new HashMap<>();
    }
}