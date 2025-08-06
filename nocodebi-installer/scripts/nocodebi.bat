@echo off
setlocal EnableDelayedExpansion

rem -- Determine directories
set SCRIPT_DIR=%~dp0
set APP_ROOT=%SCRIPT_DIR%\..
set CLI_DIR=%APP_ROOT%\cli

rem -- Build classpath: start with the main jar
set CLASSPATH=%CLI_DIR%\NoCodeBI_CLI_Tool.jar

rem -- Append every JAR under cli\lib
for %%J in ("%CLI_DIR%\lib\*.jar") do (
    set CLASSPATH=!CLASSPATH!;%%~fJ
)

rem -- Launch your main class (replace with your actual Main)
java -cp "%CLASSPATH%" io.nocodebi.App %*

endlocal
