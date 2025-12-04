package com.example.multi.datasource.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class PageViewInterceptor implements HandlerInterceptor {
    
    @Autowired
    private PageViewCounter pageViewCounter;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        
        // 统计首页访问
        if ("/".equals(uri) || "/index.html".equals(uri)) {
            pageViewCounter.incrementHomePageViews();
        }
        
        // 统计API调用
        if (uri.startsWith("/api/")) {
            pageViewCounter.incrementApiCalls();
        }
        
        return true;
    }
}