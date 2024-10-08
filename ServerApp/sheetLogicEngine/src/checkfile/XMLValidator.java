package checkfile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class XMLValidator {

    public static boolean isValidXML(InputStream inputStream) {
        if (!inputStream.markSupported()) {
            inputStream = new BufferedInputStream(inputStream);
        }

        try {
            //mark in order to return the stream to its initiate state
            inputStream.mark(Integer.MAX_VALUE);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(inputStream);
            boolean isValid = document.getDocumentElement() != null;

            //return the stream to its initiate state
            inputStream.reset();
            return isValid;

        } catch (ParserConfigurationException | SAXException | IOException e) {
            try {
                inputStream.reset();
            } catch (IOException ignore) {
                return false;
            }
            return false;
        }
    }
}