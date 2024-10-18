package utils;

import java.util.Objects;

public class AppUser {

    private final String userUploaded;
    private final String sheetName;
    private final String sheetSize;

    public AppUser(String userUploaded, String sheetName, String sheetSize) {
        this.userUploaded = userUploaded;
        this.sheetName = sheetName;
        this.sheetSize = sheetSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppUser appUser = (AppUser) o;
        return Objects.equals(userUploaded, appUser.userUploaded) &&
                Objects.equals(sheetName, appUser.sheetName) &&
                Objects.equals(sheetSize, appUser.sheetSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userUploaded, sheetName, sheetSize);
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
