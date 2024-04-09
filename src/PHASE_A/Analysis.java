package PHASE_A;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;

import static kotlin.test.AssertionsKt.assertEquals;

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
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document doc = db.parse(file);
                    doc.getDocumentElement().normalize();

                    // Retrieve subj-group elements
                    NodeList subjGroupList = doc.getElementsByTagName("article");
                    for (int i = 0; i < subjGroupList.getLength(); i++) {
                        Node subjGroupNode = subjGroupList.item(i);
                        if (subjGroupNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element subjGroupElement = (Element) subjGroupNode;

                            // Retrieve subject elements under each subj-group
                            NodeList subjectList = subjGroupElement.getElementsByTagName("subject");

                            // Print information about each subject
                            for (int j = 0; j < subjectList.getLength(); j++) {
                                Node subjectNode = subjectList.item(j);
                                System.out.println("Subject: " + subjectNode.getTextContent());
                            }
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }
}
