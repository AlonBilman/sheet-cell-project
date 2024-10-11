package http;

import constants.Constants;
import com.google.gson.Gson;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

public class HttpClientUtil {

    private final static SimpleCookieManager simpleCookieManager = new SimpleCookieManager();

    private final static OkHttpClient HTTP_CLIENT =
            new OkHttpClient.Builder()
                    .cookieJar(simpleCookieManager)
                    .followRedirects(false)
                    .build();

    private static final Gson GSON = new Gson();

    public static void setCookieManagerLoggingFacility(Consumer<String> logConsumer) {
        simpleCookieManager.setLogData(logConsumer);
    }

    public static void runAsyncPost(String finalUrl, RequestBody body, Callback callback) {
        Request request = new Request.Builder().url(finalUrl).post(body).build();
        Call call = HTTP_CLIENT.newCall(request);
        call.enqueue(callback);
    }

    public static void runAsyncGet(String baseUrl, Map<String, String> queryParams, Callback callback) {
        // Construct the full URL with query parameters
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl).newBuilder();

        // Add each query parameter to the URL
        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        String finalUrl = urlBuilder.build().toString();

        // Create the request with the final URL
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
                // Ignore - this happens when a client opens the login page and closes it immediately
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println("[LOGOUT]    Disconnecting from Server: Server returns: " + response.code());
            }
        });
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
        simpleCookieManager.clearAllCookies();
    }

    public static ErrorResponse handleErrorResponse(Response response) throws IOException {
        if (response.body() != null) {
            String responseBody = response.body().string();
            return GSON.fromJson(responseBody, ErrorResponse.class);
        }
        return null;
    }

    public static class ErrorResponse {
        private String error;
        private int status;

        public String getError() {
            return error;
        }

        public int getStatus() {
            return status;
        }
    }
}
