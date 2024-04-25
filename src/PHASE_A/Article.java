//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package PHASE_A;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import mitos.stemmer.Stemmer;

public class Article {
    String pmcId;
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
    Map<String, Map<String, ArrayList<Integer>>> vocabulary;
    ArrayList<Word> vocab = new ArrayList();
    private int maxFrequency;
    private String maxFrequencyTerm;
    String path;

    public Article(String id, String tt, String ab, String bod, String j, String pub, ArrayList<String> auth, HashSet<String> cat, String path) {
        this.pmcId = id;
        this.title = tt;
        this.abstr = ab;
        this.body = bod;
        this.journal = j;
        this.publisher = pub;
        this.authors = auth;
        this.categories = cat;
        this.path = path;
    }

    public static String tokenize(String word, List<String> tokens) {
        char[] punctuation = new char[]{'.', ',', '?', '!', ';', ':', '\'', '"', ')', ']', '}', '(', '[', '{', '<', '>', '/', '\\', '-', '=', '+', '*', '~', '\t', '|', '^', '_', '#', '$', '%', '&', '\n'};
        if (tokens.contains(word)) {
            return "A";
        } else {
            char[] var3 = punctuation;
            int var4 = punctuation.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                char p = var3[var5];
                if (word.indexOf(p) != -1) {
                    String tmp = word.replace(Character.toString(p), "");
                    word = tokenize(tmp, tokens);
                }
            }

            if (word.isEmpty()) {
                return "A";
            } else {
                return word;
            }
        }
    }

    public void tokenize(List<String> stopWords) {
        this.titleTokenized = new ArrayList();
        this.abstrTokenized = new ArrayList();
        this.bodyTokenized = new ArrayList();
        this.journalTokenized = new ArrayList();
        this.publisherTokenized = new ArrayList();
        int rank = 0;
        String[] words = this.title.split(" ");
        String[] var4 = words;
        int var5 = words.length;

        int var6;
        String word;
        String tmp;
        for(var6 = 0; var6 < var5; ++var6) {
            word = var4[var6];
            ++rank;
            tmp = tokenize(word, stopWords);
            if (!tmp.endsWith("A")) {
                this.titleTokenized.add(Stemmer.Stem(tmp));
            }
        }

        words = this.abstr.split(" ");
        var4 = words;
        var5 = words.length;

        for(var6 = 0; var6 < var5; ++var6) {
            word = var4[var6];
            ++rank;
            tmp = tokenize(word, stopWords);
            if (!tmp.endsWith("A")) {
                this.abstrTokenized.add(Stemmer.Stem(tmp));
            }
        }

        words = this.body.split(" ");
        var4 = words;
        var5 = words.length;

        for(var6 = 0; var6 < var5; ++var6) {
            word = var4[var6];
            ++rank;
            tmp = tokenize(word, stopWords);
            if (!tmp.endsWith("A")) {
                this.bodyTokenized.add(Stemmer.Stem(tmp));
            }
        }

        words = this.journal.split(" ");
        var4 = words;
        var5 = words.length;

        for(var6 = 0; var6 < var5; ++var6) {
            word = var4[var6];
            ++rank;
            tmp = tokenize(word, stopWords);
            if (!tmp.endsWith("A")) {
                this.journalTokenized.add(Stemmer.Stem(tmp));
            }
        }

        words = this.publisher.split(" ");
        var4 = words;
        var5 = words.length;

        for(var6 = 0; var6 < var5; ++var6) {
            word = var4[var6];
            ++rank;
            tmp = tokenize(word, stopWords);
            if (!tmp.endsWith("A")) {
                this.publisherTokenized.add(Stemmer.Stem(tmp));
            }
        }

    }

    public void setVocabulary(Map<String, Map<String, ArrayList<Integer>>> vocabulary) {
        this.vocabulary = vocabulary;
    }

    public Map<String, Map<String, ArrayList<Integer>>> getVocabulary() {
        return this.vocabulary;
    }

    public int getMaxFrequency() {
        return this.maxFrequency;
    }

    public String getMaxFrequencyTerm() {
        return this.maxFrequencyTerm;
    }

    public void setMaxFrequency(int maxFrequency) {
        this.maxFrequency = maxFrequency;
    }

    public void setMaxFrequencyTerm(String maxFrequencyTerm) {
        this.maxFrequencyTerm = maxFrequencyTerm;
    }
}
