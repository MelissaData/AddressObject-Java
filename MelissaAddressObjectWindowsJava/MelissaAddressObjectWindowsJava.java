import com.melissadata.*;
import java.io.*;
import java.util.Arrays;
import java.util.List;

public class MelissaAddressObjectWindowsJava {

  public static void main(String args[]) throws IOException {
    // Variables
    String[] arguments = ParseArguments(args);
    String license = arguments[0];
    String testAddress = arguments[1];
    String testCity = arguments[2];
    String testState = arguments[3];
    String testZip = arguments[4];
    String dataPath = arguments[5];

    RunAsConsole(license, testAddress, testCity, testState, testZip, dataPath);
  }

  public static String[] ParseArguments(String[] args) {
    String license = "", testAddress = "", testCity = "", testState = "", testZip = "", dataPath = "";
    List<String> argumentStrings = Arrays.asList("--license", "-l", "--address", "-a", "--city", "-c", "--state", "-s", "--zip", "-z", "--dataPath", "-d");
    for (int i = 0; i < args.length - 1; i++) {
      if ((args[i].equals("--license") || args[i].equals("-l") ) && (!argumentStrings.contains(args[i+1]))) {

        if (args[i + 1] != null) {
          license = args[i + 1];
        }
      }
      if ((args[i].equals("--address") || args[i].equals("-a")) && (!argumentStrings.contains(args[i+1]))) {
        if (args[i + 1] != null) {
          testAddress = args[i + 1];
        }
      }
      if ((args[i].equals("--city") || args[i].equals("-c")) && (!argumentStrings.contains(args[i+1]))) {
        if (args[i + 1] != null) {
          testCity = args[i + 1];
        }
      }
      if ((args[i].equals("--state") || args[i].equals("-s")) && (!argumentStrings.contains(args[i+1]))) {
        if (args[i + 1] != null) {
          testState = args[i + 1];
        }
      }
      if ((args[i].equals("--zip") || args[i].equals("-z")) && (!argumentStrings.contains(args[i+1]))) {
        if (args[i + 1] != null) {
          testZip = args[i + 1];
        }
      }
      if ((args[i].equals("--dataPath") || args[i].equals("-d")) && (!argumentStrings.contains(args[i+1]))) {
        if (args[i + 1] != null) {
          dataPath = args[i + 1];
        }
      }
    }
    return new String[] { license, testAddress, testCity, testState, testZip, dataPath };

  }

  public static void RunAsConsole(String license, String testAddress, String testCity, String testState, String testZip, String dataPath) throws IOException {
    System.out.println("\n\n========== WELCOME TO MELISSA ADDRESS OBJECT WINDOWS JAVA ==========\n");
    AddressObject addressObject = new AddressObject(license, dataPath);
    Boolean shouldContinueRunning = true;

    if (!addressObject.mdAddressObj.GetInitializeErrorString().equals("No error."))
      shouldContinueRunning = false;

    while (shouldContinueRunning) {
      DataContainer dataContainer = new DataContainer();
      BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

      if ((testAddress + testCity + testState + testZip) == null || (testAddress + testCity + testState + testZip).trim().isEmpty())
      {
        System.out.println("\nFill in each value to see the Address Object results");
        
        System.out.print("Address: ");
        dataContainer.Address = stdin.readLine();

        System.out.print("City: ");
        dataContainer.City = stdin.readLine();

        System.out.print("State: ");
        dataContainer.State = stdin.readLine();

        System.out.print("Zip: ");
        dataContainer.Zip = stdin.readLine();


      } else {
        dataContainer.Address = testAddress;
        dataContainer.City = testCity;
        dataContainer.State = testState;
        dataContainer.Zip = testZip;
      }

      // Print user input
      System.out.println("\n============================== INPUTS ==============================\n");
      System.out.println("                      Address: " + dataContainer.Address);
      System.out.println("                         City: " + dataContainer.City);
      System.out.println("                        State: " + dataContainer.State);
      System.out.println("                          Zip: " + dataContainer.Zip);



      // Execute Address Object
      addressObject.ExecuteObjectAndResultCodes(dataContainer);

      // Print output
      System.out.println("\n============================== OUTPUT ==============================\n");
      System.out.println("\n\tAddress Object Information:");

      System.out.println("\t                     MAK: " + addressObject.mdAddressObj.GetMelissaAddressKey());
      System.out.println("\t          Address Line 1: " + addressObject.mdAddressObj.GetAddress());
      System.out.println("\t          Address Line 2: " + addressObject.mdAddressObj.GetAddress2());
      System.out.println("\t                    City: " + addressObject.mdAddressObj.GetCity());
      System.out.println("\t                   State: " + addressObject.mdAddressObj.GetState());
      System.out.println("\t                     Zip: " + addressObject.mdAddressObj.GetZip());
      System.out.println("\t            Result Codes: " + dataContainer.ResultCodes);

      String[] rs = dataContainer.ResultCodes.split(",");
      for (String r : rs) {
        System.out.println("        " + r + ":"
            + addressObject.mdAddressObj.GetResultCodeDescription(r, mdAddr.ResultCdDescOpt.ResultCodeDescriptionLong));
      }

      Boolean isValid = false;
      if ((testAddress + testCity + testState + testZip) != null && !(testAddress + testCity + testState + testZip).trim().isEmpty()) {
        isValid = true;
        shouldContinueRunning = false;
      }

      while (!isValid) {
        System.out.println("\nTest another address? (Y/N)");
        String testAnotherResponse = stdin.readLine();

        if (testAnotherResponse != null && !testAnotherResponse.trim().isEmpty()) {
          testAnotherResponse = testAnotherResponse.toLowerCase();
          if (testAnotherResponse.equals("y")) {
            isValid = true;
          } else if (testAnotherResponse.equals("n")) {
            isValid = true;
            shouldContinueRunning = false;
          } else {
            System.out.println("Invalid Response, please respond 'Y' or 'N'");
          }
        }
      }
    }
    System.out.println("\n=============== THANK YOU FOR USING MELISSA JAVA OBJECT ============\n");

  }
}

class AddressObject {
  // Path to Phone Object data files (.dat, etc)
  String dataFilePath;

  // Create instance of Melissa Phone Object
  mdAddr mdAddressObj = new mdAddr();

  public AddressObject(String license, String dataPath) {
    // Set license string and set path to data files (.dat, etc)
    mdAddressObj.SetLicenseString(license);
    dataFilePath = dataPath;

    mdAddressObj.SetPathToUSFiles(dataPath);
    mdAddressObj.SetPathToAddrKeyDataFiles(dataPath);
    mdAddressObj.SetPathToDPVDataFiles(dataPath);
    mdAddressObj.SetPathToLACSLinkDataFiles(dataPath);
    mdAddressObj.SetPathToRBDIFiles(dataPath);
    mdAddressObj.SetPathToSuiteFinderDataFiles(dataPath);
    mdAddressObj.SetPathToSuiteLinkDataFiles(dataPath);



    // If you see a different date than expected, check your license string and
    // either download the new data files or use the Melissa Updater program to
    // update your data files.

    mdAddr.ProgramStatus pStatus = mdAddressObj.InitializeDataFiles();

    if (pStatus != mdAddr.ProgramStatus.ErrorNone) {
      // Problem during initialization
      System.out.println("Failed to Initialize Object.");
      System.out.println(pStatus);
      return;
    }

    System.out.println("                DataBase Date: " + mdAddressObj.GetDatabaseDate());
    System.out.println("              Expiration Date: " + mdAddressObj.GetLicenseExpirationDate());

    /**
     * This number should match with the file properties of the Melissa Object
     * binary file.
     * If TEST appears with the build number, there may be a license key issue.
     */
    System.out.println("               Object Version: " + mdAddressObj.GetBuildNumber());
    System.out.println();

  }

  // This will call the lookup function to process the input address as well as
  // generate the result codes
  public void ExecuteObjectAndResultCodes(DataContainer data) {
    mdAddressObj.ClearProperties();
    mdAddressObj.SetAddress(data.Address);
    mdAddressObj.SetCity(data.City);
    mdAddressObj.SetState(data.State);
    mdAddressObj.SetZip(data.Zip);
    mdAddressObj.VerifyAddress();
    data.ResultCodes = mdAddressObj.GetResults();

    // ResultsCodes explain any issues Address Object has with the object.
    // List of result codes for Address Object
    // https://wiki.melissadata.com/?title=Result_Code_Details#Address_Object

  }
}

class DataContainer {
  public String Address;
  public String City;
  public String State;
  public String Zip;
  public String ResultCodes;
}
