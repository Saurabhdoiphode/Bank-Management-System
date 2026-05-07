@echo off
REM ==================== INSTALLATION SCRIPT FOR WINDOWS ====================
REM This script helps set up the Bank Management System on Windows

setlocal enabledelayedexpansion

echo ================================
echo Bank Management System Setup
echo ================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java is not installed. Please install Java 17 or higher.
    exit /b 1
) else (
    echo ✅ Java is installed
    java -version
)

echo.

REM Check if Maven is installed
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven is not installed. Please install Maven.
    exit /b 1
) else (
    echo ✅ Maven is installed
    mvn -version | findstr /R "Apache"
)

echo.
echo ================================
echo Building Backend...
echo ================================

cd backend

REM Clean and install dependencies
mvn clean install -DskipTests

if %errorlevel% equ 0 (
    echo ✅ Backend built successfully!
) else (
    echo ❌ Backend build failed!
    exit /b 1
)

echo.
echo ================================
echo Setup Complete!
echo ================================
echo.
echo Next steps:
echo.
echo 1. Start MongoDB (ensure it's installed and running)
echo.
echo 2. Start Backend (open new command prompt in project root):
echo    cd backend
echo    mvn spring-boot:run
echo.
echo 3. Start Frontend (open new command prompt in project root):
echo    cd frontend
echo    python -m http.server 3000
echo.
echo 4. Open browser:
echo    http://localhost:3000
echo.
echo 5. Login with admin credentials:
echo    Email: admin@gmail.com
echo    Password: admin123
echo.
echo ================================
echo Ready to go! Happy Banking! 🏦
echo ================================
echo.
pause
