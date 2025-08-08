package com.example.multi.datasource.demo.service;

import com.example.multi.datasource.demo.config.DynamicDataSource;
import com.example.multi.datasource.demo.entity.User;
import com.example.multi.datasource.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 在指定的动态数据源中保存用户
     * @param name 用户名
     * @param email 邮箱
     * @param dataSourceName 数据源名称
     * @return 保存的用户
     */
    public User saveToDynamicDataSource(String name, String email, String dataSourceName) {
        // 手动设置数据源
        DynamicDataSource.setContext(dataSourceName);
        try {
            User user = userRepository.save(new User(name, email));
            
            // 同时保存到对应的Redis中
            RedisTemplate<String, Object> redisTemplate = DynamicDataSource.getInstance().getRedisTemplate(dataSourceName);
            if (redisTemplate != null) {
                // 将用户信息保存到Redis中，设置1小时过期时间
                String userKey = "user:" + user.getId();
                redisTemplate.opsForValue().set(userKey, user, 1, TimeUnit.HOURS);
            }
            
            return user;
        } finally {
            // 清除数据源设置
            DynamicDataSource.clearContext();
        }
    }
    
    /**
     * 向所有数据源中保存用户
     * @param name 用户名
     * @param email 邮箱
     * @return 保存的用户列表
     */
    public List<User> saveToAllDataSources(String name, String email) {
        List<User> users = new ArrayList<>();
        
        // 获取所有数据源名称
        DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
        Map<Object, DataSource> dataSources = dynamicDataSource.getDynamicDataSources();
        if (dataSources != null) {
            for (Object dsName : dataSources.keySet()) {
                if (dsName instanceof String) {
                    // 手动设置数据源
                    DynamicDataSource.setContext((String) dsName);
                    try {
                        User user = userRepository.save(new User(name, email));
                        users.add(user);
                        
                        // 同时保存到对应的Redis中
                        RedisTemplate<String, Object> redisTemplate = dynamicDataSource.getRedisTemplate((String) dsName);
                        if (redisTemplate != null) {
                            // 将用户信息保存到Redis中，设置1小时过期时间
                            String userKey = "user:" + user.getId();
                            redisTemplate.opsForValue().set(userKey, user, 1, TimeUnit.HOURS);
                        }
                    } catch (Exception e) {
                        // 记录错误但继续处理其他数据源
                        e.printStackTrace();
                    } finally {
                        // 清除数据源设置
                        DynamicDataSource.clearContext();
                    }
                }
            }
        }
        
        return users;
    }
    
    /**
     * 从指定的动态数据源中获取所有用户
     * @param dataSourceName 数据源名称
     * @return 用户列表
     */
    public List<User> getAllUsersFromDynamicDataSource(String dataSourceName) {
        // 先尝试从Redis中获取
        RedisTemplate<String, Object> redisTemplate = DynamicDataSource.getInstance().getRedisTemplate(dataSourceName);
        if (redisTemplate != null) {
            // 检查是否有缓存的用户列表
            String listKey = "user:list";
            List<User> cachedUsers = (List<User>) redisTemplate.opsForValue().get(listKey);
            if (cachedUsers != null) {
                return cachedUsers;
            }
        }
        
        // 手动设置数据源
        DynamicDataSource.setContext(dataSourceName);
        try {
            List<User> users = userRepository.findAll();
            
            // 将结果缓存到Redis中
            if (redisTemplate != null) {
                String listKey = "user:list";
                redisTemplate.opsForValue().set(listKey, users, 10, TimeUnit.MINUTES);
            }
            
            return users;
        } finally {
            // 清除数据源设置
            DynamicDataSource.clearContext();
        }
    }
    
    /**
     * 从所有数据源中获取所有用户
     * @return 所有用户列表
     */
    public List<User> getAllUsersFromAllDataSources() {
        List<User> allUsers = new ArrayList<>();
        
        // 获取所有数据源名称
        DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
        Map<Object, DataSource> dataSources = dynamicDataSource.getDynamicDataSources();
        if (dataSources != null) {
            for (Object dsName : dataSources.keySet()) {
                if (dsName instanceof String) {
                    // 手动设置数据源
                    DynamicDataSource.setContext((String) dsName);
                    try {
                        List<User> users = userRepository.findAll();
                        allUsers.addAll(users);
                    } catch (Exception e) {
                        // 记录错误但继续处理其他数据源
                        e.printStackTrace();
                    } finally {
                        // 清除数据源设置
                        DynamicDataSource.clearContext();
                    }
                }
            }
        }
        
        return allUsers;
    }
    
    /**
     * 从指定数据源的Redis中获取用户信息
     * @param dataSourceName 数据源名称
     * @param userId 用户ID
     * @return 用户信息
     */
    public User getUserFromRedis(String dataSourceName, Long userId) {
        RedisTemplate<String, Object> redisTemplate = DynamicDataSource.getInstance().getRedisTemplate(dataSourceName);
        if (redisTemplate != null) {
            String userKey = "user:" + userId;
            return (User) redisTemplate.opsForValue().get(userKey);
        }
        return null;
    }
    
    /**
     * 将用户信息保存到指定数据源的Redis中
     * @param dataSourceName 数据源名称
     * @param user 用户信息
     */
    public void saveUserToRedis(String dataSourceName, User user) {
        RedisTemplate<String, Object> redisTemplate = DynamicDataSource.getInstance().getRedisTemplate(dataSourceName);
        if (redisTemplate != null) {
            String userKey = "user:" + user.getId();
            redisTemplate.opsForValue().set(userKey, user, 1, TimeUnit.HOURS);
        }
    }
}