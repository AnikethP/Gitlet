package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Aniketh Prasad
 */
public class Main {


    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) throws IOException {
        if (args.length == 0) {
            exitWithError("Must have at least one argument");
        }

        switch (args[0]) {
            case "init":
                if(args.length == 1){
                    db.init();
                }
                break;
            case "add":
                if(args.length == 2){
                    db.add(args[1]);
                }
                break;
            case "commit":
                if(args.length == 2){
                    if(args[1].length() == 0){
                        System.out.println("Please enter a commit message.");
                    }
                    else{
                        db.commit(args[1]);
                    }
                }

                break;
            case "checkout":
                if (args.length==2){
                    db.branchCheckout(args[1]);
                }
                if(args.length == 3){
                    db.checkout(args[2]);
                }
                if(args.length == 4){
                    if(!args[2].equals("--")){
                        System.out.println("Incorrect operands.");
                    }
                    db.checkout(args[1], args[3]);
                }

                break;
            case "log":
                if(args.length == 1){
                    db.log();
                }
                break;
            case "global-log":
                if(args.length == 1){
                    db.globalLog();
                }
                break;
            case "rm":
                if(args.length == 2){
                    db.rm(args[1]);
                }
                break;
            case "status":
                if(args.length == 1){
                    db.status();
                }
                break;
            case "find":
                if(args.length == 2){
                    db.find(args[1]);
                }
                break;
            case "branch":
                if(args.length == 2){
                    db.addBranch(args[1]);
                }
                break;
            case "rm-branch":
                if(args.length == 2){
                    db.rmBranch(args[1]);
                }
            case "merge":
                if(args.length==2){
                    db.merge(args[1]);
                }
            case "reset":
                if(args.length==2){
                    db.reset(args[1]);
                }

        }
    }

    public static void exitWithError(String message) {
        if (message != null && !message.equals("")) {
            System.out.println(message);
        }

        System.exit(-1);
    }

    /**
     * Database which manages all git operations
     */
    private static Database db = new Database();
}
