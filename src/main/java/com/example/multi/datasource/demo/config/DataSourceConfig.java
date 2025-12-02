package com.example.multi.datasource.demo.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {
    
    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Bean
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:h2:mem:dataSource1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setDriverClassName("org.h2.Driver");
        return dataSource;
    }

    @Bean
    @Primary
    public DataSource dynamicDataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();

        // 设置默认数据源
        dynamicDataSource.setDefaultTargetDataSource(dataSource());

        // 设置目标数据源Map
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("dataSource1", dataSource());
        
        // 添加预定义的数据源
        if (dataSourceProperties != null && dataSourceProperties.getDataSources() != null) {
            for (Map.Entry<String, DataSourceProperties.DruidDataSourceProperties> entry : 
                 dataSourceProperties.getDataSources().entrySet()) {
                String dsName = entry.getKey();
                DataSourceProperties.DruidDataSourceProperties props = entry.getValue();
                
                DruidDataSource druidDataSource = new DruidDataSource();
                druidDataSource.setUrl(props.getUrl());
                druidDataSource.setUsername(props.getUsername());
                druidDataSource.setPassword(props.getPassword());
                druidDataSource.setDriverClassName(props.getDriverClassName());
                druidDataSource.setInitialSize(props.getInitialSize());
                druidDataSource.setMinIdle(props.getMinIdle());
                druidDataSource.setMaxActive(props.getMaxActive());
                druidDataSource.setMaxWait(props.getMaxWait());
                
                dataSourceMap.put(dsName, druidDataSource);
            }
        }
        
        dynamicDataSource.setTargetDataSources(dataSourceMap);

        return dynamicDataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dynamicDataSource) {
        return new JdbcTemplate(dynamicDataSource);
    }
}