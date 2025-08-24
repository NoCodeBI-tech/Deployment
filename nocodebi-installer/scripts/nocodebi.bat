@echo off
setlocal enabledelayedexpansion

for %%I in ("%~dp0java_path.cfg") do set "CONFIG=%%~fI"

REM Check if we have a stored Java path
if exist "%CONFIG%" (
    set /p JAVA_HOME=<"%CONFIG_FILE%"
    set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
    
    if exist "%JAVA_EXE%" (
        goto :run_jar
    )
)

REM If no stored path, find Java 17
@REM echo Finding Java 17 installation...
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
                @REM echo Checking Java at: !possible_java_home!
                
                REM Check if this is Java 17
                "!possible_java_home!\bin\java.exe" -version 2>temp_version.txt
                set "is_java_17="
                for /f "tokens=*" %%v in (temp_version.txt) do (
                    @REM echo Java version: %%v
                    @REM echo %%v | findstr /c:"17" >nul
                    if !errorlevel! equ 0 (
                        set "is_java_17=true"
                    )
                )
                del temp_version.txt
                
                if defined is_java_17 (
                    set "JAVA_HOME=!possible_java_home!"
                    set "JAVA_EXE=!java_path!"
                    @REM echo Found Java 17 at: !JAVA_HOME!
                    @REM echo !JAVA_HOME! > "%~dp0java_path.cfg"
                    goto :validate_java
                )
            )
        )
    )
)

:java_not_found
echo Error: Java 17 not found on your system.

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

:run_jar
for %%I in ("%~dp0..\Build\nocodebi-cli.jar") do set "JAR_PATH=%%~fI"

if not exist "%JAR_PATH%" (
    echo Error: nocodebi-cli.jar not found in %JAR_PATH%
    exit /b 1
)

REM Run the JAR
"%JAVA_EXE%" -jar "%JAR_PATH%" %*

if %errorlevel% neq 0 (
    echo.
    echo Error: Failed to execute the JAR file.
    echo This might be due to:
    echo 1. Java version incompatibility
    echo 2. Corrupted JAR file
    echo 3. Missing dependencies in the JAR
    echo.
    echo Please check your Java installation and try again.
)