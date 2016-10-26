BEGIN;
  CREATE TABLE `activity` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title` varchar(30) NOT NULL COMMENT '活动名称',
    `web_activity_url` varchar(150) NOT NULL COMMENT '活动目标地址',
    `app_activity_url` varchar(150) NOT NULL COMMENT 'APP活动备用地址',
    `description` varchar(15) NOT NULL COMMENT '活动介绍',
    `web_picture_url` varchar(150) NOT NULL COMMENT 'PC端活动图',
    `app_picture_url` varchar(150) NOT NULL COMMENT '移动端活动图',
    `activated_time` datetime DEFAULT NULL COMMENT '活动发布时间',
    `expired_time` datetime DEFAULT NULL,
    `source` varchar(50) NOT NULL COMMENT '活动来源渠道',
    `status` varchar(20) NOT NULL COMMENT '活动状态',
    `created_by` varchar(25) NOT NULL COMMENT '活动创建人',
    `created_time` datetime NOT NULL COMMENT '活动创建时间',
    `updated_by` varchar(25) NOT NULL COMMENT '活动更新人',
    `updated_time` datetime NOT NULL COMMENT '活动更新时间',
    `activated_by` varchar(25) DEFAULT NULL COMMENT '活动审核人',
    `share_title` varchar(50) NOT NULL,
    `share_content` varchar(100) DEFAULT NULL,
    `share_url` varchar(100) NOT NULL,
    `seq` bigint(20) NOT NULL,
    `long_term` tinyint(1) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_ACTIVITY_ACTIVATED_BY_REF_USER_LOGIN_NAME` (`activated_by`),
    KEY `FK_ACTIVITY_CREATED_BY_REF_USER_LOGIN_NAME` (`created_by`),
    KEY `FK_ACTIVITY_UPDATED_BY_REF_USER_LOGIN_NAME` (`updated_by`),
    CONSTRAINT `FK_ACTIVITY_ACTIVATED_BY_REF_USER_LOGIN_NAME` FOREIGN KEY (`activated_by`) REFERENCES `user` (`login_name`),
    CONSTRAINT `FK_ACTIVITY_CREATED_BY_REF_USER_LOGIN_NAME` FOREIGN KEY (`created_by`) REFERENCES `user` (`login_name`),
    CONSTRAINT `FK_ACTIVITY_UPDATED_BY_REF_USER_LOGIN_NAME` FOREIGN KEY (`updated_by`) REFERENCES `user` (`login_name`)
  ) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8 COMMENT='活动中心活动';

COMMIT;