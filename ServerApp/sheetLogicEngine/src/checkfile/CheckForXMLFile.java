package checkfile;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.InputStream;


public class CheckForXMLFile {

    protected static boolean isXMLFile(InputStream fileContent) {
        return XMLValidator.isValidXML(fileContent);
    }

    public static STLSheet readXMLFile(InputStream fileContent) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance("checkfile");
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (STLSheet) unmarshaller.unmarshal(fileContent);
        } catch (JAXBException e) {
            return null;

        }
    }
}

