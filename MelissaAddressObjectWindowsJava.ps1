# Name:    MelissaAddressObjectWindowsJava
# Purpose: Use the MelissaUpdater to make the MelissaAddressObjectWindowsJava code usable

######################### Parameters ##########################

param(
    $address = "",
    $city = "",
    $state = "",
    $zip = "",
    $license = "",
    [switch]$quiet = $false
    )

######################### Classes ##########################

class FileConfig {
  [string] $FileName;
  [string] $ReleaseVersion;
  [string] $OS;
  [string] $Compiler;
  [string] $Architecture;
  [string] $Type;
}

######################### Config ###########################

$RELEASE_VERSION = '2023.12'
$ProductName = "DQ_ADDR_DATA"

# Uses the location of the .ps1 file 
# Modify this if you want to use 
$CurrentPath = $PSScriptRoot
Set-Location $CurrentPath
$ProjectPath = "$CurrentPath\MelissaAddressObjectWindowsJava"
$DataPath = "$ProjectPath\Data"

If (!(Test-Path $DataPath)) {
  New-Item -Path $ProjectPath -Name 'Data' -ItemType "directory"
}

$DLLs = @(
  [FileConfig]@{
    FileName       = "mdAddr.dll";
    ReleaseVersion = "$RELEASE_VERSION";
    OS             = "WINDOWS";
    Compiler       = "DLL";
    Architecture   = "64BIT";
    Type           = "BINARY";
  }
)

$WrapperCom = @(
  [FileConfig]@{
    FileName       = "mdAddrJavaWrapper.dll";
    ReleaseVersion = $RELEASE_VERSION;
    OS             = "WINDOWS";
    Compiler       = "JAVA";
    Architecture   = "64BIT";
    Type           = "INTERFACE";
  },
  [FileConfig]@{
    FileName       = "mdAddr_JavaCode.zip";
    ReleaseVersion = $RELEASE_VERSION;
    OS             = "ANY";
    Compiler       = "ANY";
    Architecture   = "ANY";
    Type           = "DATA";
  }
)

######################## Functions #########################

function DownloadDataFiles([string] $license) {
  $DataProg = 0
  Write-Host "========================== MELISSA UPDATER ========================="
  Write-Host "MELISSA UPDATER IS DOWNLOADING DATA FILE(S)..."
  
  .\MelissaUpdater\MelissaUpdater.exe manifest -p $ProductName -r $RELEASE_VERSION -l $license -t $DataPath
  
  if ($? -eq $false) {
    Write-Host "`nCannot run Melissa Updater. Please check your license string!"
    exit
  }

  Write-Host "Melissa Updater finished downloading data file(s)!"
}

function DownloadDLLs() {
  Write-Host "MELISSA UPDATER IS DOWNLOADING DLL(S)..."
  $DLLProg = 0
  foreach ($DLL in $DLLs) {
    Write-Progress -Activity "Downloading DLL(S)" -Status "$([math]::round($DLLProg / $DLLs.Count * 100, 2))% Complete:"  -PercentComplete ($DLLProg / $DLLs.Count * 100)

    # Check for quiet mode
    if ($quiet) {
      .\MelissaUpdater\MelissaUpdater.exe file --filename $DLL.FileName --release_version $DLL.ReleaseVersion --license $LICENSE --os $DLL.OS --compiler $DLL.Compiler --architecture $DLL.Architecture --type $DLL.Type --target_directory $ProjectPath > $null
	  if ($? -eq $false) {
        Write-Host "`nCannot run Melissa Updater. Please check your license string!"
		exit
	  }
	}
    else {
      .\MelissaUpdater\MelissaUpdater.exe file --filename $DLL.FileName --release_version $DLL.ReleaseVersion --license $LICENSE --os $DLL.OS --compiler $DLL.Compiler --architecture $DLL.Architecture --type $DLL.Type --target_directory $ProjectPath 
      if ($? -eq $false) {
        Write-Host "`nCannot run Melissa Updater. Please check your license string!"
		exit
	  }
	}
    
    Write-Host "Melissa Updater finished downloading " $DLL.FileName "!"
    $DLLProg++
  }
}

function DownloadWrappers() {
  foreach ($File in $WrapperCom) {
    # Check for quiet mode
    if ($quiet) {
      .\MelissaUpdater\MelissaUpdater.exe file --filename $File.FileName --release_version $File.ReleaseVersion --license $LICENSE --os $File.OS --compiler $File.Compiler --architecture $File.Architecture --type $File.Type --target_directory $ProjectPath > $null
      if (($?) -eq $False) {
        Write-Host "`nCannot run Melissa Updater. Please check your license string!"
        Exit
      }
    }
    else {
      .\MelissaUpdater\MelissaUpdater.exe file --filename $File.FileName --release_version $File.ReleaseVersion --license $LICENSE --os $File.OS --compiler $File.Compiler --architecture $File.Architecture --type $File.Type --target_directory $ProjectPath 
      if (($?) -eq $False) {
        Write-Host "`nCannot run Melissa Updater. Please check your license string!"
        Exit
      }
    }
      
    Write-Host "Melissa Updater finished downloading " $File.FileName "!"

    # Check for the zip folder and extract from the zip folder if it was downloaded
    if ($File.FileName -eq "mdAddr_JavaCode.zip") {
      if (!(Test-Path ("$ProjectPath\mdAddr_JavaCode.zip"))) {
        Write-Host "mdAddr_JavaCode.zip not found." 
        
        Write-Host "`nAborting program, see above.  Press any button to exit."
        $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
        exit
      }
      else {
        if (!(Test-Path ("$ProjectPath/com"))) {
        Expand-Archive -Path "$ProjectPath\mdAddr_JavaCode.zip" -DestinationPath $ProjectPath
        }
        else {
          # Remove the com folder before extracting
          Remove-Item -Path "$ProjectPath/com" -Recurse -Force

          Expand-Archive -Path "$ProjectPath\mdAddr_JavaCode.zip" -DestinationPath $ProjectPath
        }
      }
    }
  }
}

function CheckDLLs() {
  Write-Host "`nDouble checking dll(s) were downloaded."
  $FileMissing = $false 
  if (!(Test-Path ("$ProjectPath\mdAddr.dll"))) {
    Write-Host "mdAddr.dll not found." 
    $FileMissing = $true
  }
  if ($FileMissing) {
    Write-Host "`nMissing the above data file(s).  Please check that your license string and directory are correct."
    return $false
  }
  else {
    return $true
  }
}

########################## Main ############################

Write-Host "`n====================== Melissa Address Object ======================`n                    [ Java | Windows | 64BIT ]`n"

# Get license (either from parameters or user input)
if ([string]::IsNullOrEmpty($license) ) {
  $License = Read-Host "Please enter your license string"
}

# Check for License from Environment Variables 
if ([string]::IsNullOrEmpty($License) ) {
  $License = $env:MD_LICENSE # Get-ChildItem -Path Env:\MD_LICENSE   #[System.Environment]::GetEnvironmentVariable('MD_LICENSE')
}

if ([string]::IsNullOrEmpty($License)) {
  Write-Host "`nLicense String is invalid!"
  Exit
}

# Use Melissa Updater to download data file(s) 
# Download data file(s) 
DownloadDataFiles -license $License     # comment out this line if using DQS Release

# Set data file(s) path
#$DataPath = "C:\\Program Files\\Melissa DATA\\DQT\\Data"			# uncomment this line and change to your DQS Release data file(s) directory

# Download dll(s)
DownloadDlls - license $License

# Download wrapper and com folder
DownloadWrappers -license $License

# Check if all dll(s) have been downloaded. Exit script if missing
$DllsAreDownloaded = CheckDLLs

if (!$DLLsAreDownloaded) {
	Write-Host "`nAborting program, see above.  Press any button to exit."
  $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
  exit
}

Write-Host "All file(s) have been downloaded/updated!"

# Start program
# Build project
Write-Host "`n=========================== BUILD PROJECT =========================="

Set-Location $ProjectPath

javac MelissaAddressObjectWindowsJava.java
jar cvfm MelissaAddressObjectWindowsJava.jar manifest.txt *.class *.dll com\melissadata\*.class

# Run Project
if ([String]::IsNullOrEmpty($address) -and [String]::IsNullOrEmpty($city) -and [String]::IsNullOrEmpty($state) -and [String]::IsNullOrEmpty($zip)){
  java -jar MelissaAddressObjectWindowsJava.jar --license $License --dataPath $DataPath
}
else {
  java -jar MelissaAddressObjectWindowsJava.jar --license "$License" --dataPath "$DataPath" --address "$address" --city "$city" --state "$state" --zip "$zip"
}

Set-Location ..

