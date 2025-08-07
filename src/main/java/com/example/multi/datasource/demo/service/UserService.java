package com.example.multi.datasource.demo.service;

import com.example.multi.datasource.demo.config.DataSourceEnum;
import com.example.multi.datasource.demo.config.DataSourceSwitcher;
import com.example.multi.datasource.demo.config.DynamicDataSource;
import com.example.multi.datasource.demo.entity.User;
import com.example.multi.datasource.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 在指定的数据源中保存用户
     * @param name 用户名
     * @param email 邮箱
     * @param dataSourceIndex 数据源索引 (1-10)
     * @return 保存的用户
     */
    public User saveToDataSource(String name, String email, int dataSourceIndex) {
        DataSourceSwitcher dataSourceSwitcher = getDataSourceSwitcher(dataSourceIndex);
        if (dataSourceSwitcher != null) {
            // 使用注解方式切换数据源
            return saveWithAnnotation(name, email, dataSourceSwitcher);
        } else {
            // 使用手动方式切换数据源
            return saveToDynamicDataSource(name, email, "dataSource" + dataSourceIndex);
        }
    }

    @DataSourceSwitcher(DataSourceEnum.DATASOURCE1)
    private User saveWithAnnotation(String name, String email, DataSourceSwitcher switcher) {
        return userRepository.save(new User(name, email));
    }

    /**
     * 从指定的数据源中获取所有用户
     * @param dataSourceIndex 数据源索引 (1-10)
     * @return 用户列表
     */
    public List<User> getAllUsersFromDataSource(int dataSourceIndex) {
        DataSourceSwitcher dataSourceSwitcher = getDataSourceSwitcher(dataSourceIndex);
        if (dataSourceSwitcher != null) {
            // 使用注解方式切换数据源
            return getAllUsersWithAnnotation(dataSourceSwitcher);
        } else {
            // 使用手动方式切换数据源
            return getAllUsersFromDynamicDataSource("dataSource" + dataSourceIndex);
        }
    }

    @DataSourceSwitcher(DataSourceEnum.DATASOURCE1)
    private List<User> getAllUsersWithAnnotation(DataSourceSwitcher switcher) {
        return userRepository.findAll();
    }

    /**
     * 根据数据源索引获取对应的数据源切换注解
     * @param dataSourceIndex 数据源索引
     * @return 数据源切换注解
     */
    private DataSourceSwitcher getDataSourceSwitcher(int dataSourceIndex) {
        switch (dataSourceIndex) {
            case 1: return createDataSourceSwitcher(DataSourceEnum.DATASOURCE1);
            case 2: return createDataSourceSwitcher(DataSourceEnum.DATASOURCE2);
            case 3: return createDataSourceSwitcher(DataSourceEnum.DATASOURCE3);
            case 4: return createDataSourceSwitcher(DataSourceEnum.DATASOURCE4);
            case 5: return createDataSourceSwitcher(DataSourceEnum.DATASOURCE5);
            case 6: return createDataSourceSwitcher(DataSourceEnum.DATASOURCE6);
            case 7: return createDataSourceSwitcher(DataSourceEnum.DATASOURCE7);
            case 8: return createDataSourceSwitcher(DataSourceEnum.DATASOURCE8);
            case 9: return createDataSourceSwitcher(DataSourceEnum.DATASOURCE9);
            case 10: return createDataSourceSwitcher(DataSourceEnum.DATASOURCE10);
            default: return null;
        }
    }

    /**
     * 创建数据源切换注解
     * @param dataSourceEnum 数据源枚举
     * @return 数据源切换注解
     */
    private DataSourceSwitcher createDataSourceSwitcher(DataSourceEnum dataSourceEnum) {
        return new DataSourceSwitcher() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return DataSourceSwitcher.class;
            }

            @Override
            public DataSourceEnum value() {
                return dataSourceEnum;
            }
        };
    }

    // 保留原有的方法以保持向后兼容性
    @DataSourceSwitcher(DataSourceEnum.DATASOURCE1)
    public User saveToDataSource1(String name, String email) {
        return userRepository.save(new User(name, email));
    }

    @DataSourceSwitcher(DataSourceEnum.DATASOURCE2)
    public User saveToDataSource2(String name, String email) {
        return userRepository.save(new User(name, email));
    }

    @DataSourceSwitcher(DataSourceEnum.DATASOURCE3)
    public User saveToDataSource3(String name, String email) {
        return userRepository.save(new User(name, email));
    }

    @DataSourceSwitcher(DataSourceEnum.DATASOURCE4)
    public User saveToDataSource4(String name, String email) {
        return userRepository.save(new User(name, email));
    }

    @DataSourceSwitcher(DataSourceEnum.DATASOURCE5)
    public User saveToDataSource5(String name, String email) {
        return userRepository.save(new User(name, email));
    }

    @DataSourceSwitcher(DataSourceEnum.DATASOURCE6)
    public User saveToDataSource6(String name, String email) {
        return userRepository.save(new User(name, email));
    }

    @DataSourceSwitcher(DataSourceEnum.DATASOURCE7)
    public User saveToDataSource7(String name, String email) {
        return userRepository.save(new User(name, email));
    }

    @DataSourceSwitcher(DataSourceEnum.DATASOURCE8)
    public User saveToDataSource8(String name, String email) {
        return userRepository.save(new User(name, email));
    }

    @DataSourceSwitcher(DataSourceEnum.DATASOURCE9)
    public User saveToDataSource9(String name, String email) {
        return userRepository.save(new User(name, email));
    }

    @DataSourceSwitcher(DataSourceEnum.DATASOURCE10)
    public User saveToDataSource10(String name, String email) {
        return userRepository.save(new User(name, email));
    }

    @DataSourceSwitcher(DataSourceEnum.DATASOURCE1)
    public List<User> getAllUsersFromDataSource1() {
        return userRepository.findAll();
    }

    @DataSourceSwitcher(DataSourceEnum.DATASOURCE2)
    public List<User> getAllUsersFromDataSource2() {
        return userRepository.findAll();
    }

    @DataSourceSwitcher(DataSourceEnum.DATASOURCE3)
    public List<User> getAllUsersFromDataSource3() {
        return userRepository.findAll();
    }

    @DataSourceSwitcher(DataSourceEnum.DATASOURCE4)
    public List<User> getAllUsersFromDataSource4() {
        return userRepository.findAll();
    }

    @DataSourceSwitcher(DataSourceEnum.DATASOURCE5)
    public List<User> getAllUsersFromDataSource5() {
        return userRepository.findAll();
    }

    @DataSourceSwitcher(DataSourceEnum.DATASOURCE6)
    public List<User> getAllUsersFromDataSource6() {
        return userRepository.findAll();
    }

    @DataSourceSwitcher(DataSourceEnum.DATASOURCE7)
    public List<User> getAllUsersFromDataSource7() {
        return userRepository.findAll();
    }

    @DataSourceSwitcher(DataSourceEnum.DATASOURCE8)
    public List<User> getAllUsersFromDataSource8() {
        return userRepository.findAll();
    }

    @DataSourceSwitcher(DataSourceEnum.DATASOURCE9)
    public List<User> getAllUsersFromDataSource9() {
        return userRepository.findAll();
    }

    @DataSourceSwitcher(DataSourceEnum.DATASOURCE10)
    public List<User> getAllUsersFromDataSource10() {
        return userRepository.findAll();
    }
    
    /**
     * 在指定的动态数据源中保存用户
     * @param name 用户名
     * @param email 邮箱
     * @param dataSourceName 数据源名称
     * @return 保存的用户
     */
    public User saveToDynamicDataSource(String name, String email, String dataSourceName) {
        // 手动设置数据源
        DynamicDataSource.setContext(dataSourceName);
        try {
            return userRepository.save(new User(name, email));
        } finally {
            // 清除数据源设置
            DynamicDataSource.clearContext();
        }
    }
    
    /**
     * 从指定的动态数据源中获取所有用户
     * @param dataSourceName 数据源名称
     * @return 用户列表
     */
    public List<User> getAllUsersFromDynamicDataSource(String dataSourceName) {
        // 手动设置数据源
        DynamicDataSource.setContext(dataSourceName);
        try {
            return userRepository.findAll();
        } finally {
            // 清除数据源设置
            DynamicDataSource.clearContext();
        }
    }
}