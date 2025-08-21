; ========= EDIT THESE URLS =========
#define CLI_URL "https://raw.githubusercontent.com/NoCodeBI-tech/Deployment/main/CliJar/nocodebi-cli.jar"
#define SVC_URL "https://raw.githubusercontent.com/NoCodeBI-tech/Deployment/main/ServiceJar/local/nocodebi-service.jar"
; Official NSSM 2.24 zip (contains win64/win32 builds)
#define NSSM_URL "https://nssm.cc/release/nssm-2.24.zip"
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
  Parameters: "-NoLogo -NoProfile -ExecutionPolicy Bypass -Command ""$ErrorActionPreference='Stop'; [Net.ServicePointManager]::SecurityProtocol=[Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '{#CLI_URL}' -OutFile '{app}\cli\nocodebi-cli.jar' -UseBasicParsing"""; \
  StatusMsg: "Downloading CLI..."; Flags: runhidden

; ---- Download Service jar ----
Filename: "powershell.exe"; \
  Parameters: "-NoLogo -NoProfile -ExecutionPolicy Bypass -Command ""$ErrorActionPreference='Stop'; [Net.ServicePointManager]::SecurityProtocol=[Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '{#SVC_URL}' -OutFile '{app}\service\nocodebi-service.jar' -UseBasicParsing"""; \
  StatusMsg: "Downloading service..."; Flags: runhidden

; ---- Download & extract NSSM, copy x64 nssm.exe to {app}\service ----
; (No braces used → no Inno constant conflicts)
Filename: "powershell.exe"; \
  Parameters: "-NoLogo -NoProfile -ExecutionPolicy Bypass -Command ""$ErrorActionPreference='Stop'; [Net.ServicePointManager]::SecurityProtocol=[Net.SecurityProtocolType]::Tls12; $zip='{tmp}\nssm.zip'; Invoke-WebRequest -Uri '{#NSSM_URL}' -OutFile $zip -UseBasicParsing; $unz='{tmp}\nssm_unz'; Remove-Item $unz -Recurse -Force -ErrorAction SilentlyContinue; Expand-Archive -Path $zip -DestinationPath $unz -Force; Copy-Item (Join-Path $unz 'nssm-2.24\win64\nssm.exe') '{app}\service\nssm.exe' -Force;"""; \
  StatusMsg: "Preparing NSSM..."; Flags: runhidden

; ---- Install service with NSSM ----
Filename: "{app}\service\nssm.exe"; \
  Parameters: "install NoCodeBIService ""java.exe"" -jar ""{app}\service\nocodebi-service.jar"""; \
  StatusMsg: "Installing Windows service..."; Flags: runhidden

; Set service working directory
Filename: "{app}\service\nssm.exe"; \
  Parameters: "set NoCodeBIService AppDirectory ""{app}\service"""; \
  Flags: runhidden

; Auto-start at boot
Filename: "{app}\service\nssm.exe"; \
  Parameters: "set NoCodeBIService Start SERVICE_AUTO_START"; \
  Flags: runhidden

; Optional stdout/stderr logs
Filename: "{app}\service\nssm.exe"; \
  Parameters: "set NoCodeBIService AppStdout ""{app}\service\stdout.log"""; \
  Flags: runhidden
Filename: "{app}\service\nssm.exe"; \
  Parameters: "set NoCodeBIService AppStderr ""{app}\service\stderr.log"""; \
  Flags: runhidden

; Start service
Filename: "sc.exe"; Parameters: "start NoCodeBIService"; \
  StatusMsg: "Starting service..."; Flags: runhidden

[UninstallRun]
; Stop and remove the service with NSSM on uninstall
Filename: "sc.exe"; Parameters: "stop NoCodeBIService"; Flags: runhidden
Filename: "{app}\service\nssm.exe"; Parameters: "remove NoCodeBIService confirm"; Flags: runhidden

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
  CmdContent, VbsContent: string;
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
end;
