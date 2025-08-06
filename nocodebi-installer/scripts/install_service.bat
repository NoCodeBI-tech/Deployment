@echo off
setlocal

set SERVICE_NAME=NoCodeBIService
set SCRIPT_DIR=%~dp0
set APP_ROOT=%SCRIPT_DIR%\..
set SERVICE_DIR=%APP_ROOT%\service

rem -- Install the service
nssm install "%SERVICE_NAME%" java

rem -- Build AppParameters for nssm: a wildcard classpath and your main class
set APP_PARAMS=-cp "%SERVICE_DIR%\NoCodeBI_Service.jar;%SERVICE_DIR%\lib\*" com.nocodebi.service.ServiceServiceApplication

nssm set "%SERVICE_NAME%" AppParameters %APP_PARAMS%
nssm set "%SERVICE_NAME%" AppDirectory "%SERVICE_DIR%"
nssm set "%SERVICE_NAME%" AppStdout "%SERVICE_DIR%\stdout.log"
nssm set "%SERVICE_NAME%" AppStderr "%SERVICE_DIR%\stderr.log"
nssm set "%SERVICE_NAME%" Start SERVICE_AUTO_START

endlocal
