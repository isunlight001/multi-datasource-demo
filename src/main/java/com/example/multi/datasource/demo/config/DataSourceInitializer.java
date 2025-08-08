package com.example.multi.datasource.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Map;

@Component
@Order(1) // 确保在数据初始化之前执行
public class DataSourceInitializer implements ApplicationRunner {
    
    private static final Logger log = LoggerFactory.getLogger(DataSourceInitializer.class);
    
    @Autowired
    private DynamicDataSource dynamicDataSource;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("开始初始化数据源和创建用户表");
        
        // 获取所有配置的数据源
        Map<Object, DataSource> dataSources = dynamicDataSource.getResolvedDataSources();
        
        if (dataSources != null && !dataSources.isEmpty()) {
            log.info("找到 {} 个数据源，准备创建用户表", dataSources.size());
            
            // 为每个数据源创建user表
            for (Map.Entry<Object, DataSource> entry : dataSources.entrySet()) {
                String dataSourceName = (String) entry.getKey();
                DataSource dataSource = entry.getValue();
                
                try {
                    log.info("在数据源 {} 中创建用户表", dataSourceName);
                    
                    // 切换到指定数据源
                    DynamicDataSource.setContext(dataSourceName);
                    
                    // 创建user表，user是H2的保留字，需要用引号括起来
                    String createTableSQL = "CREATE TABLE IF NOT EXISTS \"user\" (" +
                            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                            "name VARCHAR(255) NOT NULL, " +
                            "email VARCHAR(255) NOT NULL" +
                            ")";
                    
                    jdbcTemplate.execute(createTableSQL);
                    log.info("在数据源 {} 中成功创建用户表", dataSourceName);
                } catch (Exception e) {
                    log.error("在数据源 {} 中创建用户表时发生错误", dataSourceName, e);
                } finally {
                    // 清除数据源上下文
                    DynamicDataSource.clearContext();
                }
            }
        } else {
            log.warn("未找到任何数据源配置");
        }
        
        log.info("数据源初始化和表创建完成");
    }
}