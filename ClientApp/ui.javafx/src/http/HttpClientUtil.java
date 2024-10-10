package http;

import constants.Constants;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.Consumer;

public class HttpClientUtil {

    private final static SimpleCookieManager simpleCookieManager = new SimpleCookieManager();

    private final static OkHttpClient HTTP_CLIENT =
            new OkHttpClient.Builder()
                    .cookieJar(simpleCookieManager)
                    .followRedirects(false)
                    .build();

    public static void setCookieManagerLoggingFacility(Consumer<String> logConsumer) {
        simpleCookieManager.setLogData(logConsumer);
    }

    public static void runAsyncPost(String finalUrl, RequestBody body, Callback callback) {
        Request request = new Request.Builder().url(finalUrl).post(body).build();
        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);
        call.enqueue(callback);
    }
    public static void runAsyncGet(String finalUrl, Callback callback) {
        Request request = new Request.Builder().url(finalUrl).build();
        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);
        call.enqueue(callback);
    }

    public static void shutdown() {
        System.out.println("[SHUTDOWN]  Shutting down HTTP CLIENT");
        String finalURL = Constants.BASE_DIRECTORY+Constants.LOGOUT;
        runAsyncGet(finalURL, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //Ignore - this happens when a client open the login page and close it immediately
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println("[LOGOUT]    Disconnecting from Server : Server returns : " + response.code());
                if(response.body() != null) {
                    System.out.println(response.body().string());
                }
            }
        });
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
        simpleCookieManager.clearAllCookies();
    }
}
