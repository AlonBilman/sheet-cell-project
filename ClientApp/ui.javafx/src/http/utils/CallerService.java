package http.utils;

import com.google.gson.Gson;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static constants.Constants.*;

public class CallerService {

    private static final Gson GSON = new Gson();

    public void uploadFileAsync(File file, Callback callback) throws IOException {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new IOException("File does not exist or is not a valid file.");
        }
        String url = BASE_DIRECTORY + LOADFILE;

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(file, MediaType.parse("text/xml")))
                .build();

        HttpClientUtil.runAsyncPost(url, null, body, callback);
    }

    private void fetchDataAsync(String endpoint, Map<String, String> queryParams, Callback callback) {
        String url = BASE_DIRECTORY + DISPLAY + endpoint;
        HttpClientUtil.runAsyncGet(url, queryParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (response) {
                    handleErrorResponse(response);
                    callback.onResponse(call, response);
                } catch (IOException e) {
                    callback.onFailure(call, e);
                }
            }
        });
    }

    public void handleErrorResponse(Response response) throws IOException {
        if (!response.isSuccessful()) {
            HttpClientUtil.ErrorResponse error = HttpClientUtil.handleErrorResponse(response);
            if (error != null) {
                throw new IOException(error.getError());
            }
            throw new IOException("Failed to fetch data.");
        }
    }

    public void fetchSheetsAsync(Map<String, String> queryParams, Callback callback) {
        fetchDataAsync(ALL_VERSIONS, queryParams, callback);
    }

    public void fetchSheetAsync(Map<String, String> queryParams, Callback callback) {
        fetchDataAsync(SHEET_DTO, queryParams, callback);
    }

    public void fetchCellAsync(Map<String, String> queryParams, Callback callback) {
        fetchDataAsync(CELL_DTO, queryParams, callback);
    }

    public void getUpdatedVersionNumberAsync(Map<String, String> queryParams, Callback callback) {
        String url = BASE_DIRECTORY + VERSION;
        HttpClientUtil.runAsyncGet(url, queryParams, callback);
    }

    public void changeColorAsync(Map<String, String> queryParams, String endPoint, String color, Callback callback) {
        String url = BASE_DIRECTORY + MODIFY + endPoint;
        RequestBody body = RequestBody.create(GSON.toJson(color), MediaType.get("application/json"));
        HttpClientUtil.runAsyncPut(url, queryParams, body, callback);
    }

    public void changeCellAsync(Map<String, String> queryParams, String newOriginalValue, Callback callback) {
        String url = BASE_DIRECTORY + MODIFY + CELL;
        RequestBody body = RequestBody.create(GSON.toJson(newOriginalValue), MediaType.get("application/json"));
        HttpClientUtil.runAsyncPut(url, queryParams, body, callback);
    }

    public void addRange(Map<String, String> queryParams, HttpClientUtil.RangeBody range, Callback callback) {
        String url = BASE_DIRECTORY + RANGE;
        RequestBody body = RequestBody.create(GSON.toJson(range), MediaType.get("application/json"));
        HttpClientUtil.runAsyncPost(url, queryParams, body, callback);
    }

    public void deleteRange(Map<String, String> queryParams, HttpClientUtil.RangeBody range, Callback callback) {
        String url = BASE_DIRECTORY + RANGE;
        RequestBody body = RequestBody.create(GSON.toJson(range), MediaType.get("application/json"));
        HttpClientUtil.runAsyncDelete(url, queryParams, body, callback);
    }

    public void sortSheet(Map<String, String> queryParams, HttpClientUtil.SortObj sortObj, Callback callback) {
        String url = BASE_DIRECTORY + DISPLAY + SORT;
        RequestBody body = RequestBody.create(GSON.toJson(sortObj), MediaType.get("application/json"));
        HttpClientUtil.runAsyncPost(url, queryParams, body, callback);
    }

    public void filterSheet(Map<String, String> queryParams, HttpClientUtil.FilterObj filterObj, Callback callback) {
        String url = BASE_DIRECTORY + DISPLAY + FILTER;
        RequestBody body = RequestBody.create(GSON.toJson(filterObj), MediaType.get("application/json"));
        HttpClientUtil.runAsyncPost(url, queryParams, body, callback);
    }

    public void getNoNameRange(Map<String, String> queryParams, HttpClientUtil.RangeBody range, HttpClientUtil.Ranges ranges, String endPoint, Callback callback) {
        String url = BASE_DIRECTORY + endPoint;
        RequestBody body;
        if (endPoint.equals(NO_NAME_RANGE)) {
            body = RequestBody.create(GSON.toJson(range), MediaType.get("application/json"));
        } else {
            body = RequestBody.create(GSON.toJson(ranges), MediaType.get("application/json"));
        }
        HttpClientUtil.runAsyncPost(url, queryParams, body, callback);
    }

    public void startDynamicChange(Map<String, String> queryParams, String body, Callback callback) {
        String url = BASE_DIRECTORY + DISPLAY + DYNAMIC_CHANGE;
        RequestBody jsonBody = RequestBody.create(GSON.toJson(body), MediaType.get("application/json"));
        HttpClientUtil.runAsyncPut(url, queryParams, jsonBody, callback);
    }

    public void stopDynamicChange(Map<String, String> queryParams, Callback callback) {
        String url = BASE_DIRECTORY + DISPLAY + DYNAMIC_CHANGE;
        HttpClientUtil.runAsyncDelete(url, queryParams, null, callback);
    }

    public void saveCellValueForDynamicChange(Map<String, String> queryParams, Callback callback) {
        String url = BASE_DIRECTORY + DISPLAY + DYNAMIC_CHANGE;
        HttpClientUtil.runAsyncGet(url, queryParams, callback);
    }

    public void getAllUsers(Callback callback) {
        String url = BASE_DIRECTORY + DISPLAY + USERS;
        HttpClientUtil.runAsyncGet(url, null, callback);
    }

    public void getPermissions(Map<String, String> queryParams, Callback callback) {
        String url = BASE_DIRECTORY + PERMISSIONS;
        HttpClientUtil.runAsyncGet(url, queryParams, callback);
    }

    public void requestPermission(Map<String, String> queryParams, String permission, Callback callback) {
        String url = BASE_DIRECTORY + PERMISSIONS;
        RequestBody body = RequestBody.create(GSON.toJson(permission), MediaType.get("application/json"));
        HttpClientUtil.runAsyncPost(url, queryParams, body, callback);
    }

    public void acceptOrDenyPermission(Map<String, String> queryParams, String answer, Callback callback) {
        String url = BASE_DIRECTORY + PERMISSIONS;
        RequestBody body = RequestBody.create(GSON.toJson(answer), MediaType.get("application/json"));
        HttpClientUtil.runAsyncPut(url, queryParams, body, callback);
    }

    public void writeChatLine(String line, Callback callback) {
        String url = BASE_DIRECTORY + WRITE_TO_CHAT;
        RequestBody body = RequestBody.create(GSON.toJson(line), MediaType.get("application/json"));
        HttpClientUtil.runAsyncPut(url, null, body, callback);
    }

    public void getDeltaFetchingChatLines(Map<String, String> queryParams, Callback callback) {
        String url = BASE_DIRECTORY + CHAT_LINES_LIST;
        HttpClientUtil.runAsyncGet(url, queryParams, callback);
    }
}
