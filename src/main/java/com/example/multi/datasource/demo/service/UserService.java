package com.example.multi.datasource.demo.service;

import com.example.multi.datasource.demo.config.DynamicDataSource;
import com.example.multi.datasource.demo.entity.User;
import com.example.multi.datasource.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            return userRepository.save(new User(name, email));
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
        // 手动设置数据源
        DynamicDataSource.setContext(dataSourceName);
        try {
            return userRepository.findAll();
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
}