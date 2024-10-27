package manager.impl;

public class AppManager {

    private final SheetManagerImpl sheetManager;
    private int currentVersion;
    private SheetManagerImpl deepCopyForDynamicChange;

    public boolean isUpToDate() {
        return currentVersion == sheetManager.getSheetVersion();
    }

    public SheetManagerImpl getManagerDeepCopyForDynamicChange() {
        if (deepCopyForDynamicChange == null) {
            deepCopyForDynamicChange = sheetManager.deepCopy();
        }
        return deepCopyForDynamicChange;
    }

    public void dynamicChangeStopped() {
        deepCopyForDynamicChange = null; //so the garbage collector will deal with it.
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

    public AppManager(SheetManagerImpl sheetManager) {
        this.sheetManager = sheetManager;
        this.currentVersion = sheetManager.getSheetVersion();
        this.deepCopyForDynamicChange = null;
    }
}
