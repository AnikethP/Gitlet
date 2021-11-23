package gitlet;

import java.io.Serializable;
import java.util.HashMap;

import static gitlet.Utils.*;

public class Branches implements Serializable {
    public HashMap<String, String> branches;
    String curr;

    public Branches(String curr){
        this.curr = curr;
        branches = new HashMap<>();
        writeObject(Database.branchFile, this);
    }

    public String getCurr(){
        return branches.get(curr);
    }

    public void addBranch(String name, String data){
        branches.put(name, data);
        writeObject(Database.branchFile, this);
    }

    public void addToCurr(String data){
        branches.put(curr, data);
        writeObject(Database.branchFile, this);
    }

    public void removeBranch(String name){
        branches.remove(name);
        writeObject(Database.branchFile, this);
    }

}
