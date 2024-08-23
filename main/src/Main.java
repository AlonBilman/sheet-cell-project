import userInput.CheckUserInput;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        CheckUserInput checkUserInput = new CheckUserInput();
        try{
            checkUserInput.UserStartMenuInput();
        } catch (ClassNotFoundException ignored) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}