@echo off
setlocal enabledelayedexpansion

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
                    for %%I in ("%~dp0java_path.cfg") do set "CONFIG=%%~fI"
                    echo !JAVA_HOME! > "%CONFIG%"
                    goto :java_found
                )
            )
        )
    )
)

:java_not_found
echo Java 17 not found.
exit /b 1

:java_found
exit /b 0