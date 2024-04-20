package PHASE_A;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import gr.uoc.csd.hy463.NXMLFileReader;

import static java.lang.Math.sqrt;

public class Analysis {


    private ArrayList<Article> articles = new ArrayList<>();
    private Map<String,Word> Words = new TreeMap<>();
    private List<String> StopWords;
    // 4 kBytes
    private static final int MEM_THRESHOLD = 512 * 512;
    // Partial vocab Files queue
    private static final Queue<String> VocabQueue = new ArrayDeque<>();
    // Partial Posting Files queue
    private static final Queue<String> PostingQueue = new ArrayDeque<>();

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

    private static void deleteFolder(File folder) {
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteFolder(file);
                    } else {
                        file.delete();
                    }
                }
            }
            folder.delete();
        }
    }

    private void createPostingFile(File file,File folder,int index){
        File postingFile = new File(folder, "PostingFile" + index + ".txt");
        //add to a different queue
        PostingQueue.add(postingFile.getAbsolutePath());
        try{
            BufferedWriter writePost = new BufferedWriter(new FileWriter(postingFile, true));
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            while((line = reader.readLine()) != null){
                String[] words = line.split("\t");
                String word = words[0];

                Word term = this.Words.get(word);
                if(term == null){
                    System.out.println("EIMASTE: "+ word);
                }else{
                    Map<Integer,Map<String, ArrayList<Integer>>> tagFrequency= term.getTagFrequency();
                    for(Map.Entry<Integer,Map<String, ArrayList<Integer>>> entry : term.getTagFrequency().entrySet()){
                        Integer id = entry.getKey();
                        Integer termFreq = term.getTermFrequecy().get(id); // has error here
                        writePost.write(id +"\t" + termFreq + "\t" + tagFrequency + "\n");
                    }
                }
            }
            reader.close();
            writePost.close();
        }catch (IOException e) {
            System.out.println("An error occurred while creating the file: " + e.getMessage());
        }

    }

    private int generatePointer(Word word){
        String str = "";
        for(Integer id : word.getTagFrequency().keySet()){
            str +=id + "\t" + word.getTermFrequecy().get(id) +"\t"+ word.getTagFrequency() +"\n";
        }
        byte[] bytes = str.getBytes();
        return bytes.length;
    }

    private void createCollectionIndex(File folder, int index){
        System.out.println("Indexing...");
        long postingPointer = 0;

        File vocabularyFile = new File(folder, "VocabularyFile"+index+".txt");
        VocabQueue.add(vocabularyFile.getAbsolutePath());   // Merging
        try {
            FileWriter writer = new FileWriter(vocabularyFile, true);
            BufferedWriter bw = new BufferedWriter(writer);
            String line = "";
            // Write like this: bw.write("asd");
            for(Map.Entry<String,Word> entry: Words.entrySet()){
                line = entry.getKey() + "\t"+ entry.getValue().getdF()+"\t" + postingPointer + "\n";
                bw.write(line);
                postingPointer += generatePointer(entry.getValue());
            }
            bw.close();
            createPostingFile(vocabularyFile,folder,index);


        } catch (IOException e) {
            System.out.println("An error occurred while creating the file: " + e.getMessage());
        }

        /*File documentFile = new File(folder, "DocumentsFile.txt");
        clearFile(documentFile);

        try {
            FileWriter writer = new FileWriter(documentFile, true);
            BufferedWriter bw = new BufferedWriter(writer);
            // Write like this: bw.write("asd");
            for(Article article: articles){
                bw.write(article.pmcId + "\t"+ article.path+ "\n");
                double norm = 0;
                for(Map.Entry<String,Word> entry: Words.entrySet()){
                    if(entry.getValue().getTdIDFweight().containsKey(article.pmcId)){
                        norm += entry.getValue().getTdIDFweight().get(article.pmcId)* entry.getValue().getTdIDFweight().get(article.pmcId);
                    }
                }
                norm = sqrt(norm);
                bw.write(norm + "\n");
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file: " + e.getMessage());
        }*/
    }

    private  void listFilesForFolder(File folder) {


        File CollectionIndex = new File("CollectionIndex");
        deleteFolder(CollectionIndex);
        CollectionIndex.mkdir();
        int index = 0;
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                try {
                    File file = new File(fileEntry.getAbsolutePath());
                    // System.out.println(file);
                    NXMLFileReader xmlFile = new NXMLFileReader(file);
                    String pmcid = xmlFile.getPMCID();
                    String title = xmlFile.getTitle();
                    String abstr = xmlFile.getAbstr();
                    String body = xmlFile.getBody();
                    String journal = xmlFile.getJournal();
                    String publisher = xmlFile.getPublisher();
                    ArrayList<String> authors = xmlFile.getAuthors();
                    HashSet<String> categories =xmlFile.getCategories();

//                    System.out.println("- PMC ID: " + pmcid);
//                    System.out.println("- Title: " + title);
//                    System.out.println("- Abstract: " + abstr);
//                    System.out.println("- Body: " + body);
//                    System.out.println("- Journal: " + journal);
//                    System.out.println("- Publisher: " + publisher);
//                    System.out.println("- Authors: " + authors);
//                    System.out.println("- Categories: " + categories);


                    Article article = new Article(Integer.parseInt(pmcid), title, abstr, body, journal, publisher, authors, categories, fileEntry.getAbsolutePath());
                    articles.add(article);

                    generate(article);
                    System.out.println("Current mem usage " + getCurrentMemory());
                    //checkaroume to current mem > threshold
                    if(getCurrentMemory() > MEM_THRESHOLD) {
                        //grapsoume vocabFile kai Posting File
                        createCollectionIndex(CollectionIndex, index);
                        index++;
                        Words.clear();
                        System.out.println("Current mem usage " + getCurrentMemory());
                    }
                    //createCollectionIndex();
                    // vocabFile
                    // go to next articlec

                    // mem > threshold
                    // new vocabFile
                    // create Posdting
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        }

        Merge(CollectionIndex);
    } // listFilesForFolder

    private int getCurrentMemory(){
        int mem = 0;
        for(Word word : Words.values()){
            // value bytes
            mem += word.getValue().getBytes().length;
            // df bytes
            mem += 4;
            // Map overhead
            mem += word.getTagFrequency().size() *32 + word.getTermFrequecy().size() *32;
            for(Map.Entry<Integer, Map<String, ArrayList<Integer>>> entry : word.getTagFrequency().entrySet()){
                // Map  overhead
                mem += entry.getValue().size() * 32;
                // Ineger bytes
                mem += 4;
                for (Map.Entry<String, ArrayList<Integer>> innerEntry : entry.getValue().entrySet()) {
                    // Inner String
                    mem += innerEntry.getKey().getBytes().length;
                    //Array List overhead
                    mem += 24;
                    mem += innerEntry.getValue().size() * 4;
                }
            }
            // 2 Integers and 24 ArrayList overhead
            mem += word.getTermFrequecy().size() * 8 + 24;
        }
        return mem;
    }

    /**
     * Creates for every article its vocabulary, adds words to the global vocabulary
     */
    private void generate(Article article){


            article.tokenize(StopWords);
            int MaxFreq = 0;
            String maxFreqTerm = "";

            Map<String,Map<String,ArrayList<Integer>>> test = createVocabulary(article);
            article.setVocabulary(test);

            for(String w : test.keySet()) {
                // Set Tag Frequency
                Map<Integer,Map<String,ArrayList<Integer>>> TagFrequency = new HashMap<>();
                // Set Term Frequency
                Map<Integer, Integer> tf = new HashMap<>();

                Word x;
                if(!Words.containsKey(w)) {
                    x = new Word(w);
                    Words.put(w,x);
                }
                Map<String,ArrayList<Integer>> freqArticle = test.get(w);
                x = Words.get(w);
                TagFrequency.put(article.pmcId,freqArticle);
                x.setTagFrequency(TagFrequency);
                int sum = 0;
                for (ArrayList<Integer> value: freqArticle.values()){
                    sum += value.size();
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


        for(Map.Entry<String,Word> entry: Words.entrySet()) {
            int dF = entry.getValue().getTagFrequency().size();
            entry.getValue().setdF(dF);
            //entry.getValue().setTdIDFweight(articles);
            // System.out.println(entry.getValue().getValue()+"------" + entry.getValue().getTagFrequency() +
            //        "------" + entry.getValue().getdF() + "------" + entry.getValue().getTermFrequecy());
        }

    }

    private ArrayList<Article> getArticles() {
        return articles;
    }


    private Map<String,Map<String,ArrayList<Integer>>> createVocabulary(Article a) {
        Map<String,Map<String,ArrayList<Integer>>>  vocabulary = new TreeMap<>();

        Integer ID = a.pmcId;
        Integer title_counter = 0;
        for (String w : a.titleTokenized) {
            if (!vocabulary.containsKey(w)) {
                vocabulary.put(w, new HashMap<>());
            }
            Map<String,ArrayList<Integer>> temp1 = vocabulary.get(w);
            temp1.put("Title", temp1.getOrDefault("Title", new ArrayList<Integer>()));
            ArrayList<Integer> temp2 = temp1.get("Title");
            temp2.add(title_counter++);
            temp1.put("Title",temp2);
            vocabulary.put(w,temp1);
        }
        // MAP : <"Malaria", { {title = 3} , {abstract = 5},
        Integer abstract_counter = 0;
        for (String w : a.abstrTokenized) {
            if (!vocabulary.containsKey(w)) {
                vocabulary.put(w, new HashMap<>());
            }
            Map<String,ArrayList<Integer>> temp1 = vocabulary.get(w);
            temp1.put("Abstract", temp1.getOrDefault("Abstract", new ArrayList<Integer>()));
            ArrayList<Integer> temp2 = temp1.get("Abstract");
            temp2.add(abstract_counter++);
            temp1.put("Abstract",temp2);
            vocabulary.put(w,temp1);
        }
        Integer body_counter = 0;
        for (String w : a.bodyTokenized) {
            if (!vocabulary.containsKey(w)) {
                vocabulary.put(w, new HashMap<>());
            }
            Map<String,ArrayList<Integer>> temp1 = vocabulary.get(w);
            temp1.put("Body", temp1.getOrDefault("Body", new ArrayList<Integer>()));
            ArrayList<Integer> temp2 = temp1.get("Body");
            temp2.add(body_counter++);
            temp1.put("Body",temp2);
            vocabulary.put(w,temp1);
        }
        Integer journal_counter = 0;
        for (String w : a.journalTokenized) {
            if (!vocabulary.containsKey(w)) {
                vocabulary.put(w, new HashMap<>());
            }
            Map<String,ArrayList<Integer>> temp1 = vocabulary.get(w);

            temp1.put("Journal", temp1.getOrDefault("Journal", new ArrayList<Integer>()));
            ArrayList<Integer> temp2 = temp1.get("Journal");
            temp2.add(journal_counter++);
            temp1.put("Journal",temp2);
            vocabulary.put(w,temp1);
        }
        Integer publisher_counter = 0;
        for (String w : a.publisherTokenized) {
            if (!vocabulary.containsKey(w)) {
                vocabulary.put(w, new HashMap<>());
            }
            Map<String,ArrayList<Integer>> temp1 = vocabulary.get(w);
            temp1.put("Publisher", temp1.getOrDefault("Publisher", new ArrayList<Integer>()));
            ArrayList<Integer> temp2 = temp1.get("Publisher");
            temp2.add(publisher_counter++);
            temp1.put("Publisher",temp2);
            vocabulary.put(w,temp1);
        }
        return vocabulary;
    }

    private void Merge(File folder) {
        System.out.println("Merging...");
        int index = 0;
        while(!VocabQueue.isEmpty()){
            if(VocabQueue.size() == 1){
                String path1 = VocabQueue.remove();
                System.out.println("Merging:" + path1);
            }else{
                String voc_path1 = VocabQueue.remove();
                String voc_path2 = VocabQueue.remove();
                String post_path1 = PostingQueue.remove();
                String post_path2 = PostingQueue.remove();
                File vocabFile = new File(folder, "VocabularyMerged"+index+".txt");
                File postingFile = new File(folder, "PostingMerged"+index+".txt");


                try {

                    RandomAccessFile postFile1 = new RandomAccessFile(new File(post_path1),"r");
                    RandomAccessFile postFile2 = new RandomAccessFile(new File(post_path2),"r");
                    long postingPointer = 0;
                    BufferedReader reader1 = new BufferedReader(new FileReader(voc_path1));
                    BufferedReader reader2 = new BufferedReader(new FileReader(voc_path2));
                    String line1, line2,line3,line4;
                    String[] tokens1 = new String[3];
                    String[] tokens2 = new String[3];

                    String[] tokens3 = new String[3];
                    String[] tokens4 = new String[3];
                    BufferedWriter vocabWriter = new BufferedWriter(new FileWriter(vocabFile, true));
                    BufferedWriter postWriter = new BufferedWriter(new FileWriter(postingFile, true));

                    line1 = reader1.readLine();
                    line2 = reader2.readLine();
                    int equal_counter = 0;
                    while(line1 != null && line2 != null){
                        reader1.mark(4096);
                        line3 = reader1.readLine();
                        reader1.reset();
                        reader2.mark(4096);
                        line4= reader2.readLine();
                        reader2.reset();

                        tokens1 = line1.split("\t");
                        tokens2 = line2.split("\t");
                        if(line3 != null) {
                            tokens3 = line3.split("\t");
                        }
                        if (line4 != null) {
                            tokens4 = line4.split("\t");
                        }

                        // Writes word of tokens1[]
                        if(tokens2[0].compareTo(tokens1[0]) > 0){

                            vocabWriter.write(tokens1[0] + "\t" + tokens1[1] + "\t" + postingPointer+ "\n");

                            long pointer = Integer.parseInt(tokens3[2]) - Integer.parseInt(tokens1[2]); // HOW MANY TO READ FROM POSTING FILE
                            postFile1.seek(Integer.parseInt(tokens1[2]));
                            byte[] buf = new byte[(int) pointer];
                            postFile1.readFully(buf);
                            String s = new String(buf, "UTF-8");
                            postingPointer += pointer;
                            postWriter.write(s);
                            line1 = reader1.readLine();
                        }
                        else if(tokens2[0].compareTo(tokens1[0]) < 0){
                            vocabWriter.write(tokens2[0] + "\t" + tokens1[1] + "\t" +postingPointer+ "\n");
                            long pointer = Integer.parseInt(tokens4[2]) - Integer.parseInt(tokens2[2]);
                            postFile2.seek(Integer.parseInt(tokens2[2]));
                            byte[] buf = new byte[(int) pointer];
                            postFile2.readFully(buf);
                            String s = new String(buf, "UTF-8");
                            postingPointer += pointer;
                            postWriter.write(s);
                            line2 = reader2.readLine();
                        } else {
                            equal_counter++;
                            int newDf = Integer.parseInt(tokens1[1]) + Integer.parseInt(tokens2[1]);
                            vocabWriter.write(tokens2[0] + "\t" + newDf + "\t" +postingPointer+ "\n");


                            long pointer1 = Integer.parseInt(tokens3[2]) - Integer.parseInt(tokens1[2]);
                            long pointer2 = Integer.parseInt(tokens4[2]) - Integer.parseInt(tokens2[2]);

                            byte[] buf1 = new byte[(int) pointer1];
                            byte[] buf2 = new byte[(int) pointer2];
                            postFile1.readFully(buf1);
                            postFile2.readFully(buf2);
                            String s1 = new String(buf1, "UTF-8");
                            String s2 = new String(buf2, "UTF-8");
                            postingPointer += pointer1+pointer2;
                            postWriter.write(s1);
                            postWriter.write(s2);
                            line1 = reader1.readLine();
                            line2 = reader2.readLine();
                           // line1 = line3;
                           // line2 = line4;
                        }

                    }
                    System.out.println(equal_counter);
                    vocabWriter.close();

                }catch(IOException e){

                }

                System.out.println("Merging:" + voc_path1 + "\t" +voc_path2 );
            }

        }
    }
}

