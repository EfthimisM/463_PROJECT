package PHASE_A;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import gr.uoc.csd.hy463.NXMLFileReader;

public class Analysis {


    private class Article {
        int pmcId;
        String title;
        String abstr;
        String body;
        String journal;
        String publisher;
        ArrayList<String> authors;
        HashSet<String> categories;

        public Article(int id, String tt, String ab, String bod, String j, String pub, ArrayList<String> auth, HashSet<String> cat){
            pmcId = id;
            title = tt;
            abstr = ab;
            body = bod;
            journal = j;
            publisher = pub;
            authors = auth;
            categories = cat;
        }
    }

    public Analysis(File folder, File stopwords){
        listFilesForFolder(folder);

    }

    private  void listFilesForFolder(File folder) {

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

                    Article article = new Article(Integer.parseInt(pmcid), title, abstr, body, journal, publisher, authors, categories);

                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }
}
