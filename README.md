# SDT 305 Team DVA Final Project
This is the repository of Team DVA's final project for AUK's SDT 305 Principles of Database Management Course.

## Team Members
- Danylo Rybchynskyi
  - Database Design
  - System Design
  - Team Lead
  - Documentation
  - Coded View, Table, and ResultsToTable
  - Code reviews and bugfixes
- Vladyslav Shvets
  - Handler pattern design in project
  - Hosted an online database for testing
  - Created dummy database inserts
  - Worked on creating the controller communication with handlers
  - Implemented the main loop
- Andrii Tivonenko
  - Wrote SQL statements
  - Implemented Handler logic
  - Created custom, dynamic prepared statements
  - Manual testing

## Steps for Creating a Database
1. Set up a MySQL database
   1. Could be a local install, docker container, or online db
2. Use the provided creation.sql and insertion.sql scripts to populate the database

## Environment
- Gradle 8.10 (comes with wrapper in the repository)
- mysql-connector-java 8.0.33
- Java 21

To compile and run this application, use `gradle run --console plain`. This will run the app without you having to create a separate jar file. In case you would like to use a jar, then simply use `gradle fatJar` which will create a jar file under `build/libs/DVA-Database.jar`. Then run that jar file from the command line using `java -jar`.

Once the program is up and running, make sure that you connect properly to it via the link. Enter your username and password correctly.

## Video
TBA
