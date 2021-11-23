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
                    db.commit(args[1]);
                }

                break;
            case "checkout":
                System.out.println(args.length);
                if(args.length == 3){
                    db.checkout(args[2]);
                }
                if(args.length == 4){
                    db.branchCheckout(args[1], args[3]);
                }

                break;
            case "log":
                if(args.length == 1){
                    db.log();
                }
                break;
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
