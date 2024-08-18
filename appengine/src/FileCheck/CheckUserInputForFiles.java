package FileCheck;

import java.io.File;
import java.util.Scanner;

public class CheckUserInputForFiles extends CheckForXMLFile {
    public static File checkFileUserInput() {
        String filePath;
        Scanner scanner = new Scanner(System.in);
        File fileToCheck;


        System.out.print("Enter the file path: ");
        filePath = scanner.nextLine();
        fileToCheck = getXMLFile(filePath);


        return fileToCheck;
    }


}