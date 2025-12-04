# 多数据源动态管理详细指南

## 项目概述

本项目是一个基于Spring Boot的多数据源动态管理示例，展示了如何在运行时动态添加、删除和切换数据源，而无需重启应用或修改代码。项目支持为每个数据源配置独立的Redis集群，实现了完整的数据源生命周期管理和缓存机制。

## 核心特性

### 1. 动态数据源管理
- **运行时添加数据源**：通过REST API动态创建新的数据源连接
- **运行时删除数据源**：动态移除不再需要的数据源
- **数据源切换**：在不同数据源之间无缝切换
- **数据源列表查询**：实时查看所有可用数据源

### 2. Redis集群集成
- **动态Redis配置**：为每个数据源动态配置对应的Redis集群
- **缓存同步**：用户数据自动同步到对应数据源的Redis中
- **Redis操作接口**：提供直接操作Redis的API

### 3. 数据操作功能
- **单数据源操作**：在指定数据源上进行CRUD操作
- **批量数据源操作**：向所有数据源同时添加或查询数据
- **缓存优先读取**：查询时优先从Redis缓存获取数据

## 技术架构

### 主要技术栈
- **Spring Boot 2.7.0**：应用框架
- **Spring Data JPA**：ORM框架
- **Druid 1.2.8**：数据库连接池
- **H2 Database**：内存数据库（运行时）
- **Spring Data Redis**：Redis集成
- **Lettuce**：Redis客户端

### 核心组件

#### 1. DynamicDataSource（核心）
位于：[src/main/java/com/example/multi/datasource/demo/config/DynamicDataSource.java](src/main/java/com/example/multi/datasource/demo/config/DynamicDataSource.java)

这是项目的核心组件，继承自Spring的`AbstractRoutingDataSource`，负责：
- 基于`ThreadLocal`的数据源路由决策
- 动态添加和移除数据源
- 管理Redis集群配置
- 提供线程安全的数据源切换机制

#### 2. DataSourceConfig（配置）
位于：[src/main/java/com/example/multi/datasource/demo/config/DataSourceConfig.java](src/main/java/com/example/multi/datasource/demo/config/DataSourceConfig.java)

负责：
- 配置默认数据源
- 初始化DynamicDataSource
- 创建JdbcTemplate实例

#### 3. 统一控制器（UnifiedDataSourceController）
位于：[src/main/java/com/example/multi/datasource/demo/controller/UnifiedDataSourceController.java](src/main/java/com/example/multi/datasource/demo/controller/UnifiedDataSourceController.java)

提供所有REST API接口：
- 数据源管理接口
- Redis集群管理接口
- 数据操作接口

## 项目结构详解

```
src/
├── main/
│   ├── java/
│   │   └── com/example/multi/datasource/demo/
│   │       ├── config/           # 配置类
│   │       │   ├── DataSourceConfig.java         # 数据源配置
│   │       │   ├── DynamicDataSource.java        # 动态数据源核心类
│   │       │   ├── DataSourceAspect.java         # 数据源切面
│   │       │   ├── DataSourceSwitcher.java       # 数据源切换注解
│   │       │   ├── DataSourceEnum.java           # 数据源枚举
│   │       │   ├── DataSourceProperties.java     # 数据源属性配置
│   │       │   ├── DataInitializer.java          # 数据初始化
│   │       │   └── RedisConfig.java              # Redis配置
│   │       ├── controller/       # 控制器
│   │       │   ├── UnifiedDataSourceController.java  # 统一数据源控制器
│   │       │   ├── DataSourceController.java         # 数据源控制器（旧版）
│   │       │   ├── UserController.java               # 用户控制器
│   │       │   └── TableController.java              # 表管理控制器
│   │       ├── entity/           # 实体类
│   │       │   └── User.java                     # 用户实体
│   │       ├── repository/       # 数据访问层
│   │       │   └── UserRepository.java           # 用户仓库
│   │       ├── service/          # 业务逻辑层
│   │       │   ├── UserService.java              # 用户服务
│   │       │   └── TableService.java            # 表服务
│   │       └── MultiDatasourceDemoApplication.java  # 启动类
│   └── resources/
│       ├── application.yml       # 应用配置
│       └── static/
│           └── index.html        # 前端页面
└── test/                         # 测试代码
```

## 核心实现原理

### 1. 动态数据源切换机制

项目通过`DynamicDataSource`类实现动态数据源切换：

```java
public class DynamicDataSource extends AbstractRoutingDataSource {
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();
    
    @Override
    protected Object determineCurrentLookupKey() {
        return getContext();  // 返回当前线程绑定的数据源标识
    }
    
    public static void setContext(String dataSource) {
        CONTEXT_HOLDER.set(dataSource);  // 设置当前线程的数据源
    }
    
    public static void clearContext() {
        CONTEXT_HOLDER.remove();  // 清除当前线程的数据源设置
    }
}
```

### 2. 数据源动态添加/删除

通过`synchronized`方法保证线程安全：

```java
public synchronized void addTargetDataSource(String key, DataSource dataSource) {
    dynamicDataSources.put(key, dataSource);
    
    // 更新resolvedDataSources
    Map<Object, Object> targetDataSources = new HashMap<>(this.getResolvedDataSources());
    targetDataSources.put(key, dataSource);
    
    // 重新设置目标数据源
    this.setTargetDataSources(targetDataSources);
    
    // 重新初始化数据源
    this.afterPropertiesSet();
}
```

### 3. Redis集群动态管理

为每个数据源维护独立的Redis连接和模板：

```java
// 存储动态创建的Redis连接工厂
private Map<String, LettuceConnectionFactory> dynamicRedisConnectionFactories = new ConcurrentHashMap<>();

// 存储动态创建的Redis模板
private Map<String, RedisTemplate<String, Object>> dynamicRedisTemplates = new ConcurrentHashMap<>();
```

## API接口详解

### 数据源管理接口

#### 1. 添加数据源
```
POST /api/datasource/add
```
参数：
- `dsName`：数据源名称
- `url`：数据库连接URL
- `username`：用户名
- `password`：密码
- `driverClassName`：驱动类名
- `initialSize`：初始连接数（可选，默认5）
- `minIdle`：最小空闲连接数（可选，默认5）
- `maxActive`：最大活跃连接数（可选，默认20）
- `maxWait`：最大等待时间（可选，默认60000）

#### 2. 删除数据源
```
DELETE /api/datasource/remove?dsName={dsName}
```

#### 3. 切换数据源
```
POST /api/datasource/switch?dsName={dsName}
```

#### 4. 查询数据源列表
```
GET /api/datasource/list
```

### Redis集群管理接口

#### 1. 添加Redis集群配置
```
POST /api/datasource/redis/add
```
参数：
- `dsName`：数据源名称
- `redisHost`：Redis主机地址
- `redisPort`：Redis端口

#### 2. 删除Redis集群配置
```
DELETE /api/datasource/redis/remove?dsName={dsName}
```

#### 3. 查询Redis集群列表
```
GET /api/datasource/redis/list
```

### 数据操作接口

#### 1. 在指定数据源中添加用户
```
POST /api/datasource/{dsName}/users
```

#### 2. 从指定数据源中查询所有用户
```
GET /api/datasource/{dsName}/users
```

#### 3. 向所有数据源中添加用户
```
POST /api/datasource/all/users
```

#### 4. 从所有数据源中查询所有用户
```
GET /api/datasource/all/users
```

### Redis操作接口

#### 1. 在指定数据源的Redis中设置键值对
```
POST /api/datasource/{dsName}/redis/set
```

#### 2. 从指定数据源的Redis中获取值
```
GET /api/datasource/{dsName}/redis/get?key={key}
```

## 使用示例

### 1. 完整流程示例

1. **启动应用**
```bash
mvn spring-boot:run
```

2. **添加新数据源**
```bash
curl -X POST "http://localhost:8087/api/datasource/add" \
  -d "dsName=mydb1" \
  -d "url=jdbc:h2:mem:mydb1" \
  -d "username=sa" \
  -d "password=" \
  -d "driverClassName=org.h2.Driver"
```

3. **为数据源添加Redis配置**
```bash
curl -X POST "http://localhost:8087/api/datasource/redis/add" \
  -d "dsName=mydb1" \
  -d "redisHost=localhost" \
  -d "redisPort=6379"
```

4. **在数据源中添加用户**
```bash
curl -X POST "http://localhost:8087/api/datasource/mydb1/users" \
  -H "Content-Type: application/json" \
  -d '{"name":"张三", "email":"zhangsan@example.com"}'
```

5. **从数据源中查询用户**
```bash
curl -X GET "http://localhost:8087/api/datasource/mydb1/users"
```

6. **在Redis中设置值**
```bash
curl -X POST "http://localhost:8087/api/datasource/mydb1/redis/set?key=test&value=hello"
```

7. **从Redis中获取值**
```bash
curl -X GET "http://localhost:8087/api/datasource/mydb1/redis/get?key=test"
```

### 2. 批量操作示例

1. **向所有数据源添加用户**
```bash
curl -X POST "http://localhost:8087/api/datasource/all/users" \
  -H "Content-Type: application/json" \
  -d '{"name":"全局用户", "email":"all@example.com"}'
```

2. **从所有数据源查询用户**
```bash
curl -X GET "http://localhost:8087/api/datasource/all/users"
```

## 配置文件说明

### application.yml
```yaml
server:
  port: 8087  # 应用端口

spring:
  datasource:
    druid:
      # 预定义数据源配置
      one:
        url: jdbc:h2:mem:db1
        username: sa
        password: 
        driver-class-name: org.h2.Driver
        initial-size: 5
        min-idle: 5
        max-active: 20
        max-wait: 60000
      # 可以继续添加更多预定义数据源...
      
logging:
  level:
    com.example.multi.datasource.demo: debug  # 日志级别
```

## 最佳实践

### 1. 线程安全
- 使用`ThreadLocal`确保数据源切换的线程安全性
- 动态添加/删除数据源的方法使用`synchronized`关键字

### 2. 资源管理
- Redis连接工厂在移除时会正确关闭
- 数据源连接池参数可针对每个数据源独立配置

### 3. 异常处理
- 所有API接口都有完善的异常处理机制
- 提供清晰的错误信息返回

### 4. 性能优化
- 使用ConcurrentHashMap存储动态资源
- Redis缓存减少数据库访问压力
- 连接池参数可根据实际需求调整

## 注意事项

1. **默认数据源保护**：不能删除默认数据源(dataSource1)
2. **唯一性约束**：不允许添加同名数据源
3. **存在性检查**：切换数据源前需确保数据源已存在
4. **线程隔离**：数据源操作具有线程局部性，不影响其他请求
5. **Redis依赖**：使用Redis功能需要确保Redis服务正常运行

## 测试

项目包含完整的测试用例：

### 单元测试
- `DynamicDataSourceTest`：测试动态数据源基本功能
- `UserServiceTest`：测试用户服务功能
- `UnifiedDataSourceControllerTest`：测试控制器功能

### 集成测试
- `DynamicDataSourceIntegrationTest`：完整的集成测试

运行测试：
```bash
mvn test
```

## 扩展建议

1. **监控告警**：添加数据源健康检查和监控指标
2. **连接池优化**：根据不同业务场景优化连接池参数
3. **安全控制**：添加数据源操作的权限控制
4. **配置中心**：集成配置中心实现动态配置更新
5. **分布式支持**：在微服务架构中实现分布式数据源管理