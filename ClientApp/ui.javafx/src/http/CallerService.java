package http;

import com.google.gson.Gson;
import dto.CellDataDTO;
import dto.sheetDTO;
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

        HttpClientUtil.runAsyncPost(url,null, body, callback);
    }

    private <T> void fetchDataAsync(String endpoint, Map<String, String> queryParams, Class<T> responseType, Callback callback) {
        String url = BASE_DIRECTORY + DISPLAY + endpoint;
        HttpClientUtil.runAsyncGet(url, queryParams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
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

    public void fetchSheetsAsync(Map<String, String> queryParams, Callback callback){
        fetchDataAsync(ALL_VERSIONS, queryParams, Map.class, callback);
    }

    public void fetchSheetAsync(Map<String, String> queryParams, Callback callback) {
        fetchDataAsync(SHEET_DTO, queryParams, sheetDTO.class, callback);
    }

    public void fetchCellAsync(Map<String, String> queryParams, Callback callback) {
        fetchDataAsync(CELL_DTO, queryParams, CellDataDTO.class, callback);
    }

    public void changeColorAsync(Map<String, String> queryParams, String endPoint, String color, Callback callback) {
        String url = BASE_DIRECTORY + MODIFY + endPoint;
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), GSON.toJson(color));
        HttpClientUtil.runAsyncPut(url, queryParams, body, callback);
    }

    public void changeCellAsync(Map<String, String> queryParams, String newOriginalValue, Callback callback) {
        String url = BASE_DIRECTORY + MODIFY + CELL;
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), GSON.toJson(newOriginalValue));
        HttpClientUtil.runAsyncPut(url, queryParams, body, callback);
    }

    public void addRange(Map<String, String> queryParams, HttpClientUtil.RangeBody range, Callback callback) {
        String url = BASE_DIRECTORY + RANGE;
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), GSON.toJson(range));
        HttpClientUtil.runAsyncPost(url,queryParams,body,callback);

    }
    public void deleteRange(Map<String, String> queryParams, HttpClientUtil.RangeBody range,Callback callback) {
        String url = BASE_DIRECTORY + RANGE;
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), GSON.toJson(range));
        HttpClientUtil.runAsyncDelete(url,queryParams,body,callback);
    }

    public void sortSheet(Map<String, String> queryParams, HttpClientUtil.SortObj sortObj, Callback callback) {
        String url = BASE_DIRECTORY + DISPLAY + SORT;
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), GSON.toJson(sortObj));
        HttpClientUtil.runAsyncPost(url,queryParams,body,callback);
    }

    public void filterSheet(Map<String, String> queryParams, HttpClientUtil.FilterObj filterObj, Callback callback) {
        String url = BASE_DIRECTORY + DISPLAY + FILTER;
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), GSON.toJson(filterObj));
        HttpClientUtil.runAsyncPost(url,queryParams,body,callback);
    }

    public void getNoNameRange(Map<String, String> queryParams, HttpClientUtil.RangeBody range, Callback callback){
        String url = BASE_DIRECTORY + NO_NAME_RANGE;
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), GSON.toJson(range));
        HttpClientUtil.runAsyncPost(url,queryParams,body,callback);
    }
}
