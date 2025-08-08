# 动态数据源功能说明

## 项目概述

本项目演示了在Spring Boot应用中实现动态数据源管理的功能。项目完全基于动态数据源配置，支持在运行时动态添加、删除和切换数据源，无需修改代码或重启应用。

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

### Redis集群管理接口

#### 1. 为数据源添加Redis集群配置
```
POST /api/datasource/redis/add
```

**请求参数：**
- `dsName` (String, 必填) - 数据源名称
- `redisHost` (String, 必填) - Redis主机地址
- `redisPort` (int, 必填) - Redis端口

**响应示例：**
```json
{
  "success": true,
  "message": "为数据源 db2 添加Redis集群配置成功"
}
```

#### 2. 删除数据源的Redis集群配置
```
DELETE /api/datasource/redis/remove
```

**请求参数：**
- `dsName` (String, 必填) - 数据源名称

**响应示例：**
```json
{
  "success": true,
  "message": "删除数据源 db2 的Redis集群配置成功"
}
```

#### 3. 查询Redis集群列表
```
GET /api/datasource/redis/list
```

**响应示例：**
```json
{
  "success": true,
  "redisClusters": ["db2", "db3"]
}
```

### 数据操作接口

#### 1. 动态数据源操作（统一接口）
- `POST /api/datasource/{dsName}/users` - 在指定动态数据源中添加用户
- `GET /api/datasource/{dsName}/users` - 从指定动态数据源中查询所有用户
- `GET /api/datasource/{dsName}/users/{id}` - 从指定动态数据源中根据ID查询用户
- `PUT /api/datasource/{dsName}/users/{id}` - 在指定动态数据源中更新用户
- `DELETE /api/datasource/{dsName}/users/{id}` - 在指定动态数据源中删除用户

#### 2. Redis操作接口
- `POST /api/datasource/{dsName}/redis/set` - 在指定数据源的Redis中设置键值对
- `GET /api/datasource/{dsName}/redis/get` - 从指定数据源的Redis中获取值

#### 3. 所有数据源操作（批量接口）
- `POST /api/datasource/all/users` - 向所有数据源中添加用户
- `GET /api/datasource/all/users` - 从所有数据源中查询所有用户

#### 4. 简化数据操作接口
- `POST /users/dynamic/{dsName}` - 在指定动态数据源中添加用户
- `GET /users/dynamic/{dsName}` - 从指定动态数据源中查询所有用户
- `POST /users/all` - 向所有数据源中添加用户
- `GET /users/all` - 从所有数据源中查询所有用户

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

2. **为数据源添加Redis集群配置**
```bash
curl -X POST "http://localhost:8081/api/datasource/redis/add" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "dsName=mydb" \
  -d "redisHost=localhost" \
  -d "redisPort=6379"
```

3. **在新数据源中添加数据**
```bash
curl -X POST "http://localhost:8081/api/datasource/mydb/users" \
  -H "Content-Type: application/json" \
  -d '{"name":"Tom", "email":"tom@example.com"}'
```

4. **在新数据源的Redis中设置值**
```bash
curl -X POST "http://localhost:8081/api/datasource/mydb/redis/set" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "key=username" \
  -d "value=Tom"
```

5. **从新数据源的Redis中获取值**
```bash
curl -X GET "http://localhost:8081/api/datasource/mydb/redis/get?key=username"
```

6. **从新数据源中查询数据**
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

### 3. 向所有数据源添加数据

1. **向所有数据源添加用户**
```bash
curl -X POST "http://localhost:8081/api/datasource/all/users" \
  -H "Content-Type: application/json" \
  -d '{"name":"All DS User", "email":"all@example.com"}'
```

2. **从所有数据源查询用户**
```bash
curl -X GET "http://localhost:8081/api/datasource/all/users"
```

### 4. 删除数据源

```bash
curl -X DELETE "http://localhost:8081/api/datasource/remove?dsName=mydb"
```

## 技术实现要点

### 1. 动态数据源管理机制
- 使用 `DynamicDataSource` 类扩展 `AbstractRoutingDataSource`
- 通过 `ThreadLocal` 实现数据源切换的线程安全性
- 支持运行时动态添加和删除数据源

### 2. Redis集群管理机制
- 每个数据源可配置对应的Redis集群
- 使用 `LettuceConnectionFactory` 实现Redis连接
- 支持运行时动态添加和删除Redis集群配置

### 3. 数据源配置
- 使用 Druid 作为数据库连接池
- 支持通过API参数动态配置数据源连接属性
- 每个数据源可独立配置连接池参数

### 4. 缓存机制
- 用户数据自动缓存到对应数据源的Redis集群中
- 查询时优先从Redis缓存中获取数据
- 支持手动操作Redis进行缓存管理

### 5. 表管理机制
- 提供统一的表管理接口，支持在指定数据源中创建、删除和查询表
- 使用 JDBC 的 DatabaseMetaData 获取数据源中的表信息
- 支持执行原生SQL语句进行表结构操作

### 6. 线程安全
- 使用 `ThreadLocal` 确保数据源切换的线程安全性
- 每个请求可以在自己的线程上下文中切换数据源

## 优势特点

1. **完全动态**：支持在运行时动态添加、删除和切换任意数量的数据源
2. **批量操作**：支持向所有数据源同时添加数据或从所有数据源查询数据
3. **缓存支持**：每个数据源可配置对应的Redis集群，实现数据缓存
4. **无需代码修改**：添加或删除数据源时不需要修改任何代码
5. **灵活配置**：可以为每个数据源单独配置连接池参数
6. **易于测试**：提供完整的HTTP测试脚本和单元测试

## 注意事项

1. 不能删除默认数据源(dataSource1)
2. 不允许添加同名数据源
3. 切换数据源前需要确保数据源已存在
4. 数据源操作具有线程局部性，不会影响其他请求的数据源设置
5. 需要确保Redis服务正常运行才能使用Redis相关功能
6. 表操作需要具有相应权限，且表名需要符合数据库命名规范

## 测试方法

### 1. 单元测试
项目包含以下测试类：
- `DynamicDataSourceTest` - 测试动态数据源基本功能
- `UserServiceTest` - 测试用户服务功能
- `UnifiedDataSourceControllerTest` - 测试统一数据源控制器
- `DynamicDataSourceIntegrationTest` - 集成测试完整功能
- `TableManagementControllerTest` - 测试表管理功能

运行测试：
```bash
mvn test
```