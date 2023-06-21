package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
//import java.util.Set;
import java.util.List;
import java.util.TreeSet;

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
    public static final File REMOVED = join(GITLET_DIR, "removed");
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
        HashMap<String, HashMap> stageMap = new HashMap<>();
        stageMap.put("add", new HashMap<String, String>());
        stageMap.put("remove", new HashMap<String, String>());
        writeObject(STAGE, stageMap);
        try {
            REMOVED.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        TreeSet<String> removedFiles = new TreeSet<>();
        writeObject(REMOVED, removedFiles);
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
        HashMap<String, HashMap> stageMap = readObject(STAGE, HashMap.class);
        HashMap<String, String> stageAddMap = stageMap.get("add");
        HashMap<String, String> stageRemoveMap = stageMap.get("remove");
        //System.out.println(readContentsAsString(MASTER));
        Commit latestCommit = Commit.readCommit(readContentsAsString(MASTER));
        if (addFileID.equals(stageRemoveMap.get(fileName))) {
            checkout(fileName, null);
            stageRemoveMap.remove(fileName);
        } else if (addFileID.equals(latestCommit.CommitFileMap().get(fileName))) {
            //check if file content is the same as in the current commit
            // remove the file from the staging area if it's there
            stageAddMap.remove(fileName);
        } else {
            stageAddMap.put(fileName, addFileID);
        }
        writeObject(STAGE, stageMap);
    }

    public static void commit(String msg) {
        HashMap<String, HashMap> stageMap = readObject(STAGE, HashMap.class);
        HashMap<String, String> stageAddMap = stageMap.get("add");
        HashMap<String, String> stageRemoveMap = stageMap.get("remove");
        if (stageAddMap.isEmpty() && stageRemoveMap.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        if (msg.length() == 0) {
            System.out.println("Please enter a commit message.");
        } else {
            Commit latestCommit = Commit.readCommit(readContentsAsString(MASTER));
            Commit newCommit = new Commit(msg, latestCommit.CommitFileMap(), latestCommit.CommitHashID());
            for (String a : stageAddMap.keySet()) {
                String aid = stageAddMap.get(a);
                newCommit.trackNewFile(a, aid);
                saveFilefromCWD(a, aid);
            }
            stageAddMap.clear();
            //TreeSet<String> removedFiles = readObject(REMOVED, TreeSet.class);
            for (String r : stageRemoveMap.keySet()) {
                newCommit.untrackFile(r);
                //removedFiles.add(r);
            }
            //writeObject(REMOVED, removedFiles);
            stageRemoveMap.clear();
            writeObject(STAGE, stageMap);
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

    public static void status() {
        System.out.println("=== Branches ===");
        System.out.println("*master");
        //System.out.println("other-branch");
        System.out.println();
        System.out.println("=== Staged Files ===");
        HashMap<String, HashMap> stageMap = readObject(STAGE, HashMap.class);
        HashMap<String, String> stageAddMap = stageMap.get("add");
        //Set<String> stagedFileNames = stageAddMap.keySet();
        TreeSet<String> sortedAddFileNames = new TreeSet<>(stageAddMap.keySet());
        for (String fileName : sortedAddFileNames) {
            System.out.println(fileName);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        HashMap<String, String> stageRemoveMap = stageMap.get("remove");
        TreeSet<String> sortedRmFileNames = new TreeSet<>(stageRemoveMap.keySet());
        for (String fileName : sortedRmFileNames) {
            System.out.println(fileName);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    public static void rm(String fileName) {
        HashMap<String, HashMap> stageMap = readObject(STAGE, HashMap.class);
        HashMap<String, String> stageAddMap = stageMap.get("add");
        HashMap<String, String> stageRemoveMap = stageMap.get("remove");
        String rmFileID = stageAddMap.remove(fileName);
        if (rmFileID == null) {
            Commit currCommit = Commit.readCommit(readContentsAsString(MASTER));
            HashMap<String, String> currCommitFileMap = currCommit.CommitFileMap();
            String rmFileIDinCurrCommit = currCommitFileMap.get(fileName);
            if (rmFileIDinCurrCommit == null) {
                System.out.println("No reason to remove the file.");
                System.exit(0);
            } else {
                File toRemove = join(CWD, fileName);
                if (toRemove.exists()) {
                    toRemove.delete();
                }
                stageRemoveMap.put(fileName, rmFileIDinCurrCommit);
            }
        }
        writeObject(STAGE, stageMap);
    }

    public static void globalLog() {
        List<String> commitList = plainFilenamesIn(COMMIT_DIR);
        for (String c : commitList) {
            Commit currCommit = Commit.readCommit(c);
            System.out.println("===");
            System.out.println("commit " + currCommit.CommitHashID());
            System.out.println("Date: " + currCommit.returnCreatedTime());
            System.out.println(currCommit.returnMessage());
            System.out.println();
        }
    }

    public static void find(String msg) {
        boolean findCommit = false;
        List<String> commitList = plainFilenamesIn(COMMIT_DIR);
        for (String c : commitList) {
            Commit currCommit = Commit.readCommit(c);
            if (msg.equals(currCommit.returnMessage())) {
                System.out.println(currCommit.CommitHashID());
                findCommit = true;
            }
        }
        if (!findCommit) {
            System.out.println("Found no commit with that message.");
        }
    }

}
