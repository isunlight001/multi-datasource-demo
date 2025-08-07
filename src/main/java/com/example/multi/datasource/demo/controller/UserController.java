package com.example.multi.datasource.demo.controller;

import com.example.multi.datasource.demo.entity.User;
import com.example.multi.datasource.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    // 新增动态数据源操作接口
    @PostMapping("/dynamic/{dsName}")
    public User saveToDynamicDataSource(@PathVariable String dsName, @RequestBody User user) {
        return userService.saveToDynamicDataSource(user.getName(), user.getEmail(), dsName);
    }
    
    @GetMapping("/dynamic/{dsName}")
    public List<User> getAllUsersFromDynamicDataSource(@PathVariable String dsName) {
        return userService.getAllUsersFromDynamicDataSource(dsName);
    }
}