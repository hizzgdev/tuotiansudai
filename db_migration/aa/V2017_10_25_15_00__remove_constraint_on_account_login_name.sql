BEGIN;

alter table account drop FOREIGN KEY FK_ACCOUNT_LOGIN_NAME_REF_USER_LOGIN_NAME;

COMMIT;