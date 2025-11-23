@echo off
echo ================================
echo  Opening Web Chat Interface
echo ================================
echo Opening chat interface in browser...
echo ================================

cd /d "%~dp0"

if exist "web\websocket.html" (
    echo Opening WebSocket chat interface...
    start "" "web\websocket.html"
) else if exist "web\index.html" (
    echo Opening demo chat interface...
    start "" "web\index.html"
) else (
    echo Web files not found!
    pause
)

echo.
echo Instructions:
echo 1. Make sure WebSocket server is running (run-websocket-server.bat)
echo 2. Enter your username in the browser
echo 3. Start chatting!
echo.
pause