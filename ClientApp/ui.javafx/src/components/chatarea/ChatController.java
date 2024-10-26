package components.chatarea;

import components.chatarea.model.ChatLinesWithVersion;
import components.page.view.mainscreen.MainScreenController;
import http.CallerService;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.stream.Collectors;

import static constants.Constants.CHAT_LINE_FORMATTING;
import static constants.Constants.REFRESH_RATE;


public class ChatController implements Closeable {

    private final IntegerProperty chatVersion;
    private final BooleanProperty autoScroll;
    private final BooleanProperty autoUpdate;

    public TextArea chatData;
    public TextField messageInputField;
    public Button sendLineChatButton;

    private MainScreenController mainScreenController;
    private CallerService httpCallerService;
    private Map<String, String> query;

    private ChatAreaRefresher chatAreaRefresher;
    private Timer timer;

    public void setMainScreenControllerAndCallerService(MainScreenController mainScreenController, CallerService httpCallerService) {
        this.mainScreenController = mainScreenController;
        this.httpCallerService = httpCallerService;
    }

    public ChatController() {
        chatVersion = new SimpleIntegerProperty();
        autoScroll = new SimpleBooleanProperty();
        autoUpdate = new SimpleBooleanProperty();
    }

    @FXML
    public void initialize() {
        query = new HashMap<>();
    }

    public void sendLineChatButtonListener() {
        String chatLine = messageInputField.getText();
        query.clear();
        query.put("userstring", chatLine);
        //sends the line to the server.
    }


    //    @FXML
//    void sendButtonClicked(ActionEvent event) {
//        String chatLine = messageInputField.getText();
//        String finalUrl = HttpUrl
//                .parse(Constants.SEND_CHAT_LINE)
//                .newBuilder()
//                .addQueryParameter("userstring", chatLine)
//                .build()
//                .toString();
//
//        httpStatusUpdate.updateHttpLine(finalUrl);
//        HttpClientUtil.runAsync(finalUrl, new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                httpStatusUpdate.updateHttpLine("Attempt to send chat line [" + chatLine + "] request ended with failure...:(");
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                if (!response.isSuccessful()) {
//                    httpStatusUpdate.updateHttpLine("Attempt to send chat line [" + chatLine + "] request ended with failure. Error code: " + response.code());
//                }
//            }
//        });
//
//        chatLineTextArea.clear();
//    }
//
//    public void setHttpStatusUpdate(HttpStatusUpdate chatRoomMainController) {
//        this.httpStatusUpdate = chatRoomMainController;
//    }
//
//    private void updateChatLines(ChatLinesWithVersion chatLinesWithVersion) {
//        if (chatLinesWithVersion.getVersion() != chatVersion.get()) {
//            String deltaChatLines = chatLinesWithVersion
//                    .getEntries()
//                    .stream()
//                    .map(singleChatLine -> {
//                        long time = singleChatLine.getTime();
//                        return String.format(CHAT_LINE_FORMATTING, time, time, time, singleChatLine.getUsername(), singleChatLine.getChatString());
//                    }).collect(Collectors.joining());
//
//            Platform.runLater(() -> {
//                chatVersion.set(chatLinesWithVersion.getVersion());
//
//                if (autoScroll.get()) {
//                    mainChatLinesTextArea.appendText(deltaChatLines);
//                    mainChatLinesTextArea.selectPositionCaret(mainChatLinesTextArea.getLength());
//                    mainChatLinesTextArea.deselect();
//                } else {
//                    int originalCaretPosition = mainChatLinesTextArea.getCaretPosition();
//                    mainChatLinesTextArea.appendText(deltaChatLines);
//                    mainChatLinesTextArea.positionCaret(originalCaretPosition);
//                }
//            });
//        }
//    }
//
//    public void startListRefresher() {
//        chatAreaRefresher = new ChatAreaRefresher(
//                chatVersion,
//                autoUpdate,
//                httpStatusUpdate::updateHttpLine,
//                this::updateChatLines);
//        timer = new Timer();
//        timer.schedule(chatAreaRefresher, REFRESH_RATE, REFRESH_RATE);
//    }
//
    @Override
    public void close() throws IOException {
        chatVersion.set(0);
        //chatLineTextArea.clear();
        if (chatAreaRefresher != null && timer != null) {
            chatAreaRefresher.cancel();
            timer.cancel();
        }
    }
}


