# Multi-Datasource Demo

一个用于演示在 Spring Boot 中配置和使用多个数据源的示例项目。

## 项目概述

本项目演示了在 Spring Boot 应用中实现动态数据源管理的功能。项目完全基于动态数据源配置，支持在运行时动态添加、删除和切换数据源，无需修改代码或重启应用。同时，项目还支持为每个数据源配置对应的Redis集群，实现数据缓存功能。

## 核心功能

### 动态数据源管理
- **动态添加数据源**：可在运行时通过API添加新的数据源
- **动态删除数据源**：可删除不再需要的数据源
- **动态切换数据源**：可在不同数据源之间自由切换
- **数据源列表查询**：可查看当前所有可用的数据源

### Redis集群管理
- **动态添加Redis集群**：可为每个数据源动态配置对应的Redis集群
- **动态删除Redis集群**：可删除数据源对应的Redis集群配置
- **Redis集群列表查询**：可查看当前所有已配置的Redis集群

### 数据操作功能
- **动态数据源操作**：支持在任意动态添加的数据源上进行数据操作
- **批量数据源操作**：支持向所有数据源同时添加数据或从所有数据源查询数据
- **Redis缓存操作**：支持在对应数据源的Redis集群中进行缓存操作

### 表管理功能
- **动态创建表**：可在指定数据源中创建表
- **动态删除表**：可在指定数据源中删除表
- **表列表查询**：可查看指定数据源中的所有表

## 技术架构

- **后端框架**：Spring Boot 2.7.0
- **数据库**：H2（运行时）
- **连接池**：Alibaba Druid 1.2.8
- **持久层框架**：Spring Data JPA
- **缓存**：Spring Data Redis
- **Redis客户端**：Lettuce

## 快速开始

### 环境要求
- JDK 1.8 或更高版本
- Maven 3.x
- Redis服务（可选，用于Redis集群功能）

### 构建和运行

```bash
# 克隆项目
git clone <repository-url>

# 进入项目目录
cd multi-datasource-demo

# 构建项目
mvn clean package

# 运行项目
mvn spring-boot:run
```

### 访问前端界面

项目运行后，可以通过以下URL访问前端测试界面：
```
http://localhost:8087
```

前端界面提供了完整的图形化操作界面，可以方便地测试所有API功能。

## API 接口

项目提供以下API接口：

### 动态数据源管理接口
- `POST /api/datasource/add` - 添加数据源
- `DELETE /api/datasource/remove` - 删除数据源
- `POST /api/datasource/switch` - 切换数据源
- `GET /api/datasource/list` - 查询所有数据源

### Redis集群管理接口
- `POST /api/datasource/redis/add` - 为数据源添加Redis集群配置
- `DELETE /api/datasource/redis/remove` - 删除数据源的Redis集群配置
- `GET /api/datasource/redis/list` - 查询所有Redis集群配置

### 数据操作接口
- `POST /api/datasource/{dsName}/users` - 在指定数据源中添加用户
- `GET /api/datasource/{dsName}/users` - 从指定数据源中查询所有用户
- `GET /api/datasource/{dsName}/users/{id}` - 从指定数据源中根据ID查询用户
- `PUT /api/datasource/{dsName}/users/{id}` - 在指定数据源中更新用户
- `DELETE /api/datasource/{dsName}/users/{id}` - 在指定数据源中删除用户

### Redis操作接口
- `POST /api/datasource/{dsName}/redis/set` - 在指定数据源的Redis中设置键值对
- `GET /api/datasource/{dsName}/redis/get` - 从指定数据源的Redis中获取值

### 批量数据操作接口
- `POST /api/datasource/all/users` - 向所有数据源中添加用户
- `GET /api/datasource/all/users` - 从所有数据源中查询所有用户

### 简化数据操作接口
- `POST /users/dynamic/{dsName}` - 在指定数据源中添加用户
- `GET /users/dynamic/{dsName}` - 从指定数据源中查询所有用户
- `POST /users/all` - 向所有数据源中添加用户
- `GET /users/all` - 从所有数据源中查询所有用户

### 表管理接口
- `POST /api/table/{dsName}/create` - 在指定数据源中创建表
- `DELETE /api/table/{dsName}/drop` - 在指定数据源中删除表
- `GET /api/table/{dsName}/list` - 查询指定数据源中的所有表

详细接口说明请参考 [DATASOURCE_GUIDE.md](DATASOURCE_GUIDE.md) 和 [MULTI_DATASOURCE_DETAILED_GUIDE.md](MULTI_DATASOURCE_DETAILED_GUIDE.md) 文件。

## 测试

项目包含完整的测试用例，可通过以下命令运行：

```bash
mvn test
```

HTTP接口测试可使用 [datasource-tests.http](datasource-tests.http) 文件。

### 单元测试
- `DynamicDataSourceTest` - 测试动态数据源基本功能
- `UserServiceTest` - 测试用户服务功能
- `UnifiedDataSourceControllerTest` - 测试统一数据源控制器
- `DynamicDataSourceIntegrationTest` - 集成测试完整功能

### 集成测试
- `TableManagementControllerTest` - 测试表管理功能

## 许可证

本项目基于 MIT 许可证开源，详细信息请查看 [LICENSE](LICENSE) 文件（如果存在）。