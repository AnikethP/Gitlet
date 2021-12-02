package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static gitlet.Utils.*;
/**
 * Manages all Commit data.
 * @author Aniketh Prasad
 */
public class Commit implements Serializable {
    /**
     * my message.
     */
    private final String message;
    /**
     * my time.
     */
    private final Date time;
    /**
     * my blobs.
     */
    private final HashMap<String, String> blobs;
    /**
     * my parent's hash.
     */
    private final String parentHash;
    /**
     * my hash.
     */
    private String myHash;
    /**
     * Initializes very first commit.
     */
    public Commit() throws IOException {
        time = new Date(0);
        parentHash = "";
        blobs = null;
        message = "initial commit";

        myHash = sha1(Utils.serialize(this));
        File commit = Utils.join(Database.COMMITFOLDER, myHash);
        commit.createNewFile();
        writeObject(commit, this);
    }

    /**
     * creates a new commit.
     * @param prev Parent commit hash
     * @param msg commit message for this commit.
     */
    public Commit(String prev, String msg) throws IOException {
        Stage stagingArea = readObject(Database.STAGEFILE, Stage.class);
        parentHash = prev;
        message = msg;
        blobs = new HashMap<String, String>();
        time = new Date();
        myHash = sha1(Utils.serialize(this));

        if (stagingArea.getAddStage().isEmpty()
                && stagingArea.getRemoveStage().isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        } else {


            File parentFile = join(Database.COMMITFOLDER, parentHash);
            Commit parentCommit = Utils.readObject(parentFile, Commit.class);
            if (parentCommit.blobs != null) {
                blobs.putAll(parentCommit.blobs);
            }
            Set<Map.Entry<String, String>> toBeRemoved =
                    stagingArea.getRemoveStage().entrySet();
            for (Map.Entry<String, String> x : toBeRemoved) {
                blobs.remove(x.getKey());
            }
            Set<Map.Entry<String, String>> toBeAdded =
                    stagingArea.getAddStage().entrySet();
            for (Map.Entry<String, String> x : toBeAdded) {
                blobs.put(x.getKey(), x.getValue());
            }

            myHash = sha1(Utils.serialize(this));
            File commit = Utils.join(Database.COMMITFOLDER, myHash);
            commit.createNewFile();
            writeObject(commit, this);
        }
    }
    /**
     * gets blobs of commit.
     * @return Hashmap of blobs name and hash.
     */
    public HashMap<String, String> getBlobs() {
        return blobs;
    }
    /**
     * gets my hash.
     * @return String object of my hash.
     */
    public String getMyHash() {
        return myHash;
    }
    /**
     * gets commit parent's hash.
     * @return String object of the parent's Hash.
     */
    public String getParentHash() {
        return parentHash;
    }
    /**
     * gets commit time.
     * @return Date object of the commit's time.
     */
    public Date getTime() {
        return time;
    }
    /**
     * gets commit message.
     * @return returns a string of the commit's message.
     */
    public String getMessage() {
        return message;
    }

}
