package DTO;

import java.io.File;

public class LoadDTO {
    private File loadedFile = null;

    public LoadDTO(File newFile) {
        if (newFile != null && newFile.exists() && newFile.getName().endsWith(".xml")) {
            loadedFile = newFile;  // Update to the new file if it's valid
        } else {
            if (loadedFile != null) {
                System.out.println("Invalid XML file. The previous file will be used.");
            } else {
                System.out.println("No valid XML file loaded.");
            }
        }
    }


    public File getLoadedFile() {
        return this.loadedFile;
    }

    public boolean isNotValid() {
        return loadedFile == null;
    }

}