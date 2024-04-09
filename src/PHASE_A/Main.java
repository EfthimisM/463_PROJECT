package PHASE_A;
import java.io.File;

public class Main {
    // edit this
    public static void main(String[] args) {
        String path = args[0];
        File folder = new File(path);
        Analysis A = new Analysis(folder);
    }
}
