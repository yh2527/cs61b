package gitlet;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author TODO
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                if (args.length > 1) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.init();
                break;
            case "add":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                String addFileName = args[1];
                Repository.add(addFileName);
                break;
            case "commit":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                String commitMsg = args[1];
                Repository.commit(commitMsg);
                break;
            case "checkout":
                String checkoutFileName = null;
                String checkoutCommitID = null;
                if (args.length < 2 || args.length > 4) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                if (args.length == 3) {
                    checkoutFileName = args[2];
                    //checkoutCommitID = Utils.readContentsAsString(Repository.MASTER);
                }
                if (args.length == 4) {
                    checkoutFileName = args[3];
                    checkoutCommitID = args[1];
                }
                Repository.checkout(checkoutFileName, checkoutCommitID);
                break;
            case "log":
                if (args.length > 1) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.log();
                break;
            case "status":
                if (args.length > 1) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.status();
                break;
            case "rm":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                String rmFileName = args[1];
                Repository.rm(rmFileName);
                break;
            case "global-log":
                if (args.length > 1) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.globalLog();
                break;
            case "find":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                String MsgToLookUp = args[1];
                Repository.find(MsgToLookUp);
                break;
            case "branch":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                String branchName = args[1];
                Repository.branch(branchName);
                break;
            // TODO: FILL THE REST IN
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }
}
