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
import java.util.Map;

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
        b.updateBranch("master");


    }

    void add(String s) {

        Branches branch = Utils.readObject(branchFile, Branches.class);
        String headCommitName = branch.getCurr();
        Commit commit = readObject(new File(commitFolder + "/" + headCommitName), Commit.class);
        File file = join(CWD, s);
        if(!file.exists()){
            System.out.println("File does not exist.");
            return;
        }
        String hash = sha1(Utils.serialize(readContentsAsString(file)));
        File blob = join(blobFolder, hash);
        Stage stage = Utils.readObject(stageFile, Stage.class);
        String info = readContentsAsString(file);

        if(stage.getRemoveStage().containsKey(s)){
            stage.getRemoveStage().remove(s);
            writeObject(stageFile, stage);
            return;
        }
        else if(commit.getBlobs()!=null) {
            if (commit.getBlobs().containsKey(s) && commit.getBlobs().get(s).equals(hash)) {
                return;
            }
        }


        writeContents(blob, info);
        stage.addToStage(s, hash);
        writeObject(stageFile, stage);

    }

    void commit(String msg) throws IOException{
        Branches b = Utils.readObject(branchFile, Branches.class);
        Commit newCommit = new Commit(b.getCurr(), msg);
        Utils.readObject(stageFile, Stage.class).emptyStage();
        b.addToCurr(newCommit.getMyHash());
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
        else if(headCommit.getBlobs() != null && !s.getAddStage().containsKey(removeFile) && !headCommit.getBlobs().containsKey(removeFile)){
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
        System.out.println("=== Branches ===");
        Branches b = Utils.readObject(branchFile, Branches.class);
        for(String key : b.branches.keySet()){
            if(b.getCurr().equals(b.branches.get(key))) {
                System.out.println("*" + key);
            }
            else{
                System.out.println(key);
            }
        }
        Stage stage = Utils.readObject(stageFile, Stage.class);
        System.out.println("\n" + "=== Staged Files ===");
        for(String key : stage.getAddStage().keySet()){
            System.out.println(key);
        }
        System.out.println("\n" + "=== Removed Files ===");
        for(String key : stage.getRemoveStage().keySet()){
            System.out.println(key);
        }
        System.out.println("\n"+"=== Modifications Not Staged For Commit ===" + "\n");
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    void find(String msg){
        boolean found = false;
        for(String c : plainFilenamesIn(commitFolder)){
            if(readObject(join(commitFolder, c), Commit.class).getMessage().equals(msg)){
                System.out.println(readObject(join(commitFolder, c), Commit.class).getMyHash());
                found = true;
            }
        }
        if(!found){
            System.out.print("Found no commit with that message.");
        }


    }
    void checkout(String commit, String name) throws IOException{
        List<String> filesInCWD = plainFilenamesIn(CWD);
        Commit com = null;
        for(String c : plainFilenamesIn(commitFolder)){
            if (c.equals(commit)){
                com = readObject(join(commitFolder, commit), Commit.class);
            }
        }
        if(com == null){
            System.out.println("No commit with that id exists.");
            return;
        }
        else if(!com.getBlobs().containsKey(name)){
            System.out.println("File does not exist in that commit.");
            return;
        }
        String info = readContentsAsString(join (blobFolder, com.getBlobs().get(name)));
        if(!filesInCWD.contains(name)){
            join(CWD, name).createNewFile();
        }
        writeContents(join(CWD, name), info);
    }

    void branchCheckout(String name) throws IOException{

        Branches b = Utils.readObject(branchFile, Branches.class);
        if(b.branches.containsKey(name)){
            if(b.curr.equals(name)){
                System.out.println("No need to checkout the current branch.");
                return;
            }
            Commit headCommit = readObject(join(commitFolder, b.getCurr()), Commit.class);

            String branchCommitName = b.branches.get(name);
            Commit branchCommit = readObject(join(commitFolder, branchCommitName), Commit.class);

            if (headCommit.getBlobs() == null) {
                if(plainFilenamesIn(CWD).size() > 0){
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                }
            }
            else{
                for(String file : plainFilenamesIn(CWD)){
                    if(branchCommit.getBlobs()!= null && branchCommit.getBlobs().containsKey(file)){
                        if(!headCommit.getBlobs().containsKey(file)){
                            System.out.print("There is an untracked file in the way; delete it, or add and commit it first.");
                        }
                    }
                }
            }
        }
        else{
            System.out.print("No such branch exists.");
            return;
        }

        b.updateBranch(name);
        String branchCommitName = b.branches.get(name);
        Commit branchCommit = readObject(join(commitFolder, branchCommitName), Commit.class);

        for(String f : plainFilenamesIn(CWD)){
            if(branchCommit.getBlobs() != null && branchCommit.getBlobs().containsKey(f)) {
                Utils.writeContents(join(CWD, f), readContentsAsString(join(blobFolder, branchCommit.getBlobs().get(f))));

            }
            else{
                restrictedDelete(f);
            }
        }
        if(branchCommit.getBlobs() != null){
            for(Map.Entry<String, String> pair : branchCommit.getBlobs().entrySet()){
                File filePath = join(CWD, pair.getKey());
                if(!filePath.exists()){
                    join(CWD, pair.getKey()).createNewFile();
                    writeContents(join(CWD, pair.getKey()), readContentsAsString(join(blobFolder, pair.getValue())));
                }
            }
        }
        Stage s = Utils.readObject(stageFile, Stage.class);
        s.emptyStage();
    }

    void addBranch(String branchName){
        boolean needed = true;
        if(Utils.readObject(branchFile, Branches.class).branches.size() == 0){
            needed = false;
        }


        if(!needed || Utils.readObject(branchFile, Branches.class).branches.containsKey(branchName)){
            System.out.print("A branch with that name already exists.");
            return;
        }

        Branches b = readObject(branchFile, Branches.class);
        b.addBranch("Fork", b.getCurr());
        b.addBranch(branchName, b.getCurr());

    }

    void rmBranch(String branchName){
        if(readObject(branchFile, Branches.class).curr.equals(branchName)){
            System.out.println("Cannot remove the current branch.");
            return;
        }
        if(!readObject(branchFile, Branches.class).branches.containsKey(branchName)){
            System.out.println("A branch with that name does not exist.");
            return;
        }
        readObject(branchFile, Branches.class).removeBranch(branchName);

    }

    void reset(String id){
        boolean found = false;

        for(String k : plainFilenamesIn(commitFolder)){
            if(k.equals(id)){
                found = true;
                break;
            }
        }
        if(!found){
            System.out.println("No commit with that id exists.");
        }

        Branches branch = readObject(branchFile, Branches.class);
        branch.addBranch("xyz", id);
        try {
            branchCheckout("xyz");
            branch.removeBranch("xyz");
            branch.addToCurr(id);
        }
        catch (IOException e){
            return;
        }
    }
    void mergeHelper(String x, File f, Commit c) throws IOException{
        String info = Utils.readContentsAsString(f);
        File blob = join(blobFolder, sha1(serialize(info)));
        Stage stage = Utils.readObject(stageFile, Stage.class);
        if (stage.getAddStage().containsKey(x)) {
            stage.getAddStage().remove(x);
            writeObject(stageFile, stage);

        } else if (c.getBlobs() != null) {
            if(c.getBlobs().containsKey(x) && c.getBlobs().get(x).equals(sha1(serialize(info)))){
                return;
            }
        } else if (!stage.getAddStage().containsKey(x) && !(stage.getAddStage().get(x) == sha1(serialize(info)))) {
            blob.createNewFile();
            Utils.writeContents(blob,info);
            stage.getAddStage().put(x, sha1(serialize(info)));
            writeObject(stageFile, stage);

        }

    }
    void mergeHelper2(String x, Commit c){
        Stage stage = Utils.readObject(stageFile, Stage.class);
        if (stage.getAddStage().containsKey(x)) {
            stage.getAddStage().remove(x);
            writeObject(stageFile, stage);
        }
        else if ((c.getBlobs() == null) || (!stage.getAddStage().containsKey(x) && !c.getBlobs().containsKey(x))) {
            System.out.print("No reason to remove the file.");
            System.exit(0);
        }
        else if (c.getBlobs().containsKey(x)) {
            stage.getRemoveStage().put(x, null);
            writeObject(stageFile, stage);

        }
    }
    void merge(String name) throws IOException{
        Branches b = readObject(branchFile, Branches.class);

        if(b.curr.equals(name)){
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        if(!b.branches.containsKey(name)){
            System.out.print("A branch with that name does not exist.");
            return;
        }
        Stage s = readObject(stageFile, Stage.class);
        if(s.removeStage.size() != 0){
            System.out.println("You have uncommitted changes.");
            return;
        }
        else if(s.addStage.size() != 0){
            System.out.println("You have uncommitted changes.");
            return;
        }
        boolean flag = false;
        List<String> fileList = plainFilenamesIn(CWD);
        Commit branchHEAD = readObject(join(commitFolder, b.getCurr()), Commit.class);
        String checkedOutCommitName = b.branches.get(name);
        Commit checkedOutCommit = readObject(join(commitFolder, checkedOutCommitName),Commit.class);
        if (branchHEAD.getBlobs() != null) {
            for (String file : fileList) {
                if (!branchHEAD.getBlobs().containsKey(file) && checkedOutCommit.getBlobs().containsKey(file)) {
                    System.out.print("There is an untracked file in the way; delete it, or add and commit it first.");
                    return;
                }
            }
        }
        else {
            if (fileList.size() > 0) {
                System.out.print("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }

    }
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File gitletFolder = join(CWD, ".gitlet");
    public static final File stageFile = join(gitletFolder, "/stage");
    public static final File commitFolder = join(gitletFolder, "/commits");
    public static final File blobFolder = join(gitletFolder, "/blobs");
    public static final File branchFile = join(gitletFolder, "/branches");

    private Stage stage;
}
