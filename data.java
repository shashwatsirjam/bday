dotnet publish -c Release -r win-x64 --self-contained
New-Service -Name "JavaRunnerService" -BinaryFile "C:\path\to\published\JavaRunnerService.exe" -StartupType Automatic
Start-Service -Name "JavaRunnerService"
