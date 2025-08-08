package com.example.multi.datasource.demo.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

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
        dynamicDataSource.setTargetDataSources(dataSourceMap);

        return dynamicDataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dynamicDataSource) {
        return new JdbcTemplate(dynamicDataSource);
    }
}