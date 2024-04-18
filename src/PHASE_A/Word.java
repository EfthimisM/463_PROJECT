package PHASE_A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Word {

    /**
     * Integer : Document ID
     * String : tag
     * Integer(inside of map) : frequency
     */
   private Map<Integer,Map<String,ArrayList<Integer>>> TagFrequency = new HashMap<>();
    /**
     * String : Word Value
     */
    private String value;
    /**
     * document frequency : Number of documents where the term is encountered
     */
    private int dF;
    /**
     * Integer(first parameter) : document ID.
     * Integer(second parameter) : frequency on this file.
     */
    private Map<Integer, Integer> TermFrequecy = new HashMap<>();
    /**
     * Integer : document ID
     * ArrayList of Integers : position where the term is found in the document in each tag
     */
    private Map<Integer,Map <String ,ArrayList<Integer>>> WordDocumentRank = new HashMap<>();
    /**
     * Integer : document Id
     * Double : Cosine similarity of the term in the given document
     */
    private Map<Integer, Double> TdIDFweight = new HashMap<>();

    public Word(String word){
        this.value = word;
    }

    public Map<Integer, Map<String, ArrayList<Integer>>> getTagFrequency() {
        return TagFrequency;
    }

    public Map<Integer, Integer> getTermFrequecy() {
        return TermFrequecy;
    }

    public void setdF(int dF) {
        this.dF = dF;
    }

    public int getdF() {
        return dF;
    }

    public String getValue() {
        return value;
    }

    public void setTagFrequency( Map<Integer,Map<String,ArrayList<Integer>>> tagFrequency) {
        TagFrequency.putAll(tagFrequency);
    }

    public void setTermFrequecy(Map<Integer, Integer> termFrequecy) {
        TermFrequecy.putAll(termFrequecy);
    }

    public Map<Integer, Double> getTdIDFweight() {
        return TdIDFweight;
    }

    public void setTdIDFweight(ArrayList<Article> articles){
        for(Article article : articles){
            if(TermFrequecy.containsKey(article.pmcId)){
                double tf = (double) TermFrequecy.get(article.pmcId) / article.getMaxFrequency();
                double idf = Math.log( articles.size()/ (double) dF) / Math.log(2);
                if(this.TagFrequency.get(article.pmcId).containsKey("Title")){
                    tf *= 2.5;
                }
                if(this.TagFrequency.get(article.pmcId).containsKey("Authors")){
                    tf *= 2;
                }
                if(this.TagFrequency.get(article.pmcId).containsKey("Categories")){
                    tf *= 1.5;
                }
                TdIDFweight.put(article.pmcId, tf * idf);
            }
        }
    }

}
