package com.example.idpaproject2.other;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class MatrixRepresentation {


    public Document parseXML(String inputFile) {


        JDOMParser parser = new JDOMParser(inputFile);
        return parser.DOMParser();
    }

    public List<String> loadStopwords() throws IOException {

        List<String> stopwords = Files.readAllLines(Paths.get("src/english_stopwords.txt"));
        return stopwords;
    }
    public ArrayList<String> getIndexingNodes(Document document) {

        ArrayList<String> indexingNodes = new ArrayList<>();

        try {
            final XPathExpression xpath = XPathFactory.newInstance().newXPath().compile("//*[count(./*) = 0]");
            final NodeList nodeList = (NodeList) xpath.evaluate(document, XPathConstants.NODESET);
            for(int i = 0; i < nodeList.getLength(); i++) {
                Element el = (Element) nodeList.item(i);
                String textualContent = el.getTextContent();
                ArrayList<String> tokenizedArr =
                        Stream.of(textualContent.toLowerCase().split(" "))
                                .collect(Collectors.toCollection(ArrayList<String>::new));
                // List<String> tokenizedArr = Arrays.asList(textualContent.split(" "));
                tokenizedArr.removeAll(loadStopwords());

                for (String s : tokenizedArr) {
                    if (!(s.equalsIgnoreCase("")) && !(s.equalsIgnoreCase(" "))) {

                        PorterStemmer stemmer = new PorterStemmer();
                        String stemmedWord = stemmer.stem(s);  //get the stemmed word
                        indexingNodes.add(stemmedWord);

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return indexingNodes;

    }

    public ArrayList<String> getLeafNodes(Document document) {

        ArrayList<String> leafNodes = new ArrayList<>();
        PorterStemmer porterStemmer = new PorterStemmer();

        try {
            final XPathExpression xpath = XPathFactory.newInstance().newXPath().compile("//*[count(./*) = 0]");
            final NodeList nodeList = (NodeList) xpath.evaluate(document, XPathConstants.NODESET);
            for(int i = 0; i < nodeList.getLength(); i++) {
                Element el = (Element) nodeList.item(i);
                leafNodes.add(porterStemmer.stem(el.getNodeName().toLowerCase()));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return leafNodes;

    }
    public ArrayList<String> getMergedIndexingNodes(ArrayList<String> arr1, ArrayList<String> arr2) {

        ArrayList<String> mergedIndexingNodes = new ArrayList<>();
        mergedIndexingNodes.addAll(arr1);

        mergedIndexingNodes.addAll(arr2);

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

    public int[][] TF_Matrix(Document document1, ArrayList<String> mergedIndexingNodes, ArrayList<String> mergedStructure) throws IOException {

        int[][] tf_matrix = new int[getUniqueContext(mergedStructure).size()][getUniqueIndexingNodes(mergedIndexingNodes).size()];
        ArrayList<String> indexingNodesOf1 = getUniqueIndexingNodes(getIndexingNodes(document1));

        for (int i = 0; i < getUniqueContext(mergedStructure).size(); i++) {
            for (int j = 0; j < getUniqueIndexingNodes(mergedIndexingNodes).size(); j++) {

                NodeList nodeList1 = document1.getElementsByTagName(getUniqueContext(mergedStructure).get(i).split("/")[getUniqueContext(mergedStructure).get(i).split("/").length - 1]);
                ArrayList<String> terms = new ArrayList<>();
                Map<String, Integer> hm = new HashMap();

                for (int k = 0; k < nodeList1.getLength(); k++) {
                    String textualContent = nodeList1.item(k).getTextContent();

                    ArrayList<String> tokenizedArr =
                            Stream.of(textualContent.toLowerCase().split(" "))
                                    .collect(Collectors.toCollection(ArrayList<String>::new));
                    // List<String> tokenizedArr = Arrays.asList(textualContent.split(" "));
                    tokenizedArr.removeAll(loadStopwords());

                    for (String s : tokenizedArr) {


                            PorterStemmer stemmer = new PorterStemmer();
                            String stemmedWord = stemmer.stem(s);  //get the stemmed word
                            terms.add(stemmedWord);

                        }


                    }

                    for (String x : terms) {

                        if (!hm.containsKey(x)) {
                            hm.put(x, 1);
                        } else {
                            hm.put(x, hm.get(x) + 1);
                        }
                    }
                    System.out.println(hm);
                    if (!hm.containsKey(getUniqueIndexingNodes(mergedIndexingNodes).get(j))) {
                        tf_matrix[i][j] = 0;
                    } else tf_matrix[i][j] = hm.get(getUniqueIndexingNodes(mergedIndexingNodes).get(j));
                }
            }



        for (int i = 0; i < tf_matrix.length; i++) { //this equals to the row in our matrix.
            for (int j = 0; j < tf_matrix[i].length; j++) { //this equals to the column in each row.
                System.out.print(tf_matrix[i][j] + " ");
            }
            System.out.println(); //change line on console as row comes to end in the matrix.
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
                } else if(!findStructureOrIndexingNode(indexingNodesOf1, getUniqueIndexingNodes(mergedIndexingNodes).get(j))) {
                        idf_matrix[i][j] = 0.0;
                    }
                else {
                    NodeList nodeList1 = document1.getElementsByTagName(getUniqueContext(mergedStructure).get(i).split("/")[getUniqueContext(mergedStructure).get(i).split("/").length-1]);
                    NodeList nodeList2 = document2.getElementsByTagName(getUniqueContext(mergedStructure).get(i).split("/")[getUniqueContext(mergedStructure).get(i).split("/").length-1]);
                    for (int k = 0; k < nodeList1.getLength(); k++) {

                        PorterStemmer stemmer = new PorterStemmer();
                        if (nodeList1.item(k).getTextContent().toLowerCase().contains(getUniqueIndexingNodes(mergedIndexingNodes).get(j))) {

                                idf_matrix[i][j] = Math.log(3 / 1);
                                for (int l = 0; l < nodeList2.getLength(); l++) {
                                    if (nodeList2.item(l).getTextContent().toLowerCase().contains(getUniqueIndexingNodes(mergedIndexingNodes).get(j))) {
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

    public Double[][] TF_IDF_Matrix(Document document1, Document document2,int[][] tf_matrix, Double[][] idf_matrix) {
        //ArrayList<String> structureList1 = new ArrayList<>();
       // ArrayList<String> structureList2 = new ArrayList<>();
       // StringBuilder str1 = new StringBuilder();
       // StringBuilder str2 = new StringBuilder();
       // tf_matrix = TF_Matrix(document1, getMergedIndexingNodes(getIndexingNodes(document1), getIndexingNodes(document2)), getMergedStructure(getContext(document1.getDocumentElement(), str1, structureList1), getContext(document2.getDocumentElement(), str2, structureList2)));
      //  idf_matrix = IDF_Matrix(document1, document2, getMergedIndexingNodes(getIndexingNodes(document1), getIndexingNodes(document2)), getMergedStructure(getContext(document1.getDocumentElement(), str1, structureList1), getContext(document2.getDocumentElement(), str2, structureList2)));

        Double[][] TF_IDF_Matrix = new Double[tf_matrix.length][tf_matrix[0].length];

        for(int i = 0; i < TF_IDF_Matrix.length; i++) {
            for(int j = 0; j < TF_IDF_Matrix[i].length; j++) {
                TF_IDF_Matrix[i][j] = tf_matrix[i][j]*idf_matrix[i][j];
            }
        }

        return TF_IDF_Matrix;
    }
    public boolean findStructureOrIndexingNode(ArrayList<String> documentList, String indexingNode) {

        for(int i = 0; i < documentList.size(); i++) {

            if(indexingNode.equalsIgnoreCase(documentList.get(i))) {

                return true;
            }
        }

        return false;
}

        public Double computeNumCosine(Double[][] tf_idf_1, Double[][] tf_idf_2) {

         Double NumCosine = 0.0;

         for(int i = 0; i < tf_idf_1.length; i++) {
             for(int j = 0; j < tf_idf_1[i].length; j++) {
                 NumCosine += tf_idf_1[i][j]*tf_idf_2[i][j];
             }
         }

         return NumCosine;
        }


    public Double computeDenomCosine(Double[][] tf_idf_1, Double[][] tf_idf_2) {

        Double DenomCosine;
        Double sumSquareWeights1 = 0.0;
        Double sumSquareWeights2 = 0.0;

        for(int i = 0; i < tf_idf_1.length; i++) {
            for(int j = 0; j < tf_idf_1[i].length; j++) {
                sumSquareWeights1 += tf_idf_1[i][j]*tf_idf_1[i][j];
            }
        }

        for(int i = 0; i < tf_idf_2.length; i++) {
            for(int j = 0; j < tf_idf_2[i].length; j++) {
                sumSquareWeights2 += tf_idf_2[i][j]*tf_idf_2[i][j];
            }
        }

        DenomCosine = Math.sqrt(sumSquareWeights1*sumSquareWeights2);

        return DenomCosine;
    }

    public Double computeCosineSimTF(Double num, Double denom) {

        return num/denom;
    }


    public Double computeNumCosineTF(int[][] tf_idf_1, int[][] tf_idf_2) {

        Double NumCosine = 0.0;

        for(int i = 0; i < tf_idf_1.length; i++) {
            for(int j = 0; j < tf_idf_1[i].length; j++) {
                NumCosine += tf_idf_1[i][j]*tf_idf_2[i][j];
            }
        }

        return NumCosine;
    }


    public Double computeDenomCosineTF(int[][] tf_idf_1, int[][] tf_idf_2) {

        Double DenomCosine;
        Double sumSquareWeights1 = 0.0;
        Double sumSquareWeights2 = 0.0;

        for(int i = 0; i < tf_idf_1.length; i++) {
            for(int j = 0; j < tf_idf_1[i].length; j++) {
                sumSquareWeights1 += tf_idf_1[i][j]*tf_idf_1[i][j];
            }
        }

        for(int i = 0; i < tf_idf_2.length; i++) {
            for(int j = 0; j < tf_idf_2[i].length; j++) {
                sumSquareWeights2 += tf_idf_2[i][j]*tf_idf_2[i][j];
            }
        }

        DenomCosine = Math.sqrt(sumSquareWeights1*sumSquareWeights2);

        return DenomCosine;
    }

    public Double computeCosineSim(Double num, Double denom) {

        return num/denom;
    }



}
