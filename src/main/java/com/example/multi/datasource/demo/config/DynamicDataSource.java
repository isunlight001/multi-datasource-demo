package com.example.multi.datasource.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicDataSource extends AbstractRoutingDataSource implements ApplicationContextAware {
    
    private static final Logger log = LoggerFactory.getLogger(DynamicDataSource.class);
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();
    
    // 保存DynamicDataSource实例，用于动态添加数据源
    private static volatile DynamicDataSource instance;
    
    private ApplicationContext applicationContext;
    
    // 存储动态创建的数据源
    private final Map<Object, DataSource> dynamicDataSources = new ConcurrentHashMap<>();
    
    // 存储动态创建的Redis连接工厂
    private final Map<String, LettuceConnectionFactory> dynamicRedisConnectionFactories = new ConcurrentHashMap<>();
    
    // 存储动态创建的Redis模板
    private final Map<String, RedisTemplate<String, Object>> dynamicRedisTemplates = new ConcurrentHashMap<>();
    
    public DynamicDataSource() {
        instance = this;
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        // 从Spring容器中获取动态Redis连接工厂和模板Map
        Map<String, LettuceConnectionFactory> redisConnectionFactoryMap = applicationContext.getBean("dynamicRedisConnectionFactoryMap", Map.class);
        Map<String, RedisTemplate<String, Object>> redisTemplateMap = applicationContext.getBean("dynamicRedisTemplateMap", Map.class);
        
        // 安全地替换Map引用
        this.dynamicRedisConnectionFactories.clear();
        this.dynamicRedisConnectionFactories.putAll(redisConnectionFactoryMap);
        
        this.dynamicRedisTemplates.clear();
        this.dynamicRedisTemplates.putAll(redisTemplateMap);
    }
    
    @Override
    protected Object determineCurrentLookupKey() {
        return getContext();
    }
    
    public static String getContext() {
        return CONTEXT_HOLDER.get();
    }
    
    public static void setContext(String dataSource) {
        log.debug("设置数据源上下文: {}", dataSource);
        CONTEXT_HOLDER.set(dataSource);
    }
    
    public static void clearContext() {
        log.debug("清除数据源上下文");
        CONTEXT_HOLDER.remove();
    }
    
    /**
     * 获取DynamicDataSource实例
     * @return DynamicDataSource实例
     */
    public static DynamicDataSource getInstance() {
        return instance;
    }
    
    /**
     * 动态添加目标数据源
     * @param key 数据源键
     * @param dataSource 数据源
     */
    public synchronized void addTargetDataSource(String key, DataSource dataSource) {
        log.info("添加目标数据源: {}", key);
        
        dynamicDataSources.put(key, dataSource);
        
        // 更新resolvedDataSources
        Map<Object, Object> targetDataSources = new HashMap<>(this.getResolvedDataSources());
        targetDataSources.put(key, dataSource);
        
        // 重新设置目标数据源
        this.setTargetDataSources(targetDataSources);
        
        // 重新初始化数据源
        this.afterPropertiesSet();
        
        log.info("目标数据源 {} 添加成功", key);
    }
    
    /**
     * 动态移除目标数据源
     * @param key 数据源键
     */
    public synchronized void removeTargetDataSource(String key) {
        log.info("移除目标数据源: {}", key);
        
        // 防止移除默认数据源
        if ("dataSource1".equals(key)) {
            log.warn("不能移除默认数据源: {}", key);
            throw new IllegalArgumentException("不能移除默认数据源: " + key);
        }
        
        dynamicDataSources.remove(key);
        
        // 更新resolvedDataSources
        Map<Object, Object> targetDataSources = new HashMap<>(this.getResolvedDataSources());
        targetDataSources.remove(key);
        
        // 重新设置目标数据源
        this.setTargetDataSources(targetDataSources);
        
        // 重新初始化数据源
        this.afterPropertiesSet();
        
        log.info("目标数据源 {} 移除成功", key);
    }
    
    /**
     * 获取动态数据源
     * @return 动态数据源Map
     */
    public Map<Object, DataSource> getDynamicDataSources() {
        return new HashMap<>(dynamicDataSources);
    }
    
    /**
     * 添加Redis集群配置
     * @param dataSourceKey 数据源键（与数据库对应）
     * @param host Redis主机地址
     * @param port Redis端口
     */
    public synchronized void addRedisCluster(String dataSourceKey, String host, int port) {
        log.info("为数据源 {} 添加Redis集群配置: {}:{}", dataSourceKey, host, port);
        
        // 检查数据源是否存在
        if (!dynamicDataSources.containsKey(dataSourceKey)) {
            log.warn("数据源 {} 不存在，无法添加Redis集群配置", dataSourceKey);
            throw new IllegalArgumentException("数据源 " + dataSourceKey + " 不存在");
        }
        
        // 检查Redis集群配置是否已存在
        if (dynamicRedisConnectionFactories.containsKey(dataSourceKey)) {
            log.warn("数据源 {} 的Redis集群配置已存在", dataSourceKey);
            throw new IllegalArgumentException("数据源 " + dataSourceKey + " 的Redis集群配置已存在");
        }
        
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
        
        log.info("为数据源 {} 添加Redis集群配置成功", dataSourceKey);
    }
    
    /**
     * 移除Redis集群配置
     * @param dataSourceKey 数据源键
     */
    public synchronized void removeRedisCluster(String dataSourceKey) {
        log.info("移除数据源 {} 的Redis集群配置", dataSourceKey);
        
        // 检查Redis集群配置是否存在
        if (!dynamicRedisConnectionFactories.containsKey(dataSourceKey)) {
            log.warn("数据源 {} 的Redis集群配置不存在", dataSourceKey);
            throw new IllegalArgumentException("数据源 " + dataSourceKey + " 的Redis集群配置不存在");
        }
        
        // 关闭并移除Redis连接工厂
        LettuceConnectionFactory connectionFactory = dynamicRedisConnectionFactories.get(dataSourceKey);
        if (connectionFactory != null) {
            try {
                connectionFactory.destroy();
            } catch (Exception e) {
                log.warn("关闭Redis连接工厂时发生异常: {}", dataSourceKey, e);
            }
            dynamicRedisConnectionFactories.remove(dataSourceKey);
        }
        
        // 移除Redis模板
        dynamicRedisTemplates.remove(dataSourceKey);
        
        log.info("移除数据源 {} 的Redis集群配置成功", dataSourceKey);
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
        return new HashMap<>(dynamicRedisConnectionFactories);
    }
    
    /**
     * 获取所有Redis模板
     * @return Redis模板Map
     */
    public Map<String, RedisTemplate<String, Object>> getDynamicRedisTemplates() {
        return new HashMap<>(dynamicRedisTemplates);
    }
}