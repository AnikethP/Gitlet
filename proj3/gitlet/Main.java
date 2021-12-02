package gitlet;

import java.io.IOException;

/**
 * Driver class for Gitlet, the tiny stupid version-control system.
 *
 * @author Aniketh Prasad
 */
public class Main {


    /**
     * Database which manages all git operations.
     */
    private static Database db = new Database();

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND> ....
     */
    public static void main(String... args) throws IOException {
        if (args.length == 0) {
            exitWithError("Must have at least one argument");
        }
        switch (args[0]) {
        case "init":
            if (args.length == 1) {
                db.init();
            }
            break;
        case "add":
            if (args.length == 2) {
                db.add(args[1]);
            }
            break;
        case "commit":
            doCommit(args);
            break;
        case "checkout":
            doCheckout(args);
            break;
        case "log":
            doLog(args);
            break;
        case "global-log":
            doGLog(args);
            break;
        case "rm":
            doRm(args);
            break;
        case "status":
            doStatus(args);
            break;
        case "find":
            doFind(args);
            break;
        case "branch":
            if (args.length == 2) {
                db.addBranch(args[1]);
            }
            break;
        case "rm-branch":
            if (args.length == 2) {
                db.rmBranch(args[1]);
            }
            break;
        case "merge":
            if (args.length == 2) {
                db.merge(args[1]);
            }
            break;
        case "reset":
            if (args.length == 2) {
                db.reset(args[1]);
            }
            break;
        default:
            break;
        }
    }
    /**
     *
     * Validates args and runs checkout.
     * @param args user input.
     */
    static void doCheckout(String... args) throws IOException {
        if (args.length == 2) {
            db.branchCheckout(args[1]);
        }
        if (args.length == 3) {
            db.checkout(args[2]);
        }
        if (args.length == 4) {
            if (!args[2].equals("--")) {
                System.out.println("Incorrect operands.");
            }
            db.checkout(args[1], args[3]);
        }
    }
    /**
     *
     * Validates args and runs commit.
     * @param args user input.
     */
    static void doCommit(String... args) throws IOException {
        if (args.length == 2) {
            if (args[1].length() == 0) {
                System.out.println("Please enter a commit message.");
            } else {
                db.commit(args[1]);
            }
        }
    }
    /**
     *
     * Validates args and runs log.
     * @param args user input.
     */
    static void doLog(String... args) throws IOException {
        if (args.length == 1) {
            db.log();
        }
    }
    /**
     *
     * Validates args and runs global-log.
     * @param args user input.
     */
    static void doGLog(String... args) throws IOException {
        if (args.length == 1) {
            db.globalLog();
        }
    }
    /**
     *
     * Validates args and runs rm.
     * @param args user input.
     */
    static void doRm(String... args) throws IOException {
        if (args.length == 2) {
            db.rm(args[1]);
        }
    }
    /**
     *
     * Validates args and runs status.
     * @param args user input.
     */
    static void doStatus(String... args) throws IOException {
        if (args.length == 1) {
            db.status();
        }
    }
    /**
     *
     * Validates args and runs find.
     * @param args user input.
     */
    static void doFind(String... args) throws IOException {
        if (args.length == 2) {
            db.find(args[1]);
        }
    }

    /**
     *
     * Exits gitlet.
     * @param message error msg.
     */
    public static void exitWithError(String message) {
        if (message != null && !message.equals("")) {
            System.out.println(message);
        }

        System.exit(-1);
    }
}
