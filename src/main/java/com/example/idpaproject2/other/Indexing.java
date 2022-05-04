package com.example.idpaproject2.other;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Indexing {

    MatrixRepresentation mp = new MatrixRepresentation();

    public StringBuilder flattenXMLWithStructure(Document document) throws XPathExpressionException, IOException {


            PorterStemmer stemmer = new PorterStemmer();


        final XPathExpression xpath = XPathFactory.newInstance().newXPath().compile("//*[count(./*) = 0]");
        final NodeList nodeList = (NodeList) xpath.evaluate(document, XPathConstants.NODESET);
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < nodeList.getLength(); i++) {
            Element el = (Element) nodeList.item(i);
            stringBuilder.append(stemmer.stem(el.getNodeName().toLowerCase()) + ": ");
            ArrayList<String> tokenizedArr =
                    Stream.of((el.getTextContent().toLowerCase()).split(" "))
                            .collect(Collectors.toCollection(ArrayList<String>::new));
            tokenizedArr.removeAll(loadStopwords());

            for(String token : tokenizedArr) {
                String stemmedWord = stemmer.stem(token);
                stringBuilder.append(stemmedWord + " "  );
            }

            stringBuilder.append("\n");

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

    public List<String> loadStopwords() throws IOException {

        List<String> stopwords = Files.readAllLines(Paths.get("src/english_stopwords.txt"));
        return stopwords;
    }


    public ArrayList<String> indexingTerms(String query) throws XPathExpressionException, IOException {

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

        ArrayList<String> tokenizedQuery =
                Stream.of(query.toLowerCase().split(" "))
                        .collect(Collectors.toCollection(ArrayList<String>::new));

        tokenizedQuery.removeAll(loadStopwords());

        for(String token : tokenizedQuery) {

            PorterStemmer stemmer = new PorterStemmer();
            String stemmedWord = stemmer.stem(token);

        }
         //get the stemmed word
        terms.addAll(tokenizedQuery);
        Set<String> set = new LinkedHashSet<>(terms);
        return new ArrayList<String>(set);
    }

    public int getFileCount() {
        int count = 0;
        File path = new File("src/XMLDocuments");
        File [] files = path.listFiles();
        for (int i = 0; i < files.length; i++){
            if (files[i].isFile()){ //this line weeds out other directories/folders
                count++;

            }
        }
        return count;
    }
    public HashMap<String, ArrayList<String>> getIndexingTable(ArrayList<String> indexingTerms) throws XPathExpressionException, IOException {

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

    public ArrayList<String> searchInTable(String query, HashMap<String, ArrayList<String>> indexMap) throws IOException {

        PorterStemmer stemmer = new PorterStemmer();
        ArrayList<String> tokenizedQuery =
                Stream.of(query.toLowerCase().split(" "))
                        .collect(Collectors.toCollection(ArrayList<String>::new));
        tokenizedQuery.removeAll(loadStopwords());

        for(String token : tokenizedQuery) {


            String stemmedWord = stemmer.stem(token);

        }

        ArrayList<String> documents = new ArrayList<>();

        for(String token: tokenizedQuery) {

            if(indexMap.containsKey(token)) {
                documents.addAll(indexMap.get(token));
            }
        }

        Set<String> documentSet = new LinkedHashSet<>(documents);
        return new ArrayList<String>(documentSet);

    }

    public HashMap <String, double[]> getDocumentMatrices(ArrayList<String> documentSet, ArrayList<String> indexingTerms, HashMap<String, ArrayList<String>> indexMap) throws XPathExpressionException, IOException {


        HashMap<String, ArrayList<String>> indexTable = getIndexingTable(indexingTerms);
       HashMap <String, double[]> tf_matrices_map = new HashMap <String, double[]>();
        for(String document: documentSet) {
            double[] TF_Matrix = new double[indexingTerms.size()];
            for(int i = 0; i < indexingTerms.size(); i++) {
                if(indexMap.get(indexingTerms.get(i)).contains(document)) {
                    TF_Matrix[i] =
                            Math.log((double) getFileCount()/indexTable.get(indexingTerms.get(i)).size());

                }
            }

            tf_matrices_map.put(document, TF_Matrix);
        }

        return tf_matrices_map;
    }



    public int[] query_TF_Matrix(String query, ArrayList<String> indexingTerms) {

        int[] query_TF_Matrix = new int[indexingTerms.size()];
        PorterStemmer stemmer = new PorterStemmer();

        for(int i = 0; i < indexingTerms.size(); i++) {

            if(stemmer.stem(query).contains(indexingTerms.get(i))) {
                query_TF_Matrix[i] = 1;
            }
        }

        return query_TF_Matrix;

    }

    public HashMap<String, Double> getSimilarity(int[] query_TF_Matrix, HashMap<String, double[]> TF_matrices_map) {

        HashMap<String, Double> similarityMap = new HashMap<>();
        double squareSumQuery = 0;
        for(int i = 0; i < query_TF_Matrix.length; i++) {
            squareSumQuery += Math.pow(query_TF_Matrix[i], 2);
        }

        for (Map.Entry<String, double[]> e : TF_matrices_map.entrySet()) {
            double[] multiplied = new double[query_TF_Matrix.length];
            double[] oneDocumentMatrix = e.getValue();
            double squareSumDocument = 0.0;
            Double cosineSim;
            double sum = 0.0;
            for(int i = 0; i <oneDocumentMatrix.length; i++) {
                multiplied[i] = (double) (query_TF_Matrix[i]*oneDocumentMatrix[i]);
            }
            for(int i = 0; i < multiplied.length; i++) {
                sum += multiplied[i];
            }
            for(int i = 0; i < oneDocumentMatrix.length; i++) {
                squareSumDocument += Math.pow(oneDocumentMatrix[i], 2);
            }

            cosineSim = sum/Math.sqrt(squareSumQuery*squareSumDocument);

            similarityMap.put(e.getKey(), cosineSim);
        }

        return similarityMap;

    }

    public HashMap<String, Double> rankedSimilarityMap(HashMap<String, Double> similarityMap) {


        // Create a list from elements of HashMap
        List<Map.Entry<String, Double> > list =
                new LinkedList<Map.Entry<String, Double> >(similarityMap.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Double> >() {
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        LinkedHashMap<String, Double> rankedSimMap = new LinkedHashMap<>();

//Use Comparator.reverseOrder() for reverse ordering
        similarityMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> rankedSimMap.put(x.getKey(), x.getValue()));

        return rankedSimMap;
    }


}
