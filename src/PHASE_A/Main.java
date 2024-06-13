package PHASE_A;
import PHASE_B.GUI;
import PHASE_C.Evaluator;
import PHASE_C.Reader;
import PHASE_C.Relevance;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        String path = args[0];
        File folder = new File(path);
        File stopWords = new File("StopWords");
        LocalTime currentTime1 = LocalTime.now();
        System.out.println("Current time: " + currentTime1);
        if(args.length == 2) {
            Analysis A = new Analysis(folder, stopWords,true);
        }else{
            Analysis A = new Analysis(folder, stopWords,false);
        }

        LocalTime currentTime2 = LocalTime.now();
        System.out.println("Starting time\t " + currentTime1+ " \tCurrent time: " + currentTime2);

        String flag = args[1];
        boolean mini;
        if(flag.equals("1")) {
            mini = false;
            String qrels = args[2];
            File qrels_file = new File(qrels);
            List<Relevance> relevanceList = new ArrayList<>();

            try{
                FileReader fr = new FileReader(qrels_file);
                BufferedReader br = new BufferedReader(fr);
                Relevance relevance = new Relevance(1);
                String l;
                int current_doc = 1;
                while((l = br.readLine()) != null){
                    String[] tokens = l.split("\\s+");
                    if(current_doc != Integer.parseInt(tokens[0])){

                        relevanceList.add(relevance);
                        relevance = new Relevance(Integer.parseInt(tokens[0]));
                        current_doc = Integer.parseInt(tokens[0]);

                    }
                    if(Integer.parseInt(tokens[3]) == 2){
                        relevance.addHigh(Integer.parseInt(tokens[2]));
                    }
                    if(Integer.parseInt(tokens[3]) == 1){
                        relevance.addMedium(Integer.parseInt(tokens[2]));
                    }
                    if(Integer.parseInt(tokens[3]) == 0){
                        relevance.addLow(Integer.parseInt(tokens[2]));
                    }

                }

            }catch (Exception e){
                System.out.println(e);
            }

//        if(!relevanceList.isEmpty()){
//            for(Relevance r : relevanceList){
//                r.print();
//            }
//        }
            GUI search = new GUI(false,false);
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
                if(reader.getNumber()-1 <29){
                    results += search.ExternalSearch(words, reader.getNumber(),relevanceList.get(reader.getNumber()-1).getHigh().size(),
                            relevanceList.get(reader.getNumber()-1).getMedium().size());
                }

            }

            File file = new File("Results.txt");
            PrintWriter writer = new PrintWriter(file);
            BufferedWriter bw = new BufferedWriter(writer);

            bw.write(results);
            bw.close();
            writer.close();

            // Evaluation
            try{
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);

                float[] hits = new float[relevanceList.size()] ;
                int[] misses = new int[relevanceList.size()];

                String l;
                while((l = br.readLine()) != null){
                    String[] tokens = l.split("\\s+");
                    // 0 : topicNumber, 2 : pmcid, 3 : rank, 4 : score, 5 : eval score(1,2)
                    int topic = Integer.parseInt(tokens[0]);
                    int pmcid = Integer.parseInt(tokens[2]);
                    int eval_score = Integer.parseInt(tokens[5]);

                    boolean isHIgh = false;
                    boolean isLow = false;
                    if(eval_score == 1){
                        isLow = true;
                        if(relevanceList.get(topic-1).getMedium().contains(pmcid)){
                            hits[topic-1] += 1;
                        }else if(relevanceList.get(topic-1).getHigh().contains(pmcid)){
                            hits[topic-1] += 1;
                        }else{
                            misses[topic-1] ++;
                        }
                    }else{
                        isHIgh = true;
                        if(relevanceList.get(topic-1).getMedium().contains(pmcid)){
                            hits[topic-1] += 1F;
                        }else if(relevanceList.get(topic-1).getHigh().contains(pmcid)){
                            hits[topic-1] += 1;
                        }else{
                            misses[topic-1] ++;
                        }
                    }

                }

                float[] ratios = new float[relevanceList.size()];
                for( int i = 0 ; i < hits.length ; i++){
                    ratios[i] = hits[i]/ (hits[i] + misses[i]);
                    System.out.println("HIT RATIO FOR TOPIC: "+ (i+1) +"\t"+ratios[i]);
                }

//            System.out.println(hits);
//            System.out.println(misses);

            }catch (Exception e){
                System.out.println(e);
            }

        }else{
            mini = true;
            GUI gui = new GUI(true,mini);
        }

    }
}
