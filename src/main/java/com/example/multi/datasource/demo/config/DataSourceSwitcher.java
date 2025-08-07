package com.example.multi.datasource.demo.config;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSourceSwitcher {
    DataSourceEnum value() default DataSourceEnum.DATASOURCE1;
}