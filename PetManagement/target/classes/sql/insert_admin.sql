-- 插入管理员数据
USE pet_manage;

-- 插入管理员账号（密码为 admin123，使用 MD5 加密）
INSERT INTO admin (admin_name, password, real_name, phone) VALUES
('admin', '0192023a7bbd73250516f069df18b500', '系统管理员', '13800000000'),
('admin2', '0192023a7bbd73250516f069df18b500', '管理员2', '13800000001');
