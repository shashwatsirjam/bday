# Define Paths
$sourcePath = "C:\source\path"          # Path where start.bat and stop.bat are located
$targetPath = "C:\uat\path"              # UAT path where you want to deploy the .bat files

# List of .bat files to deploy
$batFilesToDeploy = @("start.bat", "stop.bat")

# Ensure the target directory exists
if (!(Test-Path -Path $targetPath)) {
    New-Item -ItemType Directory -Path $targetPath -Force
}

# Copy .bat files to UAT
foreach ($batFile in $batFilesToDeploy) {
    $sourceFile = Join-Path -Path $sourcePath -ChildPath $batFile
    $targetFile = Join-Path -Path $targetPath -ChildPath $batFile
    Copy-Item -Path $sourceFile -Destination $targetFile -Force
    Write-Output "$batFile deployed to UAT."
}

# Start the start.bat file
$startBatPath = Join-Path -Path $targetPath -ChildPath "start.bat"
Start-Process -FilePath $startBatPath

Write-Output "start.bat has been executed to start the application."
