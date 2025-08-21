; ========= EDIT THESE URLS =========
#define CLI_URL "https://raw.githubusercontent.com/NoCodeBI-tech/Deployment/main/CliJar/nocodebi-cli.jar"
#define SVC_URL "https://raw.githubusercontent.com/NoCodeBI-tech/Deployment/main/ServiceJar/local/nocodebi-service.jar"
; WinSW (x64) download URL (change version if you want)
#define WINSW_URL "https://github.com/winsw/winsw/releases/download/v3.4.0/WinSW-x64.exe"
; ===================================

[Setup]
AppId={{A61AB6C2-6E73-4A2E-A3E4-5F2C5F2695C8}}
AppName=NoCodeBI
AppVersion=1.0.0
AppPublisher=NoCodeBI Technology Pvt Ltd
DefaultDirName={pf}\NoCodeBI
DefaultGroupName=NoCodeBI
DisableProgramGroupPage=yes
OutputBaseFilename=NoCodeBI-Setup
Compression=lzma
SolidCompression=yes
ArchitecturesInstallIn64BitMode=x64
PrivilegesRequired=admin
WizardStyle=modern
ChangesEnvironment=yes

[Languages]
Name: "en"; MessagesFile: "compiler:Default.isl"

[Dirs]
Name: "{app}\bin"
Name: "{app}\cli"
Name: "{app}\service"

[Registry]
; Add {app}\bin to PATH so 'nocodebi' works in NEW consoles
Root: HKLM; Subkey: "SYSTEM\CurrentControlSet\Control\Session Manager\Environment"; \
  ValueType: expandsz; ValueName: "Path"; \
  ValueData: "{olddata};{app}\bin"; Flags: preservestringtype

[Run]
; ---- Download CLI jar ----
Filename: "powershell.exe"; \
  Parameters: "-NoLogo -NoProfile -ExecutionPolicy Bypass -Command ""$ErrorActionPreference='Stop'; Invoke-WebRequest -Uri '{#CLI_URL}' -OutFile '{app}\cli\nocodebi-cli.jar' -UseBasicParsing"""; \
  StatusMsg: "Downloading CLI..."; Flags: runhidden

; ---- Download Service jar ----
Filename: "powershell.exe"; \
  Parameters: "-NoLogo -NoProfile -ExecutionPolicy Bypass -Command ""$ErrorActionPreference='Stop'; Invoke-WebRequest -Uri '{#SVC_URL}' -OutFile '{app}\service\nocodebi-service.jar' -UseBasicParsing"""; \
  StatusMsg: "Downloading service..."; Flags: runhidden

; ---- Download WinSW executable ----
Filename: "powershell.exe"; \
  Parameters: "-NoLogo -NoProfile -ExecutionPolicy Bypass -Command ""$ErrorActionPreference='Stop'; Invoke-WebRequest -Uri '{#WINSW_URL}' -OutFile '{app}\service\NoCodeBIService.exe' -UseBasicParsing"""; \
  StatusMsg: "Preparing Windows Service wrapper..."; Flags: runhidden

; ---- Install & start service (XML is generated in [Code]) ----
Filename: "{app}\service\NoCodeBIService.exe"; Parameters: "install"; StatusMsg: "Installing Windows service..."; Flags: runhidden
Filename: "{app}\service\NoCodeBIService.exe"; Parameters: "start";  StatusMsg: "Starting Windows service...";  Flags: runhidden

[UninstallRun]
Filename: "{app}\service\NoCodeBIService.exe"; Parameters: "stop";      Flags: runhidden
Filename: "{app}\service\NoCodeBIService.exe"; Parameters: "uninstall"; Flags: runhidden

[Code]
function InitializeSetup(): Boolean;
var
  RC: Integer;
begin
  { Require Java in PATH }
  if not Exec('java', '-version', '', SW_HIDE, ewWaitUntilTerminated, RC) then
  begin
    MsgBox('Java 17+ is required in PATH. Please install Java 17 and retry.', mbCriticalError, MB_OK);
    Result := False;
    exit;
  end;
  Result := True;
end;

procedure CreateFile(const Path, Content: string);
begin
  if not SaveStringToFile(Path, Content, False) then
    RaiseException('Failed to create: ' + Path);
end;

procedure CurStepChanged(CurStep: TSetupStep);
var
  BinDir, CliDir, SvcDir: string;
  CmdContent, VbsContent, XmlContent: string;
begin
  if CurStep <> ssInstall then
    exit;

  BinDir := ExpandConstant('{app}\bin');
  CliDir := ExpandConstant('{app}\cli');
  SvcDir := ExpandConstant('{app}\service');

  ForceDirectories(BinDir);
  ForceDirectories(CliDir);
  ForceDirectories(SvcDir);

  { --- nocodebi.cmd (auto-elevates; runs CLI JAR) --- }
  CmdContent :=
    '@echo off' #13#10 +
    'setlocal' #13#10 +
    '' #13#10 +
    'whoami /groups | find "S-1-16-12288" >NUL' #13#10 +
    'if %errorlevel% neq 0 (' #13#10 +
    '  cscript //nologo "%~dp0elevate.vbs" "%~f0" %*' #13#10 +
    '  exit /b' #13#10 +
    ')' #13#10 +
    '' #13#10 +
    'set "APP_HOME=%~dp0.."' #13#10 +
    'set "CLI_JAR=%APP_HOME%\cli\nocodebi-cli.jar"' #13#10 +
    'set "JAVA_EXE=java"' #13#10 +
    '' #13#10 +
    '"%JAVA_EXE%" -jar "%CLI_JAR%" %*' #13#10 +
    'endlocal' #13#10;
  CreateFile(BinDir + '\nocodebi.cmd', CmdContent);

  { --- elevate.vbs (UAC helper) --- }
  VbsContent :=
    'If WScript.Arguments.Count >= 1 Then' #13#10 +
    '  Dim shell, i, cmd' #13#10 +
    '  Set shell = CreateObject("Shell.Application")' #13#10 +
    '  cmd = """" & WScript.Arguments(0) & """"' #13#10 +
    '  For i = 1 To WScript.Arguments.Count - 1' #13#10 +
    '    cmd = cmd & " """ & WScript.Arguments(i) & """"' #13#10 +
    '  Next' #13#10 +
    '  shell.ShellExecute "cmd.exe", "/c " & cmd, "", "runas", 1' #13#10 +
    'End If' #13#10;
  CreateFile(BinDir + '\elevate.vbs', VbsContent);

  { --- WinSW service config --- }
  XmlContent :=
    '<service>' #13#10 +
    '  <id>NoCodeBIService</id>' #13#10 +
    '  <name>NoCodeBI Service</name>' #13#10 +
    '  <description>Runs the NoCodeBI backend service (Spring Boot).</description>' #13#10 +
    '' #13#10 +
    '  <executable>java.exe</executable>' #13#10 +
    '  <argument>-jar</argument>' #13#10 +
    '  <argument>%BASE%\nocodebi-service.jar</argument>' #13#10 +
    '' #13#10 +
    '  <workingdirectory>%BASE%</workingdirectory>' #13#10 +
    '' #13#10 +
    '  <log mode="roll-by-size">' #13#10 +
    '    <sizeThreshold>10485760</sizeThreshold>' #13#10 +
    '    <keepFiles>5</keepFiles>' #13#10 +
    '  </log>' #13#10 +
    '' #13#10 +
    '  <startmode>Automatic</startmode>' #13#10 +
    '  <delayedAutoStart>true</delayedAutoStart>' #13#10 +
    '  <onfailure action="restart" delay="10 sec"/>' #13#10 +
    '  <onfailure action="restart" delay="20 sec"/>' #13#10 +
    '</service>' #13#10;
  CreateFile(SvcDir + '\NoCodeBIService.xml', XmlContent);
end;
