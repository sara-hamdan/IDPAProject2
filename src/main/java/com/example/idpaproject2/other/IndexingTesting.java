package com.example.idpaproject2.other;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IndexingTesting {
    public static void main (String[] args) throws XPathExpressionException, IOException {

        Indexing index = new Indexing();


        String query = "I eat apples";

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

    }
}
