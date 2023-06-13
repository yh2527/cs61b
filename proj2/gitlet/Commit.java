package gitlet;

import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static gitlet.Utils.*;

/**
 * Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
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
    private String message;
    private String ID;
    private String parentID;
    private String createStamp;
    private HashMap<String, String> fileMap = new HashMap<>();

    public Commit(File blob, String msg) {
        message = msg;
        createStamp = createTime(false);
        ID = Utils.sha1(message, createStamp, blob);
    }

    //create initial commit
    public Commit(String msg) {
        message = msg;
        createStamp = createTime(true);
        ID = Utils.sha1(message, createStamp);
    }

    public static Commit readCommit(String hashID) {
        File inFile = join(Repository.COMMIT_DIR, hashID);
        Commit c = readObject(inFile, Commit.class);
        return c;
    }

    public void saveCommit() {
        File outFile = join(Repository.COMMIT_DIR, ID);
        writeObject(outFile, this);
    }

    public String CommitHashID() {
        return this.ID;
    }

    public String ParentHashID() {
        return this.parentID;
    }
    public HashMap CommitFileMap() {
        return this.fileMap;
    }

    public static String createTime(boolean init) {
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

