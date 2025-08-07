# 动态数据源功能说明

## 项目概述

本项目演示了在Spring Boot应用中实现动态数据源管理的功能。项目完全基于动态数据源配置，支持在运行时动态添加、删除和切换数据源，无需修改代码或重启应用。

## 核心功能

### 动态数据源管理
- **动态添加数据源**：可在运行时通过API添加新的数据源
- **动态删除数据源**：可删除不再需要的数据源
- **动态切换数据源**：可在不同数据源之间自由切换
- **数据源列表查询**：可查看当前所有可用的数据源

### 数据操作功能
- **动态数据源操作**：支持在任意动态添加的数据源上进行数据操作

## API 接口说明

### 数据源管理接口

#### 1. 添加数据源
```
POST /api/datasource/add
```

**请求参数：**
- `dsName` (String, 必填) - 数据源名称
- `url` (String, 必填) - 数据库连接URL
- `username` (String, 必填) - 数据库用户名
- `password` (String, 必填) - 数据库密码
- `driverClassName` (String, 必填) - 数据库驱动类名
- `initialSize` (int, 可选, 默认5) - 初始连接数
- `minIdle` (int, 可选, 默认5) - 最小空闲连接数
- `maxActive` (int, 可选, 默认20) - 最大活跃连接数
- `maxWait` (long, 可选, 默认60000) - 最大等待时间(ms)

**响应示例：**
```json
{
  "success": true,
  "message": "数据源 db2 添加成功"
}
```

#### 2. 删除数据源
```
DELETE /api/datasource/remove
```

**请求参数：**
- `dsName` (String, 必填) - 要删除的数据源名称

**响应示例：**
```json
{
  "success": true,
  "message": "数据源 db2 删除成功"
}
```

#### 3. 切换数据源
```
POST /api/datasource/switch
```

**请求参数：**
- `dsName` (String, 必填) - 要切换到的数据源名称

**响应示例：**
```json
{
  "success": true,
  "message": "成功切换到数据源: db2"
}
```

#### 4. 查询数据源列表
```
GET /api/datasource/list
```

**响应示例：**
```json
{
  "success": true,
  "dataSources": ["dataSource1", "db2", "db3"]
}
```

### 数据操作接口

#### 1. 动态数据源操作（统一接口）
- `POST /api/datasource/{dsName}/users` - 在指定动态数据源中添加用户
- `GET /api/datasource/{dsName}/users` - 从指定动态数据源中查询所有用户
- `GET /api/datasource/{dsName}/users/{id}` - 从指定动态数据源中根据ID查询用户
- `PUT /api/datasource/{dsName}/users/{id}` - 在指定动态数据源中更新用户
- `DELETE /api/datasource/{dsName}/users/{id}` - 在指定动态数据源中删除用户

#### 2. 简化数据操作接口
- `POST /users/dynamic/{dsName}` - 在指定动态数据源中添加用户
- `GET /users/dynamic/{dsName}` - 从指定动态数据源中查询所有用户

## 使用示例

### 1. 添加并使用新的数据源

1. **添加数据源**
```bash
curl -X POST "http://localhost:8081/api/datasource/add" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "dsName=mydb" \
  -d "url=jdbc:h2:mem:mydb" \
  -d "username=sa" \
  -d "password=" \
  -d "driverClassName=org.h2.Driver"
```

2. **在新数据源中添加数据**
```bash
curl -X POST "http://localhost:8081/api/datasource/mydb/users" \
  -H "Content-Type: application/json" \
  -d '{"name":"Tom", "email":"tom@example.com"}'
```

3. **从新数据源中查询数据**
```bash
curl -X GET "http://localhost:8081/api/datasource/mydb/users"
```

### 2. 使用简化接口操作数据

1. **在数据源中添加用户**
```bash
curl -X POST "http://localhost:8081/users/dynamic/mydb" \
  -H "Content-Type: application/json" \
  -d '{"name":"Jerry", "email":"jerry@example.com"}'
```

2. **从数据源中查询用户**
```bash
curl -X GET "http://localhost:8081/users/dynamic/mydb"
```

### 3. 删除数据源

```bash
curl -X DELETE "http://localhost:8081/api/datasource/remove?dsName=mydb"
```

## 技术实现要点

### 1. 动态数据源管理机制
- 使用 `DynamicDataSource` 类扩展 `AbstractRoutingDataSource`
- 通过 `ThreadLocal` 实现数据源切换的线程安全性
- 支持运行时动态添加和删除数据源

### 2. 数据源配置
- 使用 Druid 作为数据库连接池
- 支持通过API参数动态配置数据源连接属性
- 每个数据源可独立配置连接池参数

### 3. 线程安全
- 使用 `ThreadLocal` 确保数据源切换的线程安全性
- 每个请求可以在自己的线程上下文中切换数据源

## 优势特点

1. **完全动态**：支持在运行时动态添加、删除和切换任意数量的数据源
2. **无需代码修改**：添加或删除数据源时不需要修改任何代码
3. **灵活配置**：可以为每个数据源单独配置连接池参数
4. **易于测试**：提供完整的HTTP测试脚本和单元测试

## 注意事项

1. 不能删除默认数据源(dataSource1)
2. 不允许添加同名数据源
3. 切换数据源前需要确保数据源已存在
4. 数据源操作具有线程局部性，不会影响其他请求的数据源设置

## 测试方法

### 1. 单元测试
项目包含以下测试类：
- `DynamicDataSourceTest` - 测试动态数据源基本功能
- `UserServiceTest` - 测试用户服务功能
- `UnifiedDataSourceControllerTest` - 测试统一数据源控制器
- `DynamicDataSourceIntegrationTest` - 集成测试完整功能

运行测试：
```bash
mvn test
```

### 2. HTTP接口测试
可以使用项目中的 `datasource-tests.http` 文件进行测试，该文件包含了完整的测试用例。

### 3. 手动测试
使用curl或Postman等工具按照上述使用示例进行测试。