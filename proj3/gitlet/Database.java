package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
        String info = readContentsAsString(file);
        writeContents(blob, info);
        Stage stage = Utils.readObject(stageFile, Stage.class);
        stage.addToStage(s, hash);
        writeObject(stageFile, stage);

    }

    void commit(String msg) throws IOException{
        Branches b = Utils.readObject(branchFile, Branches.class);
        Commit newCommit = new Commit(b.getCurr(), msg);
        Utils.readObject(stageFile, Stage.class).emptyStage();
        b.addToCurr(newCommit.getMyHash());
    }

    void leaveBranch(String name){

        Stage stage = readObject(stageFile, Stage.class);
        Branches branch = readObject(branchFile, Branches.class);
        if(!branch.branches.containsKey(name)){
            System.out.print("No such branch exists.");
        }
        else{
            String checkoutCommitName = branch.branches.get(name);
            Commit prevCommit = readObject(new File(commitFolder + "/" + checkoutCommitName), Commit.class);
            String headCommitName = branch.branches.get(branch.getCurr());
            Commit headCommit = readObject(new File(commitFolder + "/" + headCommitName), Commit.class);

            List<String> files = plainFilenamesIn(CWD);
            branch.updateBranch(name);
            headCommit = readObject(new File(commitFolder + "/" + branch.branches.get(name)), Commit.class);
            for (String f : files) {
                if (headCommit.getBlobs() == null) {
                    restrictedDelete(f);
                }
            }
        }
        stage.emptyStage();
    }

    void log(){
        SimpleDateFormat formatter = new SimpleDateFormat ("E MMM dd hh:mm:ss yyyy -0800");
        System.out.println("===");
        Branches branch = readObject(branchFile, Branches.class);
        String headCommitName = branch.getCurr();
        Commit commit = readObject(new File(commitFolder + "/" + headCommitName), Commit.class);
        System.out.println("commit " +  branch.getCurr());
        System.out.println("Date: " + formatter.format(commit.getTime()));
        System.out.println(commit.getMessage() + "\n");
        while(commit.getParentHash().length() != 0){
            System.out.println("===");
            System.out.println("commit " + commit.getParentHash());
            commit =  Utils.readObject(join(commitFolder, commit.getParentHash()), Commit.class);
            System.out.println("Date: " + formatter.format(commit.getTime()));
            System.out.println(commit.getMessage());
            System.out.println("");
        }

    }

    void globalLog(){
        SimpleDateFormat formatter = new SimpleDateFormat ("E MMM dd hh:mm:ss yyyy -0800");
        for(String s : plainFilenamesIn(commitFolder)){
            Commit commit = Utils.readObject(Utils.join(commitFolder, s), Commit.class);
            System.out.println("===");
            System.out.println("commit " + s);
            System.out.println("Date: " + formatter.format(commit.getTime()));
            System.out.println(commit.getMessage() + "\n");
        }
    }

    void rm(String removeFile) {
        Stage s = Utils.readObject(stageFile, Stage.class);
        Branches b = Utils.readObject(branchFile, Branches.class);
        Commit headCommit = readObject(join(commitFolder, b.getCurr()), Commit.class);
        if(s.getAddStage().containsKey(removeFile)){
            s.getAddStage().remove(removeFile);
            writeObject(stageFile, s);
        }
        else if(!s.getAddStage().containsKey(removeFile) && !headCommit.getBlobs().containsKey(removeFile)){
            System.out.print("No reason to remove the file.");
        }

        else if(headCommit.getBlobs() == null){
            System.out.print("No reason to remove the file.");
        }
        else{
            s.getRemoveStage().put(removeFile, null);
            restrictedDelete(join(CWD, removeFile));
            writeObject(stageFile, s);
        }
    }
    void checkout(String s) throws IOException{
        Branches b = Utils.readObject(Database.branchFile, Branches.class);
        Commit HC = Utils.readObject(join(commitFolder, b.getCurr()), Commit.class);
        File blobFile = join(blobFolder, HC.getBlobs().get(s));
        //If file from previous commit not present, create it now
        if (!plainFilenamesIn(CWD).contains(s)) {
            File file = join(CWD, s);
            file.createNewFile();
        }
        Utils.writeContents(join(CWD, s), Utils.readContentsAsString(blobFile));
    }
    void status() {
        System.out.println("===Branches===");
        Branches b = Utils.readObject(branchFile, Branches.class);
        for(String key : b.branches.keySet()){
            if(b.getCurr().equals(key)){
                System.out.println("*" + key);
            }
            else{
                System.out.println(key);
            }
        }
        Stage stage = Utils.readObject(stageFile, Stage.class);
        System.out.println("\n" + "===Staged Files===");
        for(String key : stage.getAddStage().keySet()){
            System.out.println(key);
        }
        System.out.println("");
        System.out.println("\n" + "===Removed Files===");
        for(String key : stage.getRemoveStage().keySet()){
            System.out.println(key);
        }
        System.out.println("\n"+"=== Modifications Not Staged For Commit ===" + "\n");
        System.out.println("=== Untracked Files ===" + "\n");

    }

    void find(String msg){
        for(String c : plainFilenamesIn(commitFolder)){
            if(readObject(join(commitFolder, c), Commit.class).getMessage().equals(msg)){
                System.out.println(readObject(join(commitFolder, c), Commit.class).getMyHash());
                return;
            }
        }

        System.out.print("Found no commit with that message.");

    }
    void checkout(String commit, String name) throws IOException{
        List<String> filesInCWD = plainFilenamesIn(CWD);
        Commit com = null;
        for(String c : plainFilenamesIn(commitFolder)){
            if (c.equals(commit)){
                com = readObject(join(commitFolder, commit), Commit.class);
            }
        }
        String info = readContentsAsString(join (blobFolder, com.getBlobs().get(name)));
        if(!filesInCWD.contains(name)){
            join(CWD, name).createNewFile();
        }
        writeContents(join(CWD, name), info);
    }

    void branchCheckout(String branchName){
        
    }

    void addBranch(){

    }

    void rmBranch(){

    }

    void reset(String id){
        Branches branch = readObject(branchFile, Branches.class);

    }

    void merge(){

    }
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File gitletFolder = join(CWD, ".gitlet");
    public static final File stageFile = join(gitletFolder, "/stage");
    public static final File commitFolder = join(gitletFolder, "/commits");
    public static final File blobFolder = join(gitletFolder, "/blobs");
    public static final File branchFile = join(gitletFolder, "/branches");

    private Stage stage;
}
