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
    private Map<String,Integer> vocabulary;

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

    public void tokenize(List<String> stopWords){

        titleTokenized = new ArrayList<>();
        abstrTokenized = new ArrayList<>();
        bodyTokenized = new ArrayList<>();
        journalTokenized = new ArrayList<>();
        publisherTokenized = new ArrayList<>();
        //title
        String[] words = title.split(" ");
        String[] punctuation = {".", ",", "?", "!", ";", ":", "'", "\"", ")", "]", "}", "(", "[", "{", "<", ">", "/", "\\"};
        for(String word: words){
            // remove punctuation before tokenizing
            String tmp = word;
            for(String punc: punctuation){
                if(word.endsWith(punc)){
                    tmp = word.substring(0, word.length() -1 );
                }
            }

            if(!stopWords.contains(word) && tmp.length() > 1) {
                titleTokenized.add(tmp);
            }
        }
        //abs
        words = abstr.split(" ");
        for(String word: words){
            // remove punctuation before tokenizing
            String tmp = word;
            for(String punc: punctuation){
                if(word.endsWith(punc)){
                    tmp = word.substring(0, word.length() -1 );
                }
            }

            if(!stopWords.contains(word) && tmp.length() > 1) {
                abstrTokenized.add(tmp);
            }
        }
        //body
        words = body.split(" ");
        for(String word: words){
            // remove punctuation before tokenizing
            String tmp = word;
            for(String punc: punctuation){
                if(word.endsWith(punc)){
                    tmp = word.substring(0, word.length() -1 );
                }
            }

            if(!stopWords.contains(word) && tmp.length() > 1) {
                bodyTokenized.add(tmp);
            }
        }

        words = journal.split(" ");
        for(String word: words){
            // remove punctuation before tokenizing
            String tmp = word;
            for(String punc: punctuation){
                if(word.endsWith(punc)){
                    tmp = word.substring(0, word.length() -1 );
                }
            }

            if(!stopWords.contains(word) && tmp.length() > 1) {
                journalTokenized.add(tmp);
            }
        }

        words = publisher.split(" ");
        for(String word: words){
            // remove punctuation before tokenizing
            String tmp = word;
            for(String punc: punctuation){
                if(word.endsWith(punc)){
                    tmp = word.substring(0, word.length() -1 );
                }
            }

            if(!stopWords.contains(word) && tmp.length() > 1) {
                publisherTokenized.add(tmp);
            }
        }

    }
    public void setVocabulary(Map<String,Integer> vocabulary) {
        this.vocabulary = vocabulary;
    }
}
