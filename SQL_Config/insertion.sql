-- 2025 Vladyslav Shvets --
INSERT INTO Restaurant VALUES
(1, '08:00:00', '22:00:00', '2023-05-15'),
(2, '07:30:00', '23:00:00', '2024-09-01'),
(3, '09:00:00', '21:00:00', '2025-03-01');

INSERT INTO Employee VALUES
(1, 'Vladyslav', 'Shvets', 150000, '2024-07-01', 'Director', 1),
(9, 'Danylo', 'Rybchynskyi', 150000, '2024-06-01', 'Director', 2),
(10, 'Andrii', 'enter your surname', 150000, '2024-05-07', 'Director', 3),
(2, 'Noah', 'Smith', 45000, '2025-02-15', 'Manager', 1),
(3, 'Mykola', 'Shevchenko', 38000, '2024-01-10', 'Trainer', 2),
(4, 'Emma', 'Johnson', 28000, '2024-05-01', 'Employee', 3),
(6, 'Liam', 'Williams', 32000, '2023-02-01', 'Employee', 1),
(7, 'Nataliya', 'Boyko', 55000, '2024-09-01', 'Manager', 3),
(8, 'Oliver', 'Brown', 42000, '2025-01-15', 'Trainer', 1);

INSERT INTO Schedule VALUES
(1, '09:00:00', '17:00:00', '2025-02-01'),
(1, '09:00:00', '17:00:00', '2025-02-02'),
(1, '09:00:00', '17:00:00', '2025-02-03'),
(1, '09:00:00', '17:00:00', '2025-02-04'),
(1, '09:00:00', '17:00:00', '2025-02-05'),
(2, '08:30:00', '16:30:00', '2025-02-06'),
(3, '10:00:00', '18:00:00', '2025-02-02'),
(4, '12:00:00', '20:00:00', '2025-02-03'),
(7, '08:00:00', '16:00:00', '2025-02-04'),
(6, '14:00:00', '22:00:00', '2025-02-05');


INSERT INTO ProductionStation VALUES
('Food Station', 'Kitchen'),
('Cold Meals Preparation', 'Kitchen'),
('Dining Area', 'Service'),
('Bar Counter', 'Service'),
('Storage', 'Warehouse');

INSERT INTO Train VALUES
(3, 4), -- Mykola trains Emma
(8, 6); -- Oliver trains Liam

INSERT INTO Manages VALUES
(2, 'Food Station'),
(7, 'Dining Area'),   
(2, 'Storage');  

INSERT INTO TrainedIn VALUES
(4, 'Cold Meals Preparation'),
(6, 'Food Station'),
(3, 'Bar Counter'),
(8, 'Storage');
