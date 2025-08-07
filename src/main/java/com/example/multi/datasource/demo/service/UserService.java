package com.example.multi.datasource.demo.service;

import com.example.multi.datasource.demo.config.DynamicDataSource;
import com.example.multi.datasource.demo.entity.User;
import com.example.multi.datasource.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}