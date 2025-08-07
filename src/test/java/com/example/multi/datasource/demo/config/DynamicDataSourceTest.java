package com.example.multi.datasource.demo.config;

import com.example.multi.datasource.demo.MultiDatasourceDemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MultiDatasourceDemoApplication.class)
public class DynamicDataSourceTest {

    @Test
    public void testDynamicDataSourceCreation() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        assertNotNull(dynamicDataSource);
        assertNotNull(DynamicDataSource.getInstance());
    }

    @Test
    public void testContextHolder() {
        String testDataSource = "testDS";
        DynamicDataSource.setContext(testDataSource);
        assertEquals(testDataSource, DynamicDataSource.getContext());
        DynamicDataSource.clearContext();
        assertNull(DynamicDataSource.getContext());
    }
}