package components.chatarea;

import components.chatarea.model.ChatLinesWithVersion;
import http.CallerService;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.Timer;
import java.util.stream.Collectors;

import static constants.Constants.CHAT_LINE_FORMATTING;
import static constants.Constants.REFRESH_RATE;

public class ChatController implements Closeable {

    @FXML
    public TextArea chatData;
    @FXML
    public TextField messageInputField;
    @FXML
    public Button sendLineChatButton;

    private IntegerProperty chatVersion;
    private BooleanProperty autoScroll;
    private BooleanProperty autoUpdate;
    private CallerService httpCallerService;
    private ChatAreaRefresher chatAreaRefresher;
    private Timer timer;

    public void initialize() {
        chatVersion = new SimpleIntegerProperty();
        autoScroll = new SimpleBooleanProperty(true);
        autoUpdate = new SimpleBooleanProperty(true);
        httpCallerService = new CallerService();
    }

    public void sendLineChatButtonListener() {
        String chatLine = messageInputField.getText();
        httpCallerService.writeChatLine(chatLine, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Problem sending chat line");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    httpCallerService.handleErrorResponse(response);
                } catch (Exception e) {
                    System.out.println("Problem sending chat line : " + e.getMessage());
                }
            }
        });
        messageInputField.clear();
    }

    private void updateChatLines(ChatLinesWithVersion chatLinesWithVersion) {
        if (chatLinesWithVersion.getVersion() != chatVersion.get()) {
            String deltaChatLines = chatLinesWithVersion
                    .getEntries()
                    .stream()
                    .map(singleChatLine -> {
                        long time = singleChatLine.getTime();
                        return String.format(CHAT_LINE_FORMATTING, time, time, time, singleChatLine.getUsername(), singleChatLine.getChatString());
                    }).collect(Collectors.joining());

            Platform.runLater(() -> {
                chatVersion.set(chatLinesWithVersion.getVersion());

                if (autoScroll.get()) {
                    chatData.appendText(deltaChatLines);
                    chatData.selectPositionCaret(chatData.getLength());
                    chatData.deselect();
                } else {
                    int originalCaretPosition = chatData.getCaretPosition();
                    chatData.appendText(deltaChatLines);
                    chatData.positionCaret(originalCaretPosition);
                }
            });
        }
    }

    public void startChatRefresher() {
        chatAreaRefresher = new ChatAreaRefresher(
                chatVersion,
                autoUpdate,
                this::updateChatLines);
        timer = new Timer();
        timer.schedule(chatAreaRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    @Override
    public void close() {
        chatVersion.set(0);
        chatData.clear();

        if (chatAreaRefresher != null) {
            chatAreaRefresher.cancel();
            chatAreaRefresher = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}