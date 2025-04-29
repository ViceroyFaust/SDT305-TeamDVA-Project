CREATE ROLE IF NOT EXISTS employee_role;
CREATE ROLE IF NOT EXISTS trainer_role;
CREATE ROLE IF NOT EXISTS manager_role;
CREATE ROLE IF NOT EXISTS director_role;

-- employee
GRANT SELECT ON *.* TO employee_role;

-- trainer
GRANT SELECT ON *.* TO trainer_role;
GRANT INSERT, UPDATE, DELETE ON Train TO trainer_role;

-- manager
GRANT SELECT ON *.* TO manager_role;
GRANT INSERT, UPDATE, DELETE ON Schedule TO manager_role;
GRANT INSERT, UPDATE, DELETE ON Manages TO manager_role;
GRANT INSERT, UPDATE, DELETE ON TrainedIn TO manager_role;

-- director
GRANT SELECT ON *.* TO director_role;
GRANT INSERT, UPDATE, DELETE ON Schedule TO director_role;
GRANT INSERT, UPDATE, DELETE ON Train TO director_role;
GRANT INSERT, UPDATE, DELETE ON ProductionStation TO director_role;
GRANT INSERT, UPDATE, DELETE ON Employee TO director_role;
GRANT INSERT, UPDATE, DELETE ON Manages TO director_role;
GRANT INSERT, UPDATE, DELETE ON TrainedIn TO director_role;


-- create users
CREATE USER IF NOT EXISTS 'employee_user'@'%' IDENTIFIED BY '123';
GRANT employee_role TO 'employee_user'@'%';

CREATE USER IF NOT EXISTS 'trainer_user'@'%' IDENTIFIED BY '12345';
GRANT trainer_role TO 'trainer_user'@'%';

CREATE USER IF NOT EXISTS 'manager_user'@'%' IDENTIFIED BY '123456';
GRANT manager_role TO 'manager_user'@'%';

CREATE USER IF NOT EXISTS 'director_user'@'%' IDENTIFIED BY '123456789';
GRANT director_role TO 'director_user'@'%';

-- remove online root login
DELETE FROM mysql.user WHERE User='root' AND Host NOT IN ('localhost', '127.0.0.1', '::1');
FLUSH PRIVILEGES;