package DTO;

import java.io.File;

public class LoadDTO {
    private File loadedFile = null;

    public LoadDTO(File newFile) {
        if (newFile != null && newFile.exists() && newFile.getName().endsWith(".xml")) {
            if (loadedFile == null) {
                System.out.println("New xml file loaded.");
                loadedFile = newFile;
            }
        }
        else {
            if(loadedFile != null) {
                System.out.println("Wrong XML file, old one was saved");
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