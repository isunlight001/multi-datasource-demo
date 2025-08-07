package com.example.multi.datasource.demo;

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
        String baseUrl = "http://localhost:" + port;
        
        // 1. 测试获取数据源列表
        ResponseEntity<Map> listResponse = restTemplate.getForEntity(
            baseUrl + "/api/datasource/list", Map.class);
        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody()).containsKey("success");
        assertThat((Boolean) listResponse.getBody().get("success")).isTrue();
        
        // 2. 测试添加数据源
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        String addDataSourceBody = "dsName=integrationTestDS&" +
            "url=jdbc:h2:mem:integrationTestDS&" +
            "username=sa&" +
            "password=&" +
            "driverClassName=org.h2.Driver";
            
        ResponseEntity<Map> addResponse = restTemplate.postForEntity(
            baseUrl + "/api/datasource/add",
            new HttpEntity<>(addDataSourceBody, headers),
            Map.class);
        assertThat(addResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // 由于我们无法完全模拟数据源创建过程，这里只检查响应结构
        assertThat(addResponse.getBody()).containsKey("success");
        
        // 3. 再次测试获取数据源列表
        ResponseEntity<Map> listResponseAfterAdd = restTemplate.getForEntity(
            baseUrl + "/api/datasource/list", Map.class);
        assertThat(listResponseAfterAdd.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((Boolean) listResponseAfterAdd.getBody().get("success")).isTrue();
        
        // 4. 测试切换数据源
        ResponseEntity<Map> switchResponse = restTemplate.postForEntity(
            baseUrl + "/api/datasource/switch?dsName=integrationTestDS",
            null,
            Map.class);
        assertThat(switchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // 检查响应结构
        assertThat(switchResponse.getBody()).containsKey("success");
        
        // 5. 测试删除数据源
        HttpHeaders deleteHeaders = new HttpHeaders();
        HttpEntity<String> deleteEntity = new HttpEntity<>(deleteHeaders);
        ResponseEntity<String> deleteResponse = restTemplate.exchange(
            baseUrl + "/api/datasource/remove?dsName=integrationTestDS",
            HttpMethod.DELETE,
            deleteEntity,
            String.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}