package com.example.multi.datasource.demo.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
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
    @Primary
    public DynamicDataSource dynamicDataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        
        // 创建默认数据源 (数据源1)
        DataSource defaultDataSource = dataSource("one");
        
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("dataSource1", defaultDataSource);
        
        dynamicDataSource.setTargetDataSources(dataSourceMap);
        dynamicDataSource.setDefaultTargetDataSource(defaultDataSource);
        return dynamicDataSource;
    }

    @Bean(name = "dataSource1")
    @ConfigurationProperties(prefix = "spring.datasource.druid.one")
    public DataSource dataSource1() {
        return dataSource("one");
    }

    /**
     * 通用数据源创建方法
     * @param dsName 数据源名称
     * @return 数据源
     */
    public DataSource dataSource(String dsName) {
        return DataSourceBuilder.create()
                .type(DruidDataSource.class)
                .url(getUrl(dsName))
                .username(getUsername(dsName))
                .password(getPassword(dsName))
                .driverClassName(getDriverClassName(dsName))
                .build();
    }

    /**
     * 根据数据源名称获取URL
     * @param dsName 数据源名称
     * @return URL
     */
    private String getUrl(String dsName) {
        switch (dsName) {
            case "one": return "jdbc:h2:mem:db1";
            case "two": return "jdbc:h2:mem:db2";
            case "three": return "jdbc:h2:mem:db3";
            case "four": return "jdbc:h2:mem:db4";
            case "five": return "jdbc:h2:mem:db5";
            case "six": return "jdbc:h2:mem:db6";
            case "seven": return "jdbc:h2:mem:db7";
            case "eight": return "jdbc:h2:mem:db8";
            case "nine": return "jdbc:h2:mem:db9";
            case "ten": return "jdbc:h2:mem:db10";
            default: return "jdbc:h2:mem:" + dsName;
        }
    }

    /**
     * 根据数据源名称获取用户名
     * @param dsName 数据源名称
     * @return 用户名
     */
    private String getUsername(String dsName) {
        return "sa";
    }

    /**
     * 根据数据源名称获取密码
     * @param dsName 数据源名称
     * @return 密码
     */
    private String getPassword(String dsName) {
        return "";
    }

    /**
     * 根据数据源名称获取驱动类名
     * @param dsName 数据源名称
     * @return 驱动类名
     */
    private String getDriverClassName(String dsName) {
        return "org.h2.Driver";
    }

    @Bean
    @Primary
    public JdbcTemplate jdbcTemplate(@Qualifier("dynamicDataSource") DataSource dynamicDataSource) {
        return new JdbcTemplate(dynamicDataSource);
    }
}