package http;

import constants.Constants;
import com.google.gson.Gson;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

public class HttpClientUtil {

    private final static SimpleCookieManager SIMPLE_COOKIE_MANAGER = new SimpleCookieManager();

    private final static OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder().cookieJar(SIMPLE_COOKIE_MANAGER).followRedirects(false).build();

    private static final Gson GSON = new Gson();

    public static void setCookieManagerLoggingFacility(Consumer<String> logConsumer) {
        SIMPLE_COOKIE_MANAGER.setLogData(logConsumer);
    }

    public static void runAsyncPost(String finalUrl, RequestBody body, Callback callback) {
        Request request = new Request.Builder().url(finalUrl).post(body).build();
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

    public static Response runSyncGet(String baseUrl, Map<String, String> queryParams) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl).newBuilder();

        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        String finalUrl = urlBuilder.build().toString();

        Request request = new Request.Builder().url(finalUrl).build();
        return HTTP_CLIENT.newCall(request).execute();
    }


    public static Response runSyncPut(String baseUrl, Map<String, String> queryParams, String bodyAsString) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl).newBuilder();

        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        String finalUrl = urlBuilder.build().toString();
        RequestBody body = RequestBody.create(GSON.toJson(bodyAsString), MediaType.parse("application/json"));

        Request request = new Request.Builder().url(finalUrl).put(body).build();

        return HTTP_CLIENT.newCall(request).execute();
    }

    public static Response runSyncPost(String finalUrl, RequestBody body) throws IOException {
        Request request = new Request.Builder().url(finalUrl).post(body).build();

        return HTTP_CLIENT.newCall(request).execute();
    }

    public static void shutdown() {
        System.out.println("[SHUTDOWN]  Shutting down HTTP CLIENT");
        String finalURL = Constants.BASE_DIRECTORY + Constants.LOGOUT;
        runAsyncGet(finalURL, null, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //ignore
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println("[LOGOUT]    Disconnecting from Server: Server returns: " + response.code());
            }
        });
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
        SIMPLE_COOKIE_MANAGER.clearAllCookies();
    }

    public static ErrorResponse handleErrorResponse(Response response) throws IOException {
        if (response.body() != null) {
            String responseBody = response.body().string();
            return GSON.fromJson(responseBody, ErrorResponse.class);
        }
        return null;
    }

    public static String handleStringResponse(Response response) throws IOException {
        if (response.body() != null) {
            String responseBody = response.body().string();
            return GSON.fromJson(responseBody, String.class);
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
