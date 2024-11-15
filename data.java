@echo off
REM Stop script for Spring Boot application

echo Stopping Spring Boot application...

REM Find the PID of the running Java application
for /f "tokens=2" %%a in ('jcmd /V ^| findstr /i "funds-data-extractor"') do set PID=%%a

REM Check if a PID was found and terminate the process
if defined PID (
    taskkill /PID %PID% /F
    if %ERRORLEVEL% equ 0 (
        echo Application stopped successfully.
    ) else (
        echo Failed to stop the application. Please check if the application is running.
    )
) else (
    echo No running application found.
)
