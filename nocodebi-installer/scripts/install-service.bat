@echo off
setlocal enabledelayedexpansion

echo Installing NoCodeBI Service...
echo.

REM Check for administrative privileges
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: This script requires administrative privileges.
    echo Please run as Administrator.
    exit /b 1
)

for %%I in ("%~dp0java_path.cfg") do set "CONFIG=%%~fI"

REM Try to use stored Java path first
if exist "%CONFIG%" (
    set /p JAVA_HOME=<"%CONFIG_FILE%"
    set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
    
    if exist "%JAVA_EXE%" (
        echo Using Java from: %JAVA_HOME%
        goto :validate_java
    )
)

REM If no stored path, find Java 17
echo Finding Java 17 installation...
set "JAVA_HOME="
set "JAVA_EXE="

REM Use where.exe to find all java executables in PATH
for /f "tokens=*" %%i in ('where.exe java 2^>nul') do (
    set "java_path=%%i"
    
    REM Get the Java installation directory (two levels up from java.exe)
    for %%p in ("!java_path!\..\..") do (
        set "possible_java_home=%%~fp"
        
        REM Check if this is a valid Java installation
        if exist "!possible_java_home!\bin\java.exe" (
            if exist "!possible_java_home!\lib" (
                echo Checking Java at: !possible_java_home!
                
                REM Check if this is Java 17
                "!possible_java_home!\bin\java.exe" -version 2>temp_version.txt
                set "is_java_17="
                for /f "tokens=*" %%v in (temp_version.txt) do (
                    echo Java version: %%v
                    echo %%v | findstr /c:"17" >nul
                    if !errorlevel! equ 0 (
                        set "is_java_17=true"
                    )
                )
                del temp_version.txt
                
                if defined is_java_17 (
                    set "JAVA_HOME=!possible_java_home!"
                    set "JAVA_EXE=!java_path!"
                    echo Found Java 17 at: !JAVA_HOME!
                    echo !JAVA_HOME! > "%~dp0java_path.cfg"
                    goto :validate_java
                )
            )
        )
    )
)

:java_not_found
echo Error: Java 17 not found on your system.
echo.
echo Please install Java JDK 17 from:
echo https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
echo.
echo After installation, please run this script again.
exit /b 1

:validate_java
REM Validate Java installation
if not exist "%JAVA_EXE%" (
    echo Error: Java 17 not found at %JAVA_HOME%
    echo Please install Java JDK 17 or update the path in this script.
    exit /b 1
)

if not exist "%JAVA_HOME%\bin" (
    echo Error: Java bin directory not found.
    exit /b 1
)

if not exist "%JAVA_HOME%\lib" (
    echo Error: Java lib directory not found.
    exit /b 1
)

REM Set service details
set "SERVICE_NAME=NoCodeBIService"
for %%I in ("%~dp0..\Build\nocodebi-service.jar") do set "SERVICE_JAR=%%~fI"
set "SERVICE_DISPLAY_NAME=NoCodeBI Background Service"
for %%I in ("%~dp0..\Nssm\nssm.exe") do set "NSSM_FILE=%%~fI"

if not exist "%SERVICE_JAR%" (
    echo Error: Service JAR file not found at %SERVICE_JAR%
    exit /b 1
)

REM Check if service already exists
sc query %SERVICE_NAME% >nul 2>&1
if %errorlevel% equ 0 (
    echo Service already exists. Stopping and removing...
    sc stop %SERVICE_NAME%
    timeout /t 5 /nobreak >nul
    sc delete %SERVICE_NAME%
    timeout /t 5 /nobreak >nul
)


REM Install the service
"%NSSM_FILE%" install %SERVICE_NAME% "%JAVA_EXE%" "-Xms256m" "-Xmx512m" "-Dfile.encoding=UTF-8" "-jar" "%SERVICE_JAR%"
if %errorlevel% neq 0 (
    echo Error: Failed to install service.
    goto :cleanup
)

@REM "%NSSM_FILE%" set %SERVICE_NAME% DisplayName "%SERVICE_DISPLAY_NAME%"
"%NSSM_FILE%" set %SERVICE_NAME% Start SERVICE_AUTO_START
@REM "%NSSM_FILE%" set %SERVICE_NAME% AppDirectory "%~dp0"
@REM "%NSSM_FILE%" set %SERVICE_NAME% AppStdout "%~dp0service.log"
@REM "%NSSM_FILE%" set %SERVICE_NAME% AppStderr "%~dp0service-error.log"

:cleanup
if %errorlevel% equ 0 (
    echo Service installed successfully.
    echo Starting service...
    
    echo %JAVA_EXE%
    sc start %SERVICE_NAME%
    if %errorlevel% equ 0 (
        echo Service started successfully.
    ) else (
        echo Warning: Service installed but could not be started.
    )
) else (
    echo Service installation failed.
)