package com.example.idpaproject2.other;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class FlattenXML {

    public StringBuilder flattenXML(Document document) throws XPathExpressionException {

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
}
