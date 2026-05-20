-- 领养申请表新增字段迁移脚本
-- 执行时间：2026-05-17

ALTER TABLE adopt_apply
  ADD COLUMN applicant_name VARCHAR(20) COMMENT '申请人姓名' AFTER apply_info,
  ADD COLUMN applicant_phone VARCHAR(11) COMMENT '申请人联系方式' AFTER applicant_name,
  ADD COLUMN applicant_occupation VARCHAR(50) COMMENT '申请人职业' AFTER applicant_phone,
  ADD COLUMN applicant_address VARCHAR(255) COMMENT '申请人家庭地址' AFTER applicant_occupation,
  ADD COLUMN applicant_experience VARCHAR(500) COMMENT '申请人养宠经验' AFTER applicant_address;
