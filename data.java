@echo off
REM Set the path to Java installation
set JAVA_HOME=I:\DMDT\Newfolder\JRE_17\java-17-openjdk-17.0.8.0.7-1-jre-win.x86_64
set PATH=%JAVA_HOME%\bin;%PATH%

REM Set the path to configuration files
set CONFIG_PATH=I:\DMDT\application.properties
set LOG_PATH=I:\DMDT\config\log4j2-rfasfsat.xml

REM Optional: Set custom options for logging if needed
set LOG_OPTS=-Dlogging.file.path=%LOG_PATH%

REM Automatically find the latest JAR file based on pattern "funds-data-extractor"
for /f "delims=" %%a in ('dir /b /o:-d I:\DMDT\funds-data-extractor-*.jar') do (
    set JAR_PATH=I:\DMDT\%%a
    goto :found
)

:found
REM Set Java options for Spring Boot application
set JAVA_OPTS=-Dspring.config.location=file:%CONFIG_PATH%

REM Start the application
echo Starting Spring Boot application...
%JAVA_HOME%\bin\java %JAVA_OPTS% %LOG_OPTS% -jar %JAR_PATH%

REM Check if the application started successfully
if %ERRORLEVEL% equ 0 (
    echo Application started successfully.
) else (
    echo Failed to start application.
)
