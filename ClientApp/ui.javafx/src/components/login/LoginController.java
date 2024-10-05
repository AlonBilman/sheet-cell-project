package components.login;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.concurrent.Task;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

public class LoginController {

    @FXML
    private Text titleText;

    @FXML
    private TextField usernameField;

    @FXML
    private Button loginButton;

    @FXML
    private ProgressIndicator spinner;

    // Listener for successful login
    private Runnable loginListener;

    // Ensures the TextField is focused when the window opens
    @FXML
    public void initialize() {
        usernameField.requestFocus();
        usernameField.setOnKeyPressed(this::handleKeyPress); // Set key event handler
    }

    // Method to set the login listener
    public void setLoginListener(Runnable listener) {
        this.loginListener = listener;
    }

    // Handle Enter key press to trigger login
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            onLoginButtonClick(); // Call login method when Enter is pressed
        }
    }

    @FXML
    private void onLoginButtonClick() {
        // Disable the button and show the spinner to simulate "thinking"
        loginButton.setDisable(true);
        spinner.setVisible(true);

        // Create a Task to simulate a background process (e.g., logging in)
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Simulate processing time (3 seconds)
                Thread.sleep(3000); // Replace this with actual login validation logic
                return null;
            }

            @Override
            protected void succeeded() {
                // Re-enable the button and hide the spinner after processing
                loginButton.setDisable(false);
                spinner.setVisible(false);

                // Notify listener on successful login
                if (loginListener != null) {
                    loginListener.run(); // Notify the Main class to show the main app
                }
            }
        };

        // Start the task in a new thread
        new Thread(task).start();
    }
}
