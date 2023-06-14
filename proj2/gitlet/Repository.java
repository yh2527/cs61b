package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static gitlet.Utils.*;


/**
 * Represents a gitlet repository.
 * It's a good idea to give a description here of what else this Class
 * does at a high level.
 *
 * @author TODO
 */
public class Repository {
    /**
     * <p>
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File FILE_DIR = join(GITLET_DIR, "files");
    public static final File COMMIT_DIR = join(GITLET_DIR, "commits");
    public static final File HEAD = join(GITLET_DIR, "heads");
    public static final File MASTER = join(HEAD, "master");
    public static final File STAGE = join(GITLET_DIR, "stage");
    public static final File initCommitID = join(GITLET_DIR, "initCommitID");

    public static void init() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        //dir for commits and blobs
        FILE_DIR.mkdir();
        COMMIT_DIR.mkdir();
        //file for head pointer
        HEAD.mkdir();
        try {
            MASTER.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //file for staging area
        try {
            STAGE.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HashMap<String, String> stageMap = new HashMap<>();
        writeObject(STAGE, stageMap);
        //set up default commit: no file "initial commit"
        Commit initCommit = new Commit("initial commit");
        writeContents(MASTER, initCommit.CommitHashID());
        initCommit.saveCommit();
        try {
            initCommitID.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeContents(initCommitID, initCommit.CommitHashID());
    }

    public static void add(String fileName) {
        File addFile = join(CWD, fileName);
        if (!addFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        String addFileID = sha1(readContents(addFile));
        HashMap<String, String> stageMap = readObject(STAGE, HashMap.class);
        //System.out.println(readContentsAsString(MASTER));
        Commit latestCommit = Commit.readCommit(readContentsAsString(MASTER));
        //check if file content is the same as in the current commit
        if (addFileID.equals(latestCommit.CommitFileMap().get(fileName))) {
            //remove the file from the staging area if it's there
            stageMap.remove(fileName);
        } else {
            stageMap.put(fileName, addFileID);
        }
        writeObject(STAGE, stageMap);
    }

    public static void commit(String msg) {
        HashMap<String, String> stageMap = readObject(STAGE, HashMap.class);
        if (stageMap.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        if (msg.length() == 0) {
            System.out.println("Please enter a commit message.");
        } else {
            Commit latestCommit = Commit.readCommit(readContentsAsString(MASTER));
            HashMap<String, String> CommitMap = latestCommit.CommitFileMap();
            for (String e : stageMap.keySet()) {
                String eid = stageMap.get(e);
                CommitMap.put(e, eid);
                saveFilefromCWD(e, eid);
            }
            stageMap.clear();
            writeObject(STAGE, stageMap);
            Commit newCommit = new Commit(msg, CommitMap, latestCommit.CommitHashID());
            newCommit.saveCommit();
            writeContents(MASTER, newCommit.CommitHashID());
        }
    }

    public static void log() {
        String currHash = readContentsAsString(MASTER);
        while (currHash != null) {
            Commit currCommit = Commit.readCommit(currHash);
            System.out.println("===");
            System.out.println("commit " + currCommit.CommitHashID());
            System.out.println("Date: " + currCommit.returnCreatedTime());
            System.out.println(currCommit.returnMessage());
            System.out.println();
            currHash = currCommit.ParentHashID();
            //System.out.println(currCommit.returnMessage());
        }
    }

    public static void checkout(String checkoutFileName, String checkoutCommitID) {
        Commit targetCommit;
        if (checkoutCommitID == null) {
            targetCommit = Commit.readCommit(readContentsAsString(MASTER));
        } else {
            targetCommit = Commit.readCommit(checkoutCommitID);
        }
        HashMap<String, String> targetFileMap = targetCommit.CommitFileMap();
        String checkoutFileHashID = targetFileMap.get(checkoutFileName);
        if (checkoutFileHashID == null) {
            System.out.println("File does not exist in that commit.");
        } else
            saveFiletoCWD(checkoutFileName, checkoutFileHashID);
    }

    public static void saveFilefromCWD(String fileName, String fileHashID) {
        File toSave = join(CWD, fileName);
        String contents = readContentsAsString(toSave);
        //System.out.println(contents);
        File saveAs = join(FILE_DIR, fileHashID);
        writeContents(saveAs, contents);
    }

    public static void saveFiletoCWD(String fileName, String fileHashID) {
        File toSave = join(FILE_DIR, fileHashID);
        String contents = readContentsAsString(toSave);
        //System.out.println(contents);
        File saveAs = join(CWD, fileName);
        writeContents(saveAs, contents);
    }

}
