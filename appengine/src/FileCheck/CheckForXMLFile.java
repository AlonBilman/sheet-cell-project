package FileCheck;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;


public class CheckForXMLFile {
    protected static boolean isXMLFile(String fileName) {
        File file = new File(fileName);
        return file.getName().toLowerCase().endsWith(".xml");
    }
    protected static boolean doesFileExist(File fileToCheck) {
        return fileToCheck.exists();
    }
    public static File getXMLFile(String filePath) {
        File file = new File(filePath);
        if (doesFileExist(file) && isXMLFile(filePath)) {
            System.out.print("file loaded successfully \n");
            return file;
        }
        return null;
    }

    public static STLSheet readXMLFile(String filePath) {
      try {

        JAXBContext jaxbContext = JAXBContext.newInstance("FileCheck");
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return   (STLSheet) unmarshaller.unmarshal(new File(filePath));
    }
      catch(JAXBException e) {
          System.out.println("Error reading file " + filePath );
          return null;
      }
    }
}

