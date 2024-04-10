package PHASE_A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Word {

    /**
     * Integer : Document ID
     * String : tag
     * Integer(inside of map) : frequency
     */
    private Map<Integer,Map<String, Integer>> TagFrequency = new HashMap<>();
    /**
     * String : Word Value
     */
    private String value;
    /**
     * Integer(first parameter) : document ID.
     * Integer(second parameter) : frequency on this file.
     */
    private Map<Integer, Integer> TermFrequecy = new HashMap<>();
    // Document Id, Array of ranks [1,4,6,..]
    /**
     * Integer : document ID
     * ArrayList of Integers : position where the term is found in the document
     */
    private Map<Integer, ArrayList<Integer>> WordDocumentRank = new HashMap<>();

    public Word(String word){
        this.value = word;
    }

    public Map<Integer, Map<String, Integer>> getTagFrequency() {
        return TagFrequency;
    }

    public Map<Integer, Integer> getTermFrequecy() {
        return TermFrequecy;
    }

    public Map<Integer, ArrayList<Integer>> getWordDocumentRank() {
        return WordDocumentRank;

    }

    public String getValue() {
        return value;
    }

    public void setTagFrequency(Map<Integer, Map<String, Integer>> tagFrequency) {
        TagFrequency = tagFrequency;
    }

    public void setTermFrequecy(Map<Integer, Integer> termFrequecy) {
        TermFrequecy = termFrequecy;
    }

    public void setWordDocumentRank(Map<Integer, ArrayList<Integer>> wordDocumentRank) {
        WordDocumentRank = wordDocumentRank;
    }

}
