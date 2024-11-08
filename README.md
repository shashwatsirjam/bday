trigger:
  branches:
    include:
      - main  # Adjust the branch as needed

pool:
  vmImage: 'windows-latest'

steps:
- task: DownloadSecureFile@1
  inputs:
    secureFile: 'certificate_name.pfx'  # Replace with your certificate file name

- powershell: |
    # Define UAT server and paths
    $uatServer = "uat-server-address"  # Replace with your UAT server address
    $destinationPath = "C:\path\to\certificates"  # Path on UAT to store certs

    # Ensure the destination directory exists
    if (!(Test-Path -Path $destinationPath)) {
      New-Item -Path $destinationPath -ItemType Directory
    }

    # Copy the certificate to the UAT server
    $certPath = "$(Agent.TempDirectory)\certificate_name.pfx"  # Replace with the downloaded cert path
    Copy-Item -Path $certPath -Destination "\\$uatServer\$destinationPath" -Force

    Write-Host "Certificate deployed to UAT server at $destinationPath"
  displayName: 'Deploy Certificate to UAT'
