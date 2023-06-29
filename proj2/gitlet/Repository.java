package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;
//import java.util.Set;

import static gitlet.Utils.*;
import static gitlet.Utils.readContentsAsString;


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
    public static final File REFS = join(GITLET_DIR, "refs");
    public static final File MASTER = join(REFS, "master");
    public static final File HEAD = join(GITLET_DIR, "head");
    public static final File STAGE = join(GITLET_DIR, "stage");
    public static final File REMOVED = join(GITLET_DIR, "removed");
    public static final File INITCOMMITID = join(GITLET_DIR, "initCommitID");

    public static void init() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
        } else {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
            System.exit(0);
        }
        //dir for commits and blobs
        FILE_DIR.mkdir();
        COMMIT_DIR.mkdir();
        //create dir for refs: default master and branches
        REFS.mkdir();
        try {
            MASTER.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //file for head pointer, set it to MASTER
        try {
            HEAD.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeObject(HEAD, MASTER);
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
        writeContents(MASTER, initCommit.commitHashID());
        initCommit.saveCommit();
        try {
            INITCOMMITID.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeContents(INITCOMMITID, initCommit.commitHashID());
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
        File curPointer = readObject(HEAD, File.class);
        Commit latestCommit = Commit.readCommit(readContentsAsString(curPointer));
        if (addFileID.equals(stageRemoveMap.get(fileName))) {
            checkout(fileName, null, null);
            stageRemoveMap.remove(fileName);
        } else if (addFileID.equals(latestCommit.commitFileMap().get(fileName))) {
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
            File curPointer = readObject(HEAD, File.class);
            Commit latestCommit = Commit.readCommit(readContentsAsString(curPointer));
            Commit newCommit = new Commit(
                    msg,
                    latestCommit.commitFileMap(),
                    latestCommit.commitHashID()
            );
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
            writeContents(curPointer, newCommit.commitHashID());
        }
    }

    public static void log() {
        File curPointer = readObject(HEAD, File.class);
        String currHash = readContentsAsString(curPointer);
        while (currHash != null) {
            Commit currCommit = Commit.readCommit(currHash);
            System.out.println("===");
            System.out.println("commit " + currCommit.commitHashID());
            System.out.println("Date: " + currCommit.returnCreatedTime());
            System.out.println(currCommit.returnMessage());
            System.out.println();
            currHash = currCommit.parentHashID();
            //System.out.println(currCommit.returnMessage());
        }
    }

    public static void checkout(String checkoutFileName,
                                String checkoutCommitID, String checkoutBranch) {
        Commit targetCommit;
        File curPointer = readObject(HEAD, File.class);
        Commit currCommit = Commit.readCommit(readContentsAsString(curPointer));
        if (checkoutBranch != null) {
            File targetBranch = join(REFS, checkoutBranch);
            if (!targetBranch.exists()) {
                System.out.println("No such branch exists.");
                System.exit(0);
            }
            if (curPointer.equals(targetBranch)) {
                System.out.println("No need to checkout the current branch.");
                System.exit(0);
            }
            HashMap<String, HashMap> stageMap = readObject(STAGE, HashMap.class);
            HashMap<String, String> stageAddMap = stageMap.get("add");
            HashMap<String, String> currCommitMap = currCommit.commitFileMap();
            Set<String> currFileSet = currCommitMap.keySet();
            for (File file : CWD.listFiles()) {
                String fileName = file.getName();
                if (fileName.charAt(0) != '.'
                        && !currFileSet.contains(file.getName())
                        && !stageAddMap.containsKey(file.getName())) {
                    System.out.println("There is an untracked file in the way;"
                            + " delete it, or add and commit it first.");
                    System.exit(0);
                }
                file.delete();
            }
            stageAddMap.clear();
            writeObject(STAGE, stageMap);
            writeObject(HEAD, targetBranch);
            Commit targCommit = Commit.readCommit(readContentsAsString(targetBranch));
            HashMap<String, String> targCommitMap = targCommit.commitFileMap();
            Set<String> fileSet = targCommitMap.keySet();
            for (String fileName : fileSet) {
                String fid = targCommitMap.get(fileName);
                saveFiletoCWD(fileName, fid);
            }
        } else {
            if (checkoutCommitID == null) {
                targetCommit = currCommit;
            } else {
                targetCommit = Commit.readCommit(checkoutCommitID);
            }
            HashMap<String, String> targetFileMap = targetCommit.commitFileMap();
            String checkoutFileHashID = targetFileMap.get(checkoutFileName);
            if (checkoutFileHashID == null) {
                System.out.println("File does not exist in that commit.");
            } else {
                saveFiletoCWD(checkoutFileName, checkoutFileHashID);
            }
        }
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
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        System.out.println("=== Branches ===");
        File curPointer = readObject(HEAD, File.class);
        String curBranchName = curPointer.getName();
        //System.out.println("*master");
        TreeSet<String> sortedBranchNames = new TreeSet<>();
        for (File b : REFS.listFiles()) {
            sortedBranchNames.add(b.getName());
        }
        for (String fileName : sortedBranchNames) {
            if (fileName.equals(curBranchName)) {
                System.out.print("*");
            }
            System.out.println(fileName);
        }
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
            File curPointer = readObject(HEAD, File.class);
            Commit currCommit = Commit.readCommit(readContentsAsString(curPointer));
            HashMap<String, String> currCommitFileMap = currCommit.commitFileMap();
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
            System.out.println("commit " + currCommit.commitHashID());
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
                System.out.println(currCommit.commitHashID());
                findCommit = true;
            }
        }
        if (!findCommit) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void branch(String branchName) {
        File newBranch = join(REFS, branchName);
        if (newBranch.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        try {
            newBranch.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // point the new branch to the current head
        File curPointer = readObject(HEAD, File.class);
        writeContents(newBranch, readContentsAsString(curPointer));
    }

    public static void rmbranch(String branchName) {
        File rmBranch = join(REFS, branchName);
        if (!rmBranch.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        File curPointer = readObject(HEAD, File.class);
        if (rmBranch.equals(curPointer)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        rmBranch.delete();
    }

    public static void reset(String resetCommitId) {
        File curPointer = readObject(HEAD, File.class);
        Commit currCommit = Commit.readCommit(readContentsAsString(curPointer));
        HashMap<String, HashMap> stageMap = readObject(STAGE, HashMap.class);
        HashMap<String, String> stageAddMap = stageMap.get("add");
        HashMap<String, String> currCommitMap = currCommit.commitFileMap();
        Set<String> currFileSet = currCommitMap.keySet();
        for (File file : CWD.listFiles()) {
            String fileName = file.getName();
            if (fileName.charAt(0) != '.'
                    && !currFileSet.contains(file.getName())
                    && !stageAddMap.containsKey(file.getName())) {
                System.out.println("There is an untracked file in the way;"
                        + " delete it, or add and commit it first.");
                System.exit(0);
            }
            file.delete();
        }
        stageAddMap.clear();
        writeObject(STAGE, stageMap);
        Commit targCommit = Commit.readCommit(resetCommitId);
        HashMap<String, String> targCommitMap = targCommit.commitFileMap();
        Set<String> fileSet = targCommitMap.keySet();
        for (String fileName : fileSet) {
            String fid = targCommitMap.get(fileName);
            saveFiletoCWD(fileName, fid);
        }
        writeContents(curPointer, resetCommitId);
    }

    public static void merge(String mergeBranch) {
        Commit split = findSplit(mergeBranch);
        Commit currCommit = getCurrCommit();
        Commit targetBranchCommit = getBranchHeadCommit(mergeBranch);
        File targetBranch = join(REFS, mergeBranch);
        String targBCommitHash = readContentsAsString(targetBranch);
        if (targetBranchCommit.equals(split)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        if (currCommit.equals(split)) {
            checkout(null, null, mergeBranch);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
        HashMap<String, String> currCommitMap = currCommit.commitFileMap();
        HashMap<String, String> splitCommitMap = split.commitFileMap();
        HashMap<String, String> targetCommitMap = targetBranchCommit.commitFileMap();
        for (String fileName : splitCommitMap.keySet()) {
            String curContent = currCommitMap.get(fileName);
            String targBranchContent = targetCommitMap.get(fileName);
            String splitContent = splitCommitMap.get(fileName);
            if (!splitContent.equals(targBranchContent)) {
                if (splitContent.equals(curContent)) {
                    checkout(fileName, targBCommitHash, null);
                    add(fileName);
                } else if (!targBranchContent.equals(curContent)) {
                    String curText = readContentsAsString(join(FILE_DIR, curContent));
                    String tarText = readContentsAsString(join(FILE_DIR, targBranchContent));
                    String newText = "<<<<<<< HEAD\n";
                    newText += curText + "\n" + tarText + "\n";
                    newText += ">>>>>>>";
                    System.out.println("Encountered a merge conflict.");
                }
            }
            if (splitContent.equals(curContent) && targBranchContent == null) {
                rm(fileName);
            }
        }
        for (String fileName : targetCommitMap.keySet()) {
            String curContent = currCommitMap.get(fileName);
            String targBranchContent = targetCommitMap.get(fileName);
            String splitContent = splitCommitMap.get(fileName);
            if (splitContent == null && curContent == null) {
                checkout(fileName, targBCommitHash, null);
                add(fileName);
            }
        }
    }


    private static Commit findSplit(String mergeBranch) {
        Commit currCommit = getCurrCommit();
        Commit targetBranchHead = getBranchHeadCommit(mergeBranch);
        HashMap<String, Integer> currCommitAncestors = readAncestors(currCommit);
        HashMap<String, Integer> targetBranchAncestors = readAncestors(targetBranchHead);
        File curPointer = readObject(HEAD, File.class);
        String latestAncestor = readContentsAsString(curPointer);
        int smallestValue = Integer.MAX_VALUE;
        for (Map.Entry<String, Integer> entry : currCommitAncestors.entrySet()) {
            String key = entry.getKey();
            Integer closestDistance = entry.getValue();
            if (targetBranchAncestors.containsKey(key) && closestDistance < smallestValue) {
                latestAncestor = key;
                smallestValue = closestDistance;
            }
        }
        return Commit.readCommit(latestAncestor);
    }

    private static HashMap<String, Integer> readAncestors(Commit c) {
        HashMap<String, Integer> path = new HashMap<>();
        Stack<Commit> stack = new Stack<>();
        Stack<Integer> distances = new Stack<>();
        stack.push(c);
        distances.push(0);
        while (!stack.isEmpty()) {
            c = stack.pop();
            int distance = distances.pop();
            String commitID = c.commitHashID();
            path.put(commitID, distance);
            List<Commit> parentCommits = new ArrayList<>();
            if (c.parentHashID() != null) {
                parentCommits.add(Commit.readCommit(c.parentHashID()));
            }
            if (c.parentHashID2() != null) {
                parentCommits.add(Commit.readCommit(c.parentHashID2()));
            }
            for (Commit parentCommit : parentCommits) {
                if (!path.containsKey(parentCommit.commitHashID())) {
                    stack.push(parentCommit);
                    distances.push(distance + 1);
                }
            }
        }
        return path;
    }

    private static Commit getCurrCommit() {
        File curPointer = readObject(HEAD, File.class);
        Commit currCommit = Commit.readCommit(readContentsAsString(curPointer));
        return currCommit;
    }

    private static Commit getBranchHeadCommit(String branchName) {
        File targetBranch = join(REFS, branchName);
        Commit branchHeadCommit = Commit.readCommit(readContentsAsString(targetBranch));
        return branchHeadCommit;
    }
}
