package PHASE_B;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import mitos.stemmer.Stemmer;

public class GUI {

    private List<String> query;
    private String type;
    private List<String> StopWords = new ArrayList<>();
    private String display = "";
    private int N;

    public GUI(){

        List<String> stp = new ArrayList<>();


        try (BufferedReader br = new BufferedReader(new FileReader("StopWords"))) {
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

        JFrame frame = new JFrame("Phase B GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 550);

        // Create three text fields
        JTextField textField1 = new JTextField(30);
        JTextField textField2 = new JTextField(10);

        // Create the labels
        JLabel queryLabel = new JLabel("Query: ");
        JLabel typeLabel = new JLabel("Type: ");

        // Create a text area for displaying text
        JTextArea textArea = new JTextArea(10, 40);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);

        // Create button
        JButton button = new JButton("Search");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] tmp = textField1.getText().split("\\s+");
                query = new ArrayList<>();

                for(int i =0; i< tmp.length; i++){
                    if(!tokenize(Stemmer.Stem(tmp[i]), StopWords).equals("A")){
                        query.add(tokenize(Stemmer.Stem(tmp[i]), StopWords));
                    }
                }
                for(String word: query){
                    System.out.println(word);
                }

                type = textField2.getText();

                try {
                    search();
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            // Update the text area with the result
                            textArea.setText(display);
                        }
                    });
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // Create a panel to hold the text fields
        JPanel textFieldPanel = new JPanel();
        textFieldPanel.setLayout(new FlowLayout()); // Use FlowLayout to arrange components horizontally
        textFieldPanel.add(queryLabel);
        textFieldPanel.add(textField1);
        textFieldPanel.add(typeLabel);
        textFieldPanel.add(textField2);
        textFieldPanel.add(button);
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);

        // Create a panel to hold the text fields and separator
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(textFieldPanel, BorderLayout.CENTER);
        topPanel.add(separator, BorderLayout.SOUTH); // Add separator below the text fields

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);


        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // Function that removes punctuation and removes stop words
    public static String tokenize(String word, List<String> tokens) {
        char[] punctuation = {'.', ',', '?', '!', ';', ':', '\'', '\"', ')', ']', '}', '(', '[', '{', '<', '>', '/', '\\', '-', '=', '+', '*', '~', '\t','|','^','_','#','$','%','&','\n'};
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

    private void search() throws FileNotFoundException {
        File CollectionIndex = new File("CollectionIndex");
        File posting;
        String vocabPath = "";
        BufferedReader readerVocab = null;
        RandomAccessFile postings = null;
        List<String> documents = new ArrayList<>();
        display = "";

        // pmcid : score = score gia kathe term apo to query gia to document
        Map<String,Double> score = new HashMap<>();

        for (File fileEntry : CollectionIndex.listFiles()){

            if(fileEntry.getName().contains("Documents")){
                System.out.println("Documents File: "+fileEntry.getName());
                BufferedReader readDoc = new BufferedReader( new FileReader(fileEntry.getAbsolutePath()));
                try{
                    N = Integer.parseInt(readDoc.readLine());
                    String line;
                    while((line = readDoc.readLine()) != null){
                        String[] tokens = line.split("\t");
                        documents.add(tokens[0]);
                    }
                }catch (IOException e){
                    N = 0;
                }

            }

            if(fileEntry.getName().contains("Vocabulary")){
                System.out.println("Vocabulary File: "+fileEntry.getName());
                vocabPath = fileEntry.getAbsolutePath();
                readerVocab = new BufferedReader( new FileReader(fileEntry.getAbsolutePath()));

            }

            if(fileEntry.getName().contains("Posting")){
                System.out.println("Posting File: "+fileEntry.getName());
                posting = new File(fileEntry.getAbsolutePath());
                postings = new RandomAccessFile(posting, "r");
            }
        }

        try{

            for(String term : query){
                System.out.println("test");
                readerVocab = new BufferedReader( new FileReader(vocabPath));
                String line = readerVocab.readLine();
                while(line != null){

                    String[] tokens = line.split("\t");
                    long posting_pointer = 0;
                    if(term.equals(tokens[0])){
                        // To find the pointer
                        readerVocab.mark(4096);
                        String line1 = readerVocab.readLine();
                        readerVocab.reset();
                        String[] tokens1 = new String[3];

                        // watch for initialization of postingfile
                        if (line1 != null) {
                            tokens1 = line1.split("\t");
                            posting_pointer = Math.abs(Long.parseLong(tokens1[2]) - Long.parseLong(tokens[2]));
                            System.out.println("correct");
                        } else {
                            System.out.println("not correct");
                            posting_pointer = postings.length() - Math.abs(Long.parseLong(tokens[2]));
                        }

                        int df = Integer.parseInt(tokens[1]);
                        double idf = Math.log(N/ (double)df)/Math.log(2);
                        postings.seek(Long.parseLong(tokens[2]));
                        byte[] buf1 = new byte[(int) posting_pointer];
                        postings.readFully(buf1);
                        String s = new String(buf1, "UTF-8");
                        String[] lines = s.split("\n");
                        for(String l : lines){
                            String[] tmp = l.split("\t");
                            double tf = Double.parseDouble(tmp[1]);
                            double tfIdf = tf * idf;
                            String id = tmp[0];
                            if(score.containsKey(id)){
                                double prev = score.get(id);
                                score.put(id, prev + tfIdf);
                            }else{
                                score.put(id,tfIdf);
                            }

                        }

                    }
                    line = readerVocab.readLine();
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Sort the scores Map
        List<Map.Entry<String, Double>> entryList = new ArrayList<>(score.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> entry1, Map.Entry<String, Double> entry2) {
                // Compare values of the entries in reverse order
                return -Double.compare(entry1.getValue(), entry2.getValue());
            }
        });

        Map<String, Double> sortedScores = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : entryList) {
            sortedScores.put(entry.getKey(), entry.getValue());
        }

//        score.entrySet().stream()
//                .sorted(Map.Entry.<String,Double>comparingByValue().reversed())
//                .collect(Collectors.toMap((oldValue,newValue) -> oldValue,Map.Entry::getValue)
//                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));


        System.out.println(sortedScores);

        // Create an output String with the top 10 Scores
        int stop = 1;
        for (Map.Entry<String, Double> entry : sortedScores.entrySet()) {
           if(stop == 10){
               break;
           }
           String docPath = entry.getKey();
           for(String path : documents){
               if(path.contains(entry.getKey())){
                   docPath = path;
               }
           }
           display += stop+"\t"+docPath + ":\n\t\t" + entry.getValue() +"\n\n";
           stop ++;
        }


    }
}

