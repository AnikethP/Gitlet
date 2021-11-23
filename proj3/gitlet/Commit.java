package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import gitlet.Utils;

import static gitlet.Utils.*;

public class Commit implements Serializable {

    private String message;
    private Date time;
    private HashMap<String, String> blobs;
    private String parentHash;
    private String myHash;

    public Commit() throws IOException {
        time = new Date(0);
        parentHash = "";
        blobs = null;
        message = "initial commit";

        myHash = sha1(Utils.serialize(this));
        File commit = Utils.join(Database.commitFolder, myHash);
        commit.createNewFile();
        writeObject(commit, this);
    }

    public Commit(String prev, String msg) throws IOException{
        Stage stagingArea = readObject(Database.stageFile, Stage.class);
        parentHash = prev;
        message = msg;
        blobs = new HashMap<String, String>();
        time = new Date();
        myHash = sha1(Utils.serialize(this));

        if(stagingArea.getAddStage().isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        else {
            Set<Map.Entry<String,String>> toBeAdded = stagingArea.getAddStage().entrySet();
            File parentFile = join(Database.commitFolder, parentHash);
            Commit parentCommit = Utils.readObject(parentFile, Commit.class);
            if (parentCommit.blobs != null) {
                blobs.putAll(parentCommit.blobs);
            }

            for(Map.Entry<String,String> x: toBeAdded){
                blobs.put(x.getKey(), x.getValue());
            }
            myHash = sha1(Utils.serialize(this));
            File commit = Utils.join(Database.commitFolder, myHash);
            commit.createNewFile();
            writeObject(commit, this);
        }
    }

    public HashMap<String, String> getBlobs() {
        return blobs;
    }

    public String getMyHash(){
        return myHash;
    }

    public String getParentHash(){
        return parentHash;
    }

    public Date getTime(){
        return time;
    }

    public String getMessage(){
        return message;
    }

}
