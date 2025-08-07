# Multi-Datasource Demo

一个用于演示在 Spring Boot 中配置和使用多个数据源的示例项目。

## 项目概述

本项目演示了在 Spring Boot 应用中实现动态数据源管理的功能。与传统的静态多数据源配置不同，本项目支持在运行时动态添加、删除和切换数据源，无需修改代码或重启应用。

## 核心功能

### 1. 动态数据源管理
- **动态添加数据源**：可在运行时通过API添加新的数据源
- **动态删除数据源**：可删除不再需要的数据源
- **动态切换数据源**：可在不同数据源之间自由切换
- **数据源列表查询**：可查看当前所有可用的数据源

### 2. 数据操作功能
- **静态数据源操作**：保留原有的10个静态数据源操作接口
- **动态数据源操作**：支持在任意动态添加的数据源上进行数据操作
- **统一数据源操作**：通过索引方式操作预配置的静态数据源

## 技术架构

- **后端框架**：Spring Boot 2.7.0
- **数据库**：H2（运行时）
- **连接池**：Alibaba Druid 1.2.8
- **持久层框架**：Spring Data JPA

## 快速开始

### 环境要求
- JDK 1.8 或更高版本
- Maven 3.x

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

## API 接口

项目提供多种方式操作数据源：

1. **统一动态数据源管理接口**：`/api/datasource/*`
2. **静态数据源操作（通过索引）**：`/users/ds/{index}`
3. **静态数据源操作（原有接口）**：`/users/ds1` 到 `/users/ds10`

详细接口说明请参考 [DATASOURCE_GUIDE.md](DATASOURCE_GUIDE.md) 文件。

## 测试

项目包含完整的测试用例，可通过以下命令运行：

```bash
mvn test
```

HTTP接口测试可使用 [datasource-tests.http](datasource-tests.http) 文件。

## 许可证

本项目基于 MIT 许可证开源，详细信息请查看 [LICENSE](LICENSE) 文件（如果存在）。