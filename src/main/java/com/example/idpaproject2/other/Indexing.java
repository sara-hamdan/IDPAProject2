package com.example.idpaproject2.other;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class Indexing {

    MatrixRepresentation mp = new MatrixRepresentation();

    public StringBuilder flattenXMLWithStructure(Document document) throws XPathExpressionException {

        final XPathExpression xpath = XPathFactory.newInstance().newXPath().compile("//*[count(./*) = 0]");
        final NodeList nodeList = (NodeList) xpath.evaluate(document, XPathConstants.NODESET);
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < nodeList.getLength(); i++) {
            Element el = (Element) nodeList.item(i);
            stringBuilder.append(el.getNodeName() + ": " + el.getTextContent() + " \n");

        }
        System.out.println(stringBuilder.toString());

        return stringBuilder;
    }

    public StringBuilder flattenXMLWithoutStructure(Document document) throws XPathExpressionException {

        final XPathExpression xpath = XPathFactory.newInstance().newXPath().compile("//*[count(./*) = 0]");
        final NodeList nodeList = (NodeList) xpath.evaluate(document, XPathConstants.NODESET);
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < nodeList.getLength(); i++) {
            Element el = (Element) nodeList.item(i);
            stringBuilder.append(el.getTextContent() + " \n");

        }
        System.out.println(stringBuilder.toString());

        return stringBuilder;
    }

    public ArrayList<String> indexingTerms() throws XPathExpressionException {

        ArrayList<String> terms = new ArrayList<>();

        String[] genericTerms = {"Movie", "Professor", "Student", "Song"};
        File path = new File("src/XMLDocuments");

        File [] files = path.listFiles();
        for (int i = 0; i < files.length; i++){
            if (files[i].isFile()){ //this line weeds out other directories/folders
                Document document = mp.parseXML(files[i].toString());
                terms.addAll(mp.getUniqueIndexingNodes(mp.getIndexingNodes(document)));
                terms.addAll(mp.getLeafNodes(document));
            }
        }
        Set<String> set = new LinkedHashSet<>(terms);
        return new ArrayList<String>(set);
    }
    public HashMap<String, ArrayList<String>> getIndexingTable(ArrayList<String> indexingTerms) throws XPathExpressionException {

        File path = new File("src/XMLDocuments");
        HashMap<String, ArrayList<String>> indexMap = new HashMap<>();

        for(String s: indexingTerms)
            indexMap.put(s, new ArrayList<String>());

        File [] files = path.listFiles();
        for (int i = 0; i < files.length; i++){
            if (files[i].isFile()){ //this line weeds out other directories/folders
                Document document = mp.parseXML(files[i].toString());
                StringBuilder sb = flattenXMLWithStructure(document);

                for(String s: indexingTerms) {
                    if(sb.toString().contains(s)) {
                        indexMap.get(s).add(files[i].toString());
                    }
                 }
            }
        }
        return indexMap;
    }
}
