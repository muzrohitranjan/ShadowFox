@echo off
echo Starting Inventory Management System...
echo.

REM Check if Maven is available
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Maven not found. Trying to compile and run manually...
    echo.
    
    REM Manual compilation and run
    echo Compiling Java files...
    javac --module-path "C:\Program Files\Java\javafx-17.0.2\lib" --add-modules javafx.controls,javafx.fxml -cp src\main\java src\main\java\*.java
    
    if %ERRORLEVEL% EQU 0 (
        echo Running application...
        java --module-path "C:\Program Files\Java\javafx-17.0.2\lib" --add-modules javafx.controls,javafx.fxml -cp src\main\java InventoryApp
    ) else (
        echo Compilation failed. Please ensure JavaFX is properly installed.
    )
) else (
    echo Using Maven to run the application...
    mvn clean javafx:run
)

echo.
pause