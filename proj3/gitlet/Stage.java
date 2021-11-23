package gitlet;

import java.io.Serializable;
import java.util.HashMap;

import static gitlet.Database.stageFile;
import static gitlet.Utils.*;

public class Stage implements Serializable, Dumpable {

    @Override
    public void dump() {
        System.out.println(addStage);
        System.out.println(removeStage);
    }

    public HashMap<String, String> addStage;
    public HashMap<String, String> removeStage;

    public Stage(){
        addStage = new HashMap<>();
        removeStage = new HashMap<>();
        writeObject(stageFile, this);
    }

    void addToStage(String filename, String hash){
        addStage.put(filename, hash);
    }

    void rm(String filename){
        removeStage.put(filename, filename);

    }

    HashMap<String, String> getAddStage() {
        return addStage;
    }

    HashMap<String, String> getRemoveStage() {
        return removeStage;
    }

    void emptyStage(){
        addStage = new HashMap<>();
        removeStage = new HashMap<>();
        writeObject(stageFile, this);
    }

}
