package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gitlet.Utils.*;
/**
 * Manages the entire functionalities of the git repository.
 * @author Aniketh Prasad
 */
public class Database {
    /**
     *
     * CWD is the current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     *
     * Entire .gitlet directory.
     */
    public static final File GITLETFOLDER = join(CWD, ".gitlet");
    /**
     *
     * File storing the stage.
     */
    public static final File STAGEFILE = join(GITLETFOLDER, "/stage");
    /**
     *
     * File storing all commits.
     */
    public static final File COMMITFOLDER = join(GITLETFOLDER, "/commits");
    /**
     *
     * File storing all blobs.
     */
    public static final File BLOBFOLDER = join(GITLETFOLDER, "/blobs");
    /**
     *
     * File storing all branches.
     */
    public static final File BRANCHFILE = join(GITLETFOLDER, "/branches");
    /**
     *
     * Staging Area.
     */
    private Stage stage;
    /**
     *
     * Empty initialization.
     */

    public Database() {
    }

    /**
     *
     * @throws IOException
     */
    void init() throws IOException {
        Path path = Paths.get(".gitlet");
        if (Files.exists(path)) {
            String l = "Gitlet version-control system"
                    + " already exists in the current directory.";
            System.out.println(l);
            return;
        }
        GITLETFOLDER.mkdir();
        COMMITFOLDER.mkdir();
        BLOBFOLDER.mkdir();
        BRANCHFILE.createNewFile();
        STAGEFILE.createNewFile();

        Commit commit = new Commit();

        stage = new Stage();
        Branches b = new Branches("master");
        b.addToCurr(commit.getMyHash());
        b.updateBranch("master");


    }
    /**
     * adds file.
     * @param s file to add.
     */
    void add(String s) {

        Branches branch = Utils.readObject(BRANCHFILE, Branches.class);
        String headCommitName = branch.getCurr();
        Commit commit =
                readObject(new File(COMMITFOLDER + "/" + headCommitName),
                        Commit.class);
        File file = join(CWD, s);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        String hash = sha1(Utils.serialize(readContentsAsString(file)));
        File blob = join(BLOBFOLDER, hash);
        Stage st = Utils.readObject(STAGEFILE, Stage.class);
        String info = readContentsAsString(file);

        if (st.getRemoveStage().containsKey(s)) {
            st.getRemoveStage().remove(s);
            writeObject(STAGEFILE, st);
            return;
        } else if (commit.getBlobs() != null) {
            if (commit.getBlobs().containsKey(s)
                    && commit.getBlobs().get(s).equals(hash)) {
                return;
            }
        }


        writeContents(blob, info);
        st.addToStage(s, hash);
        writeObject(STAGEFILE, st);

    }
    /**
     * commits file.
     * @param msg commit message.
     */
    void commit(String msg) throws IOException {
        Branches b = Utils.readObject(BRANCHFILE, Branches.class);
        Commit newCommit = new Commit(b.getCurr(), msg);
        Utils.readObject(STAGEFILE, Stage.class).emptyStage();
        b.addToCurr(newCommit.getMyHash());
    }
    /**
     * displays log.
     *
     */
    void log() {
        SimpleDateFormat formatter =
                new SimpleDateFormat("E MMM dd hh:mm:ss yyyy -0800");
        System.out.println("===");
        Branches branch = readObject(BRANCHFILE, Branches.class);
        String headCommitName = branch.getCurr();
        Commit commit =
                readObject(new File(COMMITFOLDER + "/" + headCommitName),
                        Commit.class);
        System.out.println("commit " + branch.getCurr());
        System.out.println("Date: " + formatter.format(commit.getTime()));
        System.out.println(commit.getMessage() + "\n");
        while (commit.getParentHash().length() != 0) {
            System.out.println("===");
            System.out.println("commit " + commit.getParentHash());
            commit = readObject(join(COMMITFOLDER, commit.getParentHash()),
                    Commit.class);
            System.out.println("Date: " + formatter.format(commit.getTime()));
            System.out.println(commit.getMessage());
            System.out.println();
        }

    }
    /**
     * displays the global log.
     *
     */
    void globalLog() {
        SimpleDateFormat formatter =
                new SimpleDateFormat("E MMM dd hh:mm:ss yyyy -0800");
        for (String s : plainFilenamesIn(COMMITFOLDER)) {
            Commit commit =
                    readObject(Utils.join(COMMITFOLDER, s), Commit.class);
            System.out.println("===");
            System.out.println("commit " + s);
            System.out.println("Date: " + formatter.format(commit.getTime()));
            System.out.println(commit.getMessage() + "\n");
        }
    }
    /**
     * removes file.
     * @param removeFile name of file to be removed.
     */
    void rm(String removeFile) {
        Stage s = Utils.readObject(STAGEFILE, Stage.class);
        Branches b = Utils.readObject(BRANCHFILE, Branches.class);
        Commit headCommit =
                readObject(join(COMMITFOLDER, b.getCurr()), Commit.class);
        if (s.getAddStage().containsKey(removeFile)) {
            s.getAddStage().remove(removeFile);
            writeObject(STAGEFILE, s);
        } else if (headCommit.getBlobs() != null
                && !s.getAddStage().containsKey(removeFile)
                && !headCommit.getBlobs().containsKey(removeFile)) {
            System.out.print("No reason to remove the file.");
        } else if (headCommit.getBlobs() == null) {
            System.out.print("No reason to remove the file.");
        } else {
            s.getRemoveStage().put(removeFile, null);
            restrictedDelete(join(CWD, removeFile));
            writeObject(STAGEFILE, s);
        }
    }
    /**
     * checkouts out from branch.
     * @param s checkout branch name.
     */
    void checkout(String s) throws IOException {
        Branches b = Utils.readObject(Database.BRANCHFILE, Branches.class);
        Commit h = readObject(join(COMMITFOLDER, b.getCurr()), Commit.class);
        File blobFile = join(BLOBFOLDER, h.getBlobs().get(s));
        if (!plainFilenamesIn(CWD).contains(s)) {
            File file = join(CWD, s);
            file.createNewFile();
        }
        Utils.writeContents(join(CWD, s), Utils.readContentsAsString(blobFile));
    }
    /**
     * displays status.
     *
     */
    void status() {
        System.out.println("=== Branches ===");
        Branches b = Utils.readObject(BRANCHFILE, Branches.class);
        Stage s = Utils.readObject(STAGEFILE, Stage.class);
        statusHelper();
        String c = "=== Modifications Not Staged For Commit ===";
        System.out.println("\n" + c);
        File joined = join(COMMITFOLDER, b.getCurr());
        Commit headCommit = readObject(joined, Commit.class);
        HashMap<String, String> blobs = headCommit.getBlobs();
        HashMap<String, String> modified = new HashMap<>();
        for (String f : plainFilenamesIn(CWD)) {
            String info = readContentsAsString(join(CWD, f));
            if (headCommit.getBlobs() != null && blobs.containsKey(f)
                    && !blobs.get(f).equals(sha1(serialize(info)))
                    && b.branches().size() == 1) {
                modified.put(f, "(modified)");
            }
        }
        if (blobs != null) {
            for (Map.Entry<String, String> entry : blobs.entrySet()) {
                if (!plainFilenamesIn(CWD).contains(entry.getKey())
                        && b.branches().size() == 1
                        && !s.getRemoveStage().containsKey(entry.getKey())) {
                    modified.put(entry.getKey(), "(deleted)");
                }
            }
        }
        for (Map.Entry<String, String> entry : modified.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        System.out.println();
        System.out.println("=== Untracked Files ===");
        List<String> untracked = new ArrayList<>();
        if (headCommit.getBlobs() == null) {
            untracked.addAll(plainFilenamesIn(CWD));
        } else {
            for (String f : plainFilenamesIn(CWD)) {
                if (!blobs.containsKey(f) && b.branches().size() == 1) {
                    untracked.add(f);
                }
            }
        }
        for (String k : s.getAddStage().keySet()) {
            if (untracked.contains(k)) {
                untracked.remove(k);
            }
        }
        for (String k : s.getRemoveStage().keySet()) {
            if (untracked.contains(k)) {
                untracked.remove(k);
            }
        }
        untracked = ((plainFilenamesIn(CWD).size() == 0)
                ? new ArrayList<>() : untracked);
        untracked.forEach(System.out::println);
        System.out.println();
    }
    /**
     * Helps displays status.
     *
     */
    void statusHelper() {
        Branches b = Utils.readObject(BRANCHFILE, Branches.class);
        Stage s = Utils.readObject(STAGEFILE, Stage.class);
        for (String key : b.branches().keySet()) {
            if (!key.equals("Fork")
                    && b.getCurr().equals(b.branches().get(key))) {
                System.out.println("*" + key);
            } else if (!key.equals("Fork")) {
                System.out.println(key);
            }
        }
        System.out.println("\n" + "=== Staged Files ===");
        for (String key : s.getAddStage().keySet()) {
            System.out.println(key);
        }
        System.out.println("\n" + "=== Removed Files ===");
        for (String key : s.getRemoveStage().keySet()) {
            System.out.println(key);
        }
    }
    /**
     * finds commit with msg.
     * @param msg commit msg to search.
     */
    void find(String msg) {
        boolean found = false;
        for (String c : plainFilenamesIn(COMMITFOLDER)) {
            if (readObject(join(COMMITFOLDER, c),
                    Commit.class).getMessage().equals(msg)) {
                System.out.println(readObject(join(COMMITFOLDER, c),
                                Commit.class).getMyHash());
                found = true;
            }
        }
        if (!found) {
            System.out.print("Found no commit with that message.");
        }


    }
    /**
     * checks out to specific commit.
     * @param commit name of commit.
     * @param name id of commit.
     */
    void checkout(String commit, String name) throws IOException {
        List<String> filesInCWD = plainFilenamesIn(CWD);
        Commit com = null;
        for (String c : plainFilenamesIn(COMMITFOLDER)) {
            if (c.equals(commit)
                    || commit.equals(c.substring(0,
                    commit.length()))) {
                com = readObject(join(COMMITFOLDER, c), Commit.class);
            }
        }
        if (com == null) {
            System.out.println("No commit with that id exists.");
            return;
        } else if (!com.getBlobs().containsKey(name)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        String info = readContentsAsString
                (join(BLOBFOLDER, com.getBlobs().get(name)));
        if (!filesInCWD.contains(name)) {
            join(CWD, name).createNewFile();
        }
        writeContents(join(CWD, name), info);
    }
    /**
     * checks out to specifc branch.
     * @param name name of branch.
     */
    void branchCheckout(String name) throws IOException {
        String k = "There is an untracked file in the way; "
                + "delete it, or add and commit it first.";
        Branches b = Utils.readObject(BRANCHFILE, Branches.class);
        if (b.branches().containsKey(name)) {
            if (b.curr().equals(name)) {
                System.out.println("No need to checkout the current branch.");
                return;
            }
            Commit headCommit =
                    readObject(join(COMMITFOLDER, b.getCurr()),
                            Commit.class);
            String branchCommitName = b.branches().get(name);
            Commit branchCommit =
                    readObject(join(COMMITFOLDER, branchCommitName),
                            Commit.class);
            if (headCommit.getBlobs() == null) {
                if (plainFilenamesIn(CWD).size() > 0) {
                    System.out.println(k);
                }
            } else {
                for (String file : plainFilenamesIn(CWD)) {
                    if (branchCommit.getBlobs() != null
                            && branchCommit.getBlobs().containsKey(file)) {
                        if (!headCommit.getBlobs().containsKey(file)) {
                            System.out.print(k);
                        }
                    }
                }
            }
        } else {
            System.out.print("No such branch exists.");
            return;
        }
        b.updateBranch(name);
        Commit branchCommit =
                readObject(join(COMMITFOLDER, b.branches().get(name)),
                        Commit.class);
        for (String f : plainFilenamesIn(CWD)) {
            if (branchCommit.getBlobs() != null
                    && branchCommit.getBlobs().containsKey(f)) {
                writeContents(join(CWD, f), readContentsAsString
                        (join(BLOBFOLDER, branchCommit.getBlobs().get(f))));
            } else {
                restrictedDelete(f);
            }
        }
        if (branchCommit.getBlobs() != null) {
            for (Map.Entry<String, String> pair
                    : branchCommit.getBlobs().entrySet()) {
                File filePath = join(CWD, pair.getKey());
                if (!filePath.exists()) {
                    join(CWD, pair.getKey()).createNewFile();
                    writeContents(join(CWD, pair.getKey()), readContentsAsString
                                    (join(BLOBFOLDER, pair.getValue())));
                }
            }
        }
        Utils.readObject(STAGEFILE, Stage.class).emptyStage();
    }
    /**
     * adds a branch.
     * @param branchName name of branch.
     */
    void addBranch(String branchName) {
        boolean needed = readObject(BRANCHFILE, Branches.class)
                .branches().size() != 0;


        if (!needed || Utils.readObject(BRANCHFILE, Branches.class)
                .branches().containsKey(branchName)) {
            System.out.print("A branch with that name already exists.");
            return;
        }

        Branches b = readObject(BRANCHFILE, Branches.class);
        b.addBranch("Fork", b.getCurr());
        b.addBranch(branchName, b.getCurr());

    }
    /**
     * removes a branch.
     * @param branchName name of branch.
     */
    void rmBranch(String branchName) {
        if (readObject(BRANCHFILE, Branches.class).curr().equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        if (!readObject(BRANCHFILE, Branches.class)
                .branches().containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        readObject(BRANCHFILE, Branches.class).removeBranch(branchName);

    }
    /**
     * resets a branch with id.
     * @param id id name.
     */
    void reset(String id) {
        boolean found = false;
        String temp = id;
        for (String k : plainFilenamesIn(COMMITFOLDER)) {
            if (k.equals(id) || id.equals(k.substring(0, id.length()))) {
                temp = k;
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("No commit with that id exists.");
            return;
        }

        Branches branch = readObject(BRANCHFILE, Branches.class);
        branch.addBranch("xyz", temp);
        try {
            branchCheckout("xyz");
            branch.removeBranch("xyz");
            branch.addToCurr(id);
        } catch (IOException e) {
            return;
        }
    }
    /**
     * Helper for merge.
     * @param x name of addFile.
     * @param f File to add.
     * @param c Commit name.
     */
    void mergeHelper(String x, File f, Commit c) throws IOException {
        String info = Utils.readContentsAsString(f);
        File blob = join(BLOBFOLDER, sha1(serialize(info)));
        Stage s = Utils.readObject(STAGEFILE, Stage.class);
        String key = "";
        for (Map.Entry<String, String> entry : c.getBlobs().entrySet()) {
            if (entry.getValue().equals(x)) {
                key = entry.getKey();
            }
        }

        if (s.getAddStage().containsKey(x)) {
            s.getAddStage().remove(x);
            writeObject(STAGEFILE, s);
        } else if (!s.getAddStage().containsValue(x)
                && !(s.getAddStage().get(x) == sha1(serialize(info)))) {
            blob.createNewFile();
            Utils.writeContents(blob, info);
            if (join(CWD, key).exists()) {
                writeContents(join(CWD, key), info);
            } else {
                join(CWD, key).createNewFile();
                writeContents(join(CWD, key), info);
            }

            writeObject(STAGEFILE, s);

        }

    }
    /**
     * Another helper for merge.
     * @param x name of add.
     * @param c name of commit.
     */
    void mergeHelper2(String x, Commit c) {
        String key = "";
        Stage s = Utils.readObject(STAGEFILE, Stage.class);
        for (Map.Entry<String, String> entry : c.getBlobs().entrySet()) {
            if (entry.getValue().equals(x)) {
                key = entry.getKey();
            }
        }
        if (s.getAddStage().containsKey(x)) {
            s.getAddStage().remove(x);
            writeObject(STAGEFILE, s);
        } else if ((c.getBlobs() == null)
                || (!s.getAddStage().containsKey(x)
                        && !c.getBlobs().containsValue(x))) {
            System.out.print("No reason to remove the file");
            System.exit(0);
        } else if (c.getBlobs().containsKey(key)) {
            s.getRemoveStage().put(key, null);
            restrictedDelete(join(CWD, key));
            writeObject(STAGEFILE, s);

        }
    }
    /**
     * Performs all merge possibilities.
     * @param name name of branch.
     */
    void merge(String name) throws IOException {
        Branches b = readObject(BRANCHFILE, Branches.class);
        if (!mergeGood(name)) {
            return;
        }
        boolean conflicted = false;
        List<String> fileList = plainFilenamesIn(CWD);
        Commit branchHEAD =
                readObject(join(COMMITFOLDER, b.getCurr()), Commit.class);
        String checkedOutCommitName = b.branches().get(name);
        Commit checkedOutCommit =
                readObject(join(COMMITFOLDER, checkedOutCommitName),
                        Commit.class);
        if (!mergeGood2(branchHEAD, fileList, checkedOutCommit)) {
            return;
        }
        String h = b.getCurr();
        Commit head =
                readObject(new File(COMMITFOLDER + "/" + h), Commit.class);
        Commit split =
                readObject
                        (join(COMMITFOLDER, b.branches().get("Fork")),
                                Commit.class);
        String otherName = b.branches().get(name);
        Commit other = readObject(join(COMMITFOLDER, otherName), Commit.class);
        for (Map.Entry<String, String> entry : head.getBlobs().entrySet()) {
            String headInfo = ((head.getBlobs().get(entry.getKey()) == null)
                    ? "" : head.getBlobs().get(entry.getKey()));
            String splitInfo = ((split.getBlobs().get(entry.getKey()) == null)
                    ? "" : split.getBlobs().get(entry.getKey()));
            String otherInfo = ((other.getBlobs().get(entry.getKey()) == null)
                    ? "" : other.getBlobs().get(entry.getKey()));
            boolean x = otherInfo.equals(splitInfo);
            boolean y = headInfo.equals(otherInfo);
            boolean z = headInfo.equals(splitInfo);
            if (mergeConflict2(x, y, z, headInfo, otherInfo, entry)) {
                conflicted = true;
            }
            if (mergeConflict(split, entry, other, otherInfo,
                    headInfo, head, x, z)) {
                conflicted = true;
            } else if ((split.getBlobs().containsKey(entry.getKey()))) {
                mergeHelper2(headInfo, head);
            } else if (!(split.getBlobs().containsKey(entry.getKey()))
                    && !(other.getBlobs().containsKey(entry.getKey()))) {
                File file = join(BLOBFOLDER, headInfo);
                mergeHelper(headInfo, file, head);
            }
        }
        if (!mergeGood3(other, split, head)) {
            return;
        }
        rmBranch("Fork");
        commit("Merged " + name + " into " + b.curr() + ".");
        if (conflicted) {
            System.out.println("Encountered a merge conflict.");
        }
    }
    /**
     * Checks for merge errors.
     * @param name name of branch.
     * @return boolean if merge is good or not.
     */
    boolean mergeGood(String name) {
        Branches b = readObject(BRANCHFILE, Branches.class);
        if (b.curr().equals(name)) {
            System.out.println("Cannot merge a branch with itself.");
            return false;
        }
        if (!b.branches().containsKey(name)) {
            System.out.print("A branch with that name does not exist.");
            return false;
        }
        Stage s = readObject(STAGEFILE, Stage.class);
        if (s.getRemoveStage().size() != 0) {
            System.out.println("You have uncommitted changes.");
            return false;
        } else if (s.getAddStage().size() != 0) {
            System.out.println("You have uncommitted changes.");
            return false;
        }
        return true;
    }
    /**
     * Checks for merge errors.
     * @param branchHEAD head commit of branch.
     * @param fileList list of files in cwd.
     * @param checkedOutCommit commit which was checkedOut.
     * @return boolean if merge is good or not.
     */
    boolean mergeGood2(Commit branchHEAD, List<String> fileList,
                       Commit checkedOutCommit) {
        if (branchHEAD.getBlobs() != null) {
            for (String file : fileList) {
                if (!branchHEAD.getBlobs().containsKey(file)
                        && checkedOutCommit.getBlobs().containsKey(file)) {
                    String h = "There is an untracked file in the way; "
                            + "delete it, or add and commit it first.";
                    System.out.print(h);
                    return false;
                }
            }
        } else {
            if (fileList.size() > 0) {
                String h = "There is an untracked file in the way; "
                        + "delete it, or add and commit it first.";
                System.out.print(h);
                return false;
            }
        }
        return true;
    }
    /**
     * Checks for merge errors.
     * @param other other commit.
     * @param split split commit.
     * @param head head commit.
     * @return boolean if merge is good or not.
     */
    boolean mergeGood3(Commit other, Commit split, Commit head)
            throws IOException {
        for (Map.Entry<String, String> entry : other.getBlobs().entrySet()) {
            String otherInfo = other.getBlobs().get(entry.getKey());
            if (!(split.getBlobs().containsKey(entry.getKey()))
                    && !(head.getBlobs().containsKey(entry.getKey()))) {
                File file = join(BLOBFOLDER, otherInfo);
                mergeHelper(otherInfo, file, other);
            }
            if (split.getBlobs().containsKey(entry.getKey())) {
                String splitInfo = split.getBlobs().get(entry.getKey());
                if (!(head.getBlobs().containsKey(entry.getKey()))
                        && !(otherInfo.equals(splitInfo))) {
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * Checks for merge errors.
     * @param split split
     * @param entry entry
     * @param other other
     * @param otherInfo otherInfo
     * @param headInfo headInfo
     * @param head head
     * @param x x
     * @param z z
     * @return boolean if merge is good or not.
     */
    boolean mergeConflict(Commit split, Map.Entry<String, String> entry,
                          Commit other, String otherInfo, String headInfo,
                          Commit head, boolean x, boolean z)
            throws IOException {
        boolean y = headInfo.equals(otherInfo);
        if ((split.getBlobs().containsKey(entry.getKey()))
                && (other.getBlobs().containsKey(entry.getKey()))) {

            if (!(x) && z) {
                File file = join(BLOBFOLDER, otherInfo);
                mergeHelper(otherInfo, file, other);
            } else if (!(z) && x) {
                File file = join(BLOBFOLDER, headInfo);
                mergeHelper(headInfo, file, head);
            } else if (!(z) && !(x) && y) {
                System.exit(0);
            } else if (!(z) && !(x) && !(y)) {
                String content1 =
                        readContentsAsString(join(BLOBFOLDER, headInfo));
                String content2 =
                        readContentsAsString(join(BLOBFOLDER, otherInfo));
                String newContent = "<<<<<<< HEAD" + "\n" + content1 + "\n"
                        + "=======" + "\n" + content2 + ">>>>>>>\n";
                writeContents(join(CWD, entry.getKey()), newContent);
                add(entry.getKey());
                return true;
            }
        }
        return false;
    }
    /**
     * Checks for merge errors.
     * @param entry entry
     * @param otherInfo otherInfo
     * @param headInfo headInfo
     * @param x x
     * @param y y
     * @param z z
     * @return boolean if merge is good or not.
     */
    boolean mergeConflict2(boolean x, boolean y, boolean z, String headInfo,
                           String otherInfo, Map.Entry<String, String> entry) {
        if (!(z) && !(x) && !(y)) {
            String content1 = "";
            if (headInfo.length() != 0) {
                content1 =
                        readContentsAsString(join(BLOBFOLDER, headInfo));
            }
            String content2 = "";
            if (otherInfo.length() != 0) {
                content2 =
                        readContentsAsString(join(BLOBFOLDER, otherInfo));
            }
            String newContent = "<<<<<<< HEAD" + "\n" + content1
                    + "\n" + "=======" + "\n" + content2 + ">>>>>>>\n";
            writeContents(join(CWD, entry.getKey()), newContent);
            add(entry.getKey());
            return true;
        }
        return false;
    }
}
