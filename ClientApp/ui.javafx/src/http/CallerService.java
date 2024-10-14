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

        HttpClientUtil.runAsyncPost(url, body, callback);
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

    private void handleErrorResponse(Response response) throws IOException {
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
        HttpClientUtil.runAsyncPut(url, queryParams, color, callback);
    }

    public void changeCellAsync(Map<String, String> queryParams, String newOriginalValue, Callback callback) {
        String url = BASE_DIRECTORY + MODIFY + CELL;
        HttpClientUtil.runAsyncPut(url, queryParams, newOriginalValue, callback);
    }
}
