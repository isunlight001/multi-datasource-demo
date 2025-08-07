package com.example.multi.datasource.demo.config;

import com.example.multi.datasource.demo.entity.User;
import com.example.multi.datasource.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 初始化数据到第一个数据源
        DynamicDataSource.setContext(DataSourceEnum.DATASOURCE1.getName());
        userRepository.save(new User("Alice", "alice@example.com"));
        userRepository.save(new User("Bob", "bob@example.com"));
        DynamicDataSource.clearContext();
        
        System.out.println("Data initialized successfully!");
    }
}