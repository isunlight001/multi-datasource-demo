package com.example.multi.datasource.demo;

import com.example.multi.datasource.demo.config.DynamicDataSource;
import com.example.multi.datasource.demo.controller.UnifiedDataSourceController;
import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "server.port=0"
})
public class DynamicDataSourceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testDynamicDataSourceOperations() {
        // 1. 测试获取数据源列表
        ResponseEntity<Map> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/datasource/list", Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("success");
        
        // 2. 测试添加数据源
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        String addDataSourceBody = "dsName=integrationTestDS&" +
            "url=jdbc:h2:mem:integrationTestDS&" +
            "username=sa&" +
            "password=&" +
            "driverClassName=org.h2.Driver";
            
        ResponseEntity<Map> addResponse = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/datasource/add",
            new HttpEntity<>(addDataSourceBody, headers),
            Map.class);
        assertThat(addResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // 3. 再次测试获取数据源列表，确认新数据源已添加
        ResponseEntity<Map> listResponseAfterAdd = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/datasource/list", Map.class);
        assertThat(listResponseAfterAdd.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // 4. 测试在新数据源中添加用户
        String userJson = "{\"name\":\"Integration Test User\",\"email\":\"integration@test.com\"}";
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        
        ResponseEntity<Map> addUserResponse = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/datasource/integrationTestDS/users",
            new HttpEntity<>(userJson, jsonHeaders),
            Map.class);
        assertThat(addUserResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // 5. 测试在新数据源中获取用户
        ResponseEntity<Object[]> getUsersResponse = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/datasource/integrationTestDS/users",
            Object[].class);
        assertThat(getUsersResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // 6. 测试切换数据源
        ResponseEntity<Map> switchResponse = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/datasource/switch?dsName=integrationTestDS",
            null,
            Map.class);
        assertThat(switchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // 7. 测试删除数据源
        restTemplate.delete("http://localhost:" + port + "/api/datasource/remove?dsName=integrationTestDS");
    }
}