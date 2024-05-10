package PHASE_B;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import mitos.stemmer.Stemmer;

public class GUI {

    private List<String> query;
    private String type;
    private List<String> StopWords = new ArrayList<>();

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

        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
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
        File vocab;
        File posting;
        File documents;
        BufferedReader readerVocab = null;
        RandomAccessFile postings;

        for (File fileEntry : CollectionIndex.listFiles()){

            if(fileEntry.getName().contains("Documents")){
                System.out.println("Documents File: "+fileEntry.getName());
                documents = new File(fileEntry.getAbsolutePath());

            }

            if(fileEntry.getName().contains("Vocabulary")){
                System.out.println("Vocabulary File: "+fileEntry.getName());
                vocab = new File(fileEntry.getAbsolutePath());
                readerVocab = new BufferedReader( new FileReader(fileEntry.getAbsolutePath()));

            }

            if(fileEntry.getName().contains("Posting")){
                System.out.println("Posting File: "+fileEntry.getName());
                posting = new File(fileEntry.getAbsolutePath());
                postings = new RandomAccessFile(posting, "r");
            }
        }

        try{
            String line = readerVocab.readLine();
            while(line != null){

                String[] tokens = line.split("\t");
                line = readerVocab.readLine();

                for(String term : query){
                    if(term.equals(tokens[0])){
                        int df = Integer.parseInt(tokens[1]);
                        System.out.print(df + " ");

                    }
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
