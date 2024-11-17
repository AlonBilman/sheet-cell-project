package components.header.title;

import com.google.gson.Gson;
import http.utils.CallerService;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;


import java.io.IOException;
import java.util.Map;
import java.util.TimerTask;
import java.util.function.Consumer;

public class VersionRefresher extends TimerTask {

    private final Gson GSON = new Gson();
    private final Consumer<Integer> versionConsumer;
    private final BooleanProperty shouldUpdate;
    private final Map<String, String> queryParams;

    public VersionRefresher(BooleanProperty shouldUpdate, Consumer<Integer> versionConsumer, Map<String, String> queryParams) {
        this.shouldUpdate = shouldUpdate;
        this.versionConsumer = versionConsumer;
        this.queryParams = queryParams;
    }

    @Override
    public void run() {
        if (!shouldUpdate.get()) {
            return;
        }
        CallerService caller = new CallerService();

        caller.getUpdatedVersionNumberAsync(queryParams, new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    caller.handleErrorResponse(response);
                    int version = GSON.fromJson(response.body().string(), Integer.class);
                    versionConsumer.accept(version);
                } catch (Exception e) {
                    System.out.println("Error processing version response: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Error fetching version: " + e.getMessage());
            }
        });
    }
}
