package dto;

import java.io.File;
import java.io.Serializable;

public class LoadDTO implements Serializable {
    private File loadedFile = null;

    public LoadDTO(File newFile) {
        if (newFile != null && newFile.exists() && newFile.getName().endsWith(".xml")) {
            loadedFile = newFile;  // Update to the new file if it's valid
        }
    }

    public File getLoadedFile() {
        return this.loadedFile;
    }

    public boolean isNotValid() {
        return loadedFile == null;
    }

}