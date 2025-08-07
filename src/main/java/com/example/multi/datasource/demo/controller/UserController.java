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

    @PostMapping("/ds/{index}")
    public User saveToDataSource(@PathVariable int index, @RequestBody User user) {
        return userService.saveToDataSource(user.getName(), user.getEmail(), index);
    }

    @GetMapping("/ds/{index}")
    public List<User> getAllUsersFromDataSource(@PathVariable int index) {
        return userService.getAllUsersFromDataSource(index);
    }

    // 保留原有的方法以保持向后兼容性
    @PostMapping("/ds1")
    public User saveToDataSource1(@RequestBody User user) {
        return userService.saveToDataSource1(user.getName(), user.getEmail());
    }

    @PostMapping("/ds2")
    public User saveToDataSource2(@RequestBody User user) {
        return userService.saveToDataSource2(user.getName(), user.getEmail());
    }

    @PostMapping("/ds3")
    public User saveToDataSource3(@RequestBody User user) {
        return userService.saveToDataSource3(user.getName(), user.getEmail());
    }

    @PostMapping("/ds4")
    public User saveToDataSource4(@RequestBody User user) {
        return userService.saveToDataSource4(user.getName(), user.getEmail());
    }

    @PostMapping("/ds5")
    public User saveToDataSource5(@RequestBody User user) {
        return userService.saveToDataSource5(user.getName(), user.getEmail());
    }

    @PostMapping("/ds6")
    public User saveToDataSource6(@RequestBody User user) {
        return userService.saveToDataSource6(user.getName(), user.getEmail());
    }

    @PostMapping("/ds7")
    public User saveToDataSource7(@RequestBody User user) {
        return userService.saveToDataSource7(user.getName(), user.getEmail());
    }

    @PostMapping("/ds8")
    public User saveToDataSource8(@RequestBody User user) {
        return userService.saveToDataSource8(user.getName(), user.getEmail());
    }

    @PostMapping("/ds9")
    public User saveToDataSource9(@RequestBody User user) {
        return userService.saveToDataSource9(user.getName(), user.getEmail());
    }

    @PostMapping("/ds10")
    public User saveToDataSource10(@RequestBody User user) {
        return userService.saveToDataSource10(user.getName(), user.getEmail());
    }

    @GetMapping("/ds1")
    public List<User> getAllUsersFromDataSource1() {
        return userService.getAllUsersFromDataSource1();
    }

    @GetMapping("/ds2")
    public List<User> getAllUsersFromDataSource2() {
        return userService.getAllUsersFromDataSource2();
    }

    @GetMapping("/ds3")
    public List<User> getAllUsersFromDataSource3() {
        return userService.getAllUsersFromDataSource3();
    }

    @GetMapping("/ds4")
    public List<User> getAllUsersFromDataSource4() {
        return userService.getAllUsersFromDataSource4();
    }

    @GetMapping("/ds5")
    public List<User> getAllUsersFromDataSource5() {
        return userService.getAllUsersFromDataSource5();
    }

    @GetMapping("/ds6")
    public List<User> getAllUsersFromDataSource6() {
        return userService.getAllUsersFromDataSource6();
    }

    @GetMapping("/ds7")
    public List<User> getAllUsersFromDataSource7() {
        return userService.getAllUsersFromDataSource7();
    }

    @GetMapping("/ds8")
    public List<User> getAllUsersFromDataSource8() {
        return userService.getAllUsersFromDataSource8();
    }

    @GetMapping("/ds9")
    public List<User> getAllUsersFromDataSource9() {
        return userService.getAllUsersFromDataSource9();
    }

    @GetMapping("/ds10")
    public List<User> getAllUsersFromDataSource10() {
        return userService.getAllUsersFromDataSource10();
    }
    
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