package PHASE_A;
import java.io.File;

public class Main {
    // edit this
    static String path = "C:\\Test_mikro\\Test";
    public static void main(String[] args) {
        File folder = new File(path);
        Analysis A = new Analysis(folder);
    }
}
