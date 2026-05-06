
# 宠物管理平台 - 快速启动指南

## 前置要求

1. JDK 1.8 或更高版本
2. Maven 3.6 或更高版本
3. MySQL 5.7 或更高版本

## 启动步骤

### 第一步：数据库准备

1. 登录MySQL数据库
```bash
mysql -u root -p
```

2. 执行初始化脚本
```bash
mysql -u root -p < src/main/resources/sql/init.sql
```

或者手动执行以下SQL：
```sql
CREATE DATABASE IF NOT EXISTS pet_manage DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE pet_manage;

CREATE TABLE IF NOT EXISTS `pet` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '宠物名称',
  `type` varchar(20) DEFAULT NULL COMMENT '宠物类型：狗、猫、鸟等',
  `breed` varchar(50) DEFAULT NULL COMMENT '品种',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `gender` varchar(10) DEFAULT NULL COMMENT '性别：公、母',
  `color` varchar(50) DEFAULT NULL COMMENT '毛色',
  `weight` decimal(5,2) DEFAULT NULL COMMENT '体重(kg)',
  `owner_name` varchar(50) NOT NULL COMMENT '主人姓名',
  `owner_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `health_status` varchar(20) DEFAULT NULL COMMENT '健康状态：健康、生病、康复中等',
  `notes` text COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物信息表';

INSERT INTO `pet` (`name`, `type`, `breed`, `age`, `gender`, `color`, `weight`, `owner_name`, `owner_phone`, `health_status`, `notes`) VALUES
('小白', '狗', '金毛', 2, '公', '金色', 28.5, '张三', '13800138001', '健康', '性格温顺，喜欢玩球'),
('咪咪', '猫', '英短', 1, '母', '灰色', 4.2, '李四', '13800138002', '健康', '喜欢睡觉，不爱动'),
('小黑', '狗', '拉布拉多', 3, '公', '黑色', 30.0, '王五', '13800138003', '康复中', '前腿受伤，正在康复'),
('花花', '猫', '波斯猫', 2, '母', '白色', 3.8, '赵六', '13800138004', '健康', '毛发需要定期护理'),
('球球', '仓鼠', '金丝熊', 1, '公', '棕色', 0.15, '钱七', '13800138005', '健康', '晚上比较活跃');
```

### 第二步：修改数据库配置

打开 `src/resources/application.yml` 文件，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pet_manage
    username: root        # 修改为你的MySQL用户名
    password: 123456      # 修改为你的MySQL密码
```

### 第三步：编译项目

在项目根目录下执行：

```bash
mvn clean package
```

### 第四步：启动应用

方式一：使用Maven启动
```bash
mvn spring-boot:run
```

方式二：使用Java命令启动
```bash
java -jar target/pet-manage-platform-1.0.0.jar
```

方式三：在IDE中运行
找到 `PetManagePlatformApplication.java` 文件，右键选择 "Run"

### 第五步：访问应用

打开浏览器，访问：
```
http://localhost:8080/pet-api/
```

## 功能使用

1. **首页**: 查看平台功能概览
2. **宠物列表**: 查看所有宠物信息，支持搜索和分页
3. **添加宠物**: 点击"添加宠物"按钮，填写表单
4. **编辑宠物**: 在列表中点击编辑按钮
5. **删除宠物**: 在列表中点击删除按钮

## 常见问题

### 1. 数据库连接失败

请检查：
- MySQL服务是否启动
- 数据库用户名密码是否正确
- 数据库是否已创建

### 2. 端口被占用

如果8080端口被占用，可以在 `application.yml` 中修改端口：

```yaml
server:
  port: 8081  # 修改为其他端口
```

### 3. 页面无法访问

请检查：
- 应用是否成功启动
- 访问路径是否正确（需要加上 `/pet-api` 前缀）

## 技术支持

如有问题，请查看 README.md 文件或联系开发人员。
