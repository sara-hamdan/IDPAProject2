package com.example.idpaproject2.other;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Testing {

    public static void main(String[] args) throws ParserConfigurationException {

        MatrixRepresentation mp = new MatrixRepresentation();
        Document document1 = mp.parseXML("src/doc1.xml");
        Document document2 = mp.parseXML("src/doc2.xml");
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


        mp.IDF_Matrix(document2, document1, mergedIndexingNodes, mergedStructureList);


    }


    }





