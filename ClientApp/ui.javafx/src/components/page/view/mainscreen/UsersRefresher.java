package components.page.view.mainscreen;

import com.google.gson.reflect.TypeToken;
import http.CallerService;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static http.HttpClientUtil.GSON;

public class UsersRefresher extends TimerTask {

    private final Consumer<List<AppUser>> usersListConsumer;
    private final BooleanProperty shouldUpdate;

    public UsersRefresher(BooleanProperty shouldUpdate, Consumer<List<AppUser>> usersListConsumer) {
        this.shouldUpdate = shouldUpdate;
        this.usersListConsumer = usersListConsumer;
    }

    @Override
    public void run() {

        if (!shouldUpdate.get()) {
            return;
        }

        CallerService caller = new CallerService();
        caller.getAllUsers(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Error: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    caller.handleErrorResponse(response);
                    String jsonArrayOfUsersAndSheets = response.body().string();
                    Type listType = new TypeToken<List<AppUser>>() {
                    }.getType();
                    List<AppUser> usersNames = GSON.fromJson(jsonArrayOfUsersAndSheets, listType);
                    usersListConsumer.accept(usersNames);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });

    }
}