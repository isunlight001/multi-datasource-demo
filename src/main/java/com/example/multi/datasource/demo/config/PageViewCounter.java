package com.example.multi.datasource.demo.config;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class PageViewCounter {
    
    private final AtomicLong homePageViews = new AtomicLong(0);
    private final AtomicLong apiCalls = new AtomicLong(0);
    
    public void incrementHomePageViews() {
        homePageViews.incrementAndGet();
    }
    
    public void incrementApiCalls() {
        apiCalls.incrementAndGet();
    }
    
    public long getHomePageViews() {
        return homePageViews.get();
    }
    
    public long getApiCalls() {
        return apiCalls.get();
    }
}