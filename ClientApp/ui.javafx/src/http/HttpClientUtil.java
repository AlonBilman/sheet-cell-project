package http;

import common.dto.response.ErrorResponse;
import constants.Constants;
import com.google.gson.Gson;
import dto.CellDataDTO;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class HttpClientUtil {

    private final static SimpleCookieManager SIMPLE_COOKIE_MANAGER = new SimpleCookieManager();

    private final static OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder().cookieJar(SIMPLE_COOKIE_MANAGER).followRedirects(false).build();

    public static final Gson GSON = new Gson();

    public static void setCookieManagerLoggingFacility(Consumer<String> logConsumer) {
        SIMPLE_COOKIE_MANAGER.setLogData(logConsumer);
    }

    public static void runAsyncPost(String url, Map<String, String> queryParams, RequestBody body, Callback callback) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        String finalUrl = urlBuilder.build().toString();

        Request request = new Request.Builder().url(finalUrl).post(body).build();
        Call call = HTTP_CLIENT.newCall(request);
        call.enqueue(callback);
    }

    public static void runAsyncDelete(String url, Map<String, String> queryParams, RequestBody body, Callback callback) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        String finalUrl = urlBuilder.build().toString();

        Request request = new Request.Builder().url(finalUrl).delete(body).build();
        Call call = HTTP_CLIENT.newCall(request);
        call.enqueue(callback);
    }


    public static void runAsyncPut(String baseUrl, Map<String, String> queryParams, RequestBody body, Callback callback) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl).newBuilder();

        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        String finalUrl = urlBuilder.build().toString();

        Request request = new Request.Builder().url(finalUrl).put(body).build();
        Call call = HTTP_CLIENT.newCall(request);
        call.enqueue(callback);
    }

    public static void runAsyncGet(String baseUrl, Map<String, String> queryParams, Callback callback) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl).newBuilder();

        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        String finalUrl = urlBuilder.build().toString();

        Request request = new Request.Builder().url(finalUrl).build();
        Call call = HTTP_CLIENT.newCall(request);
        call.enqueue(callback);
    }

    public static void shutdown() {
        System.out.println("[SHUTDOWN]  Shutting down HTTP CLIENT");
        String finalURL = Constants.BASE_DIRECTORY + Constants.LOGOUT;
        runAsyncGet(finalURL, null, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                cleanUpResources();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                System.out.println("[LOGOUT]    Disconnecting from Server: Server returns: " + response.code());
                cleanUpResources();
            }
        });
    }

    private static void cleanUpResources() {
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
        System.out.println("[SHUTDOWN] Starting cookie cleanup...");
        SIMPLE_COOKIE_MANAGER.clearAllCookies();
        System.out.println("[SHUTDOWN] Cookie cleanup complete.");
    }

    public static ErrorResponse handleErrorResponse(Response response) throws IOException {
        if (response.body() != null) {
            String responseBody = response.body().string();
            return GSON.fromJson(responseBody, ErrorResponse.class);
        }
        return null;
    }

}
