package FileCheck;

import java.io.File;
import java.util.Scanner;

public class CheckUserInputForFiles extends CheckForXMLFile {
    public static File checkFileUserInput() {
        String filePath;
        Scanner scanner = new Scanner(System.in);
        File fileToCheck;

        do {
            System.out.print("Enter the file path: ");
            filePath = scanner.nextLine();
            fileToCheck = getXMLFile(filePath);

            if (fileToCheck == null) {
                System.out.println("Invalid file. Ensure it exists and is an XML file. Please try again.");
            }
        } while (fileToCheck == null);

        scanner.close();
        return fileToCheck;
    }


}