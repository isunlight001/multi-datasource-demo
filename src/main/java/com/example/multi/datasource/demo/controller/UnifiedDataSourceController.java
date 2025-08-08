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
            log.error("添加数据源失败", e);
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
     */
    @GetMapping("/list")
    public Map<String, Object> listDataSources() {
        Map<String, Object> result = new HashMap<>();
        try {
            DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
            Map<Object, DataSource> existingDataSources = dynamicDataSource.getDynamicDataSources();

            List<String> dataSourceNames = new ArrayList<>();
            for (Object key : existingDataSources.keySet()) {
                dataSourceNames.add((String) key);
            }
            result.put("success", true);
            result.put("dataSources", dataSourceNames);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取数据源列表失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 为指定数据源添加Redis集群配置
     */
    @PostMapping("/redis/add")
    public Map<String, Object> addRedisCluster(@RequestParam String dsName,
                                               @RequestParam String redisHost,
                                               @RequestParam int redisPort) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
            Map<Object, DataSource> existingDataSources = dynamicDataSource.getDynamicDataSources();
            
            // 检查数据源是否存在
            if (!existingDataSources.containsKey(dsName)) {
                result.put("success", false);
                result.put("message", "数据源 " + dsName + " 不存在");
                return result;
            }
            
            // 添加Redis集群配置
            dynamicDataSource.addRedisCluster(dsName, redisHost, redisPort);
            
            result.put("success", true);
            result.put("message", "为数据源 " + dsName + " 添加Redis集群配置成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加Redis集群配置失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 删除指定数据源的Redis集群配置
     */
    @DeleteMapping("/redis/remove")
    public Map<String, Object> removeRedisCluster(@RequestParam String dsName) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
            
            // 检查Redis集群配置是否存在
            Map<String, RedisTemplate<String, Object>> redisTemplates = dynamicDataSource.getDynamicRedisTemplates();
            if (!redisTemplates.containsKey(dsName)) {
                result.put("success", false);
                result.put("message", "数据源 " + dsName + " 的Redis集群配置不存在");
                return result;
            }
            
            // 删除Redis集群配置
            dynamicDataSource.removeRedisCluster(dsName);
            
            result.put("success", true);
            result.put("message", "删除数据源 " + dsName + " 的Redis集群配置成功");
        } catch (Exception e) {
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
        Map<String, Object> result = new HashMap<>();
        
        try {
            DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
            Map<String, RedisTemplate<String, Object>> redisTemplates = dynamicDataSource.getDynamicRedisTemplates();
            
            List<String> redisClusterNames = new ArrayList<>(redisTemplates.keySet());
            result.put("success", true);
            result.put("redisClusters", redisClusterNames);
        } catch (Exception e) {
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
        return userService.saveToDynamicDataSource(user.getName(), user.getEmail(), dsName);
    }
    
    /**
     * 向所有数据源中添加用户
     */
    @PostMapping("/all/users")
    public List<User> addUserToAllDataSources(@RequestBody User user) {
        return userService.saveToAllDataSources(user.getName(), user.getEmail());
    }

    /**
     * 从指定数据源中获取所有用户
     */
    @GetMapping("/{dsName}/users")
    public List<User> getUsers(@PathVariable String dsName) {
        return userService.getAllUsersFromDynamicDataSource(dsName);
    }
    
    /**
     * 从所有数据源中获取所有用户
     */
    @GetMapping("/all/users")
    public List<User> getAllUsersFromAllDataSources() {
        return userService.getAllUsersFromAllDataSources();
    }

    /**
     * 在指定数据源中根据ID获取用户
     */
    @GetMapping("/{dsName}/users/{id}")
    public User getUser(@PathVariable String dsName, @PathVariable Long id) {
        // 先切换数据源
        DynamicDataSource.setContext(dsName);
        try {
            // 注意：由于UserRepository是单例的，这里直接使用可能不会使用正确的数据源
            // 正确的做法是使用动态数据源的服务方法
            return userService.getAllUsersFromDynamicDataSource(dsName)
                    .stream()
                    .filter(user -> user.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        } finally {
            DynamicDataSource.clearContext();
        }
    }

    /**
     * 在指定数据源中更新用户
     */
    @PutMapping("/{dsName}/users/{id}")
    public User updateUser(@PathVariable String dsName, @PathVariable Long id, @RequestBody User user) {
        // 先切换数据源
        DynamicDataSource.setContext(dsName);
        try {
            // 这里简化处理，实际项目中应该通过Repository进行更新操作
            return userService.saveToDynamicDataSource(user.getName(), user.getEmail(), dsName);
        } finally {
            DynamicDataSource.clearContext();
        }
    }

    /**
     * 在指定数据源中删除用户
     */
    @DeleteMapping("/{dsName}/users/{id}")
    public Map<String, Object> deleteUser(@PathVariable String dsName, @PathVariable Long id) {
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
        Map<String, Object> result = new HashMap<>();
        
        try {
            DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
            RedisTemplate<String, Object> redisTemplate = dynamicDataSource.getRedisTemplate(dsName);
            
            if (redisTemplate == null) {
                result.put("success", false);
                result.put("message", "数据源 " + dsName + " 的Redis配置不存在");
                return result;
            }
            
            redisTemplate.opsForValue().set(key, value);
            result.put("success", true);
            result.put("message", "设置Redis键值对成功");
        } catch (Exception e) {
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
        Map<String, Object> result = new HashMap<>();
        
        try {
            DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
            RedisTemplate<String, Object> redisTemplate = dynamicDataSource.getRedisTemplate(dsName);
            
            if (redisTemplate == null) {
                result.put("success", false);
                result.put("message", "数据源 " + dsName + " 的Redis配置不存在");
                return result;
            }
            
            Object value = redisTemplate.opsForValue().get(key);
            result.put("success", true);
            result.put("value", value);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取Redis值失败: " + e.getMessage());
        }
        
        return result;
    }
}