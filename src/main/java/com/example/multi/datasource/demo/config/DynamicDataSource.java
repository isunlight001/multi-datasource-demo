package com.example.multi.datasource.demo.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicDataSource extends AbstractRoutingDataSource implements ApplicationContextAware {
    
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();
    
    private static DynamicDataSource instance;
    
    private ApplicationContext applicationContext;
    
    // 存储动态添加的数据源
    private final Map<Object, DataSource> dynamicDataSources = new ConcurrentHashMap<>();
    
    // 存储动态添加的Redis连接工厂
    private Map<String, LettuceConnectionFactory> dynamicRedisConnectionFactories;
    
    // 存储动态添加的Redis模板
    private Map<String, RedisTemplate<String, Object>> dynamicRedisTemplates;
    
    public DynamicDataSource() {
        instance = this;
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        // 从Spring容器中获取动态Redis连接工厂和模板Map
        this.dynamicRedisConnectionFactories = applicationContext.getBean("dynamicRedisConnectionFactoryMap", Map.class);
        this.dynamicRedisTemplates = applicationContext.getBean("dynamicRedisTemplateMap", Map.class);
    }
    
    @Override
    protected Object determineCurrentLookupKey() {
        return getContext();
    }
    
    public static String getContext() {
        return CONTEXT_HOLDER.get();
    }
    
    public static void setContext(String dataSourceKey) {
        CONTEXT_HOLDER.set(dataSourceKey);
    }
    
    public static void clearContext() {
        CONTEXT_HOLDER.remove();
    }
    
    public static DynamicDataSource getInstance() {
        return instance;
    }
    
    /**
     * 添加目标数据源
     * @param key 数据源键
     * @param dataSource 数据源
     */
    public void addTargetDataSource(String key, DataSource dataSource) {
        dynamicDataSources.put(key, dataSource);
        // 更新resolvedDataSources
        if (getResolvedDataSources() != null) {
            getResolvedDataSources().put(key, dataSource);
        }
    }
    
    /**
     * 移除目标数据源
     * @param key 数据源键
     */
    public void removeTargetDataSource(String key) {
        dynamicDataSources.remove(key);
        // 更新resolvedDataSources
        if (getResolvedDataSources() != null) {
            getResolvedDataSources().remove(key);
        }
    }
    
    /**
     * 获取动态数据源
     * @return 动态数据源Map
     */
    public Map<Object, DataSource> getDynamicDataSources() {
        return dynamicDataSources;
    }
    
    /**
     * 添加Redis集群配置
     * @param dataSourceKey 数据源键（与数据库对应）
     * @param host Redis主机地址
     * @param port Redis端口
     */
    public void addRedisCluster(String dataSourceKey, String host, int port) {
        // 创建Redis连接工厂
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(config);
        connectionFactory.afterPropertiesSet();
        
        // 创建Redis模板
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Object.class));
        redisTemplate.setHashValueSerializer(new GenericToStringSerializer<>(Object.class));
        redisTemplate.afterPropertiesSet();
        
        // 存储到动态Map中
        dynamicRedisConnectionFactories.put(dataSourceKey, connectionFactory);
        dynamicRedisTemplates.put(dataSourceKey, redisTemplate);
    }
    
    /**
     * 移除Redis集群配置
     * @param dataSourceKey 数据源键
     */
    public void removeRedisCluster(String dataSourceKey) {
        // 关闭连接工厂
        LettuceConnectionFactory connectionFactory = dynamicRedisConnectionFactories.get(dataSourceKey);
        if (connectionFactory != null) {
            connectionFactory.destroy();
        }
        
        // 从Map中移除
        dynamicRedisConnectionFactories.remove(dataSourceKey);
        dynamicRedisTemplates.remove(dataSourceKey);
    }
    
    /**
     * 获取指定数据源对应的Redis模板
     * @param dataSourceKey 数据源键
     * @return Redis模板
     */
    public RedisTemplate<String, Object> getRedisTemplate(String dataSourceKey) {
        return dynamicRedisTemplates.get(dataSourceKey);
    }
    
    /**
     * 获取所有Redis连接工厂
     * @return Redis连接工厂Map
     */
    public Map<String, LettuceConnectionFactory> getDynamicRedisConnectionFactories() {
        return dynamicRedisConnectionFactories;
    }
    
    /**
     * 获取所有Redis模板
     * @return Redis模板Map
     */
    public Map<String, RedisTemplate<String, Object>> getDynamicRedisTemplates() {
        return dynamicRedisTemplates;
    }
}