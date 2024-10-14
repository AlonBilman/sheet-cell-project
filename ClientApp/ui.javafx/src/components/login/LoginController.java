package components.login;

import com.google.gson.Gson;
import http.HttpClientUtil;
import jakarta.servlet.http.HttpServletResponse;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.concurrent.Task;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static constants.Constants.BASE_DIRECTORY;

import static constants.Constants.LOGIN;

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

    private Runnable loginListener;

    @FXML
    public void initialize() {
        errorLabel.setTextFill(Paint.valueOf("red"));
        errorLabel.textProperty().bind(errorMessageProperty);
        errorLabel.setTextAlignment(TextAlignment.CENTER);
        usernameField.requestFocus();
        usernameField.setOnKeyPressed(this::handleKeyPress);
        HttpClientUtil.setCookieManagerLoggingFacility(message -> System.out.println("[Login] " + message));

    }

    public void setLoginListener(Runnable listener) {
        this.loginListener = listener;
    }

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

        HttpClientUtil.runAsyncPost(finalUrl,null ,body, new Callback() {

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
                    if (statusCode == HttpServletResponse.SC_OK) {
                        Platform.runLater(() -> {
                            loginButton.setVisible(false);
                            errorMessageProperty.set("");
                            spinner.setVisible(true);
                            // Loading simulation (sort of)
                            Task<Void> task = new Task<Void>() {
                                @Override
                                protected Void call() throws Exception {
                                    Thread.sleep(1000);
                                    return null;
                                }

                                @Override
                                protected void succeeded() {
                                    loginButton.setDisable(false);
                                    spinner.setVisible(false);

                                    if (loginListener != null) {
                                        loginListener.run(); // Notify the main class to show the main app
                                    }
                                }
                            };

                            new Thread(task).start();
                        });
                    } else {
                        HttpClientUtil.ErrorResponse errorResponse = HttpClientUtil.handleErrorResponse(response);
                        if(errorResponse != null) {
                            Platform.runLater(() -> {
                                errorMessageProperty.set("Something went wrong: \n" + errorResponse.getError());
                                errorLabel.setVisible(true);
                            });
                        }
                    }
                } finally {
                    if (response.body() != null) {
                        response.body().close(); // Close response body after use
                    }
                }
            }

        });
    }

}

