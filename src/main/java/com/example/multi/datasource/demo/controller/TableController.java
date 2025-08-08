package com.example.multi.datasource.demo.controller;

import com.example.multi.datasource.demo.service.TableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/datasource")
public class TableController {
    
    private static final Logger log = LoggerFactory.getLogger(TableController.class);

    @Autowired
    private TableService tableService;

    /**
     * 在指定数据源中创建表
     * 
     * @param dataSourceName 数据源名称
     * @param tableName 表名
     * @param sql 创建表的SQL语句
     * @return 操作结果
     */
    @PostMapping("/{dataSourceName}/table/create")
    public Map<String, Object> createTable(
            @PathVariable String dataSourceName,
            @RequestParam String tableName,
            @RequestParam String sql) {
        log.info("在数据源 {} 中创建表 {}, SQL: {}", dataSourceName, tableName, sql);
        return tableService.createTable(dataSourceName, tableName, sql);
    }

    /**
     * 在指定数据源中删除表
     * 
     * @param dataSourceName 数据源名称
     * @param tableName 表名
     * @return 操作结果
     */
    @DeleteMapping("/{dataSourceName}/table/drop")
    public Map<String, Object> dropTable(
            @PathVariable String dataSourceName,
            @RequestParam String tableName) {
        log.info("在数据源 {} 中删除表 {}", dataSourceName, tableName);
        return tableService.dropTable(dataSourceName, tableName);
    }

    /**
     * 获取指定数据源中的所有表
     * 
     * @param dataSourceName 数据源名称
     * @return 表列表
     */
    @GetMapping("/{dataSourceName}/table/list")
    public Map<String, Object> listTables(@PathVariable String dataSourceName) {
        log.info("获取数据源 {} 中的所有表", dataSourceName);
        return tableService.listTables(dataSourceName);
    }
}