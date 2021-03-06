CREATE TABLE `ump_operations`.`normal_repay_notify_request` (
  `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `service`        VARCHAR(64)     NOT NULL,
  `sign_type`      VARCHAR(16)     NOT NULL,
  `sign`           VARCHAR(512)    NOT NULL,
  `mer_id`         VARCHAR(16)     NOT NULL,
  `version`        VARCHAR(6)      NOT NULL,
  `order_id`       VARCHAR(64)     NOT NULL,
  `mer_date`       VARCHAR(16)     NOT NULL,
  `trade_no`       VARCHAR(32)     NOT NULL,
  `ret_code`       VARCHAR(16),
  `ret_msg`        VARCHAR(256),
  `request_time`   DATETIME        NOT NULL,
  `response_time`  DATETIME,
  `request_data`   TEXT            NOT NULL,
  `response_data`  TEXT,
  `status`         VARCHAR(32)      NOT NULL     DEFAULT 'NOT_DONE',
  PRIMARY KEY (`id`),
  KEY `INDEX_NORMAL_REPAY_NOTIFY_REQUEST_ORDER_ID` (`order_id`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 100001
  DEFAULT CHARSET = utf8;

  CREATE TABLE `ump_operations`.`advance_repay_notify_request` (
  `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `service`        VARCHAR(64)     NOT NULL,
  `sign_type`      VARCHAR(16)     NOT NULL,
  `sign`           VARCHAR(512)    NOT NULL,
  `mer_id`         VARCHAR(16)     NOT NULL,
  `version`        VARCHAR(6)      NOT NULL,
  `order_id`       VARCHAR(64)     NOT NULL,
  `mer_date`       VARCHAR(16)     NOT NULL,
  `trade_no`       VARCHAR(32)     NOT NULL,
  `ret_code`       VARCHAR(16),
  `ret_msg`        VARCHAR(256),
  `request_time`   DATETIME        NOT NULL,
  `response_time`  DATETIME,
  `request_data`   TEXT            NOT NULL,
  `response_data`  TEXT,
  `status`         VARCHAR(32)      NOT NULL     DEFAULT 'NOT_DONE',
  PRIMARY KEY (`id`),
  KEY `INDEX_ADVANCE_REPAY_NOTIFY_REQUEST_ORDER_ID` (`order_id`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 100001
  DEFAULT CHARSET = utf8;