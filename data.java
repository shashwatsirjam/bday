@echo off
echo Installing FSDT Service...
echo.

REM Check for administrative privileges
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo Error: Administrative privileges required.
    echo Please run this script as Administrator.
    pause
    exit /b 1
)

REM Set working directory
cd /d C:\user\shaswat

REM Verify Java installation
java -version >nul 2>&1
if %errorLevel% neq 0 (
    echo Error: Java is not installed or not in PATH.
    pause
    exit /b 1
)

REM Create required directories if they don't exist
if not exist "config" mkdir config
if not exist "cert" mkdir cert
if not exist "logs" mkdir logs

REM Install the service
java -jar fsdt-service-wrapper.jar install

echo.
echo Installation completed.
echo You can now start the service from Windows Services or by running: sc start FSDTService
pause












  @echo off
echo Uninstalling FSDT Service...
echo.

REM Check for administrative privileges
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo Error: Administrative privileges required.
    echo Please run this script as Administrator.
    pause
    exit /b 1
)

REM Set working directory
cd /d C:\user\shaswat

REM Uninstall the service
java -jar fsdt-service-wrapper.jar uninstall

echo.
echo Uninstallation completed.
pause
