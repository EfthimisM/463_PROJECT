package PHASE_A;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import gr.uoc.csd.hy463.NXMLFileReader;

public class Analysis {

    public Analysis(File folder){
        listFilesForFolder(folder);
    }

    private static void listFilesForFolder(File folder) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                try {
                    File file = new File(fileEntry.getAbsolutePath());
                    System.out.println(file);
                    NXMLFileReader xmlFile = new NXMLFileReader(file);
                    String pmcid = xmlFile.getPMCID();
                    String title = xmlFile.getTitle();
                    String abstr = xmlFile.getAbstr();
                    String body = xmlFile.getBody();
                    String journal = xmlFile.getJournal();
                    String publisher = xmlFile.getPublisher();
                    ArrayList<String> authors = xmlFile.getAuthors();
                    HashSet<String> categories =xmlFile.getCategories();

                    System.out.println("- PMC ID: " + pmcid);
                    System.out.println("- Title: " + title);
                    System.out.println("- Abstract: " + abstr);
                    System.out.println("- Body: " + body);
                    System.out.println("- Journal: " + journal);
                    System.out.println("- Publisher: " + publisher);
                    System.out.println("- Authors: " + authors);
                    System.out.println("- Categories: " + categories);
                    /*DocumentBuilder db = dbf.newDocumentBuilder();
                    Document doc = db.parse(file);
                    doc.getDocumentElement().normalize();

                    // Retrieve article elements
                    NodeList subjGroupList = doc.getElementsByTagName("article");
                    for (int i = 0; i < subjGroupList.getLength(); i++) {
                        Node subjGroupNode = subjGroupList.item(i);
                        if (subjGroupNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element subjGroupElement = (Element) subjGroupNode;

                            // Retrieve subject elements under each article
                            NodeList Titles = subjGroupElement.getElementsByTagName("title-group");

                            // Print information about each article
                            for (int j = 0; j < Titles.getLength(); j++) {
                                Node subjectNode = Titles.item(j);
                                Element sub = (Element) subjectNode;
                                System.out.println("\tTitle: " + subjectNode.getTextContent());
                            }

                            // Retrieve subject elements under each article
                            NodeList NamesList = subjGroupElement.getElementsByTagName("publisher-name");

                            // Print information about each article
                            for (int j = 0; j < NamesList.getLength(); j++) {
                                Node subjectNode = NamesList.item(j);
                                System.out.println("\tPublisher Name: " + subjectNode.getTextContent());
                            }

                            NodeList PubList = subjGroupElement.getElementsByTagName("article-id");
                            for (int j = 0; j < PubList.getLength(); j++) {
                                Node subjectNode = PubList.item(j);
                                Element sub = (Element) subjectNode;
                                if(sub.getAttribute("pub-id-type").equals("pmc")) {
                                    System.out.println("\tPUB Name: " + subjectNode.getTextContent());
                                }
                            }

                            // Retrieve Authors
                            NodeList contribList = subjGroupElement.getElementsByTagName("contrib");
                            for (int j = 0; j < contribList.getLength(); j++) {
                                Element contribElement = (Element) contribList.item(j);
                                String contribType = contribElement.getAttribute("contrib-type");

                                if (contribType.equals("author")) {
                                    Element nameElement = (Element) contribElement.getElementsByTagName("name").item(0);
                                    String surname = nameElement.getElementsByTagName("surname").item(0).getTextContent();
                                    String givenNames = nameElement.getElementsByTagName("given-names").item(0).getTextContent();

                                    System.out.println("\tAuthor: " + surname + " " + givenNames);
                                }
                            }

                            NodeList Categories = subjGroupElement.getElementsByTagName("article-categories");
                            for (int j = 0; j < Categories.getLength(); j++) {
                                Node subjectNode = Categories.item(j);
                                System.out.println("\tCategory: " + subjectNode.getTextContent());
                            }

                            NodeList Journal = subjGroupElement.getElementsByTagName("journal-title");
                            for (int j = 0; j < Journal.getLength(); j++) {
                                Node subjectNode = Journal.item(j);
                                System.out.println("\tJournal: " + subjectNode.getTextContent());
                            }

                            // Missing: περιληψη και κυριος περιεγχομενο

                        }

                    }*/
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }
}
