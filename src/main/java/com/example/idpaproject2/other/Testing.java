package com.example.idpaproject2.other;

import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;

public class Testing {

    public static void main(String[] args) throws ParserConfigurationException {
        //
        MatrixRepresentation mp = new MatrixRepresentation();
        Document document1 = mp.parseXML("src/doc1.xml");
        ArrayList<String> indexingNodes = mp.getIndexingNodes(document1);

        System.out.println(indexingNodes.size());
        for(int i = 0; i < indexingNodes.size(); i++) {
            System.out.print(indexingNodes.get(i) + " ");
        }

        mp.getContext(document1);
    }



    }
