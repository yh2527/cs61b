package gitlet;

import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import static gitlet.Utils.*;

/**
 * Represents a gitlet commit object.
 * It's a good idea to give a description here of what else this Class
 * does at a high level.
 *
 * @author TODO
 */
public class Commit implements Serializable {
    /**
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /**
     * The message of this Commit.
     */
    private static final long serialVersionUID = 1L;
    private String message;
    private String ID;
    private String parentID;
    private String createStamp;
    private HashMap<String, String> fileMap = new HashMap<>();

    public Commit(String msg, HashMap<String, String> fMap, String pID) {
        message = msg;
        createStamp = createTime(false);
        fileMap = fMap;
        parentID = pID;
        ID = Utils.sha1(fileMap.toString(), parentID, message, createStamp);
    }

    //create initial commit
    public Commit(String msg) {
        message = msg;
        createStamp = createTime(true);
        ID = Utils.sha1(message, createStamp);
    }

    public static Commit readCommit(String hashID) {
        if (hashID.length() == 5) {
            for (File c : Repository.COMMIT_DIR.listFiles()) {
                String cid = c.getName();
                if (hashID.equals(cid.substring(0,5))) {
                    hashID = cid;
                    break;
                }
            }
        }
        File inFile = join(Repository.COMMIT_DIR, hashID);
        if (!inFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit c = readObject(inFile, Commit.class);
        return c;
    }

    public void saveCommit() {
        File outFile = join(Repository.COMMIT_DIR, ID);
        writeObject(outFile, this);
    }

    public String commitHashID() {
        return this.ID;
    }

    public String parentHashID() {
        return this.parentID;
    }

    public String returnCreatedTime() {
        return this.createStamp;
    }

    public String returnMessage() {
        return this.message;
    }

    public HashMap commitFileMap() {
        return this.fileMap;
    }

    public String untrackFile(String fileName) {
        return fileMap.remove(fileName);
    }

    public void trackNewFile(String fileName, String fileHashID) {
        fileMap.put(fileName, fileHashID);
    }

    private static String createTime(boolean init) {
        ZonedDateTime time;
        if (init) {
            time = Instant.ofEpochSecond(0).atZone(ZoneId.of("PST", ZoneId.SHORT_IDS));
        } else {
            time = ZonedDateTime.now(ZoneId.of("PST", ZoneId.SHORT_IDS));
        }
        // Create a formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy Z");
        // Format the date
        String formattedDate = time.format(formatter);
        return formattedDate;
    }

}
