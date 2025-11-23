@echo off
echo ================================
echo  WebSocket Chat Server
echo ================================
echo Starting WebSocket server on port 8081...
echo Open websocket.html in your browser
echo ================================

cd /d "%~dp0"

if exist "src\main\java" (
    cd src\main\java
    echo Compiling WebSocket server...
    javac com\chat\websocket\*.java com\chat\common\*.java
    if %errorlevel% equ 0 (
        echo Starting WebSocket server...
        java com.chat.websocket.WebSocketChatServer
    ) else (
        echo Compilation failed!
        pause
    )
) else (
    echo Source files not found!
    pause
)

pause