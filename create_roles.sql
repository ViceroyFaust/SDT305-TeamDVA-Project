-- create roles
CREATE ROLE IF NOT EXISTS employee_role;
CREATE ROLE IF NOT EXISTS trainer_role;
CREATE ROLE IF NOT EXISTS manager_role;
CREATE ROLE IF NOT EXISTS director_role;

-- employee
GRANT SELECT ON * TO employee_role;

-- trainer
GRANT SELECT ON * TO trainer_role;
GRANT INSERT, UPDATE, DELETE ON Train TO trainer_role;

-- manager
GRANT SELECT ON * TO manager_role;
GRANT INSERT, UPDATE, DELETE ON Schedule TO manager_role;
GRANT INSERT, UPDATE, DELETE ON Manages TO manager_role;
GRANT INSERT, UPDATE, DELETE ON TrainedIn TO manager_role;

-- director
GRANT SELECT ON * TO director_role;
GRANT INSERT, UPDATE, DELETE ON Schedule TO director_role;
GRANT INSERT, UPDATE, DELETE ON Train TO director_role;
GRANT INSERT, UPDATE, DELETE ON ProductionStation TO director_role;
GRANT INSERT, UPDATE, DELETE ON Employee TO director_role;
GRANT INSERT, UPDATE, DELETE ON Manages TO director_role;
GRANT INSERT, UPDATE, DELETE ON TrainedIn TO director_role;

-- create users
CREATE USER IF NOT EXISTS 'employee'@'%' IDENTIFIED BY '123';
GRANT employee_role to 'employee'@'%';
SET DEFAULT ROLE employee_role to 'employee'@'%';

CREATE USER IF NOT EXISTS 'trainer'@'%' IDENTIFIED BY '12345';
GRANT trainer_role to 'trainer'@'%';
SET DEFAULT ROLE trainer_role to 'trainer'@'%';

CREATE USER IF NOT EXISTS 'manager'@'%' IDENTIFIED BY '123456';
GRANT manager_role to 'manager'@'%';
SET DEFAULT ROLE manager_role to 'manager'@'%';

CREATE USER IF NOT EXISTS 'director'@'%' IDENTIFIED BY '123456789';
GRANT director_role to 'director'@'%';
SET DEFAULT ROLE director_role to 'director'@'%';

-- remove online root login
DELETE FROM mysql.user WHERE User='root' AND Host NOT IN ('localhost', '127.0.0.1', '::1');
FLUSH PRIVILEGES;