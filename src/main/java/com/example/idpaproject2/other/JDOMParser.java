package com.example.idpaproject2.other;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class JDOMParser {

    String input = "";
    static Document document;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    public JDOMParser(String input) {
        this.input = input;
    }

    public Document DOMParser() {

        try {
            File inputFile = new File(input);
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(inputFile);

            TransformerFactory tFactory =
                    TransformerFactory.newInstance();
            Transformer transformer =
                    tFactory.newTransformer();

            DOMSource source = new DOMSource(document);
     //       StreamResult result = new StreamResult(System.out);
       //     transformer.transform(source, result);



        } catch (Exception e) {
            e.printStackTrace();
        }

        return document;
    }

}
