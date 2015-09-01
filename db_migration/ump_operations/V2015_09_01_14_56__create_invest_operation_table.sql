CREATE TABLE `ump_operations`.`project_transfer_request` (
  `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `service`      VARCHAR(64)     NOT NULL,
  `sign_type`    VARCHAR(16)      NOT NULL,
  `sign`         VARCHAR(512)    NOT NULL,
  `charset`      VARCHAR(32)     NOT NULL,
  `mer_id`       VARCHAR(16)      NOT NULL,
  `version`      VARCHAR(6)      NOT NULL,
  `ret_url`      VARCHAR(256)    NOT NULL,
  `notify_url`   VARCHAR(256)    NOT NULL,
  `order_id`     VARCHAR(64)     NOT NULL,
  `loan_id`      VARCHAR(16)      NOT NULL,
  `user_id`      VARCHAR(64)     NOT NULL,
  `amount`       VARCHAR(26)     NOT NULL,
  `request_time` DATETIME        NOT NULL,
  `request_url`  VARCHAR(4096)   NOT NULL,
  `request_data` TEXT            NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 100001
  DEFAULT CHARSET = utf8;