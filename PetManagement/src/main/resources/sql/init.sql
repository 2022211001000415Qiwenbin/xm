-- 宠物管理平台数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS pet_manage DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE pet_manage;

-- 1.宠物品种表（基础字典表）
CREATE TABLE IF NOT EXISTS pet_breed (
  breed_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '品种ID',
  breed_name VARCHAR(50) NOT NULL UNIQUE COMMENT '品种名称（如金毛、布偶猫）',
  breed_type VARCHAR(20) NOT NULL COMMENT '宠物类型（猫/狗/其他）',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物品种表';

-- 2.管理员表
CREATE TABLE IF NOT EXISTS admin (
  admin_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
  admin_name VARCHAR(30) NOT NULL UNIQUE COMMENT '管理员账号',
  password VARCHAR(64) NOT NULL COMMENT '密码（MD5加密）',
  real_name VARCHAR(20) COMMENT '真实姓名',
  phone VARCHAR(11) COMMENT '联系电话',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 初始化管理员账号：admin 密码：123456（MD5加密后：e10adc3949ba59abbe56e057f20f883e）
INSERT INTO admin (admin_name, password, real_name) VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员');

-- 3.用户表（领养人）
CREATE TABLE IF NOT EXISTS user (
  user_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(30) NOT NULL UNIQUE COMMENT '用户账号',
  password VARCHAR(64) NOT NULL COMMENT '密码（MD5加密）',
  real_name VARCHAR(20) NOT NULL COMMENT '真实姓名',
  id_card VARCHAR(18) NOT NULL UNIQUE COMMENT '身份证号',
  phone VARCHAR(11) NOT NULL COMMENT '联系电话',
  address VARCHAR(255) COMMENT '居住地址',
  pet_experience VARCHAR(255) COMMENT '养宠经历',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表（领养人）';

-- 4.弃养/待领养宠物信息表
CREATE TABLE IF NOT EXISTS pet (
  pet_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '宠物ID',
  breed_id INT NOT NULL COMMENT '品种ID',
  pet_name VARCHAR(50) COMMENT '宠物名字',
  age INT COMMENT '年龄（月）',
  gender VARCHAR(10) NOT NULL COMMENT '性别（公/母/未知）',
  health_status VARCHAR(20) NOT NULL COMMENT '健康状况（良好/一般/患病）',
  abandon_reason VARCHAR(255) COMMENT '弃养原因',
  rescue_address VARCHAR(255) COMMENT '救助地点',
  pet_photo VARCHAR(255) COMMENT '宠物照片URL',
  adopt_status VARCHAR(20) NOT NULL DEFAULT '待领养' COMMENT '领养状态（待领养/已领养/已预约）',
  remark VARCHAR(255) COMMENT '备注',
  create_admin INT COMMENT '登记管理员ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '登记时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (breed_id) REFERENCES pet_breed(breed_id),
  FOREIGN KEY (create_admin) REFERENCES admin(admin_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物信息表';

-- 5.领养申请表
CREATE TABLE IF NOT EXISTS adopt_apply (
  apply_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
  user_id INT NOT NULL COMMENT '申请人ID',
  pet_id INT NOT NULL COMMENT '申请宠物ID',
  apply_info VARCHAR(255) COMMENT '申请说明',
  audit_status VARCHAR(20) NOT NULL DEFAULT '待审核' COMMENT '审核状态（待审核/通过/拒绝）',
  audit_remark VARCHAR(255) COMMENT '审核备注',
  audit_admin INT COMMENT '审核管理员ID',
  apply_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  audit_time DATETIME COMMENT '审核时间',
  FOREIGN KEY (user_id) REFERENCES user(user_id),
  FOREIGN KEY (pet_id) REFERENCES pet(pet_id),
  FOREIGN KEY (audit_admin) REFERENCES admin(admin_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='领养申请表';

-- 6.线下看宠预约表
CREATE TABLE IF NOT EXISTS pet_reserve (
  reserve_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '预约ID',
  user_id INT NOT NULL COMMENT '预约人ID',
  pet_id INT NOT NULL COMMENT '预约宠物ID',
  reserve_time DATETIME NOT NULL COMMENT '预约时间',
  reserve_address VARCHAR(255) NOT NULL COMMENT '预约地点',
  contact_person VARCHAR(20) NOT NULL COMMENT '联系人',
  contact_phone VARCHAR(11) NOT NULL COMMENT '联系电话',
  reserve_status VARCHAR(20) NOT NULL DEFAULT '待确认' COMMENT '预约状态（待确认/已确认/已取消/已完成）',
  confirm_admin INT COMMENT '确认管理员ID',
  reserve_remark VARCHAR(255) COMMENT '预约备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '预约创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (user_id) REFERENCES user(user_id),
  FOREIGN KEY (pet_id) REFERENCES pet(pet_id),
  FOREIGN KEY (confirm_admin) REFERENCES admin(admin_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='线下看宠预约表';

-- 初始化测试数据
-- 插入宠物品种数据
INSERT INTO pet_breed (breed_name, breed_type) VALUES
('金毛', '狗'),
('拉布拉多', '狗'),
('中华田园犬', '狗'),
('英短', '猫'),
('波斯猫', '猫'),
('三花猫', '猫'),
('布偶猫', '猫'),
('仓鼠', '其他');

-- 插入宠物数据
INSERT INTO pet (breed_id, pet_name, age, gender, health_status, abandon_reason, rescue_address, adopt_status) VALUES
(1, '小白', 24, '公', '良好', '主人搬家无法带走', '北京市朝阳区', '待领养'),
(2, '小黑', 36, '公', '良好', '工作调动', '上海市浦东新区', '待领养'),
(3, '大黄', 24, '公', '一般', '家庭原因', '广州市天河区', '待领养'),
(4, '咪咪', 12, '母', '良好', '过敏', '深圳市南山区', '待领养'),
(5, '小花', 12, '母', '良好', '搬家', '杭州市西湖区', '待领养');

-- 插入用户数据
INSERT INTO user (username, password, real_name, id_card, phone, address, pet_experience) VALUES
('user1', 'e10adc3949ba59abbe56e057f20f883e', '张三', '110101199001011234', '13800138001', '北京市朝阳区', '养过2只狗'),
('user2', 'e10adc3949ba59abbe56e057f20f883e', '李四', '110101199002021234', '13800138002', '上海市浦东新区', '养过1只猫');

-- 插入领养申请数据
INSERT INTO adopt_apply (user_id, pet_id, apply_info, audit_status) VALUES
(1, 1, '希望能领养这只可爱的小白', '待审核'),
(2, 2, '已经准备好领养用品', '待审核');

-- 插入预约数据
INSERT INTO pet_reserve (user_id, pet_id, reserve_time, reserve_address, contact_person, contact_phone, reserve_status) VALUES
(1, 1, '2024-03-20 10:00:00', '北京市朝阳区宠物救助中心', '张三', '13800138001', '待确认'),
(2, 2, '2024-03-21 14:00:00', '上海市浦东新区宠物救助中心', '李四', '13800138002', '待确认');