//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package PHASE_A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Word {
    private Map<String, Map<String, ArrayList<Integer>>> TagFrequency = new HashMap();
    private String value;
    private int dF;
    private Map<String, Double> TermFrequecy = new HashMap();
    private Map<String, Map<String, ArrayList<Integer>>> WordDocumentRank = new HashMap();
    private Map<String, Double> TdIDFweight = new HashMap();

    public Word(String word) {
        this.value = word;
    }

    public Map<String, Map<String, ArrayList<Integer>>> getTagFrequency() {
        return this.TagFrequency;
    }

    public Map<String, Double> getTermFrequecy() {
        return this.TermFrequecy;
    }

    public void setdF(int dF) {
        this.dF = dF;
    }

    public int getdF() {
        return this.dF;
    }

    public String getValue() {
        return this.value;
    }

    public void setTagFrequency(Map<String, Map<String, ArrayList<Integer>>> tagFrequency) {
        this.TagFrequency.putAll(tagFrequency);
    }

    public void setTermFrequecy(Map<String, Double> termFrequecy) {
        this.TermFrequecy.putAll(termFrequecy);
    }

    public Map<String, Double> getTdIDFweight() {
        return this.TdIDFweight;
    }

    public void setTdIDFweight(ArrayList<Article> articles) {
        Iterator var2 = articles.iterator();

        while(var2.hasNext()) {
            Article article = (Article)var2.next();
            if (this.TermFrequecy.containsKey(article.pmcId)) {
                double tf = (Double)this.TermFrequecy.get(article.pmcId) / (double)article.getMaxFrequency();
                double idf = Math.log((double)articles.size() / (double)this.dF) / Math.log(2.0);
                if (((Map)this.TagFrequency.get(article.pmcId)).containsKey("Title")) {
                    tf *= 2.5;
                }

                if (((Map)this.TagFrequency.get(article.pmcId)).containsKey("Authors")) {
                    tf *= 2.0;
                }

                if (((Map)this.TagFrequency.get(article.pmcId)).containsKey("Categories")) {
                    tf *= 1.5;
                }

                this.TdIDFweight.put(article.pmcId, tf * idf);
            }
        }

    }
}
