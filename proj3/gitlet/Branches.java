package gitlet;

import java.io.Serializable;
import java.util.TreeMap;

import static gitlet.Utils.*;
/**
 * Manages all branches in git repository.
 * @author Aniketh Prasad
 */
public class Branches implements Serializable {
    /**
     * Stores my branches.
     */
    private TreeMap<String, String> branches;
    /**
     * Stores current branch name.
     */
    private String curr;
    /**
     * Creates the initial branch.
     * @param c name of initial branch.
     */
    public Branches(String c) {
        this.curr = c;
        branches = new TreeMap<>();
        writeObject(Database.BRANCHFILE, this);
    }
    /**
     * returns the id of current branch.
     * @return String representing the hash.
     */
    public String getCurr() {
        return branches.get(curr);
    }
    /**
     * Adds a new branch.
     * @param name name of branch.
     * @param data hash of branch.
     */
    public void addBranch(String name, String data) {
        branches.put(name, data);
        writeObject(Database.BRANCHFILE, this);
    }
    /**
     * adds data to current branch.
     * @param data hash ID of data.
     */
    public void addToCurr(String data) {
        branches.put(curr, data);
        writeObject(Database.BRANCHFILE, this);
    }
    /**
     * removes branch name.
     * @param name of branch to be removed.
     */
    public void removeBranch(String name) {
        branches.remove(name);
        writeObject(Database.BRANCHFILE, this);
    }
    /**
     * Updates current branch name.
     * @param name of branch to be removed.
     */
    public void updateBranch(String name) {
        curr = name;
        writeObject(Database.BRANCHFILE, this);
    }
    /**
     * Gets my branches.
     * @return TreeMap<String, String> representing the branches.
     */
    public TreeMap<String, String> branches() {
        return branches;
    }
    /**
     * Gets the name of the current branch.
     * @return String representing the name of curr branch.
     */
    public String curr() {
        return curr;
    }
}
