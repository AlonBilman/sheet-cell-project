package http.dto;


public class CellColorDtoResp {
    private String textColor;
    private String backgroundColor;

    public String getBackgroundColor() {
        return backgroundColor; //can return null
    }

    public String getTextColor() {
        return textColor; //can return null
    }

}
