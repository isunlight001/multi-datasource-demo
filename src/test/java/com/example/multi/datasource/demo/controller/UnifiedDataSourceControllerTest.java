package com.example.multi.datasource.demo.controller;

import com.example.multi.datasource.demo.MultiDatasourceDemoApplication;
import com.example.multi.datasource.demo.entity.User;
import com.example.multi.datasource.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = MultiDatasourceDemoApplication.class)
@AutoConfigureMockMvc
public class UnifiedDataSourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User("Test User", "test@example.com");
        testUser.setId(1L);
    }

    @Test
    public void testListDataSources() throws Exception {
        mockMvc.perform(get("/api/datasource/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testAddUser() throws Exception {
        when(userService.saveToDynamicDataSource(anyString(), anyString(), anyString()))
                .thenReturn(testUser);

        mockMvc.perform(post("/api/datasource/testDS/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test User\",\"email\":\"test@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    public void testGetUsers() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(testUser);
        when(userService.getAllUsersFromDynamicDataSource(anyString()))
                .thenReturn(users);

        mockMvc.perform(get("/api/datasource/testDS/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test User"))
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }
}