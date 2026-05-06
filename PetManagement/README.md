
# 宠物管理平台

一个基于Spring Boot的前后端不分离宠物管理系统，提供完整的宠物信息管理功能。

## 技术栈

- **后端框架**: Spring Boot 2.7.18
- **ORM框架**: MyBatis-Plus 3.5.3.1
- **数据库**: MySQL 5.7
- **前端**: HTML + Bootstrap 5.3 + JavaScript
- **模板引擎**: Thymeleaf (Spring Boot默认)

## 功能特性

- ✅ 宠物信息管理（增删改查）
- ✅ 分页查询功能
- ✅ 关键词搜索（支持名称、类型、品种、主人姓名）
- ✅ 健康状态标记
- ✅ 响应式界面设计
- ✅ 现代化UI界面

## 项目结构

```
PetManagement/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/qwb/petmanage/
│   │   │       ├── config/          # 配置类
│   │   │       ├── controller/      # 控制器
│   │   │       ├── entity/          # 实体类
│   │   │       ├── mapper/          # 数据访问层
│   │   │       ├── service/         # 服务层
│   │   │       └── common/          # 公共类
│   │   └── resources/
│   │       ├── templates/           # 前端页面
│   │       ├── application.yml      # 应用配置
│   │       └── sql/                 # 数据库脚本
└── pom.xml                          # Maven配置
```

## 快速开始

### 1. 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+

### 2. 数据库配置

1. 创建数据库并执行初始化脚本：
```sql
-- 执行 src/main/resources/sql/init.sql
```

2. 修改 `application.yml` 中的数据库配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pet_manage
    username: root
    password: your_password  # 修改为你的密码
```

### 3. 运行项目

1. 使用Maven编译项目：
```bash
mvn clean package
```

2. 运行主类 `PetManagePlatformApplication`

3. 访问应用：
```
http://localhost:8080/pet-api/
```

## 功能说明

### 宠物管理

- **列表查看**: 查看所有宠物信息，支持分页和搜索
- **添加宠物**: 填写宠物详细信息
- **编辑信息**: 修改宠物信息
- **删除宠物**: 删除不需要的记录

### 宠物信息字段

- 基本信息：名称、类型、品种、年龄、性别、毛色、体重
- 主人信息：主人姓名、联系电话
- 健康信息：健康状态、备注

## 开发说明

### 后端API

所有API路径前缀为 `/pet-api`

#### 宠物管理接口

- `GET /pet/list` - 宠物列表页面
- `GET /pet/form` - 宠物表单页面
- `GET /pet/page` - 分页查询宠物列表
- `POST /pet/add` - 添加宠物
- `POST /pet/update` - 更新宠物信息
- `POST /pet/delete/{id}` - 删除宠物
- `GET /pet/detail/{id}` - 获取宠物详情

## 注意事项

1. 首次运行前请确保数据库已正确配置
2. 修改数据库密码后请同步更新 `application.yml`
3. 建议使用Chrome或Edge浏览器以获得最佳体验

## 许可证

MIT License
