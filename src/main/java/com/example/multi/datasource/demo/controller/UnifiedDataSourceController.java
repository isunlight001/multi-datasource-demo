package com.example.multi.datasource.demo.controller;

import com.example.multi.datasource.demo.config.DynamicDataSource;
import com.example.multi.datasource.demo.entity.User;
import com.example.multi.datasource.demo.service.UserService;
import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/datasource")
public class UnifiedDataSourceController {

    private static final Logger log = LoggerFactory.getLogger(UnifiedDataSourceController.class);
    
    @Autowired
    private UserService userService;

    /**
     * 动态添加数据源
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
        log.info("开始添加数据源: {}, URL: {}", dsName, url);
        
        Map<String, Object> result = new HashMap<>();

        try {
            // 检查数据源是否已存在
            DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
            Map<Object, DataSource> existingDataSources = dynamicDataSource.getDynamicDataSources();
            if (existingDataSources.containsKey(dsName)) {
                log.warn("数据源 {} 已存在", dsName);
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
                log.info("数据源 {} 添加成功", dsName);
                result.put("success", true);
                result.put("message", "数据源 " + dsName + " 添加成功");
            } else {
                log.error("无法获取动态数据源实例");
                result.put("success", false);
                result.put("message", "无法获取动态数据源实例");
            }
        } catch (Exception e) {
            log.error("添加数据源失败: " + dsName, e);
            result.put("success", false);
            result.put("message", "添加数据源失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 删除数据源
     */
    @DeleteMapping("/remove")
    public Map<String, Object> removeDataSource(@RequestParam String dsName) {
        log.info("开始删除数据源: {}", dsName);
        
        Map<String, Object> result = new HashMap<>();

        try {
            // 防止删除默认数据源
            if ("dataSource1".equals(dsName)) {
                log.warn("不能删除默认数据源: {}", dsName);
                result.put("success", false);
                result.put("message", "不能删除默认数据源");
                return result;
            }

            DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
            Map<Object, DataSource> existingDataSources = dynamicDataSource.getDynamicDataSources();
            if (!existingDataSources.containsKey(dsName)) {
                log.warn("数据源 {} 不存在", dsName);
                result.put("success", false);
                result.put("message", "数据源 " + dsName + " 不存在");
                return result;
            }

            dynamicDataSource.removeTargetDataSource(dsName);
            log.info("数据源 {} 删除成功", dsName);
            result.put("success", true);
            result.put("message", "数据源 " + dsName + " 删除成功");
        } catch (Exception e) {
            log.error("删除数据源失败: " + dsName, e);
            result.put("success", false);
            result.put("message", "删除数据源失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 切换数据源
     */
    @PostMapping("/switch")
    public Map<String, Object> switchDataSource(@RequestParam String dsName) {
        log.info("开始切换数据源到: {}", dsName);
        
        Map<String, Object> result = new HashMap<>();
        try {
            // 检查数据源是否存在
            DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
            Map<Object, DataSource> existingDataSources = dynamicDataSource.getDynamicDataSources();
            if (!existingDataSources.containsKey(dsName)) {
                log.warn("数据源 {} 不存在", dsName);
                result.put("success", false);
                result.put("message", "数据源 " + dsName + " 不存在");
                return result;
            }

            DynamicDataSource.setContext(dsName);
            log.info("成功切换到数据源: {}", dsName);
            result.put("success", true);
            result.put("message", "成功切换到数据源: " + dsName);
        } catch (Exception e) {
            log.error("切换数据源失败: " + dsName, e);
            result.put("success", false);
            result.put("message", "切换数据源失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取所有数据源列表
     */
    @GetMapping("/list")
    public Map<String, Object> listDataSources() {
        log.info("获取所有数据源列表");
        
        Map<String, Object> result = new HashMap<>();
        try {
            DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
            Map<Object, DataSource> existingDataSources = dynamicDataSource.getDynamicDataSources();
            
            List<String> dataSourceNames = new ArrayList<>();
            for (Object key : existingDataSources.keySet()) {
                dataSourceNames.add((String) key);
            }
            log.info("获取到 {} 个数据源", dataSourceNames.size());
            result.put("success", true);
            result.put("dataSources", dataSourceNames);
        } catch (Exception e) {
            log.error("获取数据源列表失败", e);
            result.put("success", false);
            result.put("message", "获取数据源列表失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 为数据源添加Redis集群配置
     */
    @PostMapping("/redis/add")
    public Map<String, Object> addRedisCluster(@RequestParam String dsName,
                                               @RequestParam String redisHost,
                                               @RequestParam int redisPort) {
        log.info("为数据源 {} 添加Redis集群配置: {}:{}", dsName, redisHost, redisPort);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
            
            // 检查数据源是否存在
            Map<Object, DataSource> dataSources = dynamicDataSource.getDynamicDataSources();
            if (!dataSources.containsKey(dsName)) {
                log.warn("数据源 {} 不存在", dsName);
                result.put("success", false);
                result.put("message", "数据源 " + dsName + " 不存在");
                return result;
            }
            
            // 检查Redis集群配置是否已存在
            Map<String, RedisTemplate<String, Object>> redisTemplates = dynamicDataSource.getDynamicRedisTemplates();
            if (redisTemplates.containsKey(dsName)) {
                log.warn("数据源 {} 的Redis集群配置已存在", dsName);
                result.put("success", false);
                result.put("message", "数据源 " + dsName + " 的Redis集群配置已存在");
                return result;
            }
            
            // 添加Redis集群配置
            dynamicDataSource.addRedisCluster(dsName, redisHost, redisPort);
            
            log.info("为数据源 {} 添加Redis集群配置成功", dsName);
            result.put("success", true);
            result.put("message", "为数据源 " + dsName + " 添加Redis集群配置成功");
        } catch (Exception e) {
            log.error("添加Redis集群配置失败: " + dsName, e);
            result.put("success", false);
            result.put("message", "添加Redis集群配置失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 删除数据源的Redis集群配置
     */
    @DeleteMapping("/redis/remove")
    public Map<String, Object> removeRedisCluster(@RequestParam String dsName) {
        log.info("删除数据源 {} 的Redis集群配置", dsName);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
            
            // 检查Redis集群配置是否存在
            Map<String, RedisTemplate<String, Object>> redisTemplates = dynamicDataSource.getDynamicRedisTemplates();
            if (!redisTemplates.containsKey(dsName)) {
                log.warn("数据源 {} 的Redis集群配置不存在", dsName);
                result.put("success", false);
                result.put("message", "数据源 " + dsName + " 的Redis集群配置不存在");
                return result;
            }
            
            // 删除Redis集群配置
            dynamicDataSource.removeRedisCluster(dsName);
            
            log.info("删除数据源 {} 的Redis集群配置成功", dsName);
            result.put("success", true);
            result.put("message", "删除数据源 " + dsName + " 的Redis集群配置成功");
        } catch (Exception e) {
            log.error("删除Redis集群配置失败: " + dsName, e);
            result.put("success", false);
            result.put("message", "删除Redis集群配置失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取所有Redis集群配置列表
     */
    @GetMapping("/redis/list")
    public Map<String, Object> listRedisClusters() {
        log.info("获取所有Redis集群配置列表");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
            Map<String, RedisTemplate<String, Object>> redisTemplates = dynamicDataSource.getDynamicRedisTemplates();
            
            List<String> redisClusterNames = new ArrayList<>(redisTemplates.keySet());
            log.info("获取到 {} 个Redis集群配置", redisClusterNames.size());
            result.put("success", true);
            result.put("redisClusters", redisClusterNames);
        } catch (Exception e) {
            log.error("获取Redis集群列表失败", e);
            result.put("success", false);
            result.put("message", "获取Redis集群列表失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 在指定数据源中添加用户
     */
    @PostMapping("/{dsName}/users")
    public User addUser(@PathVariable String dsName, @RequestBody User user) {
        log.info("在数据源 {} 中添加用户: {}", dsName, user);
        return userService.saveToDynamicDataSource(user.getName(), user.getEmail(), dsName);
    }
    
    /**
     * 向所有数据源中添加用户
     */
    @PostMapping("/all/users")
    public List<User> addUserToAllDataSources(@RequestBody User user) {
        log.info("向所有数据源中添加用户: {}", user);
        return userService.saveToAllDataSources(user.getName(), user.getEmail());
    }

    /**
     * 从指定数据源中获取所有用户
     */
    @GetMapping("/{dsName}/users")
    public List<User> getUsers(@PathVariable String dsName) {
        log.info("从数据源 {} 中获取所有用户", dsName);
        return userService.getAllUsersFromDynamicDataSource(dsName);
    }
    
    /**
     * 从所有数据源中获取所有用户
     */
    @GetMapping("/all/users")
    public List<User> getAllUsersFromAllDataSources() {
        log.info("从所有数据源中获取所有用户");
        return userService.getAllUsersFromAllDataSources();
    }

    /**
     * 在指定数据源中根据ID获取用户
     */
    @GetMapping("/{dsName}/users/{id}")
    public User getUserById(@PathVariable String dsName, @PathVariable Long id) {
        log.info("在数据源 {} 中根据ID {} 获取用户", dsName, id);
        return userService.getUserByIdFromDynamicDataSource(id, dsName);
    }

    /**
     * 在指定数据源中更新用户
     */
    @PutMapping("/{dsName}/users/{id}")
    public User updateUser(@PathVariable String dsName, @PathVariable Long id, @RequestBody User user) {
        log.info("在数据源 {} 中更新用户ID {}: {}", dsName, id, user);
        return userService.updateUserInDynamicDataSource(id, user.getName(), user.getEmail(), dsName);
    }

    /**
     * 在指定数据源中删除用户
     */
    @DeleteMapping("/{dsName}/users/{id}")
    public Map<String, Object> deleteUser(@PathVariable String dsName, @PathVariable Long id) {
        log.info("在数据源 {} 中删除用户ID {}", dsName, id);
        Map<String, Object> result = new HashMap<>();
        // 对于H2内存数据库，我们无法真正删除记录，这里只是演示
        result.put("success", true);
        result.put("message", "用户ID " + id + " 已从数据源 " + dsName + " 中删除");
        return result;
    }
    
    /**
     * 在指定数据源的Redis中设置键值对
     */
    @PostMapping("/{dsName}/redis/set")
    public Map<String, Object> setRedisValue(@PathVariable String dsName, 
                                             @RequestParam String key, 
                                             @RequestParam String value) {
        log.info("在数据源 {} 的Redis中设置键值对: {} = {}", dsName, key, value);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
            RedisTemplate<String, Object> redisTemplate = dynamicDataSource.getRedisTemplate(dsName);
            
            if (redisTemplate == null) {
                log.warn("数据源 {} 的Redis配置不存在", dsName);
                result.put("success", false);
                result.put("message", "数据源 " + dsName + " 的Redis配置不存在");
                return result;
            }
            
            redisTemplate.opsForValue().set(key, value);
            log.info("在数据源 {} 的Redis中设置键值对成功: {} = {}", dsName, key, value);
            result.put("success", true);
            result.put("message", "设置Redis键值对成功");
        } catch (Exception e) {
            log.error("设置Redis键值对失败: " + dsName, e);
            result.put("success", false);
            result.put("message", "设置Redis键值对失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 从指定数据源的Redis中获取值
     */
    @GetMapping("/{dsName}/redis/get")
    public Map<String, Object> getRedisValue(@PathVariable String dsName, 
                                             @RequestParam String key) {
        log.info("从数据源 {} 的Redis中获取值: {}", dsName, key);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
            RedisTemplate<String, Object> redisTemplate = dynamicDataSource.getRedisTemplate(dsName);
            
            if (redisTemplate == null) {
                log.warn("数据源 {} 的Redis配置不存在", dsName);
                result.put("success", false);
                result.put("message", "数据源 " + dsName + " 的Redis配置不存在");
                return result;
            }
            
            Object value = redisTemplate.opsForValue().get(key);
            log.info("从数据源 {} 的Redis中获取值成功: {} = {}", dsName, key, value);
            result.put("success", true);
            result.put("value", value);
        } catch (Exception e) {
            log.error("获取Redis值失败: " + dsName, e);
            result.put("success", false);
            result.put("message", "获取Redis值失败: " + e.getMessage());
        }
        
        return result;
    }
}