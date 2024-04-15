package PHASE_A;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import mitos.stemmer.Stemmer;

public class Article {
    int pmcId;
    String title;
    ArrayList<String> titleTokenized;
    String abstr;
    ArrayList<String> abstrTokenized;
    String body;
    ArrayList<String> bodyTokenized;
    String journal;
    ArrayList<String> journalTokenized;
    String publisher;
    ArrayList<String> publisherTokenized;
    ArrayList<String> authors;
    HashSet<String> categories;
    private Map<String, Map<String,Integer>> vocabulary;
    private int maxFrequency;
    private String maxFrequencyTerm;
    String path;

    public Article(int id, String tt, String ab, String bod, String j, String pub, ArrayList<String> auth, HashSet<String> cat, String path){
        pmcId = id;
        title = tt;
        abstr = ab;
        body = bod;
        journal = j;
        publisher = pub;
        authors = auth;
        categories = cat;
        this.path = path;
    }

    // Function that removes punctuation and removes stop words
    public static String tokenize(String word, List<String> tokens) {
        char[] punctuation = {'.', ',', '?', '!', ';', ':', '\'', '\"', ')', ']', '}', '(', '[', '{', '<', '>', '/', '\\', '-', '=', '+', '*'};
        if (tokens.contains(word)) {
            return "A";
        }
        for (char p : punctuation) {
            if (word.indexOf(p) != -1) {
                String tmp = word.replace(Character.toString(p), "");
                word = tokenize(tmp, tokens);
            }
        }
        if(word.isEmpty()){
            return "A";
        }
        return word;
    }

    public void tokenize(List<String> stopWords){

        titleTokenized = new ArrayList<>();
        abstrTokenized = new ArrayList<>();
        bodyTokenized = new ArrayList<>();
        journalTokenized = new ArrayList<>();
        publisherTokenized = new ArrayList<>();

        //title
        String[] words = title.split(" ");
        for(String word: words){
            String tmp = tokenize(word, stopWords);
            if(!tmp.endsWith("A")){
                titleTokenized.add(Stemmer.Stem(tmp));
            }
        }
        //abs
        words = abstr.split(" ");
        for(String word: words){
            String tmp = tokenize(word, stopWords);
            if(!tmp.endsWith("A")){
                abstrTokenized.add(Stemmer.Stem(tmp));
            }
        }
        //body
        words = body.split(" ");
        for(String word: words){
            String tmp = tokenize(word, stopWords);
            if(!tmp.endsWith("A")){
                bodyTokenized.add(Stemmer.Stem(tmp));
            }
        }

        words = journal.split(" ");
        for(String word: words){
            String tmp = tokenize(word, stopWords);
            if(!tmp.endsWith("A")){
                journalTokenized.add(Stemmer.Stem(tmp));
            }
        }

        words = publisher.split(" ");
        for(String word: words){
            String tmp = tokenize(word, stopWords);
            if(!tmp.endsWith("A")){
                publisherTokenized.add(Stemmer.Stem(tmp));
            }
        }

    }
    public void setVocabulary(Map<String, Map<String,Integer>> vocabulary) {
        this.vocabulary = vocabulary;
    }

    public Map<String, Map<String, Integer>> getVocabulary() {
        return vocabulary;
    }

    public int getMaxFrequency() {
        return maxFrequency;
    }

    public String getMaxFrequencyTerm() {
        return maxFrequencyTerm;
    }

    public void setMaxFrequency(int maxFrequency) {
        this.maxFrequency = maxFrequency;
    }

    public void setMaxFrequencyTerm(String maxFrequencyTerm) {
        this.maxFrequencyTerm = maxFrequencyTerm;
    }
}
