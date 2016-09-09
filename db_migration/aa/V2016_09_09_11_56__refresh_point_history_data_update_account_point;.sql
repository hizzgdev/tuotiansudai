BEGIN;
USE aa;
-- default point = 0
UPDATE `aa`.`account`
SET point = 0;
-- update
INSERT INTO tmp SELECT
                  login_name,
                  SUM(point)
                FROM point_bill
                GROUP BY login_name;

UPDATE `aa`.`account`, tmp
SET account.point = tmp.point
WHERE tmp.login_name = account.login_name;

DROP TABLE tmp;
COMMIT;