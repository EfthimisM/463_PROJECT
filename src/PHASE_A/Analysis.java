package PHASE_A;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.StringCharacterIterator;
import java.util.*;
import java.io.FileReader;

import gr.uoc.csd.hy463.NXMLFileReader;

public class Analysis {


    private ArrayList<Article> articles = new ArrayList<>();
    private List<String> StopWords;

    public Analysis(File folder, File stopwords){
        listFilesForFolder(folder);
        List<String> stp = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(stopwords))){
            String line;
            while ((line = br.readLine()) != null) {
                String[] lineWords = line.split("\\s+"); // split line by whitespace
                for (String word : lineWords) {
                    stp.add(word);
                }
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
        StopWords = stp;
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
                    articles.add(article);
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }
}
