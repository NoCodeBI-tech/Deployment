@echo off
setlocal enabledelayedexpansion

echo Uninstalling NoCodeBI Service...
echo.

REM Check for administrative privileges
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: This script requires administrative privileges.
    echo Please run as Administrator.
    exit /b 1
)

set "SERVICE_NAME=NoCodeBIService"

sc query %SERVICE_NAME% >nul 2>&1
if %errorlevel% equ 0 (
    echo Stopping service...
    sc stop %SERVICE_NAME%
    timeout /t 5 /nobreak >nul
    
    echo Removing service...
    sc delete %SERVICE_NAME%
    echo Service removed successfully.
) else (
    echo Service not found.
)