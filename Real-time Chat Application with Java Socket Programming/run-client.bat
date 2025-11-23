@echo off
echo ================================
echo  Real-Time Chat Client
echo ================================
echo Connecting to chat server...
echo Type /help for available commands
echo Type /quit to exit
echo ================================

cd /d "%~dp0"

if exist "target\chat-server.jar" (
    echo Running client from JAR file...
    java -cp target\chat-server.jar com.chat.client.ChatClient
) else (
    echo Running client from source...
    if exist "src\main\java" (
        cd src\main\java
        java com.chat.client.ChatClient
    ) else (
        echo Source files not found! Please compile first.
        pause
    )
)

pause