; -- NoCodeBI Setup Script --
[Setup]
AppName=NoCodeBI
AppVersion=1.0
AppPublisher=NoCodeBI Technology Private Limited
DefaultDirName={commonappdata}\NoCodeBI
UsePreviousAppDir=no
DefaultGroupName=NoCodeBI
UninstallDisplayIcon={app}\nocodebi-cli.jar
Compression=lzma
SolidCompression=yes
OutputDir=..\Output
OutputBaseFilename=NoCodeBI-Setup
ArchitecturesAllowed=x64
ArchitecturesInstallIn64BitMode=x64
ChangesEnvironment=yes
PrivilegesRequired=admin

[Files]
Source: "..\Build\nocodebi-cli.jar"; DestDir: "{app}\Build"; Flags: ignoreversion
Source: "..\Build\nocodebi-service.jar"; DestDir: "{app}\Build"; Flags: ignoreversion
Source: "..\Scripts\nocodebi.bat"; DestDir: "{app}\Scripts"; Flags: ignoreversion
Source: "..\Scripts\install-service.bat"; DestDir: "{app}\Scripts"; Flags: ignoreversion
Source: "..\Scripts\uninstall-service.bat"; DestDir: "{app}\Scripts"; Flags: ignoreversion
Source: "..\Scripts\import-certificate.bat"; DestDir: "{app}\Scripts"; Flags: ignoreversion
Source: "..\Scripts\find-java.bat"; DestDir: "{app}\Scripts"; Flags: ignoreversion
Source: "..\Certificates\product.nocodebi.io.crt"; DestDir: "{app}\Certificates"; Flags: ignoreversion
Source: "..\README.txt"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\Nssm\nssm.exe"; DestDir: "{app}\Nssm"; Flags: ignoreversion

[Icons]
Name: "{group}\NoCodeBI CLI"; Filename: "{app}\Scripts\nocodebi.bat"
Name: "{group}\Install Service"; Filename: "{app}\Scripts\install-service.bat"
Name: "{group}\Uninstall Service"; Filename: "{app}\Scripts\uninstall-service.bat"
Name: "{group}\Import Certificate"; Filename: "{app}\Scripts\import-certificate.bat"
Name: "{group}\Uninstall NoCodeBI"; Filename: "{uninstallexe}"

[Run]
Filename: "{app}\Scripts\find-java.bat"; Description: "Detect Java Installation"; Flags: runhidden waituntilterminated
Filename: "{app}\Scripts\install-service.bat"; Description: "Install NoCodeBI Service"; Flags: postinstall shellexec waituntilterminated
Filename: "{app}\Scripts\import-certificate.bat"; Description: "Import SSL Certificate"; Flags: postinstall shellexec waituntilterminated

[Registry]
; Add application directory to system PATH
Root: HKLM; Subkey: "SYSTEM\CurrentControlSet\Control\Session Manager\Environment"; \
    ValueType: expandsz; ValueName: "Path"; ValueData: "{olddata};{app}\Scripts"; \
    Check: NeedsAddPath(ExpandConstant('{app}\Scripts'))

[Code]
function NeedsAddPath(Param: string): boolean;
var
  OrigPath: string;
begin
  if not RegQueryStringValue(HKEY_LOCAL_MACHINE,
    'SYSTEM\CurrentControlSet\Control\Session Manager\Environment',
    'Path', OrigPath)
  then begin
    Result := True;
    exit;
  end;
  Result := Pos(';' + UpperCase(Param) + ';', ';' + UpperCase(OrigPath) + ';') = 0;
end;

procedure CurStepChanged(CurStep: TSetupStep);
begin
  if CurStep = ssPostInstall then
  begin
    MsgBox('Installation completed at: ' + ExpandConstant('{app}'), mbInformation, MB_OK);
  end;
end;