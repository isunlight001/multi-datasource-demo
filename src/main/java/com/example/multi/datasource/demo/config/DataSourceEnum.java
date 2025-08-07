package com.example.multi.datasource.demo.config;

public enum DataSourceEnum {
    DATASOURCE1("dataSource1"),
    DATASOURCE2("dataSource2"),
    DATASOURCE3("dataSource3"),
    DATASOURCE4("dataSource4"),
    DATASOURCE5("dataSource5"),
    DATASOURCE6("dataSource6"),
    DATASOURCE7("dataSource7"),
    DATASOURCE8("dataSource8"),
    DATASOURCE9("dataSource9"),
    DATASOURCE10("dataSource10"),
    DYNAMIC("dynamic"); // 添加动态数据源选项

    private String name;

    DataSourceEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}