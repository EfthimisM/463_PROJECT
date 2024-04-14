package PHASE_A;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.io.FileReader;

import gr.uoc.csd.hy463.NXMLFileReader;
public class Analysis {


    private ArrayList<Article> articles = new ArrayList<>();
    private Map<String,Word> Words = new HashMap<>();
    private List<String> StopWords;
    Word word;
    public Analysis(File folder, File stopwords){
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
                    articles.add(article);
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        }

        for(Article article: articles){
            article.tokenize(StopWords);
            Map<String,Map<String,Integer>> test = createVocabulary(article);
            article.setVocabulary(test);
            for(String w : test.keySet()) {
                if(!Words.containsKey(w)) {
                    Word x = new Word(w);
                    Map<Integer, Map<String, Integer>> tagFrequency = new HashMap<>();
                    tagFrequency.put(article.pmcId,test.get(w));
                    x.setTagFrequency(tagFrequency);
                    Words.put(w,x);
                } else {
                    Map<Integer, Map<String, Integer>> tagFrequency = new HashMap<>();
                    tagFrequency.put(article.pmcId,test.get(w));

                }


            }


        }
    } // listFilesForFolder

    private ArrayList<Article> getArticles() {
        return articles;
    }


    private Map<String,Map<String,Integer>> createVocabulary(Article a) {
        Map<String,Map<String,Integer>> vocabulary = new TreeMap<>();

        Integer ID = a.pmcId;
        for (String w : a.titleTokenized) {
            if (!vocabulary.containsKey(w)) {
                vocabulary.put(w, new HashMap<>());
            }
            Map<String, Integer> temp1 = vocabulary.get(w);
            temp1.put("Title", temp1.getOrDefault("Title", 0) + 1);
            vocabulary.put(w,temp1);
        }
        // MAP : <"Malaria", { {title = 3} , {abstract = 5},
        for (String w : a.abstrTokenized) {
            if (!vocabulary.containsKey(w)) {
                vocabulary.put(w, new HashMap<>());
            }
            Map<String, Integer> temp1 = vocabulary.get(w);
            temp1.put("Abstract", temp1.getOrDefault("Abstract", 0) + 1);
            vocabulary.put(w,temp1);
        }

        for (String w : a.bodyTokenized) {
            if (!vocabulary.containsKey(w)) {
                vocabulary.put(w, new HashMap<>());
            }
            Map<String, Integer> temp1 = vocabulary.get(w);
            temp1.put("Body", temp1.getOrDefault("Body", 0) + 1);
            vocabulary.put(w,temp1);
        }

        for (String w : a.journalTokenized) {
            if (!vocabulary.containsKey(w)) {
                vocabulary.put(w, new HashMap<>());
            }
            Map<String, Integer> temp1 = vocabulary.get(w);
            temp1.put("Journal", temp1.getOrDefault("Journal", 0) + 1);
            vocabulary.put(w,temp1);
        }
        for (String w : a.publisherTokenized) {
            if (!vocabulary.containsKey(w)) {
                vocabulary.put(w, new HashMap<>());
            }
            Map<String, Integer> temp1 = vocabulary.get(w);
            temp1.put("Publisher", temp1.getOrDefault("Publisher", 0) + 1);
            vocabulary.put(w,temp1);
        }
        System.out.println(vocabulary);

        return vocabulary;
    }
}
