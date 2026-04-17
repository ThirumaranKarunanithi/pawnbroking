====================================================================
 PAWN DB SYNC  –  Deployment Guide
====================================================================

REQUIREMENTS (on the target machine)
--------------------------------------
  • Windows 10 / Server 2016 or later (64-bit)
  • JDK or JRE 17+   →  https://adoptium.net
    Make sure "java" is on the system PATH (install with "Add to PATH" ticked)
  • Local PostgreSQL running on localhost:5432
    database: pawnbroking

WHAT IS IN THIS PACKAGE
--------------------------------------
  pawndbsync.jar          Application JAR
  pawndbsync.xml          WinSW service descriptor (do NOT rename)
  application.properties  Configuration – edit before installing
  install.bat             Installs & starts the Windows service
  uninstall.bat           Stops & removes the Windows service
  winsw.exe (optional)    Bundle WinSW here to skip download
                          Download: https://github.com/winsw/winsw/releases/download/v2.12.0/WinSW-x64.exe

INSTALLATION STEPS
--------------------------------------
  1. Edit application.properties:
       - Set local.datasource.password  to the local PostgreSQL password
       - Verify cloud.datasource credentials (Railway URL/user/pass)
       - Set aws.access-key-id / aws.secret-access-key  (IAM user with S3 write access)
       - Confirm aws.region matches your S3 bucket region  (default: ap-south-1)
       - Change server.port if 8081 is already in use

  2. (Optional) Place WinSW-x64.exe in this folder, renamed to winsw.exe
     (install.bat will download it automatically if missing, needs internet)

  3. Right-click install.bat  →  Run as Administrator

  The service installs to C:\PawnDbSync\ and is set to
  start automatically every time Windows boots.

MANAGING THE SERVICE
--------------------------------------
  Start   :  net start PawnDbSync
  Stop    :  net stop  PawnDbSync
  Status  :  sc query  PawnDbSync
  Logs    :  C:\PawnDbSync\logs\

REST ENDPOINTS (while service is running)
--------------------------------------
  DB sync status   :  GET  http://localhost:8081/api/sync/status
  DB sync trigger  :  POST http://localhost:8081/api/sync/trigger
  File sync status :  GET  http://localhost:8081/api/files/status
  File sync trigger:  POST http://localhost:8081/api/files/trigger
  Diagnostics      :  GET  http://localhost:8081/api/sync/diagnose

UNINSTALLATION
--------------------------------------
  Right-click uninstall.bat  →  Run as Administrator

====================================================================
