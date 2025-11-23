@echo off
echo Starting E-Commerce Frontend...
echo.
echo Make sure backend services are running first!
echo Run start-services.bat if not already started.
echo.
echo Starting frontend server...
cd frontend
python -m http.server 3000
echo.
echo Frontend available at:
echo Customer: http://localhost:3000
echo Admin: http://localhost:3000/admin.html
pause