package com.example.multi.datasource.demo.service;

import com.example.multi.datasource.demo.config.DynamicDataSource;
import com.example.multi.datasource.demo.entity.User;
import com.example.multi.datasource.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

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
        log.info("在数据源 {} 中保存用户: name={}, email={}", dataSourceName, name, email);

        if (name == null || email == null || dataSourceName == null) {
            log.warn("参数不能为空: name={}, email={}, dataSourceName={}", name, email, dataSourceName);
            throw new IllegalArgumentException("Name, email and dataSourceName must not be null");
        }

        // 使用自动关闭的数据源上下文
        try (DynamicDataSourceContext ignored = new DynamicDataSourceContext(dataSourceName)) {
            User user = userRepository.save(new User(name, email));
            log.info("在数据源 {} 中保存用户成功，用户ID: {}", dataSourceName, user.getId());

            // 同时保存到对应的Redis中
            try {
                DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
                RedisTemplate<String, Object> redisTemplate = dynamicDataSource.getRedisTemplate(dataSourceName);
                if (redisTemplate != null) {
                    // 将用户信息保存到Redis中，设置1小时过期时间
                    String userKey = "user:" + user.getId();
                    redisTemplate.opsForValue().set(userKey, user, 1, TimeUnit.HOURS);
                    log.debug("用户信息已保存到数据源 {} 对应的Redis中，键: {}", dataSourceName, userKey);
                }
            } catch (Exception e) {
                log.warn("保存用户信息到Redis时发生异常，数据源: {}", dataSourceName, e);
            }

            return user;
        }
    }
    
    /**
     * 向所有数据源中保存用户
     * @param name 用户名
     * @param email 邮箱
     * @return 保存的用户列表
     */
    public List<User> saveToAllDataSources(String name, String email) {
        log.info("向所有数据源中保存用户: name={}, email={}", name, email);

        List<User> users = new ArrayList<>();

        // 获取所有数据源名称
        DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
        Map<Object, DataSource> dataSources = dynamicDataSource.getDynamicDataSources();
        if (dataSources != null) {
            for (Object dsName : dataSources.keySet()) {
                if (dsName instanceof String) {
                    try {
                        User user = saveToDynamicDataSource(name, email, (String) dsName);
                        users.add(user);
                    } catch (Exception e) {
                        log.error("在数据源 {} 中保存用户时发生异常", dsName, e);
                    }
                }
            }
        }

        log.info("向所有数据源中保存用户完成，共保存到 {} 个数据源", users.size());
        return users;
    }
    
    /**
     * 从指定的动态数据源中获取所有用户
     * @param dataSourceName 数据源名称
     * @return 用户列表
     */
    public List<User> getAllUsersFromDynamicDataSource(String dataSourceName) {
        log.info("从数据源 {} 中获取所有用户", dataSourceName);

        if (dataSourceName == null) {
            log.warn("数据源名称不能为空");
            throw new IllegalArgumentException("DataSourceName must not be null");
        }

        // 使用自动关闭的数据源上下文
        try (DynamicDataSourceContext ignored = new DynamicDataSourceContext(dataSourceName)) {
            List<User> users = userRepository.findAll();
            log.info("从数据源 {} 中获取到 {} 个用户", dataSourceName, users.size());
            return users;
        }
    }
    
    /**
     * 从所有数据源中获取所有用户
     * @return 所有用户列表
     */
    public List<User> getAllUsersFromAllDataSources() {
        log.info("从所有数据源中获取所有用户");

        List<User> allUsers = new ArrayList<>();

        // 获取所有数据源名称
        DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
        Map<Object, DataSource> dataSources = dynamicDataSource.getDynamicDataSources();
        if (dataSources != null) {
            for (Object dsName : dataSources.keySet()) {
                if (dsName instanceof String) {
                    try {
                        List<User> users = getAllUsersFromDynamicDataSource((String) dsName);
                        allUsers.addAll(users);
                    } catch (Exception e) {
                        log.error("从数据源 {} 中获取用户时发生异常", dsName, e);
                    }
                }
            }
        }

        log.info("从所有数据源中获取用户完成，共获取到 {} 个用户", allUsers.size());
        return allUsers;
    }
    
    /**
     * 从指定数据源的Redis中获取用户信息
     * @param dataSourceName 数据源名称
     * @param userId 用户ID
     * @return 用户信息
     */
    /**
     * 在指定数据源的Redis中获取用户信息
     * @param dataSourceName 数据源名称
     * @param userId 用户ID
     * @return 用户信息
     */
    public User getUserFromRedis(String dataSourceName, Long userId) {
        log.info("从数据源 {} 的Redis中获取用户ID: {}", dataSourceName, userId);
        RedisTemplate<String, Object> redisTemplate = DynamicDataSource.getInstance().getRedisTemplate(dataSourceName);
        if (redisTemplate != null) {
            String userKey = "user:" + userId;
            User user = (User) redisTemplate.opsForValue().get(userKey);
            if (user != null) {
                log.debug("从数据源 {} 的Redis中获取到用户: {}", dataSourceName, user);
            } else {
                log.debug("在数据源 {} 的Redis中未找到用户ID: {}", dataSourceName, userId);
            }
            return user;
        }
        return null;
    }

    /**
     * 将用户信息保存到指定数据源的Redis中
     * @param dataSourceName 数据源名称
     * @param user 用户信息
     */
    public void saveUserToRedis(String dataSourceName, User user) {
        log.info("将用户信息保存到数据源 {} 的Redis中: {}", dataSourceName, user);
        RedisTemplate<String, Object> redisTemplate = DynamicDataSource.getInstance().getRedisTemplate(dataSourceName);
        if (redisTemplate != null) {
            String userKey = "user:" + user.getId();
            redisTemplate.opsForValue().set(userKey, user, 1, TimeUnit.HOURS);
            log.debug("用户信息已保存到数据源 {} 的Redis中，键: {}", dataSourceName, userKey);
        }
    }
    
    /**
     * 在指定的动态数据源中根据ID获取用户
     * @param id 用户ID
     * @param dataSourceName 数据源名称
     * @return 用户
     */
    public User getUserByIdFromDynamicDataSource(Long id, String dataSourceName) {
        log.info("在数据源 {} 中根据ID {} 获取用户", dataSourceName, id);

        try (DynamicDataSourceContext ignored = new DynamicDataSourceContext(dataSourceName)) {
            User user = userRepository.findById(id).orElse(null);
            if (user != null) {
                log.info("在数据源 {} 中根据ID {} 获取用户成功", dataSourceName, id);
            } else {
                log.info("在数据源 {} 中未找到ID为 {} 的用户", dataSourceName, id);
            }
            return user;
        }
    }

    /**
     * 在指定的动态数据源中更新用户
     * @param id 用户ID
     * @param name 用户名
     * @param email 邮箱
     * @param dataSourceName 数据源名称
     * @return 更新后的用户
     */
    public User updateUserInDynamicDataSource(Long id, String name, String email, String dataSourceName) {
        log.info("在数据源 {} 中更新用户ID {}: name={}, email={}", dataSourceName, id, name, email);

        try (DynamicDataSourceContext ignored = new DynamicDataSourceContext(dataSourceName)) {
            User user = userRepository.findById(id).orElse(null);
            if (user != null) {
                user.setName(name);
                user.setEmail(email);
                User updatedUser = userRepository.save(user);
                log.info("在数据源 {} 中更新用户ID {} 成功", dataSourceName, id);

                // 同时更新对应的Redis
                try {
                    DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
                    RedisTemplate<String, Object> redisTemplate = dynamicDataSource.getRedisTemplate(dataSourceName);
                    if (redisTemplate != null) {
                        // 更新Redis中的用户信息
                        String userKey = "user:" + updatedUser.getId();
                        redisTemplate.opsForValue().set(userKey, updatedUser, 1, TimeUnit.HOURS);
                        log.debug("用户信息已更新到数据源 {} 对应的Redis中，键: {}", dataSourceName, userKey);
                    }
                } catch (Exception e) {
                    log.warn("更新用户信息到Redis时发生异常，数据源: {}", dataSourceName, e);
                }

                return updatedUser;
            } else {
                log.info("在数据源 {} 中未找到ID为 {} 的用户，无法更新", dataSourceName, id);
                return null;
            }
        }
    }

    /**
     * 在指定的动态数据源中执行操作
     * @param dataSourceName 数据源名称
     * @param operation 要执行的操作
     * @return 操作结果
     */
    public <T> T executeOnDynamicDataSource(String dataSourceName, DataSourceOperation<T> operation) {
        log.debug("在数据源 {} 中执行操作", dataSourceName);

        if (dataSourceName == null || operation == null) {
            log.warn("参数不能为空: dataSourceName={}, operation={}", dataSourceName, operation);
            throw new IllegalArgumentException("DataSourceName and operation must not be null");
        }

        try (DynamicDataSourceContext ignored = new DynamicDataSourceContext(dataSourceName)) {
            T result = operation.execute();
            log.debug("在数据源 {} 中执行操作成功", dataSourceName);
            return result;
        }
    }

    /**
     * 数据源操作函数式接口
     */
    @FunctionalInterface
    public interface DataSourceOperation<T> {
        T execute();
    }

    /**
     * 自动关闭的数据源上下文
     */
    private static class DynamicDataSourceContext implements AutoCloseable {
        private final String dataSourceName;

        public DynamicDataSourceContext(String dataSourceName) {
            this.dataSourceName = dataSourceName;
            DynamicDataSource.setContext(dataSourceName);
        }

        @Override
        public void close() {
            DynamicDataSource.clearContext();
        }
    }
}