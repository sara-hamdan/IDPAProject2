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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Testing {

    public static void main(String[] args) throws ParserConfigurationException, XPathExpressionException, IOException {

        MatrixRepresentation mp = new MatrixRepresentation();
        Document document1 = mp.parseXML("src/XMLDocuments/doc1.xml");
        Document document2 = mp.parseXML("src/XMLDocuments/doc2.xml");

        ArrayList<String> indexingNodes1 = mp.getUniqueIndexingNodes(mp.getIndexingNodes(document1));
        ArrayList<String> indexingNodes2 = mp.getUniqueIndexingNodes(mp.getIndexingNodes(document2));

        System.out.println("Getting the unique indexing nodes of the first document: ");


        for(int i = 0; i < indexingNodes1.size(); i++) {
            System.out.println(indexingNodes1.get(i) + " ");
        }


        System.out.println();

        System.out.println("Getting the unique indexing nodes of the second document: ");


        for(int i = 0; i < indexingNodes2.size(); i++) {
            System.out.print(indexingNodes2.get(i) + " ");
        }

        System.out.println();

        ArrayList<String> structureList1 = new ArrayList<>();
        ArrayList<String> structureList2 = new ArrayList<>();
        StringBuilder str1 = new StringBuilder();
        StringBuilder str2 = new StringBuilder();
        mp.getUniqueContext(mp.getContext(document1.getDocumentElement(), str1, structureList1));
        mp.getUniqueContext(mp.getContext(document2.getDocumentElement(), str2, structureList2));

        System.out.println("Getting the unique structure of the first document: ");
        for(int i = 0; i < structureList1.size(); i++) {
            System.out.println(structureList1.get(i) + " ");
        }
        System.out.println();

        System.out.println("Getting the unique structure of the second document: ");

        for(int i = 0; i < structureList2.size(); i++) {
            System.out.println(structureList2.get(i) + " ");
        }
        System.out.println();



        ArrayList<String> mergedIndexingNodes = mp.getMergedIndexingNodes(indexingNodes1, indexingNodes2);
        ArrayList<String> mergedStructureList = mp.getMergedStructure(structureList1, structureList2);



        System.out.println("IDF of the first document: ");

        Double[][] IDF_1 = mp.IDF_Matrix(document1, document2, mergedIndexingNodes, mergedStructureList);

        System.out.println();

        System.out.println("TDF of the first document: ");

        int[][] TF_1 = mp.TF_Matrix(document1, mergedIndexingNodes, mergedStructureList);

        System.out.println();

        Double[][] tf_idf_matrix = mp.TF_IDF_Matrix(document1, document2, TF_1, IDF_1);

        System.out.println("TD-IDF of the first document: ");
        for (int i = 0; i < tf_idf_matrix.length; i++) { //this equals to the row in our matrix.
            for (int j = 0; j < tf_idf_matrix[i].length; j++) { //this equals to the column in each row.
                System.out.print(tf_idf_matrix[i][j] + " ");
            }
            System.out.println(); //change line on console as row comes to end in the matrix.
        }


        System.out.println();

        System.out.println("IDF of the first document: ");

        Double[][] IDF_2 = mp.IDF_Matrix(document2, document1, mergedIndexingNodes, mergedStructureList);

        System.out.println();

        System.out.println("TDF of the first document: ");

        int[][] TF_2 = mp.TF_Matrix(document2, mergedIndexingNodes, mergedStructureList);

        System.out.println();

        Double[][] tf_idf_matrix2 = mp.TF_IDF_Matrix(document1, document2, TF_2, IDF_2);

        for (int i = 0; i < tf_idf_matrix2.length; i++) { //this equals to the row in our matrix.
            for (int j = 0; j < tf_idf_matrix2[i].length; j++) { //this equals to the column in each row.
                System.out.print(tf_idf_matrix2[i][j] + " ");
            }
            System.out.println(); //change line on console as row comes to end in the matrix.
        }

        System.out.println("Cosine Similarity with TF-IDF weights: ");
        Double sim = mp.computeCosineSim(mp.computeNumCosine(tf_idf_matrix, tf_idf_matrix2), mp.computeDenomCosine(tf_idf_matrix, tf_idf_matrix2) );
        System.out.println(sim);

        System.out.println("Cosine Similarity with TF weights: ");
        Double sim_TF = mp.computeCosineSimTF(mp.computeNumCosineTF(TF_1, TF_2), mp.computeDenomCosineTF(TF_1, TF_2) );
        System.out.println(sim);

        System.out.println("Cosine Similarity with IDF weights: ");
        Double sim_IDF = mp.computeCosineSim(mp.computeNumCosine(IDF_1, IDF_2), mp.computeDenomCosine(IDF_1, IDF_2) );
        System.out.println(sim);


        PorterStemmer stem = new PorterStemmer();







    }


    }





