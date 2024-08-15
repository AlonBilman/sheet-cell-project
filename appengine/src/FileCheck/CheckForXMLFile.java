//package FileCheck;
//
//import jakarta.xml.bind.JAXBContext;
//import jakarta.xml.bind.JAXBException;
//import jakarta.xml.bind.Unmarshaller;
//import jakarta.xml.bind.annotation.*;
//import java.io.File;
//import java.util.List;
//
//
//public class CheckForXMLFile extends XMLTypes{
//    protected boolean isXMLFile(String fileName) {
//        File file = new File(fileName);
//            return file.getName().toLowerCase().endsWith(".xml");
//
//    }
//    protected boolean doesFileExist(File fileToCheck) {
//        return fileToCheck.exists();
//    }
//    protected File getXMLFile(String filePath) {
//        File file = new File(filePath);
//        if (doesFileExist(file) && isXMLFile(filePath)) {
//            System.out.print("file loaded successfully \n");
//            return file;
//        }
//        return null;
//    }
//
//    public static STLSheet readXMLFile(String filePath) throws JAXBException {
//        STLSheet stlSheet = null;
//        JAXBContext jaxbContext = JAXBContext.newInstance(STLSheet.class);
//
//        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//
//
//        stlSheet = (STLSheet) unmarshaller.unmarshal(new File(filePath));
//
//        return stlSheet;
//    }
//}
//
//
