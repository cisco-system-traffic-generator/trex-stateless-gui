#define TrexAppName "trex-stateless-gui"
#define TrexDisplayAppName "TRex"
#define TrexAppVersion "2.02-SNAPSHOT"
#define TrexAppPublisher "TRex"
#define TrexAppURL "http://www.exalt-tech.com/"
#define TrexAppExeName "trex-stateless-gui.exe"
#define SetupExeBaseName "setupTrex"

;This file will be executed next to the application bundle image
;I.e. current directory will contain folder {#TrexAppName} with application files
[Setup]
AppId={{com.exalttech.trex.trextool}}
AppName={#TrexDisplayAppName}
AppVersion={#TrexAppVersion}
AppVerName={#TrexAppName} {#TrexAppVersion}
AppPublisher={#TrexAppPublisher}
AppPublisherURL={#TrexAppURL}
AppSupportURL={#TrexAppURL}
AppUpdatesURL={#TrexAppURL}
AppComments={#TrexAppName}
AppCopyright=Copyright (C) 2016
;AppPublisherURL=http://java.com/
;AppSupportURL=http://java.com/
;AppUpdatesURL=http://java.com/
DefaultDirName={pf}\{#TrexAppPublisher}\{#TrexAppName}
DefaultGroupName=TRex
DisableProgramGroupPage=Yes
;Optional License
LicenseFile=
;WinXP or above
MinVersion=0,5.1 
OutputBaseFilename={#SetupExeBaseName}{#TrexAppVersion}
Compression=lzma
SolidCompression=yes
PrivilegesRequired=admin
;SetupIconFile={#TrexAppName}.ico
UninstallDisplayIcon={app}\{#TrexAppName}.ico
UninstallDisplayName={#TrexAppName}
WizardImageStretch=No
WizardSmallImageFile={#TrexAppName}-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=x64


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "{#TrexAppName}\{#TrexAppExeName}"; DestDir: "{app}"; Flags: ignoreversion
Source: "{#TrexAppName}\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\{#TrexAppName}"; Filename: "{app}\{#TrexAppExeName}"; IconFilename: "{app}\{#TrexAppName}.ico"; Check: returnTrue()
Name: "{commondesktop}\{#TrexAppName}"; Filename: "{app}\{#TrexAppExeName}";  IconFilename: "{app}\{#TrexAppName}.ico"; Check: returnFalse()


[Run]
Filename: "{app}\{#TrexAppExeName}"; Parameters: "-Xappcds:generatecache"; Check: returnFalse()
Filename: "{app}\{#TrexAppExeName}"; Description: "{cm:LaunchProgram,{#TrexAppName}}"; Flags: nowait postinstall skipifsilent; Check: returnTrue()
Filename: "{app}\{#TrexAppExeName}"; Parameters: "-install -svcName ""{#TrexAppName}"" -svcDesc ""{#TrexAppName}"" -mainExe ""{#TrexAppExeName}""  "; Check: returnFalse()

[UninstallRun]
Filename: "{app}\{#TrexAppExeName} "; Parameters: "-uninstall -svcName {#TrexAppName} -stopOnUninstall"; Check: returnFalse()

[Code]
function returnTrue(): Boolean;
begin
  Result := True;
end;

function returnFalse(): Boolean;
begin
  Result := False;
end;

function InitializeSetup(): Boolean;
begin
// Possible future improvements:
//   if version less or same => just launch app
//   if upgrade => check if same app is running and wait for it to exit
//   Add pack200/unpack200 support? 
  Result := True;
end;  
