package com.example.multi.datasource.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent> {
    
    private static final Logger log = LoggerFactory.getLogger(ApplicationStartupListener.class);
    
    private static LocalDateTime startupTime;
    private static long startupTimestamp;
    
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        startupTime = LocalDateTime.now();
        startupTimestamp = System.currentTimeMillis();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = startupTime.format(formatter);
        log.info("应用程序启动完成，启动时间: {}", formattedTime);
        System.out.println("===============================================");
        System.out.println("应用程序启动完成!");
        System.out.println("启动时间: " + formattedTime);
        System.out.println("===============================================");
    }
    
    public static LocalDateTime getStartupTime() {
        return startupTime;
    }
    
    public static long getStartupTimestamp() {
        return startupTimestamp;
    }
    
    public static String getFormattedStartupTime() {
        if (startupTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return startupTime.format(formatter);
        }
        return "未知";
    }
}