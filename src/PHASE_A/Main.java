package PHASE_A;
import java.io.File;

public class Main {
    // edit this
    static String path = "/home/efthimis/Downloads/HY463/MedicalCollection/Test";
    public static void main(String[] args) {
        File folder = new File(path);
        Analysis A = new Analysis(folder);
    }

    public static void Skata(){
        System.out.println("SKATA");
    }
}
