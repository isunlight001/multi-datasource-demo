package com.example.multi.datasource.demo.service;

import com.example.multi.datasource.demo.config.DynamicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TableService {
    
    private static final Logger log = LoggerFactory.getLogger(TableService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 在指定数据源中创建表
     * @param dataSourceName 数据源名称
     * @param tableName 表名
     * @param sql 创建表的SQL语句
     * @return 操作结果
     */
    public Map<String, Object> createTable(String dataSourceName, String tableName, String sql) {
        log.info("开始在数据源 {} 中创建表 {}", dataSourceName, tableName);
        
        Map<String, Object> result = new HashMap<>();

        try {
            // 检查数据源是否存在
            DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
            Map<Object, DataSource> allDataSources = dynamicDataSource.getDynamicDataSources();
            if (!allDataSources.containsKey(dataSourceName)) {
                log.warn("数据源 {} 不存在", dataSourceName);
                result.put("success", false);
                result.put("message", "数据源 " + dataSourceName + " 不存在");
                return result;
            }

            // 切换到指定数据源
            DynamicDataSource.setContext(dataSourceName);
            
            try {
                // 执行创建表的SQL语句
                jdbcTemplate.execute(sql);
                log.info("在数据源 {} 中创建表 {} 成功", dataSourceName, tableName);
                result.put("success", true);
                result.put("message", "在数据源 " + dataSourceName + " 中创建表 " + tableName + " 成功");
            } finally {
                // 清除数据源设置
                DynamicDataSource.clearContext();
            }
        } catch (Exception e) {
            log.error("在数据源 " + dataSourceName + " 中创建表 " + tableName + " 失败", e);
            result.put("success", false);
            result.put("message", "在数据源 " + dataSourceName + " 中创建表 " + tableName + " 失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 在指定数据源中删除表
     * @param dataSourceName 数据源名称
     * @param tableName 表名
     * @return 操作结果
     */
    public Map<String, Object> dropTable(String dataSourceName, String tableName) {
        log.info("开始在数据源 {} 中删除表 {}", dataSourceName, tableName);
        
        Map<String, Object> result = new HashMap<>();

        try {
            // 检查数据源是否存在
            DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
            Map<Object, DataSource> allDataSources = dynamicDataSource.getDynamicDataSources();
            if (!allDataSources.containsKey(dataSourceName)) {
                log.warn("数据源 {} 不存在", dataSourceName);
                result.put("success", false);
                result.put("message", "数据源 " + dataSourceName + " 不存在");
                return result;
            }

            // 切换到指定数据源
            DynamicDataSource.setContext(dataSourceName);
            
            try {
                // 执行删除表的SQL语句
                String sql = "DROP TABLE " + tableName;
                jdbcTemplate.execute(sql);
                log.info("在数据源 {} 中删除表 {} 成功", dataSourceName, tableName);
                result.put("success", true);
                result.put("message", "在数据源 " + dataSourceName + " 中删除表 " + tableName + " 成功");
            } finally {
                // 清除数据源设置
                DynamicDataSource.clearContext();
            }
        } catch (Exception e) {
            log.error("在数据源 " + dataSourceName + " 中删除表 " + tableName + " 失败", e);
            result.put("success", false);
            result.put("message", "在数据源 " + dataSourceName + " 中删除表 " + tableName + " 失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 获取指定数据源中的所有表
     * @param dataSourceName 数据源名称
     * @return 表列表
     */
    public Map<String, Object> listTables(String dataSourceName) {
        log.info("开始获取数据源 {} 中的所有表", dataSourceName);
        
        Map<String, Object> result = new HashMap<>();

        try {
            // 检查数据源是否存在
            DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
            Map<Object, DataSource> allDataSources = dynamicDataSource.getDynamicDataSources();
            if (!allDataSources.containsKey(dataSourceName)) {
                log.warn("数据源 {} 不存在", dataSourceName);
                result.put("success", false);
                result.put("message", "数据源 " + dataSourceName + " 不存在");
                return result;
            }

            // 切换到指定数据源
            DynamicDataSource.setContext(dataSourceName);
            
            try {
                // 查询所有表名
                List<Map<String, String>> tables = new ArrayList<>();
                
                // 获取数据库元数据
                Connection connection = jdbcTemplate.getDataSource().getConnection();
                DatabaseMetaData metaData = connection.getMetaData();
                
                // 对于H2数据库，schemaPattern应该是PUBLIC
                String schemaPattern = null;
                if (isH2Database(connection)) {
                    schemaPattern = "PUBLIC";
                }
                
                java.sql.ResultSet rs = metaData.getTables(null, schemaPattern, null, new String[]{"TABLE"});
                
                while (rs.next()) {
                    Map<String, String> tableInfo = new HashMap<>();
                    tableInfo.put("TABLE_NAME", rs.getString("TABLE_NAME"));
                    tableInfo.put("TABLE_TYPE", rs.getString("TABLE_TYPE"));
                    tables.add(tableInfo);
                }
                
                rs.close();
                connection.close();
                
                log.info("获取数据源 {} 中的表列表成功，共 {} 张表", dataSourceName, tables.size());
                result.put("success", true);
                result.put("tables", tables);
            } finally {
                // 清除数据源设置
                DynamicDataSource.clearContext();
            }
        } catch (Exception e) {
            log.error("获取数据源 " + dataSourceName + " 中的表列表失败", e);
            result.put("success", false);
            result.put("message", "获取数据源 " + dataSourceName + " 中的表列表失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 判断当前数据源是否为H2数据库
     * @param connection 数据库连接
     * @return 是否为H2数据库
     */
    private boolean isH2Database(Connection connection) {
        try {
            String url = connection.getMetaData().getURL();
            boolean isH2 = url.contains(":h2:");
            if (isH2) {
                log.debug("检测到H2数据库: {}", url);
            }
            return isH2;
        } catch (SQLException e) {
            log.warn("判断数据库类型时发生异常", e);
            return false;
        }
    }
}