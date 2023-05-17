# Melissa Address Object Windows Java


## Purpose

This code showcases the Melissa Address Object using Java.

Please feel free to copy or embed this code to your own project. Happy coding!

For the latest Melissa Address Object release notes, please visit: https://releasenotes.melissa.com/on-premise-api/address-object/

The console will ask the user for:

- Address
- City
- State
- Zip

And return 

- Melissa Address Key (MAK)
- Address Line 1
- Address Line 2
- City
- State
- Zip
- ResultCodes


----------------------------------------

## Tested Environments

- Windows 64-bit Java 19
- Powershell 5.1
- Melissa data files for 2023-05

----------------------------------------

## Required Files and Programs

#### mdAddr.dll

This is the c++ code of the Melissa Object. This file will be downloaded by Melissa Updater!

#### mdAddrJavaWrapper.dll

This file needs to be added as a Project Dependency.  This wrapper will need to be in the same directory as the program using it.

#### Data Files
- Addr.dbf
- Congress.csv
- dph256.dte
- dph256.hsa
- dph256.hsc
- dph256.hsf
- dph256.hsn
- dph256.hsv
- dph256.hsx
- ews.txt
- lcd256
- mdAddr.dat
- mdAddr.lic
- mdAddr.nat
- mdAddr.str
- mdAddrKey.db
- mdAddrKeyCA.db
- mdCanada3.db
- mdCanadaPOC.db
- mdLACS256.dat
- mdRBDI.dat
- mdSteLink256.dat
- mdSuiteFinder.db
- month256.dat

 
----------------------------------------
## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.
This project is compatible with Java 19

#### Install Java

Before starting, make sure that Java has been correctly installed on your machine and your environment paths are configured. 

You can download Java here: 
https://www.oracle.com/java/technologies/downloads/

You can check that your environment is set up correctly by opening a command prompt window and typing the following:
`java --version`

![alt text](/screenshots/java_version.PNG)

If you see the version number then you have installed Java and set up your environment paths correctly!


#### Set up Powershell settings

If running Powershell for the first time, you will need to run this command in the Powershell console: `Set-ExecutionPolicy RemoteSigned`.
The console will then prompt you with the following warning shown in the image below. 
 - Enter `'A'`. 
 	- If successful, the console will not output any messages. (You may need to run Powershell as administrator to enforce this setting).
	
 ![alt text](/screenshots/powershell_executionpolicy.png)

----------------------------------------

#### Download this project
```
$ git clone https://github.com/MelissaData/AddressObject-Java.git
$ cd AddressObject-Java
```

#### Set up Melissa Updater 

Melissa Updater is a CLI application allowing the user to update their Melissa applications/data. 

- Download Melissa Updater here: <https://releases.melissadata.net/Download/Library/WINDOWS/NET/ANY/latest/MelissaUpdater.exe>
- Create a folder within the cloned repo called `MelissaUpdater`.
- Put `MelissaUpdater.exe` in `MelissaUpdater` folder you just created.

----------------------------------------

#### Different ways to get data file(s)
1.  Using Melissa Updater
	- It will handle all of the data download/path and dll(s) for you. 
2.  If you already have the latest DQS Release (ZIP), you can find the data file(s) and dll(s) in there
	- Use the location of where you copied/installed the data and update the "$DataPath" variable in the powershell script.
	- Copy all the dll(s) mentioned above into the `MelissaAddressObjectWindowsJava` project folder.
	
## Run Powershell Script
Parameters:
- -address: a test street address (house number & street name)
- -city (optional): a test city
- -state (optional): a test state
- -zip (optional): a test zip code
 	
  These are convenient when you want to get results for a specific address number in one run instead of testing multiple address numbers in interactive mode.

- -license (optional): a license string to test the Address Object
- -quiet (optional): add to the command if you do not want to get any console output from the Melissa Updater

When you have modified the script to match your data location, let's run the script. There are two modes:
- Interactive 

	The script will prompt the user for a address number, then use the provided number to test Address Object.  For example:
	```
	$ .\MelissaAddressObjectWindowsJava.ps1
	```
    For quiet mode:
    ```
    $ .\MelissaAddressObjectWindowsJava.ps1 -quiet
    ```
- Command Line 

    You can pass an address, city, state, zip, and a license string into the `-address`, `-city`, `-state`, `-zip`, and `-license` parameters respectively to test Address Object. For example:
    
    With all parameters:
    ```
    $ .\MelissaAddressObjectWindowsJava.ps1 -address "22382 Avenida Empresa" -city "Rancho Santa Margarita" -state "CA" -zip "92688"
    $ .\MelissaAddressObjectWindowsJava.ps1 -address "22382 Avenida Empresa" -city "Rancho Santa Margarita" -state "CA" -zip "92688" -license "<your_license_string>"
    ```

    With any known (optional) parameters:
    ```
    $ .\MelissaAddressObjectWindowsJava.ps1 -address "22382 Avenida Empresa" -state "CA" 
    $ .\MelissaAddressObjectWindowsJava.ps1 -address "22382 Avenida Empresa" -state "CA" -license "<your_license_string>"
    ```

    For quiet mode:
    ```
    $ .\MelissaAddressObjectWindowsJava.ps1 -address "22382 Avenida Empresa" -city "Rancho Santa Margarita" -state "CA" -zip "92688" -quiet
    $ .\MelissaAddressObjectWindowsJava.ps1 -address "22382 Avenida Empresa" -city "Rancho Santa Margarita" -state "CA" -zip "92688" -license "<your_license_string>" -quiet
  
This is the expected output from a successful setup for interactive mode:

![alt text](/screenshots/output.png)

    
## Troubleshooting

Troubleshooting for errors found while running your program.

### Errors:

| Error      | Description |
| ----------- | ----------- |
| ErrorRequiredFileNotFound      | Program is missing a required file. Please check your Data folder and refer to the list of required files above. If you are unable to obtain all required files through the Melissa Updater, please contact technical support below. |
| ErrorDatabaseExpired   | .db file(s) are expired. Please make sure you are downloading and using the latest release version. (If using the Melissa Updater, check powershell script for '$RELEASE_VERSION = {version}'  and change the release version if you are using an out of date release).     |
| ErrorFoundOldFile   | File(s) are out of date. Please make sure you are downloading and using the latest release version. (If using the Melissa Updater, check powershell script for '$RELEASE_VERSION = {version}'  and change the release version if you are using an out of date release).    |
| ErrorLicenseExpired   | Expired license string. Please contact technical support below. |


## Contact Us

For free technical support, please call us at 800-MELISSA ext. 4
(800-635-4772 ext. 4) or email us at tech@melissa.com.

To purchase this product, contact Melissa sales department at
800-MELISSA ext. 3 (800-635-4772 ext. 3).
