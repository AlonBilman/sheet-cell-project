import userInput.CheckUserInput;

public class Main {
    public static void main(String[] args) {
        CheckUserInput checkUserInput = new CheckUserInput();
        try {
            checkUserInput.UserStartMenuInput();
        } catch (Exception e) {
            System.out.println("Something went wrong with loading the program \n" +
                    "Error message: " + e.getMessage());
            System.out.println("Please try again");
        }
    }
}