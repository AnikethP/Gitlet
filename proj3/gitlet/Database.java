package gitlet;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Database {

    public Database(){

    }

    void init(){
        //Check if .gitlet already exists
        Path path = Paths.get(".gitlet");
        if(Files.exists(path)) {
            System.out.println("Gitlet version-control system already exists in the current directory.");
            return;
        }

        File hiddenGit = new File(".gitlet");

        hiddenGit.mkdirs();

    }
}
