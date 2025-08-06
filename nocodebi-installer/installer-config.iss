; ----------------------------------------
; NoCodeBI Installer — Inno Setup script
; ----------------------------------------
[Setup]
AppName=NoCodeBI
AppVersion=1.0
DefaultDirName={pf}\NoCodeBI
PrivilegesRequired=admin
OutputBaseFilename=nocodebi-installer
Compression=lzma
SolidCompression=yes
DefaultGroupName=NoCodeBI
UninstallDisplayIcon={app}\scripts\nocodebi.bat

[Files]
; CLI and its libs
Source: "cli\NoCodeBI_CLI_Tool.jar"; DestDir: "{app}\cli"; Flags: ignoreversion
Source: "cli\lib\*";               DestDir: "{app}\cli\lib"; Flags: ignoreversion recursesubdirs

; Service and its libs
Source: "service\NoCodeBI_Service.jar"; DestDir: "{app}\service"; Flags: ignoreversion
Source: "service\lib\*";                DestDir: "{app}\service\lib"; Flags: ignoreversion recursesubdirs

; Scripts
Source: "scripts\*";               DestDir: "{app}\scripts"; Flags: ignoreversion recursesubdirs
; NSSM binary
Source: "nssm.exe";                DestDir: "{app}";          Flags: ignoreversion

[Icons]
Name: "{group}\Run NoCodeBI CLI"; Filename: "{app}\scripts\nocodebi.bat"

[Run]
; Install the Windows service silently
Filename: "{app}\scripts\install-service.bat"; Flags: runhidden

[Registry]
; Add our CLI launcher dir to the System PATH
Root: HKLM; Subkey: "SYSTEM\CurrentControlSet\Control\Session Manager\Environment"; ValueType: expandsz; \
    ValueName: "Path"; \
    ValueData: "{olddata};{app}\scripts"; \
    Flags: preservestringtype uninsdeletevalue
