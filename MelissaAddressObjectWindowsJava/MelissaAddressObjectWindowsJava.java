import com.melissadata.*;
import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Address Object corrects, verifies and enhances U.S. and Canadian addresses.
 * Use Address Object to remove bad or incomplete information before it invades your
 * database and creates a negative impact on your data-driven initiatives. You'll reduce
 * undeliverables, increase communication efforts, and save money on all your marketing
 * campaigns.
 *
 * <p>High-level flow of this sample:
 * <ol>
 *   <li>SETUP     - create an mdAddr instance, hand it the license string and the
 *                   paths to the data files, then InitializeDataFiles() (one time).</li>
 *   <li>INPUT     - feed an address in with SetAddress/SetCity/SetState/SetZip.</li>
 *   <li>PROCESS   - VerifyAddress() validates, standardizes, and corrects the address.</li>
 *   <li>READ      - pull the corrected fields back out with the Get* getters
 *                   (GetAddress, GetCity, GetState, GetZip, GetMelissaAddressKey, ...).</li>
 *   <li>INTERPRET - GetResults() returns comma-separated result codes describing
 *                   what the object did/found; each code has a human description.</li>
 * </ol>
 *
 * <p>The pieces in this file map onto that flow:
 * <ul>
 *   <li>main / RunAsConsole / ParseArguments : console harness (argument parsing + the interactive loop).</li>
 *   <li>AddressObject                         : thin wrapper around mdAddr that owns setup + the call sequence.</li>
 *   <li>DataContainer                         : plain holder for one record's input and output.</li>
 * </ul>
 *
 * <p>Where mdAddr comes from:
 * The mdAddr and mdAddrJNI classes in com/melissadata come from mdAddr_JavaCode.zip,
 * which the accompanying MelissaAddressObjectWindowsJava.ps1 script downloads and
 * expands into com/melissadata on every run. mdAddrJNI declares the native methods and
 * loads mdAddrJavaWrapper.dll, the JNI shim that calls into mdAddr.dll.
 *
 * <p>Reference:
 * <ul>
 *   <li>Quickstart    : https://docs.melissa.com/on-premise-api/address-object/address-object-quickstart.html</li>
 *   <li>Release notes : https://releasenotes.melissa.com/on-premise-api/address-object/</li>
 *   <li>Result codes  : https://docs.melissa.com/on-premise-api/address-object/result-codes.html</li>
 * </ul>
 */
public class MelissaAddressObjectWindowsJava {

  /**
   * Entry point. Reads the optional command-line arguments, then hands control to
   * RunAsConsole, which performs the actual Address Object setup and processing.
   *
   * @param args The raw command-line arguments
   * @throws IOException if reading from standard input fails
   */
  public static void main(String args[]) throws IOException {
    // Populated by ParseArguments below.
    String[] arguments = ParseArguments(args);
    String license = arguments[0];
    String testAddress = arguments[1];
    String testCity = arguments[2];
    String testState = arguments[3];
    String testZip = arguments[4];
    String dataPath = arguments[5];

    RunAsConsole(license, testAddress, testCity, testState, testZip, dataPath);
  }

  /**
   * Reads the supported command-line options and returns them.
   *
   * <p>Recognized flags (each followed by its value, e.g. "--address 22382 Avenida Empresa"):
   * <ul>
   *   <li>--license / -l   : the Melissa license string</li>
   *   <li>--dataPath / -d  : path to the Address Object data files</li>
   *   <li>--address / -a   : street address to test in one-shot mode</li>
   *   <li>--city / -c      : city to test in one-shot mode</li>
   *   <li>--state / -s     : state to test in one-shot mode</li>
   *   <li>--zip / -z       : ZIP code to test in one-shot mode</li>
   * </ul>
   *
   * <p>A flag is only consumed when the token after it is not itself a recognized flag,
   * and a flag in the final position is ignored, so a value-less flag cannot swallow the
   * next option.
   *
   * @param args The raw command-line arguments to parse.
   * @return A String array of { license, testAddress, testCity, testState, testZip, dataPath }.
   */
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

  /**
   * Sets up the Address Object once, then drives the input -> process -> output cycle.
   *
   * <p>In interactive mode (no address args) it loops, asking for a new address each pass
   * until the user answers "N". In one-shot mode (address args supplied) it runs a
   * single pass and exits.
   *
   * @param license     The Melissa license string used to initialize the object.
   * @param testAddress A street address to process in one-shot mode; if empty, the program prompts interactively.
   * @param testCity    A city to process in one-shot mode.
   * @param testState   A state to process in one-shot mode.
   * @param testZip     A ZIP code to process in one-shot mode.
   * @param dataPath    Path to the Address Object data files.
   * @throws IOException if reading from standard input fails
   */
  public static void RunAsConsole(String license, String testAddress, String testCity, String testState, String testZip, String dataPath) throws IOException {
    System.out.println("\n\n========== WELCOME TO MELISSA ADDRESS OBJECT WINDOWS JAVA ==========\n");

    // Construct the wrapper. This is where the object is licensed, pointed at the
    // data files, and initialized (see the AddressObject constructor below).
    AddressObject addressObject = new AddressObject(license, dataPath);
    Boolean shouldContinueRunning = true;

    // Gate the program on a successful initialization. If the data files could not
    // be loaded (bad/expired license, missing or wrong-path data files, ...),
    // GetInitializeErrorString() returns the reason instead of "No error." and we
    // skip the processing loop entirely.
    if (!addressObject.mdAddressObj.GetInitializeErrorString().equals("No error."))
      shouldContinueRunning = false;

    while (shouldContinueRunning) {
      // Holder for this pass's input and result codes.
      DataContainer dataContainer = new DataContainer();
      BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

      if ((testAddress + testCity + testState + testZip) == null || (testAddress + testCity + testState + testZip).trim().isEmpty())
      {
        // Interactive mode: prompt the user for each address component.
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
        // One-shot mode: use the address passed on the command line.
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
      // Runs the verify sequence and stores the result codes on dataContainer.
      addressObject.ExecuteObjectAndResultCodes(dataContainer);

      // Print output
      // Each Get* getter below returns one component the object produced for the most
      // recently processed address. These read directly from the mdAddr instance, which
      // still holds the results from the Execute call above.
      System.out.println("\n============================== OUTPUT ==============================\n");
      System.out.println("\n\tAddress Object Information:");

      System.out.println("\t                     MAK: " + addressObject.mdAddressObj.GetMelissaAddressKey());
      System.out.println("\t          Address Line 1: " + addressObject.mdAddressObj.GetAddress());
      System.out.println("\t          Address Line 2: " + addressObject.mdAddressObj.GetAddress2());
      System.out.println("\t                    City: " + addressObject.mdAddressObj.GetCity());
      System.out.println("\t                   State: " + addressObject.mdAddressObj.GetState());
      System.out.println("\t                     Zip: " + addressObject.mdAddressObj.GetZip());
      System.out.println("\t            Result Codes: " + dataContainer.ResultCodes);

      // Result codes come back as a single comma-separated string (e.g. "AS01,AC01").
      // Split it and ask the object for a readable description of each code.
      // ResultCodeDescriptionLong requests the long-form text; a short form is also
      // available via ResultCodeDescriptionShort
      String[] rs = dataContainer.ResultCodes.split(",");
      for (String r : rs) {
        System.out.println("        " + r + ":"
            + addressObject.mdAddressObj.GetResultCodeDescription(r, mdAddr.ResultCdDescOpt.ResultCodeDescriptionLong));
      }

      Boolean isValid = false;

      // In one-shot mode there is nothing more to do after a single pass: mark the
      // input handled and stop the outer loop.
      if ((testAddress + testCity + testState + testZip) != null && !(testAddress + testCity + testState + testZip).trim().isEmpty()) {
        isValid = true;
        shouldContinueRunning = false;
      }

      // Interactive mode: ask whether to process another address. Keep prompting until
      // we get a valid Y/N. "N" ends the program; "Y" falls through to another pass.
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

/**
 * Wrapper that owns a single Melissa Address Object instance and encapsulates the two
 * things every Melissa object needs: one-time setup (license + data files) and the
 * per-record processing sequence. Reuse one instance across many addresses; do NOT
 * re-initialize per address.
 */
class AddressObject {
  // Path to the Address Object data files.
  String dataFilePath;

  // The underlying Melissa Address Object instance.
  mdAddr mdAddressObj = new mdAddr();

  /**
   * Performs the mandatory one-time setup, in this required order:
   * <ol>
   *   <li>SetLicenseString     - authorize the object.</li>
   *   <li>SetPathTo*DataFiles  - tell it where each set of data files lives.</li>
   *   <li>InitializeDataFiles  - load the data into memory.</li>
   * </ol>
   *
   * @param license  The Melissa license string used to authorize the object.
   * @param dataPath Path to the folder containing the Address Object data files.
   */
  public AddressObject(String license, String dataPath) {
    // Set license string and set path to data files
    mdAddressObj.SetLicenseString(license);
    dataFilePath = dataPath;

    // Address Object draws on several USPS data sets; point each one at the data folder.
    mdAddressObj.SetPathToUSFiles(dataPath);
    mdAddressObj.SetPathToAddrKeyDataFiles(dataPath);
    mdAddressObj.SetPathToDPVDataFiles(dataPath);
    mdAddressObj.SetPathToLACSLinkDataFiles(dataPath);
    mdAddressObj.SetPathToRBDIFiles(dataPath);
    mdAddressObj.SetPathToSuiteFinderDataFiles(dataPath);
    mdAddressObj.SetPathToSuiteLinkDataFiles(dataPath);



    // Load the data files. The returned ProgramStatus reports whether initialization succeeded.
    // If you see a different date than expected, check your license string and either download the new data files
    // or use the Melissa Updater program to update your data files.
    mdAddr.ProgramStatus pStatus = mdAddressObj.InitializeDataFiles();

    // If an issue occurred, please investigate the common causes.
    // Common causes: an invalid/expired license, or missing/wrong-path data files.
    if (pStatus != mdAddr.ProgramStatus.ErrorNone) {
      System.out.println("Failed to Initialize Object.");
      System.out.println(pStatus);
      return;
    }

    // Diagnostic information, handy for confirming the object loaded the data you expect:

    // Build date of the data files
    System.out.println("                DataBase Date: " + mdAddressObj.GetDatabaseDate());

    // When the license stops working
    System.out.println("              Expiration Date: " + mdAddressObj.GetLicenseExpirationDate());

    // This number should match with the file properties of the Melissa Object binary file.
    // If TEST appears with the build number, there may be a license key issue.
    System.out.println("               Object Version: " + mdAddressObj.GetBuildNumber());
    System.out.println();

  }

  /**
   * Runs the full Address Object processing sequence for one address and captures its
   * result codes. This is the canonical per-record call pattern to copy into your
   * own application:
   * ClearProperties -> SetAddress/SetCity/SetState/SetZip -> VerifyAddress -> GetResults
   *
   * @param data The record to process. Its Address/City/State/Zip are read as input, and
   *             ResultCodes is populated with this run's result codes.
   */
  public void ExecuteObjectAndResultCodes(DataContainer data) {

    // Reset any state left over from a previous address. Important when reusing the same
    // object across multiple records so fields from a prior address don't bleed into this one.
    mdAddressObj.ClearProperties();

    // Supply the raw input fields to process
    mdAddressObj.SetAddress(data.Address);
    mdAddressObj.SetCity(data.City);
    mdAddressObj.SetState(data.State);
    mdAddressObj.SetZip(data.Zip);

    // Validate, standardize, and correct the address
    mdAddressObj.VerifyAddress();

    // Collect the result codes for this run
    // ResultsCodes explain any issues Address Object has with the object.
    // List of result codes for Address Object
    // https://docs.melissa.com/on-premise-api/address-object/result-codes.html
    data.ResultCodes = mdAddressObj.GetResults();
  }
}

/**
 * Data holder for a single record: carries the input address in and the result codes out.
 */
class DataContainer {
  // Input: the street address to process.
  public String Address;

  // Input: the city to process.
  public String City;

  // Input: the state to process.
  public String State;

  // Input: the ZIP code to process.
  public String Zip;

  // Output: comma-separated result codes from GetResults().
  public String ResultCodes;
}
