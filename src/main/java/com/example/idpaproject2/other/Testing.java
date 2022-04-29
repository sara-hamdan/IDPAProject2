package com.example.idpaproject2.other;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Testing {

    public static void main(String[] args) throws ParserConfigurationException, XPathExpressionException {

        MatrixRepresentation mp = new MatrixRepresentation();
        Document document1 = mp.parseXML("src/XMLDocuments/doc1.xml");
        Document document2 = mp.parseXML("src/XMLDocuments/doc2.xml");

        Indexing index = new Indexing();


        String query = "Student John Takagi Movie Hello";

        ArrayList<String> arrayList = index.indexingTerms(query);
        HashMap<String, ArrayList<String>> indexMap = index.getIndexingTable(arrayList);


        for (Map.Entry<String, ArrayList<String>> e : indexMap.entrySet()) {
            System.out.print(e.getKey() + ": ");

            for(String doc: e.getValue())
            {
                System.out.print(doc + " ");
            }

            System.out.println();
        }

        ArrayList<String> documents = index.searchInTable(query, indexMap);

         for(String document: documents) {
             System.out.print(document + " ");
         }

        System.out.println();

        for(String s: arrayList) {
            System.out.print(s + " ");
        }

        System.out.println();

        int[] query_TF_Matrix = index.query_TF_Matrix(query, arrayList);
        HashMap<String, Double> ranked = index.rankedSimilarityMap(index.getSimilarity(query_TF_Matrix, index.getDocumentMatrices(documents, arrayList, indexMap)));

        for (Map.Entry<String, Double> e : ranked.entrySet())

            // Printing key-value pairs
            System.out.println(e.getKey() + " "
                    + e.getValue());


        System.out.println();


            ArrayList<String> indexingNodes1 = mp.getIndexingNodes(document1);
        ArrayList<String> indexingNodes2 = mp.getIndexingNodes(document2);

        for(int i = 0; i < indexingNodes1.size(); i++) {
            System.out.println(indexingNodes1.get(i) + " ");
        }


        System.out.println();

        for(int i = 0; i < indexingNodes2.size(); i++) {
            System.out.print(indexingNodes1.get(i) + " ");
        }

        ArrayList<String> structureList1 = new ArrayList<>();
        ArrayList<String> structureList2 = new ArrayList<>();
        StringBuilder str1 = new StringBuilder();
        StringBuilder str2 = new StringBuilder();
        mp.getContext(document1.getDocumentElement(), str1, structureList1);
        mp.getContext(document2.getDocumentElement(), str2, structureList2);

        for(int i = 0; i < structureList1.size(); i++) {
            System.out.println(structureList1.get(i) + " ");
        }
        System.out.println();

        for(int i = 0; i < structureList2.size(); i++) {
            System.out.println(structureList2.get(i) + " ");
        }
        System.out.println();




        ArrayList<String> mergedIndexingNodes = mp.getMergedIndexingNodes(indexingNodes1, indexingNodes2);
        HashMap<String, Integer> indexMapper = mp.indexLabelling(mp.getUniqueIndexingNodes(mergedIndexingNodes));

        for (Map.Entry<String, Integer> e : indexMapper.entrySet())

            // Printing key-value pairs
            System.out.println(e.getKey() + " "
                    + e.getValue());


        ArrayList<String> mergedStructureList = mp.getMergedStructure(structureList1, structureList2);


        HashMap<String, Integer> structureMapper = mp.structureLabelling(mp.getUniqueIndexingNodes(mergedStructureList));

        for (Map.Entry<String, Integer> e : structureMapper.entrySet())

            // Printing key-value pairs
            System.out.println(e.getKey() + " "
                    + e.getValue());


        mp.IDF_Matrix(document1, document2, mergedIndexingNodes, mergedStructureList);
        mp.TF_Matrix(document1, mergedIndexingNodes, mergedStructureList);


        Double[][] tf_idf_matrix = mp.TF_IDF_Matrix(document1,
                        document2, mp.TF_Matrix(document1, mergedIndexingNodes, mergedStructureList), mp.IDF_Matrix(document1, document2, mergedIndexingNodes, mergedStructureList));

        for (int i = 0; i < tf_idf_matrix.length; i++) { //this equals to the row in our matrix.
            for (int j = 0; j < tf_idf_matrix[i].length; j++) { //this equals to the column in each row.
                System.out.print(tf_idf_matrix[i][j] + " ");
            }
            System.out.println(); //change line on console as row comes to end in the matrix.
        }

        Double[][] tf_idf_matrix2 = mp.TF_IDF_Matrix(document2,
                        document1, mp.TF_Matrix(document2,mergedIndexingNodes, mergedStructureList), mp.IDF_Matrix(document2, document1, mergedIndexingNodes, mergedStructureList));


        for (int i = 0; i < tf_idf_matrix2.length; i++) { //this equals to the row in our matrix.
            for (int j = 0; j < tf_idf_matrix2[i].length; j++) { //this equals to the column in each row.
                System.out.print(tf_idf_matrix2[i][j] + " ");
            }
            System.out.println(); //change line on console as row comes to end in the matrix.
        }

        Double sim = mp.computeCosineSim(mp.computeNumCosine(tf_idf_matrix, tf_idf_matrix2), mp.computeDenomCosine(tf_idf_matrix, tf_idf_matrix2) );
        System.out.println(sim);

    }


    }





