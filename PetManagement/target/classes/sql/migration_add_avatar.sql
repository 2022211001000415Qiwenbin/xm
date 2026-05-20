
-- 用户表添加头像字段
ALTER TABLE user ADD COLUMN avatar VARCHAR(255) COMMENT '头像URL' AFTER pet_experience;
