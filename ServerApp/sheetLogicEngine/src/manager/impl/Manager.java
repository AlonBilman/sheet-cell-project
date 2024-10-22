package manager.impl;

public class Manager {

    private final SheetManagerImpl sheetManager;
    private int currentVersion;

    public boolean isUpToDate() {
        return currentVersion == sheetManager.getSheetVersion();
    }

    public int getCurrentVersion() {
        return currentVersion;
    }

    public void updateVersion() {
        this.currentVersion = sheetManager.getSheetVersion();
    }

    public SheetManagerImpl getSheetManager() {
        return sheetManager;
    }

    public Manager(SheetManagerImpl sheetManager) {
        this.sheetManager = sheetManager;
        this.currentVersion = sheetManager.getSheetVersion();
    }
}
