package com.example.multi.datasource.demo.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Order(1)
@Component
public class DataSourceAspect {

    @Pointcut("@annotation(com.example.multi.datasource.demo.config.DataSourceSwitcher)")
    public void dataSourcePointCut() {
    }

    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        DataSourceSwitcher dataSource = method.getAnnotation(DataSourceSwitcher.class);
        
        if (dataSource != null) {
            DynamicDataSource.setContext(dataSource.value().getName());
        }

        try {
            return point.proceed();
        } finally {
            DynamicDataSource.clearContext();
        }
    }
}