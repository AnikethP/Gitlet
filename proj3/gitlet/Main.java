package gitlet;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Aniketh Prasad
 */
public class Main {


    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
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

                break;
            case "commit":

                break;
            case "checkout":

                break;
            case
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
    private Database db = new Database();
}
