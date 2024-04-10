package PHASE_A;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Article {
    int pmcId;
    String title;
    ArrayList<String> titleTokenized;
    String abstr;
    ArrayList<String> abstrTokenized;
    String body;
    ArrayList<String> bodyTokenized;
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

    public void tokenize(List<String> stopWords){

        titleTokenized = new ArrayList<>();
        abstrTokenized = new ArrayList<>();
        bodyTokenized = new ArrayList<>();
        //title
        String[] words = title.split(" ");
        for(String word: words){
            if(!stopWords.contains(word)) {
                titleTokenized.add(word);
            }
        }
        //abs
        //body
    }
}
