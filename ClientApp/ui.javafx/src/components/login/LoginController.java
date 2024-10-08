package components.login;

import com.google.gson.Gson;
import http.HttpClientUtil;
import jakarta.servlet.http.HttpServletResponse;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.concurrent.Task;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import okhttp3.*;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;

import static constants.constants.BASE_DIRECTORY;

import static constants.constants.LOGIN;
import static http.HttpClientUtil.shutdown;

public class LoginController {
    @FXML
    public Label errorLabel;
    @FXML
    private Text titleText;
    @FXML
    private TextField usernameField;
    @FXML
    private Button loginButton;
    @FXML
    private ProgressIndicator spinner;

    private final StringProperty errorMessageProperty = new SimpleStringProperty();

    // Listener for successful login
    private Runnable loginListener;

    // Ensures the TextField is focused when the window opens
    @FXML
    public void initialize() {
        errorLabel.setTextFill(Paint.valueOf("red"));
        errorLabel.textProperty().bind(errorMessageProperty);
        errorLabel.setTextAlignment(TextAlignment.CENTER);
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
        Gson gson = new Gson();
        errorMessageProperty.set("");
        errorLabel.setVisible(false);
        String finalUrl = BASE_DIRECTORY + LOGIN;
        String jsonUserName = gson.toJson(usernameField.getText().trim());
        RequestBody body = RequestBody.create(jsonUserName, MediaType.get("application/json; charset=utf-8"));

        HttpClientUtil.runAsyncWithBody(finalUrl, body, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                            errorMessageProperty.set("Something went wrong: " + e.getMessage());
                            errorLabel.setVisible(true);
                        }
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    int statusCode = response.code();
                    if (statusCode == HttpServletResponse.SC_CREATED) {
                        Platform.runLater(() -> {
                            loginButton.setVisible(false);
                            errorMessageProperty.set("");
                            spinner.setVisible(true);

                            // Create a Task to simulate a background process (e.g., logging in)
                            Task<Void> task = new Task<Void>() {
                                @Override
                                protected Void call() throws Exception {
                                    // Simulate processing time (3 seconds)
                                    Thread.sleep(1000); // Replace this with actual login validation logic
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
                        });
                    } else {
                        String responseBody = response.body() != null ? response.body().string() : "No response body";
                        String errorMessage = gson.fromJson(responseBody, String.class);
                        Platform.runLater(() -> {
                                    errorMessageProperty.set("Something went wrong: \n" + errorMessage);
                                    errorLabel.setVisible(true);
                                }
                        );
                    }
                } finally {
                    if (response.body() != null) {
                        response.body().close(); // Ensure the response body is closed after use
                    }
                }
            }

        });
    }

}

