@echo off
echo Starting Library Management System...
echo.

REM Check if Maven is available
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Maven not found. Trying to compile and run manually...
    echo.
    
    REM Create database directory
    if not exist "database" mkdir database
    
    REM Manual compilation
    echo Compiling Java files...
    javac -cp "lib/*" -d target/classes src/main/java/*.java
    
    if %ERRORLEVEL% EQU 0 (
        echo Running application...
        java -cp "target/classes;lib/*" LibraryManagementApp
    ) else (
        echo Compilation failed. Please ensure SQLite JDBC driver is in lib/ directory.
        echo You can download it from: https://github.com/xerial/sqlite-jdbc/releases
    )
) else (
    echo Using Maven to run the application...
    mvn clean compile exec:java
)

echo.
pause