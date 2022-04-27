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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

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

                for (String s : tokenizedArr) {
                    if (!(s.equalsIgnoreCase("")) && !(s.equalsIgnoreCase(" ")))
                        indexingNodes.add(s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return indexingNodes;

    }

    public ArrayList<String> getMergedIndexingNodes(ArrayList<String> arr1, ArrayList<String> arr2) {

        ArrayList<String> mergedIndexingNodes = new ArrayList<>();
        for(int i = 0; i < arr1.size(); i++) {
            mergedIndexingNodes.add(arr1.get(i));
        }

        for(int i = 0; i < arr2.size(); i++) {
            mergedIndexingNodes.add(arr2.get(i));
        }

        return mergedIndexingNodes;
    }

    public ArrayList<String> getMergedStructure(ArrayList<String> arr1, ArrayList<String> arr2) {

        ArrayList<String> mergedStructure = new ArrayList<>();
        for(int i = 0; i < arr1.size(); i++) {
            mergedStructure.add(arr1.get(i));
        }

        for(int i = 0; i < arr2.size(); i++) {
            mergedStructure.add(arr2.get(i));
        }

        return mergedStructure;
    }



    public ArrayList<String> getUniqueIndexingNodes(ArrayList<String> list){
        Set<String> set = new LinkedHashSet<>(list);
        return new ArrayList<String>(set);
    }

    public ArrayList<String> getContext(Element elem, StringBuilder path, ArrayList<String> structureList) {
        final int pathLen = path.length();
        if (pathLen != 0) // first iteration is pathLen = 0
            path.append("/");
        path.append(elem.getNodeName()); // we get the node name and append to the string builder
        boolean hasChild = false;
        for (Node child = elem.getFirstChild(); child != null; child = child.getNextSibling())
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                hasChild = true;
                getContext((Element)child, path, structureList);

            }
        if (!hasChild) {
            structureList.add(path.toString());
        }

        path.setLength(pathLen);
        return structureList;
    }

    public ArrayList<String> getUniqueContext(ArrayList<String> list){
        Set<String> set = new LinkedHashSet<>(list);
        return new ArrayList<String>(set);
    }

    public HashMap<String, Integer> indexLabelling(ArrayList<String> indexingNodes) {

        HashMap<String, Integer> mapper = new HashMap<>();

        for(int i = 0; i < indexingNodes.size(); i++) {

            mapper.put(indexingNodes.get(i), i);

        }
        return mapper;
    }

    public HashMap<String, Integer> structureLabelling(ArrayList<String> structure) {

        HashMap<String, Integer> mapper = new HashMap<>();

        for(int i = 0; i < structure.size(); i++) {

            mapper.put(structure.get(i), i);

        }
        return mapper;
    }

    public int[][] TF_Matrix(Document document1, ArrayList<String> mergedIndexingNodes, ArrayList<String> mergedStructure) {

        int[][] tf_matrix = new int[getUniqueContext(mergedStructure).size()][getUniqueIndexingNodes(mergedIndexingNodes).size()];
        ArrayList<String> indexingNodesOf1 = getUniqueIndexingNodes(getIndexingNodes(document1));

        for (int i = 0; i < getUniqueContext(mergedStructure).size(); i++) {
            for (int j = 0; j < getUniqueIndexingNodes(mergedIndexingNodes).size(); j++) {



            }

        }

        return tf_matrix;

    }


    public Double[][] IDF_Matrix(Document document1, Document document2, ArrayList<String> mergedIndexingNodes, ArrayList<String> mergedStructure) {

        Double[][] idf_matrix = new Double[getUniqueContext(mergedStructure).size()][getUniqueIndexingNodes(mergedIndexingNodes).size()];
        ArrayList<String> indexingNodesOf1 = getUniqueIndexingNodes(getIndexingNodes(document1));
        ArrayList<String> indexingNodesOf2 = getUniqueIndexingNodes(getIndexingNodes(document2));

        ArrayList<String> structureOf1 = new ArrayList<>();
        structureOf1 = getUniqueContext(getContext(document1.getDocumentElement(), new StringBuilder(), structureOf1));
        ArrayList<String> structureOf2 = new ArrayList<>();
        structureOf2 = getUniqueContext(getContext(document2.getDocumentElement(), new StringBuilder(), structureOf2));

        for (int i = 0; i < getUniqueContext(mergedStructure).size(); i++) {
            for (int j = 0; j < getUniqueIndexingNodes(mergedIndexingNodes).size(); j++) {
                if (!findStructureOrIndexingNode(structureOf1, getUniqueContext(mergedStructure).get(i))) {
                    idf_matrix[i][j] = 0.0;
                } else if(!findStructureOrIndexingNode(indexingNodesOf1, mergedIndexingNodes.get(j))) {
                        idf_matrix[i][j] = 0.0;
                    }
                else {
                    NodeList nodeList1 = document1.getElementsByTagName(getUniqueContext(mergedStructure).get(i).split("/")[getUniqueContext(mergedStructure).get(i).split("/").length-1]);
                    NodeList nodeList2 = document2.getElementsByTagName(getUniqueContext(mergedStructure).get(i).split("/")[getUniqueContext(mergedStructure).get(i).split("/").length-1]);
                    for (int k = 0; k < nodeList1.getLength(); k++) {
                        if (nodeList1.item(k).getTextContent().contains(getUniqueIndexingNodes(mergedIndexingNodes).get(j))) {

                                idf_matrix[i][j] = Math.log(3 / 1);
                                for (int l = 0; l < nodeList2.getLength(); l++) {
                                    if (nodeList2.item(l).getTextContent().contains(getUniqueIndexingNodes(mergedIndexingNodes).get(j))) {
                                        idf_matrix[i][j] = Math.log(3.0/2);
                                        break;
                                    }

                                }

                              break;
                            }

                        else idf_matrix[i][j] = 0.0;
                    }
                }
            }
        }
        for (int i = 0; i < idf_matrix.length; i++) { //this equals to the row in our matrix.
            for (int j = 0; j < idf_matrix[i].length; j++) { //this equals to the column in each row.
                System.out.print(idf_matrix[i][j] + " ");
            }
            System.out.println(); //change line on console as row comes to end in the matrix.
        }
        return idf_matrix;
    }

    public boolean findStructureOrIndexingNode(ArrayList<String> documentList, String indexingNode) {

        for(int i = 0; i < documentList.size(); i++) {

            if(indexingNode.equalsIgnoreCase(documentList.get(i))) {

                return true;
            }
        }

        return false;
}



}
