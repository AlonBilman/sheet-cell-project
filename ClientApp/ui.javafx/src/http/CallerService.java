package http;

import com.google.gson.Gson;
import dto.CellDataDTO;
import dto.sheetDTO;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static constants.Constants.*;
import static constants.Constants.SHEET_DTO;

public class CallerService {

    private static final Gson GSON = new Gson();

    public String uploadFile(File file) throws IOException {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new IOException("File does not exist or is not a valid file.");
        }
        String url = BASE_DIRECTORY + LOADFILE;

        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", file.getName(), RequestBody.create(file, MediaType.parse("text/xml"))).build();

        //using try closes the response automatically
        try (Response response = HttpClientUtil.runSyncPost(url, body)) {
            if (!response.isSuccessful()) {
                HttpClientUtil.ErrorResponse error = HttpClientUtil.handleErrorResponse(response);
                if (error != null) throw new IOException(error.getError());
                throw new IOException("Failed to upload file.");
            }
            String sheetName = HttpClientUtil.handleStringResponse(response);
            if (sheetName != null) {
                return sheetName;
            }
            throw new IOException("Failed to upload file");
        }
    }

    private <T> T fetchData(String endpoint, Map<String, String> queryParams, Class<T> responseType) throws IOException {
        String url = BASE_DIRECTORY + DISPLAY + endpoint;
        Response response = HttpClientUtil.runSyncGet(url, queryParams);
        handleErrorResponse(response); //if there is an error.

        assert response.body() != null;
        T data = GSON.fromJson(response.body().string(), responseType);
        response.close();
        return data;
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

    public sheetDTO fetchSheet(Map<String, String> queryParams) throws IOException {
        return fetchData(SHEET_DTO, queryParams, sheetDTO.class);
    }

    public CellDataDTO fetchCell(Map<String, String> queryParams) throws IOException {
        return fetchData(CELL_DTO, queryParams, CellDataDTO.class);
    }

    public void changeCell(Map<String, String> queryParams, String newOriginalValue) throws IOException {
        String url = BASE_DIRECTORY + MODIFY + CELL;
        try (Response response = HttpClientUtil.runSyncPut(url, queryParams, newOriginalValue)) {
            if (!response.isSuccessful()) {
                HttpClientUtil.ErrorResponse error = HttpClientUtil.handleErrorResponse(response);
                if (error != null) {
                    throw new IOException(error.getError());
                }
                throw new IOException("Failed to change cell.");
            }
        }
    }
}
