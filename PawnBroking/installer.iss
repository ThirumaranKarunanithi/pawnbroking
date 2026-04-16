[Setup]
AppName=PawnBroking
AppVersion=1.0.0
AppPublisher=Magizhchi
DefaultDirName={pf}\PawnBroking
DefaultGroupName=PawnBroking
OutputDir=target\installer
OutputBaseFilename=PawnBrokingSetup
Compression=lzma
SolidCompression=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "target\app\PawnBroking\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\PawnBroking"; Filename: "{app}\PawnBroking.exe"
Name: "{commondesktop}\PawnBroking"; Filename: "{app}\PawnBroking.exe"; Tasks: desktopicon

[Tasks]
Name: "desktopicon"; Description: "Create a desktop shortcut"; GroupDescription: "Additional icons:"

[Run]
Filename: "{app}\PawnBroking.exe"; Description: "Launch PawnBroking"; Flags: nowait postinstall skipifsilent
