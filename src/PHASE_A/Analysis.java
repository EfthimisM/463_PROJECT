package PHASE_A;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

import gr.uoc.csd.hy463.NXMLFileReader;


public class Analysis {

    private Map<String, String> articles = new HashMap<>();
    private int depth = 0;
    private Map<String,Word> Words = new TreeMap<>();
    private List<String> StopWords;
    // 4 kBytes
    private static final int MEM_THRESHOLD = 1024 * 1024 *  10 ;
    // Partial vocab Files queue
    private static final ConcurrentLinkedQueue<String> VocabQueue = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<String> VocabQueue2 =  new ConcurrentLinkedQueue<>();
    // Partial Posting Files queue
    File CollectionIndex ;
    private static final  ConcurrentLinkedQueue<String> PostingQueue =  new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<String> PostingQueue2 =  new ConcurrentLinkedQueue<>();
    int index = 0;

    /**
     *
     * @param folder Folder of XML files
     * @param stopwords Words not to save
     */
    public Analysis(File folder, File stopwords, boolean isMini){
        //deleteFolder(CollectionIndex);
        List<String> stp = new ArrayList<>();
        CollectionIndex = new File("CollectionIndex");
        CollectionIndex.mkdir();

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
        listFilesForFolder(folder,isMini);
        File[] files = CollectionIndex.listFiles();

        // in case file remain
        if(files.length > 3) {
            processFolder(CollectionIndex);
        } else {
            System.out.println("You are ok");
        }

    }

    /**
     *
     * @param folderPath CollectionIndex
     *   for IDE issues that previous files werent being deleted.
     *
     */
    public static void processFolder(File folderPath) {
        if (!folderPath.isDirectory()) {
            System.out.println("Invalid folder path!");
            return;
        }

        File[] files = folderPath.listFiles();
        if (files == null) {
            System.out.println("Failed to list files in the folder!");
            return;
        }


        Arrays.sort(files, Comparator.comparing(File::getName).reversed());

        String highestVocabularyMerged = null;
        String highestPostingMerged = null;
        String documentsFile = null;


        for (File file : files) {
            if (file.isFile()) {
                String fileName = file.getName();

                if (fileName.startsWith("VocabularyMerged")) {
                    if (highestVocabularyMerged == null || fileName.compareTo(highestVocabularyMerged) > 0) {
                        highestVocabularyMerged = fileName;
                    } else {
                        System.out.println("Will clear file: "+fileName);
                        clearFile(file);
                    }
                }
                else if (fileName.startsWith("PostingMerged")) {
                    if (highestPostingMerged == null || fileName.compareTo(highestPostingMerged) > 0) {
                        highestPostingMerged = fileName;
                    } else {
                        System.out.println("Will clear file: "+fileName);
                        clearFile(file);
                    }
                }
                else if (fileName.equals("DocumentsFile.txt")) {
                    documentsFile = fileName;
                }
            }
        }

        System.out.println("Highest VocabularyMerged file: " + highestVocabularyMerged);
        System.out.println("Highest PostingMerged file: " + highestPostingMerged);
        System.out.println("DocumentsFile: " + documentsFile);
    }

    /**
     *
     * @param file Clears a file
     */
    private static void clearFile(File file) {
        if (file.exists()) {
            if (file.delete()) {
                File file1 = file;
                System.out.println("file deleted successfully: ");
            } else {
                System.out.println(file.getName());
                System.out.println("Failed to delete file: "+file.getName()+"  "+ file.delete() );
            }
        } else {
            System.out.println("file does not exist: ");
        }
    }

    /**
     *
     * @param folder folder to delete
     */
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

    /**
     *
     * @param word Word class object.
     * @return the String we will be putting into the Posting file.
     */
    private String createPostingString(Word word) {
        String str = "";
        Map<String,Map<String, ArrayList<Integer>>> tagFrequency= word.getTagFrequency();
        for(Map.Entry<String,Map<String, ArrayList<Integer>>> entry : word.getTagFrequency().entrySet()){
            String id = entry.getKey();
            Double termFreq = word.getTermFrequecy().get(id); // has error here
            //writePost.write(id +"\t" + termFreq + "\t" + tagFrequency + "\n");
            if(termFreq == null){
                termFreq = 0.0;
            }
            str +=  id + "\t" + String.format("%.4f",termFreq) + "\t";
            for (Map.Entry<String, ArrayList<Integer>> entry1 : entry.getValue().entrySet()) {
                str += entry1.getKey() + "\t";
                for (Integer freq : entry1.getValue()) {
                    str += freq + ", ";
                }
                str += "\t";
            }
            str += "\n";
        }
        return str;
    }

    /**
     *
     * @param file      Vocabulary File.
     * @param index     Regarding the file name.
     */
    private void createPostingFile(File file,int index){
        File postingFile = new File(CollectionIndex, "PostingFile" +depth+"_"+ index + ".txt");
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
                    System.out.println("Error on word: "+ word + "\t" +file.getAbsolutePath());
                }else{
                    String str = createPostingString(term);
                    writePost.write(str);
                }
            }
            reader.close();
            writePost.close();
        }catch (IOException e) {
            System.out.println("An error occurred while creating the file: " + e.getMessage());
        }

    }

    /**
     *
     * @param word  Word class object.
     * @return      Returns how many bytes the Vocabulary line will be.
     */
    private int generatePointer(Word word){
        String str = createPostingString(word);
        byte[] bytes = str.getBytes();
        return bytes.length;
    }

    /**
     *
     * @param folder Where the new files will be created
     * @param index Used for the file name
     */
    private void createCollectionIndex(File folder, int index){
        System.out.println("Indexing...");
        long postingPointer = 0;

        File vocabularyFile = new File(CollectionIndex, "VocabularyFile"+depth+"_"+index+".txt");
        VocabQueue.add(vocabularyFile.getAbsolutePath());   // Merging

        try {
            FileWriter writer = new FileWriter(vocabularyFile, true);
            BufferedWriter bw = new BufferedWriter(writer);
            String line = "";
            // Write like this: bw.write("asd");
            for(Map.Entry<String,Word> entry: Words.entrySet()){
                if(postingPointer < 0 ){
                    System.out.println("Error has occurred for pointer: " + postingPointer);
                }
                line = entry.getKey() + "\t"+ entry.getValue().getdF()+"\t" + postingPointer + "\n";
                bw.write(line);
                postingPointer += generatePointer(entry.getValue());
            }
            bw.close();
            writer.close();
            createPostingFile(vocabularyFile,index);

        } catch (IOException e) {
            System.out.println("An error occurred while creating the file: " + e.getMessage());
        }
    }

    /**
     *
     * @param folder Folder on where the XML files are being kept.
     *               Recursively checks for XML files, going into all nested folders.
     */
    private void listFilesForFolder(File folder, boolean isMini) {
        //deleteFolder(CollectionIndex);
        //CollectionIndex.mkdir();
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println(fileEntry.getAbsolutePath());
                listFilesForFolder(fileEntry,isMini);
            } else {
                try {
                    File file = new File(fileEntry.getAbsolutePath());
                    NXMLFileReader xmlFile = new NXMLFileReader(file);
                    String pmcid = xmlFile.getPMCID();
                    String title = xmlFile.getTitle();
                    String abstr = xmlFile.getAbstr();
                    String body = xmlFile.getBody();
                    String journal = xmlFile.getJournal();
                    String publisher = xmlFile.getPublisher();
                    ArrayList<String> authors = xmlFile.getAuthors();
                    HashSet<String> categories = xmlFile.getCategories();

                    Article article = new Article(pmcid, title, abstr, body, journal, publisher, authors, categories, fileEntry.getAbsolutePath());
                    generate(article);
                    //System.out.println("Current mem usage " + getCurrentMemory());
                    articles.put(fileEntry.getAbsolutePath(), pmcid);

                    // Check if current memory exceeds threshold
                    if (getCurrentMemory() > MEM_THRESHOLD) {
                        createCollectionIndex(CollectionIndex, index++);
                        Words.clear();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (!Words.isEmpty()) {
            createCollectionIndex(CollectionIndex, index++);
            Words.clear();
        }

        // Merge all collection indexes at the end

        if(isMini){
            Merge(CollectionIndex, index);
        }else{
            Merge(CollectionIndex, 0);
        }

    }

    /**
     *
     * @return An estimated memory usage of our current data.
     */
    private int getCurrentMemory(){
        int mem = 0;
        for(Word word : Words.values()){
            // value bytes
            mem += word.getValue().getBytes().length;
            // df bytes
            mem += 4;
            // Map overhead
            mem += word.getTagFrequency().size() *32 + word.getTermFrequecy().size() *32;
            for(Map.Entry<String, Map<String, ArrayList<Integer>>> entry : word.getTagFrequency().entrySet()){
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
            // 1 Integers, 1 Double and 24 ArrayList overhead
            mem += word.getTermFrequecy().size() * 12 + 24;
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

        // Find the max frequency
        for(String w : test.keySet()){
            Map<String,ArrayList<Integer>> freqArticle = test.get(w);

            int sum = 0;
            for (ArrayList<Integer> value: freqArticle.values()){
                sum += value.size();
            }
            if(sum > MaxFreq) {
                MaxFreq = sum;
                maxFreqTerm = w;
            }
        }

        for(String w : test.keySet()) {
            // Set Tag Frequency
            Map<String,Map<String,ArrayList<Integer>>> TagFrequency = new HashMap<>();
            // Set Term Frequency
            Map<String, Double> tf = new HashMap<>();

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
            double mul = 1;
            for (Map.Entry<String, ArrayList<Integer>> entry : freqArticle.entrySet()) {
                String key = entry.getKey();
                ArrayList<Integer> value = entry.getValue();
                if(key.equals("Abstract")) {
                    mul += 1;
                }else if(key.equals("Title")){
                    mul += 2;
                }else if(key.equals("Author")){
                    mul += 1;
                }
                sum += value.size();

            }
            //if(x.getTagFrequency().get(article).get)
            double tf_mod = (double) sum * mul / MaxFreq;
            tf.put(article.pmcId,tf_mod);
            x.setTermFrequecy(tf);
        }
        article.setMaxFrequency(MaxFreq);
        article.setMaxFrequencyTerm(maxFreqTerm);


        for(Map.Entry<String,Word> entry: Words.entrySet()) {
            int dF = entry.getValue().getTagFrequency().size();
            entry.getValue().setdF(dF);
        }
    }

    /**
     *
     * @param a Article object
     * @return Returns a Map that has <Term , Map <Tag, ArrayList<Positions on Tag> >
     */
    private Map<String,Map<String,ArrayList<Integer>>> createVocabulary(Article a) {
        Map<String,Map<String,ArrayList<Integer>>>  vocabulary = new TreeMap<>();

        String ID = a.pmcId;
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

    /**
     *
     * @param folder            Folder on where document file will be generated.
     * @param postingFile       Last remaining posting file.
     * @param vocabularyFile    Last remaining vocabulary file
     * @return
     */
    private File createDocumentsFile(File folder,File postingFile, File vocabularyFile){
        File documentsFile = new File(folder, "DocumentsFile.txt");
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(documentsFile.getAbsolutePath(), false));
            writer.write(articles.size() + "\n");

            BufferedReader reader = new BufferedReader(new FileReader(vocabularyFile.getAbsolutePath()));
            RandomAccessFile postingAccess = new RandomAccessFile(postingFile, "r");
            String line;
            String nextLine;

            Map<String, Double> vectorNorms = new HashMap<>();
            for(Map.Entry<String, String> entry : articles.entrySet()){
                vectorNorms.put(entry.getValue(), 0.0);
            }
            line = reader.readLine();
            while(line != null){

                reader.mark(100000);
                nextLine = reader.readLine();
                reader.reset();

                String[] tokens = line.split("\t");
                String s;

                if(nextLine != null){
                    String[] nextTokens = nextLine.split("\t");
                    long pointer = Math.abs(Long.parseLong(nextTokens[2]) - Long.parseLong(tokens[2]));
                    postingAccess.seek(Long.parseLong(tokens[2]));
                    byte[] buf = new byte[(int) pointer];
                    postingAccess.readFully(buf);
                    s = new String(buf, "UTF-8");

                }else{
                    long pointer = postingFile.length() - Long.parseLong(tokens[2]);
                    postingAccess.seek(Long.parseLong(tokens[2]));
                    byte[] buf = new byte[(int) pointer];
                    postingAccess.readFully(buf);
                    s = new String(buf, "UTF-8");
                }

                String[] linetokens = s.split("\n");

                for(String str : linetokens){
                    String[] tmp = str.split("\t");

                    if(tmp.length>1){
                        String pmcId = tmp[0];
                        try{
                            double tf = Double.parseDouble(tmp[1]);
                            double idf = Math.log(articles.size()/ (double)Integer.parseInt(tokens[1]))/Math.log(2);
                            double weight = Math.pow(tf * idf,2);
                            double a = vectorNorms.get(pmcId) + weight;
                            vectorNorms.put(pmcId,a);
                        }catch(Exception e){
                            //vectorNorms.put(pmcId,0.0);
                            //System.out.println("ERROR: "+e);
                        }

                    }else{
                       // System.out.println("Error has occured for line: "+tmp[0]);
                    }
                }
                line = reader.readLine();
            }

            for(Map.Entry<String, String> entry : articles.entrySet()){
                String path = entry.getKey();
                String type = "";
                if(path.contains("diagnosis")){
                    type = "diagnosis";
                }else if (path.contains("test")){
                    type = "test";
                } else if (path.contains("treatment")){
                    type = "treatment";
                }
                Double norm = Math.sqrt(vectorNorms.get(entry.getValue()));
                writer.write(path + "\t"+ type + "\t" + norm + "\n");
            }
            writer.close();
        }catch (IOException e){
            System.out.println("Error on createDocumentsFile");
        }
        return documentsFile;
    }

    /**
     *
     * @param folder    Where the file-merging process is happening > Works recursively be careful
     * @param index     For the file creation.
     */
    private void Merge(File folder,int index) {
        System.out.println("Merging...");
        System.out.println(VocabQueue);
        if(VocabQueue.size() == 1 ) {
            String path1 = VocabQueue.remove();
            String path2 = PostingQueue.remove();
            VocabQueue2.add(path1);
            PostingQueue2.add(path2);
            createDocumentsFile(folder, new File(path2), new File(path1));

        } else if(VocabQueue.size() > 1){

            try {
                String voc_path1 = VocabQueue.remove();
                String voc_path2 = VocabQueue.remove();
                String post_path1 = PostingQueue.remove();
                String post_path2 = PostingQueue.remove();
                File vocabFile = new File(folder, "VocabularyMerged" + index + ".txt");
                File postingFile = new File(folder, "PostingMerged" + index + ".txt");
                RandomAccessFile postFile1 = new RandomAccessFile(new File(post_path1), "r");
                RandomAccessFile postFile2 = new RandomAccessFile(new File(post_path2), "r");
                long postingPointer = 0;

                // Posting1,Posting2 , needed when one file is smaller than the other, to handle the last pointer
                File posting1 = new File(post_path1);
                File posting2 = new File(post_path2);
                File vocab1 = new File(voc_path1);
                File vocab2 = new File(voc_path2);

                BufferedReader reader1 = new BufferedReader(new FileReader(voc_path1));
                BufferedReader reader2 = new BufferedReader(new FileReader(voc_path2));
                String line1, line2, line3, line4;
                String[] tokens1 = new String[3];
                String[] tokens2 = new String[3];

                String[] tokens3 = new String[3];
                String[] tokens4 = new String[3];
                BufferedWriter vocabWriter = new BufferedWriter(new FileWriter(vocabFile, true));
                BufferedWriter postWriter = new BufferedWriter(new FileWriter(postingFile, true));

                line1 = reader1.readLine();
                line2 = reader2.readLine();

                while (line1 != null && line2 != null) {
                    reader1.mark(10000);
                    line3 = reader1.readLine();
                    reader1.reset();
                    reader2.mark(10000);
                    line4 = reader2.readLine();
                    reader2.reset();


                    if (line3 != null) {
                        tokens3 = line3.split("\t");
                    }
                    if (line4 != null) {
                        tokens4 = line4.split("\t");
                    }

                    tokens1 = line1.split("\t");
                    tokens2 = line2.split("\t");

                    // Writes word of tokens1[]
                    if (tokens2[0].compareTo(tokens1[0]) > 0) {
                        vocabWriter.write(tokens1[0] + "\t" + tokens1[1] + "\t" + postingPointer + "\n");
                        long pointer = Math.abs(Long.parseLong(tokens3[2]) - Long.parseLong(tokens1[2])); // HOW MANY TO READ FROM POSTING FILE
                        if (pointer == 0) {
                            pointer = posting1.length() - Long.parseLong(tokens1[2]);
                            postFile1.seek(Long.parseLong(tokens1[2]));
                            byte[] buf1 = new byte[(int) pointer];
                            postFile1.readFully(buf1);
                            String s = new String(buf1, "UTF-8");
                            postingPointer += pointer;
                            postWriter.write(s);

                        } else {
                            postFile1.seek(Long.parseLong(tokens1[2]));
                            byte[] buf = new byte[(int) pointer];
                            postFile1.readFully(buf);
                            String s = new String(buf, "UTF-8");
                            postingPointer += pointer;
                            postWriter.write(s);
                        }
                        line1 = reader1.readLine();
                    } else if (tokens2[0].compareTo(tokens1[0]) < 0) {
                        vocabWriter.write(tokens2[0] + "\t" + tokens2[1] + "\t" + postingPointer + "\n");
                        long pointer = Math.abs(Long.parseLong(tokens4[2]) - Long.parseLong(tokens2[2]));
                        if (pointer == 0) {
                            pointer = posting2.length() - Long.parseLong(tokens2[2]);
                            postFile2.seek(Long.parseLong(tokens2[2]));
                            byte[] buf1 = new byte[(int) pointer];
                            postFile2.readFully(buf1);
                            String s = new String(buf1, "UTF-8");
                            postingPointer += pointer;
                            postWriter.write(s);
                        } else {
                            postFile2.seek(Long.parseLong(tokens2[2]));
                            byte[] buf = new byte[(int) pointer];
                            postFile2.readFully(buf);
                            String s = new String(buf, "UTF-8");
                            postingPointer += pointer;
                            postWriter.write(s);

                        }
                        line2 = reader2.readLine();

                    } else {
                        int newDf = Integer.parseInt(tokens1[1]) + Integer.parseInt(tokens2[1]);
                        vocabWriter.write(tokens2[0] + "\t" + newDf + "\t" + postingPointer + "\n");
                        long pointer1;
                        long pointer2;
                        if (line3 == null && line4 == null) {
                            pointer1 = posting1.length() - Long.parseLong(tokens1[2]);
                            pointer2 = posting2.length() - Long.parseLong(tokens2[2]);

                        } else if (line4 == null) {
                            pointer1 = Math.abs(Long.parseLong(tokens3[2]) - Long.parseLong(tokens1[2]));
                            pointer2 = posting2.length() - Long.parseLong(tokens2[2]);

                        } else if (line3 == null) {
                            pointer1 = posting1.length() - Long.parseLong(tokens1[2]);
                            pointer2 = Math.abs(Long.parseLong(tokens4[2]) - Long.parseLong(tokens2[2]));

                        } else {
                            pointer1 = Math.abs(Long.parseLong(tokens3[2]) - Long.parseLong(tokens1[2]));
                            pointer2 = Math.abs(Long.parseLong(tokens4[2]) - Long.parseLong(tokens2[2]));
                        }
                        if (pointer1 < 0 || pointer2 < 0) {
                            System.out.print(tokens3[0] + "\t" + tokens4[0] + "  Error here");
                        }
                        byte[] buf1 = new byte[Math.abs((int) pointer1)];
                        byte[] buf2 = new byte[Math.abs((int) pointer2)];
                        try {
                            postFile1.readFully(buf1);
                        } catch (EOFException e) {
                            // Read remaining bytes
                            long remaining = (postFile1.length() - postFile1.getFilePointer());
                            buf1 = new byte[(int)remaining];
                            pointer1 = remaining;
                            postFile1.readFully(buf1);
                            System.err.println("Caught EOFException while reading postFile1: 732" + e.getMessage());
                        }

                        try {
                            postFile2.readFully(buf2);
                        } catch (EOFException e) {
                            // Read remaining bytes
                            long remaining =  (postFile2.length() - postFile2.getFilePointer());
                            buf2 = new byte[(int)remaining];
                            pointer2 = remaining;
                            postFile2.readFully(buf2);
                            System.err.println("Caught EOFException while reading postFile2: 743" + e.getMessage());
                        }

                        //postFile1.readFully(buf1);
                        //postFile2.readFully(buf2);
                        String s1 = new String(buf1, "UTF-8");
                        String s2 = new String(buf2, "UTF-8");
                        postingPointer += pointer1 + pointer2;
                        postWriter.write(s1);
                        postWriter.write(s2);
                        line1 = reader1.readLine();
                        line2 = reader2.readLine();

                    }

                }

                if (line1 == null) {
                    while (line2 != null) {

                        tokens2 = line2.split("\t");
                        vocabWriter.write(tokens2[0] + "\t" + tokens2[1] + "\t" + postingPointer + "\n");
                        reader2.mark(10000);
                        line4 = reader2.readLine();
                        reader2.reset();
                        if (line4 != null) {
                            tokens4 = line4.split("\t");
                            long pointer = Math.abs(Long.parseLong(tokens4[2]) - Long.parseLong(tokens2[2]));
                            postFile2.seek(Long.parseLong(tokens2[2]));
                            byte[] buf = new byte[(int) pointer];

                            try{
                                postFile2.readFully(buf);
                            }catch(EOFException e){
                                long remaining = (postFile2.length() - postFile2.getFilePointer());
                                buf = new byte[(int)remaining];
                                pointer = remaining;
                                postFile2.readFully(buf);
                                System.err.println("Caught EOFException while reading postFile2: 781" + e.getMessage());
                            }

                            String s = new String(buf, "UTF-8");
                            postingPointer += pointer;
                            postWriter.write(s);
                            line2 = reader2.readLine();
                        } else {
                            long pointer;
                            pointer = posting2.length() - Long.parseLong(tokens2[2]);
                            postFile2.seek(Long.parseLong(tokens2[2]));
                            byte[] buf1 = new byte[(int) pointer];

                            try{
                                postFile2.readFully(buf1);
                            }catch(EOFException e){
                                long remaining =  (postFile2.length() - postFile2.getFilePointer());
                                buf1 = new byte[(int)remaining];
                                pointer = remaining;
                                postFile2.readFully(buf1);
                                System.err.println("Caught EOFException while reading postFile2 801: " + e.getMessage());
                            }

                            String s = new String(buf1, "UTF-8");
                            postingPointer += pointer;
                            postWriter.write(s);
                            line2 = reader2.readLine();

                        }

                    }


                } else if (line2 == null) {
                    while (line1 != null) {

                        tokens1 = line1.split("\t");
                        vocabWriter.write(tokens1[0] + "\t" + tokens1[1] + "\t" + postingPointer + "\n");
                        reader1.mark(10000);
                        line3 = reader1.readLine();
                        reader1.reset();
                        if (line3 != null) {
                            tokens3 = line3.split("\t");
                            long pointer = Math.abs(Long.parseLong(tokens3[2]) - Long.parseLong(tokens1[2]));
                            postFile1.seek(Long.parseLong(tokens1[2]));
                            byte[] buf = new byte[(int) pointer];
                            try{
                                postFile1.readFully(buf);
                                postingPointer += pointer;
                            }catch(EOFException e){
                                long remaining =  (postFile1.length() - postFile1.getFilePointer());
                                buf = new byte[(int)remaining];
                                postFile1.readFully(buf);
                                postingPointer += remaining;
                                System.err.println("Caught EOFException while reading postFile1 835: " + e.getMessage());
                            }
                            String s = new String(buf, "UTF-8");
                            postWriter.write(s);
                            line1 = reader1.readLine();
                        } else {
                            long pointer;
                            pointer = posting1.length() - Long.parseLong(tokens1[2]);
                            postFile1.seek(Long.parseLong(tokens1[2]));
                            byte[] buf1 = new byte[(int) pointer];
                            try{
                                postFile1.readFully(buf1);
                            }catch(EOFException e){
                                long remaining =  (postFile1.length() - postFile1.getFilePointer());
                                buf1 = new byte[(int)remaining];
                                pointer = remaining;
                                postFile2.readFully(buf1);
                                System.err.println("Caught EOFException while reading postFile1 852: " + e.getMessage());
                            }
                            String s = new String(buf1, "UTF-8");
                            postingPointer += pointer;
                            postWriter.write(s);
                            line1 = reader1.readLine();

                        }
                    }
                }
                vocabWriter.close();
                postWriter.close();
                reader1.close();
                reader2.close();
                postFile1.close();
                postFile2.close();

                clearFile(vocab1);
                clearFile(vocab2);
                clearFile(posting1);
                clearFile(posting2);
                System.out.println("Merging:" + voc_path1 + "\t" + voc_path2);
                VocabQueue.add(vocabFile.getAbsolutePath());
                PostingQueue.add(postingFile.getAbsolutePath());

                int i = index + 1;
                Merge(folder, i);

            } catch (IOException e) {
                System.out.println("Error on merge");
                e.printStackTrace();

                // Get the line number of the error
                StackTraceElement[] stackTrace = e.getStackTrace();
                if (stackTrace.length > 0) {
                    StackTraceElement firstElement = stackTrace[0];
                    System.err.println("Exception occurred in " + firstElement.getFileName()
                            + " at line " + firstElement.getLineNumber());
                }
                System.out.println("____________________________________________________________________");
            }
        }

        if(VocabQueue.isEmpty()) {
            VocabQueue.addAll(VocabQueue2);
            PostingQueue.addAll(PostingQueue2);
            VocabQueue2.clear();
            PostingQueue2.clear();
        }
        if(VocabQueue.isEmpty()) {
            if(VocabQueue2.isEmpty()) {

            } else {
                Merge(folder,index);
            }
        }

    }
}