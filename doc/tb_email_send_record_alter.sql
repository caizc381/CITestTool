--  邮件发送记录表增加字段
alter table `tb_email_send_excel_record` add column `subject` varchar(255) DEFAULT NULL COMMENT '邮件标题';
alter table `tb_email_send_excel_record` add column `content` varchar(1023) DEFAULT NULL COMMENT '邮件内容';
alter table `tb_email_send_excel_record` add column `sender` varchar(520) DEFAULT NULL COMMENT '发送人';
alter table `tb_email_send_excel_record` add column `receiver` varchar(1023) DEFAULT NULL COMMENT '接收人';
alter table `tb_email_send_excel_record` add column `from_user_name` varchar(127) DEFAULT NULL COMMENT '发送方显示名';
alter table `tb_email_send_excel_record` add column `notify_type` varchar(48) DEFAULT NULL COMMENT '邮件类型：EXPORT_ORDER_MESSAGE(1,"浅对接邮件通知");BOTTLENECK_EXAMITEM(2,"瓶颈项目通知"),GROUP_APPLICATION_MAILL(3,"团检申请邮件");MEAL_CHANGE_NOTIFY(4,"套餐修改通知");';
