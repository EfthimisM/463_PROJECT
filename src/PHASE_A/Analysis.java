package PHASE_A;

import java.io.*;
import java.util.*;

import gr.uoc.csd.hy463.NXMLFileReader;
public class Analysis {


    private ArrayList<Article> articles = new ArrayList<>();
    private Map<String,Word> Words = new TreeMap<>();
    private List<String> StopWords;

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

    private static void clearFile(File file) {
        if (file.exists()) {
            file.delete();
        }
    }

    private void createCollectionIndex(){
        File folder = new File("CollectionIndex");
        folder.mkdir();

        File vocabularyFile = new File(folder, "VocabularyFile.txt");
        clearFile(vocabularyFile);
        
        try {
            FileWriter writer = new FileWriter(vocabularyFile, true);
            BufferedWriter bw = new BufferedWriter(writer);
            // Write like this: bw.write("asd");
            for(Map.Entry<String,Word> entry: Words.entrySet()){
                // if
                bw.write(entry.getKey() + "\t"+ entry.getValue().getdF() + "\n");
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file: " + e.getMessage());
        }

        File documentFile = new File(folder, "DocumentsFile.txt");
        clearFile(documentFile);

        try {
            FileWriter writer = new FileWriter(documentFile, true);
            BufferedWriter bw = new BufferedWriter(writer);
            // Write like this: bw.write("asd");
            for(Article article: articles){
                bw.write(article.pmcId + "\t"+ article.path+ "\n");
                for(Map.Entry<String,Word> entry: Words.entrySet()){
                    if(entry.getValue().getTdIDFweight().containsKey(article.pmcId)){
                        bw.write(entry.getValue().getValue() +"\t" + entry.getValue().getTdIDFweight().get(article.pmcId) + ",\t");
                    }
                }
                bw.write("\n");
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file: " + e.getMessage());
        }
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

                    Article article = new Article(Integer.parseInt(pmcid), title, abstr, body, journal, publisher, authors, categories, fileEntry.getAbsolutePath());
                    articles.add(article);
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        }


        for(Article article: articles){
            article.tokenize(StopWords);
            int MaxFreq = 0;
            String maxFreqTerm = "";

            Map<String,Map<String,Integer>> test = createVocabulary(article);
            article.setVocabulary(test);

            for(String w : test.keySet()) {
                // Set Document Frequency
                Map<Integer,Map<String, Integer>> TagFrequency = new HashMap<>();
                // Set Term Frequency
                Map<Integer, Integer> tf = new HashMap<>();
                Word x;
                if(!Words.containsKey(w)) {
                    x = new Word(w);
                    Words.put(w,x);
                }
                Map<String, Integer> freqArticle = test.get(w);
                x = Words.get(w);
                TagFrequency.put(article.pmcId,freqArticle);
                x.setTagFrequency(TagFrequency);

                int sum = 0;
                for (int value : freqArticle.values()){
                    sum += value;
                }
                if(sum > MaxFreq) {
                    MaxFreq = sum;
                    maxFreqTerm = w;
                }
                tf.put(article.pmcId, sum);
                x.setTermFrequecy(tf);
            }
            article.setMaxFrequency(MaxFreq);
            article.setMaxFrequencyTerm(maxFreqTerm);

        }
        for(Map.Entry<String,Word> entry: Words.entrySet()) {
            /*.out.print(entry.getKey()+" ");
            System.out.print(entry.getValue().getTagFrequency()+"------");
            System.out.print(entry.getValue().getTagFrequency().size()+"------");
            System.out.print(entry.getValue().getTagFrequency()+"------");
            System.out.print(entry.getValue().getTermFrequecy());*/
            int dF = entry.getValue().getTagFrequency().size();
            entry.getValue().setdF(dF);
            entry.getValue().setTdIDFweight(articles);
            System.out.println(entry.getValue().getValue()+"------" + entry.getValue().getTdIDFweight());
        }
        for(Article article: articles){
            System.out.println(article.pmcId+"\t"+article.getMaxFrequency() + " \t" + article.getMaxFrequencyTerm());
        }

        createCollectionIndex();

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

        for(String w : a.authors){
            if (!vocabulary.containsKey(w)) {
                vocabulary.put(w, new HashMap<>());
            }
            Map<String, Integer> temp1 = vocabulary.get(w);
            temp1.put("Authors", temp1.getOrDefault("Authors", 0) + 1);
            vocabulary.put(w,temp1);
        }

        for(String w : a.categories){
            if (!vocabulary.containsKey(w)) {
                vocabulary.put(w, new HashMap<>());
            }
            Map<String, Integer> temp1 = vocabulary.get(w);
            temp1.put("Categories", temp1.getOrDefault("Categories", 0) + 1);
            vocabulary.put(w,temp1);
        }

        System.out.println(vocabulary);

        return vocabulary;
    }
}
