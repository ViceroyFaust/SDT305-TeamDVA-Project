-- 2025 Vladyslav Shvets --
CREATE TABLE Restaurant(
    RestaurantId int NOT NULL,
    OpeningTime time NOT NULL,
    ClosingTime time NOT NULL,
    DateOpened date NOT NULL,
    PRIMARY KEY (RestaurantId)   
);

CREATE TABLE Employee(
    EmployeeId int NOT NULL,
    FirstName varchar(255) NOT NULL,
    LastName varchar(255) NOT NULL,
    Salary int,
    DateJoined date,
    Position varchar(255) NOT NULL CHECK (Position IN ('Employee', 'Trainer', 'Manager', 'Director')), -- I changed Instructor to Trainer
    RestaurantId int NOT NULL,
    PRIMARY KEY(EmployeeId),
    FOREIGN KEY (RestaurantId) REFERENCES Restaurant(RestaurantID)
);

DELIMITER //
CREATE TRIGGER chk_one_director
BEFORE INSERT ON Employee
FOR EACH ROW 
BEGIN
    IF NEW.Position = 'Director' THEN
        IF (SELECT COUNT(*)
            FROM Employee
            WHERE Position = 'Director'
            AND RestaurantId = NEW.RestaurantId) > 0 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Only one Director allowed per restaurant';
        END IF;
    END IF;
END//
DELIMITER ;

CREATE TABLE Schedule(
    EmployeeId int NOT NULL,
    StartTime time NOT NULL,
    EndTime time NOT NULL,
    Date date NOT NULL,
    PRIMARY KEY (EmployeeId, Date),  -- Our weak key
    FOREIGN KEY (EmployeeId) REFERENCES Employee(EmployeeId),
    CHECK (EndTime > StartTime)
);

CREATE TABLE Train(
    TrainerId int NOT NULL,
    EmployeeId int NOT NULL,
    PRIMARY KEY(TrainerId, EmployeeId),
    FOREIGN KEY (TrainerId) REFERENCES Employee(EmployeeId),
    FOREIGN KEY (EmployeeId) REFERENCES Employee(EmployeeId),
    CHECK (TrainerId <> EmployeeId)
);

CREATE TABLE ProductionStation(
    Name varchar(255) NOT NULL,
    Category varchar(255) NOT NULL CHECK (Category IN ('Kitchen', 'Service', 'Warehouse')),
    PRIMARY KEY (Name)
);

CREATE TABLE Manages(
    ManagerId int NOT NULL,
    StationName varchar(255) NOT NULL,
    PRIMARY KEY (ManagerId, StationName),
    FOREIGN KEY (ManagerId) REFERENCES Employee(EmployeeId),
    FOREIGN KEY (StationName) REFERENCES ProductionStation(Name)
);

CREATE TABLE TrainedIn(
    EmployeeId int NOT NULL,
    StationName varchar(255) NOT NULL,
    PRIMARY KEY (EmployeeId, StationName),
    FOREIGN KEY (EmployeeId) REFERENCES Employee(EmployeeId),
    FOREIGN KEY (StationName) REFERENCES ProductionStation(Name)
);

