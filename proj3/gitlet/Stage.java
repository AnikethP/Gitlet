package gitlet;

import java.io.Serializable;
import java.util.HashMap;

import static gitlet.Database.STAGEFILE;
import static gitlet.Utils.*;
/**
 * Stores all the info of the Staging Area.
 * @author Aniketh Prasad
 */
public class Stage implements Serializable, Dumpable {
    /**
     *
     * adding stage.
     */
    private HashMap<String, String> addStage;
    /**
     *
     * removing stage.
     */
    private HashMap<String, String> removeStage;
    /**
     *
     * Creates a new Stage and saves in stagefile.
     */
    public Stage() {
        addStage = new HashMap<>();
        removeStage = new HashMap<>();
        writeObject(STAGEFILE, this);
    }
    /**
     *
     * dumps out stage info.
     */
    @Override
    public void dump() {
        System.out.println(addStage);
        System.out.println(removeStage);
    }
    /**
     *
     * adds to stage.
     * @param filename name of file to add.
     * @param hash hash of file to add.
     */
    void addToStage(String filename, String hash) {
        addStage.put(filename, hash);
    }
    /**
     *
     * addstage getter.
     * @return returns Hashmap of the add stage.
     */
    HashMap<String, String> getAddStage() {
        return addStage;
    }
    /**
     *
     * removestage getter.
     * @return returns Hashmap of the return stage.
     *
     */
    HashMap<String, String> getRemoveStage() {
        return removeStage;
    }
    /**
     *
     * empties and resets the stage.
     */
    void emptyStage() {
        addStage = new HashMap<>();
        removeStage = new HashMap<>();
        writeObject(STAGEFILE, this);
    }

}
