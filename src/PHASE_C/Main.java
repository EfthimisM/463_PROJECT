package PHASE_C;

import PHASE_B.GUI;

import java.io.BufferedWriter;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        String qrels = args[0];
        File folder = new File(qrels);

        GUI search = new GUI(true);
        Evaluator eval = new Evaluator();
        String results = new String("");

        for(Reader reader :eval.topics){
            System.out.println(reader.getNumber());
            System.out.println(reader.getSummary());

            // Make it iteratable for the search function
            String[] line = reader.getSummary().split("\\s+");
            List<String> words = new ArrayList<String>();
            for (String word : line){
                words.add(word);
            }

            // Get the results for each query
            results += search.ExternalSearch(words, reader.getNumber());
        }

        File file = new File("Results.txt");
        PrintWriter writer = new PrintWriter(file);
        BufferedWriter bw = new BufferedWriter(writer);

        bw.write(results);
        bw.close();


    }

}
