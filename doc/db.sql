-- 数据库

-- 初始数据 
INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('1', '0', '系统管理', NULL, NULL, '0', 'fa fa-cog', '0');
INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('2', '1', '管理员列表', 'sys/user.html', NULL, '1', 'fa fa-user', '1');
INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('3', '1', '角色管理', 'sys/role.html', NULL, '1', 'fa fa-user-secret', '2');
INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('4', '1', '菜单管理', 'sys/menu.html', NULL, '1', 'fa fa-th-list', '3');
INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('5', '1', 'SQL监控', 'druid/sql.html', NULL, '1', 'fa fa-bug', '4');
-- INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('6', '1', '定时任务', 'sys/schedule.html', NULL, '1', 'fa fa-tasks', '5');
-- INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('7', '6', '查看', NULL, 'sys:schedule:list,sys:schedule:info', '2', NULL, '0');
-- INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('8', '6', '新增', NULL, 'sys:schedule:save', '2', NULL, '0');
-- INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('9', '6', '修改', NULL, 'sys:schedule:update', '2', NULL, '0');
-- INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('10', '6', '删除', NULL, 'sys:schedule:delete', '2', NULL, '0');
-- INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('11', '6', '暂停', NULL, 'sys:schedule:pause', '2', NULL, '0');
-- INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('12', '6', '恢复', NULL, 'sys:schedule:resume', '2', NULL, '0');
-- INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('13', '6', '立即执行', NULL, 'sys:schedule:run', '2', NULL, '0');
-- INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('14', '6', '日志列表', NULL, 'sys:schedule:log', '2', NULL, '0');
INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('15', '2', '查看', NULL, 'sys:user:list,sys:user:info', '2', NULL, '0');
INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('16', '2', '新增', NULL, 'sys:user:save,sys:role:select', '2', NULL, '0');
INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('17', '2', '修改', NULL, 'sys:user:update,sys:role:select', '2', NULL, '0');
INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('18', '2', '删除', NULL, 'sys:user:delete', '2', NULL, '0');
INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('19', '3', '查看', NULL, 'sys:role:list,sys:role:info', '2', NULL, '0');
INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('20', '3', '新增', NULL, 'sys:role:save,sys:menu:perms', '2', NULL, '0');
INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('21', '3', '修改', NULL, 'sys:role:update,sys:menu:perms', '2', NULL, '0');
INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('22', '3', '删除', NULL, 'sys:role:delete', '2', NULL, '0');
INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('23', '4', '查看', NULL, 'sys:menu:list,sys:menu:info', '2', NULL, '0');
INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('24', '4', '新增', NULL, 'sys:menu:save,sys:menu:select', '2', NULL, '0');
INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('25', '4', '修改', NULL, 'sys:menu:update,sys:menu:select', '2', NULL, '0');
INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('26', '4', '删除', NULL, 'sys:menu:delete', '2', NULL, '0');
-- INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('27', '1', '参数管理', 'sys/config.html', 'sys:config:list,sys:config:info,sys:config:save,sys:config:update,sys:config:delete', '1', 'fa fa-sun-o', '6');
-- INSERT INTO `tb_menu` (`id`, `parent_id`, `menu_name`, `menu_url`, `perms`, `menu_type`, `icon`, `seq`) VALUES ('28', '1', '代码生成器', 'sys/generator.html', 'sys:generator:list,sys:generator:code', '1', 'fa fa-rocket', '7');
                                        
SELECT * FROM tb_menu; 

-- truncate table tb_menu; 
