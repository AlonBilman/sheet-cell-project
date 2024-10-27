package components.chatarea;

import components.chatarea.model.ChatLinesWithVersion;
import http.CallerService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.function.Consumer;

import static constants.Constants.GSON;
import static constants.Constants.VERSION_PARAM;

public class ChatAreaRefresher extends TimerTask {
    private final Consumer<ChatLinesWithVersion> chatlinesConsumer;
    private final IntegerProperty chatVersion;
    private final BooleanProperty shouldUpdate;
    private final CallerService httpCallerService;
    private final Map<String, String> query;

    public ChatAreaRefresher(IntegerProperty chatVersion, BooleanProperty shouldUpdate, Consumer<ChatLinesWithVersion> chatlinesConsumer) {
        this.chatlinesConsumer = chatlinesConsumer;
        this.chatVersion = chatVersion;
        this.shouldUpdate = shouldUpdate;
        httpCallerService = new CallerService();
        query = new HashMap<>();
    }

    @Override
    public void run() {

        if (!shouldUpdate.get())
            return;

        query.clear();
        query.put(VERSION_PARAM, String.valueOf(chatVersion.get()));
        httpCallerService.getDeltaFetchingChatLines(query, new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    httpCallerService.handleErrorResponse(response);
                    ChatLinesWithVersion chatLinesWithVersion = GSON.fromJson(response.body().string(), ChatLinesWithVersion.class);
                    chatlinesConsumer.accept(chatLinesWithVersion);
                } catch (Exception e) {
                    System.out.println("Failed receiving chat lines: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Failed receiving chat lines");
            }
        });
    }
}
