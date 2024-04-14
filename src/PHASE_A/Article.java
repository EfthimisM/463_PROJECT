package PHASE_A;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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

    // Function that removes punctuation and removes stop words
    public static String tokenize(String word, List<String> tokens) {
        char[] punctuation = {'.', ',', '?', '!', ';', ':', '\'', '\"', ')', ']', '}', '(', '[', '{', '<', '>', '/', '\\'};
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
                titleTokenized.add(tmp);
            }
        }
        //abs
        words = abstr.split(" ");
        for(String word: words){
            String tmp = tokenize(word, stopWords);
            if(!tmp.endsWith("A")){
                abstrTokenized.add(tmp);
            }
        }
        //body
        words = body.split(" ");
        for(String word: words){
            String tmp = tokenize(word, stopWords);
            if(!tmp.endsWith("A")){
                bodyTokenized.add(tmp);
            }
        }

        words = journal.split(" ");
        for(String word: words){
            String tmp = tokenize(word, stopWords);
            if(!tmp.endsWith("A")){
                journalTokenized.add(tmp);
            }
        }

        words = publisher.split(" ");
        for(String word: words){
            String tmp = tokenize(word, stopWords);
            if(!tmp.endsWith("A")){
                publisherTokenized.add(tmp);
            }
        }

    }
    public void setVocabulary(Map<String, Map<String,Integer>> vocabulary) {
        this.vocabulary = vocabulary;
    }

    public Map<String, Map<String, Integer>> getVocabulary() {
        return vocabulary;
    }

}
