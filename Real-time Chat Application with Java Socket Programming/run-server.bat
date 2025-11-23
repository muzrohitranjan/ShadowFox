@echo off
echo ================================
echo  Real-Time Chat Server
echo ================================
echo Starting chat server on port 8080...
echo Press Ctrl+C to stop the server
echo ================================

cd /d "%~dp0"

if exist "target\chat-server.jar" (
    echo Running from JAR file...
    java -jar target\chat-server.jar
) else (
    echo Compiling and running from source...
    if exist "src\main\java" (
        cd src\main\java
        javac com\chat\server\*.java com\chat\client\*.java com\chat\common\*.java
        if %errorlevel% equ 0 (
            java com.chat.server.ChatServer
        ) else (
            echo Compilation failed!
            pause
        )
    ) else (
        echo Source files not found!
        pause
    )
)

pause