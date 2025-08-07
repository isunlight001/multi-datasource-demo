package com.example.multi.datasource.demo.service;

import com.example.multi.datasource.demo.MultiDatasourceDemoApplication;
import com.example.multi.datasource.demo.entity.User;
import com.example.multi.datasource.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = MultiDatasourceDemoApplication.class)
public class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    private UserService userService;

    private User testUser;

    @BeforeEach
    public void setUp() {
        userService = new UserService();
        testUser = new User("Test User", "test@example.com");
        testUser.setId(1L);
        
        // 使用反射设置私有字段
        try {
            java.lang.reflect.Field userRepositoryField = UserService.class.getDeclaredField("userRepository");
            userRepositoryField.setAccessible(true);
            userRepositoryField.set(userService, userRepository);
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }
    }

    @Test
    public void testSaveToDynamicDataSource() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.saveToDynamicDataSource("Test User", "test@example.com", "testDS");
        
        assertNotNull(result);
        assertEquals("Test User", result.getName());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    public void testGetAllUsersFromDynamicDataSource() {
        List<User> users = new ArrayList<>();
        users.add(testUser);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsersFromDynamicDataSource("testDS");
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test User", result.get(0).getName());
    }
}