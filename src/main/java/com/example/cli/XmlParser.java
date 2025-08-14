package com.example.cli;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlParser {
    public List<Car> parse(String filePath) {
    	 List<Car> cars = new ArrayList<>();
         try {
             File xmlFile = new File(filePath);
             DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
             DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
             Document doc = dBuilder.parse(xmlFile);
             doc.getDocumentElement().normalize();

             NodeList nList = doc.getElementsByTagName("car");

             for (int i = 0; i < nList.getLength(); i++) {
                 Node node = nList.item(i);
                 if (node.getNodeType() == Node.ELEMENT_NODE) {
                     Element carElement = (Element) node;
                     Car car = new Car();

                     car.setType(carElement.getElementsByTagName("type").item(0).getTextContent().trim());

                    
                     Element mainPriceElement = (Element) carElement.getElementsByTagName("price").item(0);
                     String mainCurrency = mainPriceElement.getAttribute("currency").trim();
                     double mainValue = Double.parseDouble(mainPriceElement.getTextContent().trim());


                     Map<String, Double> prices = new HashMap<>();
                     prices.put(mainCurrency, mainValue);

                     NodeList pricesNodeList = carElement.getElementsByTagName("prices");
                     if (pricesNodeList.getLength() > 0) {
                         Element pricesElement = (Element) pricesNodeList.item(0);
                         NodeList subPriceNodes = pricesElement.getElementsByTagName("price");
                         for (int j = 0; j < subPriceNodes.getLength(); j++) {
                             Element priceElement = (Element) subPriceNodes.item(j);
                             String currency = priceElement.getAttribute("currency").trim();
                             double value = Double.parseDouble(priceElement.getTextContent().trim());
                             prices.put(currency, value);
                         }
                     }

                     car.setPrices(prices);
                     

                     Map<String, Double> price = new HashMap<>();
                     price.put("USD", mainValue);
                     car.setPrice(price);

                     cars.add(car);
                 }
             }
         } catch (Exception e) {
             System.err.println("Error parsing XML: " + e.getMessage());
         }
         return cars;
     }
}
