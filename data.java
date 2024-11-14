# azure-pipelines.yml

trigger:
  branches:
    include:
      - main

resources:
  repositories:
    - repository: templatesRepo
      type: git
      name: YourOrganization/YourTemplateRepo  # Replace with the actual template repo path
      ref: refs/heads/main  # Ensure this is the correct branch

variables:
  secureFileName: '176-solace.jks'
  uatPath: 'C:/path/to/uat/cert'  # Update to the specific UAT path needed

stages:
- stage: DeployUsingGovernedTemplate
  jobs:
  - job: DeployCert
    displayName: "Deploy Certificate Using Governed Template"
    steps:
      # Download the secure file from Azure Secure Files
      - task: DownloadSecureFile@1
        inputs:
          secureFile: '$(secureFileName)'

      # Pass secure file and UAT path to governed template
      - template: path/to/your-governed-template.yml@templatesRepo
        parameters:
          fileName: '$(Agent.TempDirectory)/$(secureFileName)'
          targetPath: '$(uatPath)'

      # Confirm deployment (this may not be needed if the template has its own confirmation steps)
      - powershell: |
          Write-Output "Certificate file deployed successfully to $(uatPath)"
        displayName: "Confirm Deployment"
