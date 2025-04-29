CREATE ROLE IF NOT EXISTS employee_role;
CREATE ROLE IF NOT EXISTS trainer_role;
CREATE ROLE IF NOT EXISTS manager_role;
CREATE ROLE IF NOT EXISTS director_role;

--employee
GRANT SELECT ON *.* TO employee_role;

--trainer
GRANT SELECT ON *.* TO trainer_role;
GRANT INSERT, UPDATE, DELETE ON Train TO trainer_role;

--manager
GRANT SELECT, INSERT, UPDATE, DELETE ON *.* TO manager_role;
INSERT, UPDATE, DELETE ON Schedule FROM manager_role;
INSERT, UPDATE, DELETE ON Manages FROM manager_role;
INSERT, UPDATE, DELETE ON TrainedIn FROM manager_role;

--director
GRANT SELECT, INSERT, UPDATE, DELETE ON *.* TO director_role;
INSERT, UPDATE, DELETE ON Schedule FROM director_role;
INSERT, UPDATE, DELETE ON Train FROM director_role;
INSERT, UPDATE, DELETE ON ProductionStation FROM director_role;
INSERT, UPDATE, DELETE ON Employee FROM director_role;
INSERT, UPDATE, DELETE ON Manages FROM director_role;
INSERT, UPDATE, DELETE ON TrainedIn FROM director_role;
INSERT, UPDATE, DELETE ON Restaurant FROM director_role;


--create users
CREATE USER IF NOT EXISTS 'employee_user'@'%' IDENTIFIED BY '123';
GRANT employee_role TO 'employee_user'@'%';

CREATE USER IF NOT EXISTS 'trainer_user'@'%' IDENTIFIED BY '12345';
GRANT trainer_role TO 'trainer_user'@'%';

CREATE USER IF NOT EXISTS 'manager_user'@'%' IDENTIFIED BY '123456';
GRANT manager_role TO 'manager_user'@'%';

CREATE USER IF NOT EXISTS 'director_user'@'%' IDENTIFIED BY '123456789';
GRANT director_role TO 'director_user'@'%';

--remove online root login
DELETE FROM mysql.user WHERE User='root' AND Host NOT IN ('localhost', '127.0.0.1', '::1');
FLUSH PRIVILEGES;