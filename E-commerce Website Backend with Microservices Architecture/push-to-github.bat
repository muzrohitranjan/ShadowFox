@echo off
echo.
echo ========================================
echo   PUSH TO GITHUB
echo ========================================
echo.
echo 1. First create a new repository on GitHub:
echo    - Go to https://github.com
echo    - Click "New repository" 
echo    - Name: ecommerce-microservices
echo    - Keep public, don't initialize with README
echo.
echo 2. Replace YOUR_USERNAME below with your GitHub username
echo.
set /p username="Enter your GitHub username: "
echo.
echo Adding remote origin...
git remote add origin https://github.com/%username%/ecommerce-microservices.git
echo.
echo Setting main branch...
git branch -M main
echo.
echo Pushing to GitHub...
git push -u origin main
echo.
echo ========================================
echo   SUCCESS! 
echo   Repository URL: https://github.com/%username%/ecommerce-microservices
echo ========================================
pause