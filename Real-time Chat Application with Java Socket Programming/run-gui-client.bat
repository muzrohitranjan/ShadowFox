@echo off
echo ================================
echo  Real-Time Chat GUI Client
echo ================================
echo Starting GUI client...
echo ================================

cd /d "%~dp0"

if exist "target\chat-server.jar" (
    echo Running GUI client from JAR file...
    java -cp target\chat-server.jar com.chat.client.ChatClientGUI
) else (
    echo Running GUI client from source...
    if exist "src\main\java" (
        cd src\main\java
        java com.chat.client.ChatClientGUI
    ) else (
        echo Source files not found! Please compile first.
        pause
    )
)

if %errorlevel% neq 0 (
    echo Error occurred while running GUI client
    pause
)