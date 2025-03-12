# Deliverable 4 Design

## Introduction
We are required to implement a simple Java application which interfaces with our database. For the sake of simplicity and due to time limitations, we will keep the database the same as it was in Deliverable 3. 

## Use Cases
- Query the Database
  - We will include a few parametrized and non-parametrized queries
  - Queries will use either SELECT, INSERT, UPDATE, or DELETE statements
  - We should focus on the SELECT and INSERT queries
- View results

## Assumptions
We will assume that the database will be MySQL and thus we will have only a single 3rd party library requirement. Since modern Java automatically detects database drivers, we do not need to preload them as we did in our previous assignment.

Our dependency is: `implementation group: 'mysql', name: 'mysql-connector-java', version: '8.0.33'`.

The minimum SQL version that we will use is 8.4.4.

## Basic Usage and Requirements
The application will run with a CLI user interface, we will not use command-line arguments other than `--help` optionally.

When the user launches the application, it should prompt them the URI of the database, the username, and the password. Once the user enters this, it should connect to the database. If the connection cannot be established, we print an error and exit.

Once the application has connected to the database, the application prints the main menu. The main menu will include the following options:
1. View Database
2. Modify Database
3. Exit

View and Modify Database options are submenus. The View submenu is the following:
1. View Employees
   - Simply shows the full table of employees
2. View Employees by Position
   - Filters the employee table by position
3. View Employee's Stations
   - Takes in an Employee ID and prints all stations they are trained in
4. View Instructor's Students
   - Takes in an instructor ID and shows the instructor's students
5. View Manager's Stations
   - Takes in a manager's ID and shows the stations that they manage
6. View Employee Schedule
   - Takes in an Employee ID and shows a full table of their schedule
7. View Production Stations
   - Shows a table of all production stations
8. View Restaurants
   - Shows a table of all restaurants *and* their respective directors

The Modify submenu is the following:
1. Add Employee
   - Adds an employee to the list (with position and everything)
2. Add Restaurant
   - Adds a restaurant to the list
3. Add Production Station
   - Adds a production station
4. Add Schedule
   - Adds a schedule entry to an employee by their ID
5. Update Employee Station
   - Allows to add new stations that an employee is trained in
6. Update Employee Training
   - Adds an employee to an instructor's list of students
7. Update Manager
   - Adds a station to a manager's list of stations they manage

In case of errors, the program should recover from them and report them to the user. Make sure to turn off autocommit. Commit only once we are sure that there are no errors.


