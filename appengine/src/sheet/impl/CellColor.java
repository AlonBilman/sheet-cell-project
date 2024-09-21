package sheet.impl;

import java.io.Serializable;

public class CellColor implements Serializable {

    private String textColor; //can be null
    private String backgroundColor; //can be null

    public CellColor(String textColor, String backgroundColor) {
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
    }

    public CellColor(CellColor cellColor) {
        this.textColor = cellColor.textColor;
        this.backgroundColor = cellColor.backgroundColor;
    }

    public String getBackgroundColor() {
        return backgroundColor; //can return null
    }

    public String getTextColor() {
        return textColor; //can return null
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }


}
