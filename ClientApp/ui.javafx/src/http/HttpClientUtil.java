package http;

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
                //ignore
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println("[LOGOUT]    Disconnecting from Server: Server returns: " + response.code());
                Platform.runLater(()->{
                    HTTP_CLIENT.dispatcher().executorService().shutdown();
                    HTTP_CLIENT.connectionPool().evictAll();
                    SIMPLE_COOKIE_MANAGER.clearAllCookies();
                });
            }
        });
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

    public static class RangeBody {
        private final String name;
        private final String toAndFrom;

        public RangeBody() {
            name = "";
            toAndFrom = "";
        }

        public RangeBody(String name, String toAndFrom) {
            this.name = name;
            this.toAndFrom = toAndFrom;
        }

        public String getName() {
            return name;
        }

        public String getToAndFrom() {
            return toAndFrom;
        }
    }

    public static class SortObj {
        private final String params;
        private final List<String> sortBy;

        public SortObj() {
            this.params = "";
            this.sortBy = new ArrayList<>();
        }

        public SortObj(String params, List<String> sortBy) {
            this.params = params;
            this.sortBy = sortBy;
        }

        public String getParams() {
            return params;
        }

        public List<String> getSortBy() {
            return sortBy;
        }
    }

    public static class FilterObj {
        private final String params;
        private final Map<String, Set<String>> filterBy;
        private final String operator;

        public FilterObj() {
            this.params = "";
            this.filterBy = new HashMap<>();
            this.operator = "";
        }

        public FilterObj(String params, Map<String, Set<String>> filterBy, String operator) {
            this.params = params;
            this.filterBy = filterBy;
            this.operator = operator;
        }

        public String getParams() {
            return this.params;
        }

        public String getOperator() {
            return this.operator;
        }

        public Map<String, Set<String>> getFilterBy() {
            return this.filterBy;
        }
    }

    public static class Ranges {
        private final String xParams;
        private final String yParams;
        private final Set<CellDataDTO> XRange;
        private final Set<CellDataDTO> YRange;

        public Ranges(Set<CellDataDTO> XRange, Set<CellDataDTO> YRange) {
            this.XRange = XRange;
            this.YRange = YRange;
            xParams = "";
            yParams = "";
        }

        public Ranges(String xParams, String yParams) {
            this.xParams = xParams;
            this.yParams = yParams;
            this.XRange = new HashSet<>();
            this.YRange = new HashSet<>();
        }

        public Ranges() {
            this.XRange = new HashSet<>();
            this.YRange = new HashSet<>();
            xParams = "";
            yParams = "";
        }

        public String getXParams() {
            return xParams;
        }

        public String getYParams() {
            return yParams;
        }

        public Set<CellDataDTO> getXRange() {
            return XRange;
        }

        public Set<CellDataDTO> getYRange() {
            return YRange;
        }

    }

}
