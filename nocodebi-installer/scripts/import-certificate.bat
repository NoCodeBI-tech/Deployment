@echo off
setlocal enabledelayedexpansion

echo Importing SSL Certificate...
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

for %%I in ("%~dp0..\Certificates\product.nocodebi.io.crt") do set "CERT_FILE=%%~fI"

if not exist "%CERT_FILE%" (
    echo Error: Certificate file not found at %CERT_FILE%
    exit /b 1
)

REM Import certificate into Java keystore
echo Importing certificate into Java keystore...
"%JAVA_HOME%\bin\keytool" -import -alias nocodebi -keystore "%JAVA_HOME%\lib\security\cacerts" -file "%CERT_FILE%" -storepass changeit -noprompt

if %errorlevel% equ 0 (
    echo Certificate imported successfully.
) else (
    echo Error: Failed to import certificate.
)