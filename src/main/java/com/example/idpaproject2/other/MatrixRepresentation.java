package com.example.idpaproject2.other;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import org.xml.sax.SAXException;
import java.io.File;
import java.util.ArrayList;

public class MatrixRepresentation {


    public Document parseXML(String inputFile) {


        JDOMParser parser = new JDOMParser(inputFile);
        Document document = parser.DOMParser();
        return document;
    }

    public ArrayList<String> getIndexingNodes(Document document) {

        ArrayList<String> indexingNodes = new ArrayList<>();

        try {
            final XPathExpression xpath = XPathFactory.newInstance().newXPath().compile("//*[count(./*) = 0]");
            final NodeList nodeList = (NodeList) xpath.evaluate(document, XPathConstants.NODESET);
            for(int i = 0; i < nodeList.getLength(); i++) {
                Element el = (Element) nodeList.item(i);
                String textualContent = el.getTextContent();
                String[] tokenizedArr = textualContent.split(" ");

                for (int j = 0; j < tokenizedArr.length; j++) {



                    if (!(tokenizedArr[j].equalsIgnoreCase("")) && !(tokenizedArr[j].equalsIgnoreCase(" ")))
                    indexingNodes.add(tokenizedArr[j]);

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return indexingNodes;

    }

    public void getContext(Document document) {

        DocumentTraversal traversal = (DocumentTraversal) document;

        TreeWalker walker = traversal.createTreeWalker(document.getDocumentElement(),
                NodeFilter.SHOW_ELEMENT, null, true);

        ArrayList<String> structure = new ArrayList<>();
        structure.add(document.getDocumentElement().getNodeName());
        System.out.println(structure);
        for (Node n = walker.firstChild(); n != null; n = walker.nextSibling()) {

            while(n.hasChildNodes()) {


            }


        }

    }

}

