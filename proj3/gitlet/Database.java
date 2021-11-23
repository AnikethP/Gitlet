package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static gitlet.Utils.*;

public class Database {

    public Database(){

    }

    void init() throws IOException {
        //Check if .gitlet already exists
        Path path = Paths.get(".gitlet");
        if(Files.exists(path)) {
            System.out.println("Gitlet version-control system already exists in the current directory.");
            return;
        }
        //Makes all the directories
        gitletFolder.mkdir();
        commitFolder.mkdir();
        blobFolder.mkdir();
        branchFile.createNewFile();
        stageFile.createNewFile();

        Commit commit = new Commit();

        stage = new Stage();
        Branches b = new Branches("master");
        b.addToCurr(commit.getMyHash());


    }

    void add(String s) {
        File file = join(CWD, s);
        if(!new File(s).exists()){
            System.out.println("File does not exist.");
            System.exit(0);
        }

        String hash = sha1(Utils.serialize(readContentsAsString(file)));
        File blob = join(blobFolder, hash);

//        Commit currCommit = readObject(".gitlet/commits/" + readContentsAsString(".gitlet/branches/" + head + ".txt") +
//                ".txt", Commit.class);
//        if(currCommit.getBlobs().get(s) != null && currCommit.getBlobs().get(s).equals(hash)){
//
//        }
        String info = readContentsAsString(file);
        writeContents(blob, info);
        Stage stage = Utils.readObject(stageFile, Stage.class);
        stage.addToStage(s, hash);
        writeObject(stageFile, stage);

    }

    void commit(String msg) throws IOException{
        //CHANGE BEFORE SUBMISSION
        Branches b = Utils.readObject(branchFile, Branches.class);
        Stage s = Utils.readObject(stageFile, Stage.class);
        String parent = b.getCurr();
        Commit newCommit = new Commit(parent, msg);
        s.emptyStage();
        b.addToCurr(newCommit.getMyHash());

    }

    void leaveBranch(String name){
        
    }

    void checkout(String filename){

    }
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File gitletFolder = join(CWD, ".gitlet");
    public static final File stageFile = join(gitletFolder, "/stage");
    public static final File commitFolder = join(gitletFolder, "/commits");
    public static final File blobFolder = join(gitletFolder, "/blobs");
    public static final File branchFile = join(gitletFolder, "/branches");

    private Stage stage;
}
