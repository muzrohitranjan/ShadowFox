@echo off
echo Starting Online Banking System...
echo.
echo Make sure MySQL is running and database 'banking_system' exists
echo.
pause
echo.
echo Building the application...
mvn clean install
echo.
echo Starting the application...
mvn spring-boot:run