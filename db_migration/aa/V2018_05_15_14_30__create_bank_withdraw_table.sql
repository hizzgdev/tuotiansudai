CREATE TABLE `aa`.`bank_withdraw` (
  `id`              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `login_name`      VARCHAR(25)     NOT NULL,
  `amount`          BIGINT UNSIGNED NOT NULL,
  `fee`             BIGINT UNSIGNED NOT NULL,
  `source`          VARCHAR(20)     NOT NULL,
  `bank_name`       VARCHAR(256),
  `bank_code`       VARCHAR(50),
  `card_number`     VARCHAR(256),
  `bank_order_no`   VARCHAR(20),
  `bank_order_date` VARCHAR(8),
  `status`          VARCHAR(16)     NOT NULL,
  `created_time`    DATETIME        NOT NULL,
  `updated_time`    DATETIME        NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT FK_BANK_WITHDRAW_LOGIN_NAME_REF_USER_LOGIN_NAME FOREIGN KEY (`login_name`) REFERENCES `aa`.`user` (`login_name`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;