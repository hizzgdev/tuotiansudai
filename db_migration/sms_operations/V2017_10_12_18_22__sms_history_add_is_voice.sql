ALTER TABLE `sms_operations`.`sms_history` ADD COLUMN `is_voice` BOOLEAN NOT NULL DEFAULT 0 AFTER `success`;