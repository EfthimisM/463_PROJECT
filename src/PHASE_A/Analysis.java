package PHASE_A;

import java.io.File;

public class Analysis {

    public Analysis(File folder){
        listFilesForFolder(folder);
    }

    private static void listFilesForFolder(File folder) {
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                System.out.println(fileEntry.getAbsolutePath());
                File file = new File(fileEntry.getAbsolutePath());

            }
        }
    }
}
