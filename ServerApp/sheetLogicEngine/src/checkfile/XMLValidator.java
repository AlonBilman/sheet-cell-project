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
            // Wrap in a BufferedInputStream if mark/reset is not supported
            inputStream = new BufferedInputStream(inputStream);
        }

        try {
            // Mark the stream at the current position
            inputStream.mark(Integer.MAX_VALUE); // Large enough to handle the entire stream

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            // Reset the stream back to the marked position
            inputStream.reset();

            return document.getDocumentElement() != null;

        } catch (ParserConfigurationException | SAXException | IOException e) {
            return false;
        }
    }

}