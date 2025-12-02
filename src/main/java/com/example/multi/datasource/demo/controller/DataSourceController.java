package com.example.multi.datasource.demo.controller;

import com.example.multi.datasource.demo.config.DataSourceProperties;
import com.example.multi.datasource.demo.config.DynamicDataSource;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/datasource")
public class DataSourceController {
    
    @Autowired
    private DataSourceProperties dataSourceProperties;
    
    /**
     * 动态添加数据源(内存中)
     * 
     * @param dsName 数据源名称
     * @param url 数据库连接URL
     * @param username 用户名
     * @param password 密码
     * @param driverClassName 驱动类名
     * @param initialSize 初始连接数
     * @param minIdle 最小空闲连接数
     * @param maxActive 最大活跃连接数
     * @param maxWait 最大等待时间
     * @return 操作结果
     */
    @PostMapping("/add")
    public Map<String, Object> addDataSource(@RequestParam String dsName,
                                             @RequestParam String url,
                                             @RequestParam String username,
                                             @RequestParam String password,
                                             @RequestParam String driverClassName,
                                             @RequestParam(defaultValue = "5") int initialSize,
                                             @RequestParam(defaultValue = "5") int minIdle,
                                             @RequestParam(defaultValue = "20") int maxActive,
                                             @RequestParam(defaultValue = "60000") long maxWait) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查数据源是否已存在
            DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
            Map<Object, DataSource> existingDataSources = dynamicDataSource.getDynamicDataSources();
            if (existingDataSources.containsKey(dsName)) {
                result.put("success", false);
                result.put("message", "数据源 " + dsName + " 已存在");
                return result;
            }
            
            // 创建新的数据源
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setUrl(url);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setDriverClassName(driverClassName);
            dataSource.setInitialSize(initialSize);
            dataSource.setMinIdle(minIdle);
            dataSource.setMaxActive(maxActive);
            dataSource.setMaxWait(maxWait);
            
            // 获取DynamicDataSource实例并添加新的数据源
            if (dynamicDataSource != null) {
                dynamicDataSource.addTargetDataSource(dsName, dataSource);
                result.put("success", true);
                result.put("message", "数据源 " + dsName + " 添加成功");
            } else {
                result.put("success", false);
                result.put("message", "无法获取动态数据源实例");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加数据源失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 删除数据源
     * 
     * @param dsName 数据源名称
     * @return 操作结果
     */
    @DeleteMapping("/remove")
    public Map<String, Object> removeDataSource(@RequestParam String dsName) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 防止删除默认数据源
            if ("dataSource1".equals(dsName)) {
                result.put("success", false);
                result.put("message", "不能删除默认数据源");
                return result;
            }
            
            DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
            Map<Object, DataSource> existingDataSources = dynamicDataSource.getDynamicDataSources();
            if (!existingDataSources.containsKey(dsName)) {
                result.put("success", false);
                result.put("message", "数据源 " + dsName + " 不存在");
                return result;
            }
            
            dynamicDataSource.removeTargetDataSource(dsName);
            result.put("success", true);
            result.put("message", "数据源 " + dsName + " 删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除数据源失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 切换数据源
     * 
     * @param dsName 数据源名称
     * @return 操作结果
     */
    @PostMapping("/switch")
    public Map<String, Object> switchDataSource(@RequestParam String dsName) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 检查数据源是否存在
            DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
            Map<Object, DataSource> existingDataSources = dynamicDataSource.getDynamicDataSources();
            if (!existingDataSources.containsKey(dsName)) {
                result.put("success", false);
                result.put("message", "数据源 " + dsName + " 不存在");
                return result;
            }
            
            DynamicDataSource.setContext(dsName);
            result.put("success", true);
            result.put("message", "成功切换到数据源: " + dsName);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "切换数据源失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取所有数据源列表
     * 
     * @return 数据源列表
     */
    @GetMapping("/list")
    public Map<String, Object> listDataSources() {
        Map<String, Object> result = new HashMap<>();
        try {
            DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
            Map<Object, DataSource> existingDataSources = dynamicDataSource.getDynamicDataSources();
            
            List<String> dataSourceNames = new ArrayList<>();
            // 添加动态数据源
            for (Object key : existingDataSources.keySet()) {
                dataSourceNames.add((String) key);
            }
            
            // 添加预定义的数据源
            dataSourceNames.addAll(dataSourceProperties.getDataSources().keySet());
            
            result.put("success", true);
            result.put("dataSources", dataSourceNames);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取数据源列表失败: " + e.getMessage());
        }
        return result;
    }
}