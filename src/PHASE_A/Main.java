package PHASE_A;
import PHASE_B.GUI;
import PHASE_C.Evaluator;

import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {
        String path = args[0];
        File folder = new File(path);
        File stopWords = new File("StopWords");
        Analysis A = new Analysis(folder, stopWords);
        GUI gui = new GUI(true);

        //GUI search = new GUI(false);
        //Evaluator evaluator = new Evaluator();

    }
}
