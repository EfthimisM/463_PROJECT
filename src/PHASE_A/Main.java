package PHASE_A;
import java.io.File;

public class Main {

    public static void main(String[] args) {
        String path = args[0];
        File folder = new File(path);
        File stopWords = new File("StopWords");
        Analysis A = new Analysis(folder, stopWords);
    }
}
