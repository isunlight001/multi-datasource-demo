package com.example.multi.datasource.demo.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicDataSource extends AbstractRoutingDataSource {
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();
    
    // 保存DynamicDataSource实例，用于动态添加数据源
    private static DynamicDataSource instance;

    // 存储动态创建的数据源
    private Map<Object, DataSource> dynamicDataSources = new ConcurrentHashMap<>();

    public DynamicDataSource() {
        instance = this;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return getContext();
    }

    public static void setContext(String dataSource) {
        CONTEXT_HOLDER.set(dataSource);
    }

    public static String getContext() {
        return CONTEXT_HOLDER.get();
    }

    public static void clearContext() {
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
    public void addTargetDataSource(String key, DataSource dataSource) {
        dynamicDataSources.put(key, dataSource);
        updateTargetDataSources();
    }

    /**
     * 动态移除目标数据源
     * @param key 数据源键
     */
    public void removeTargetDataSource(String key) {
        dynamicDataSources.remove(key);
        updateTargetDataSources();
    }

    /**
     * 更新目标数据源
     */
    private void updateTargetDataSources() {
        Map<Object, Object> targetDataSources = new HashMap<>(dynamicDataSources);
        this.setTargetDataSources(targetDataSources);
        this.afterPropertiesSet();
    }

    /**
     * 获取所有动态数据源
     * @return 数据源映射
     */
    public Map<Object, DataSource> getDynamicDataSources() {
        return new HashMap<>(dynamicDataSources);
    }
}