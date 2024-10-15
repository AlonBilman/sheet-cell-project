package components.view.mainscreen;

public class SheetTableData {

    private final String userUploaded;
    private final String sheetName;
    private final String sheetSize;

    public SheetTableData(String userUploaded, String sheetName, String sheetSize) {
        this.userUploaded = userUploaded;
        this.sheetName = sheetName;
        this.sheetSize = sheetSize;
    }

    public String getUserUploaded() {
        return userUploaded;
    }

    public String getSheetName() {
        return sheetName;
    }

    public String getSheetSize() {
        return sheetSize;
    }
}
