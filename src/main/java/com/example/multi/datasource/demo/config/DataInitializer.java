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
        // 由于我们现在使用完全动态的数据源，这里不初始化任何数据
        // 数据源和数据将在运行时通过API动态添加
        System.out.println("Dynamic datasource demo application started. Use REST APIs to add datasources and data.");
    }
}