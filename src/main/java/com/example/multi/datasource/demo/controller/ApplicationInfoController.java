package com.example.multi.datasource.demo.controller;

import com.example.multi.datasource.demo.config.ApplicationStartupListener;
import com.example.multi.datasource.demo.config.PageViewCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/info")
public class ApplicationInfoController {
    
    @Autowired
    private PageViewCounter pageViewCounter;
    
    /**
     * 获取应用启动信息
     * @return 启动信息
     */
    @GetMapping("/startup")
    public Map<String, Object> getStartupInfo() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String startupTime = ApplicationStartupListener.getFormattedStartupTime();
            LocalDateTime startupDateTime = ApplicationStartupListener.getStartupTime();
            
            result.put("success", true);
            result.put("startupTime", startupTime);
            
            // 计算运行时间
            if (startupDateTime != null) {
                Duration duration = Duration.between(startupDateTime, LocalDateTime.now());
                long days = duration.toDays();
                long hours = duration.toHours() % 24;
                long minutes = duration.toMinutes() % 60;
                long seconds = duration.getSeconds() % 60;
                
                String uptime = String.format("%d天 %d小时 %d分钟 %d秒", days, hours, minutes, seconds);
                result.put("uptime", uptime);
            }
            
            result.put("message", "获取启动信息成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取启动信息失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取应用状态信息
     * @return 状态信息
     */
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            result.put("success", true);
            result.put("status", "RUNNING");
            result.put("timestamp", System.currentTimeMillis());
            result.put("startupTime", ApplicationStartupListener.getFormattedStartupTime());
            result.put("homePageViews", pageViewCounter.getHomePageViews());
            result.put("apiCalls", pageViewCounter.getApiCalls());
            result.put("message", "应用运行正常");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取状态信息失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取访问统计信息
     * @return 访问统计信息
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            result.put("success", true);
            result.put("homePageViews", pageViewCounter.getHomePageViews());
            result.put("apiCalls", pageViewCounter.getApiCalls());
            result.put("startupTime", ApplicationStartupListener.getFormattedStartupTime());
            result.put("currentTime", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            result.put("message", "获取统计信息成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取统计信息失败: " + e.getMessage());
        }
        
        return result;
    }
}