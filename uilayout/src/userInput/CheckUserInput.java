package userInput;

import FileCheck.CheckUserInputForFiles;
import jakarta.xml.bind.JAXBException;

import java.io.File;
import java.util.Scanner;


public class CheckUserInput extends CheckUserInputForFiles {

    public static void printMenu() {
        System.out.print("Hello, and welcome to griddler \n");
        System.out.print("Please choose an option from the menu:" + "(1) to load a file, or (2) to start a game from loaded file \n");
        System.out.print("1. load a file \n");
        System.out.print("2. start a game from loaded file \n");
    }

    public File UserStartInput() {
        final char start = '2';
        final char load = '1';
        Scanner scanner = new Scanner(System.in);
        File newFile = null;

        printMenu();
        String userChoice = scanner.nextLine();

        char command = userChoice.charAt(0);

        switch (command) {
            case load:
                System.out.println("Loading file...");
                newFile = checkFileUserInput();
                break;
            case start:
                if(newFile.exists()) {}
                System.out.println("Can't start a game before loading. Try again.\n");
                UserStartInput();
                break;
            default:
                System.out.println("Invalid option. Please try again.\n");
                UserStartInput();
                break;
        }
        scanner.close();
        return newFile;
    }

}

