package fi.metropolia.audiostory.filestorage;

import android.util.Log;

import java.io.File;
import java.io.IOException;

/** Class for creating container for .raw */
public class RawFile {

    private final String RAW_FILE = "record.raw";
    private final String DEBUG_TAG = "RawFile";

    private Folder folder = null;
    private File rawFile = null;

    private boolean deleted;

    public RawFile(Folder folder){
        this.folder = folder;
        init();
    }

    private void init() {
        rawFile = new File(folder.getFolderPath(), RAW_FILE);
        deleteIfExists();
    }

    private void deleteIfExists() {
        if (rawFile.exists()) {
            deleted = rawFile.delete();

            if(deleted)
                Log.d(DEBUG_TAG, ".raw deleted");
            else
                Log.d(DEBUG_TAG, ".raw was not deleted");
        }
    }

    /** Creates new .raw file. Also removes old .raw file if it existed.*/
    public void createNewFile() {

        deleteIfExists();

        try {
            if (rawFile.createNewFile()) {
                Log.d(DEBUG_TAG, ".raw created");
            } else {
                Log.d(DEBUG_TAG, ".raw already exists");
            }
        } catch (IOException e) {
            Log.e(DEBUG_TAG, e.getMessage());
        }
    }

    /** Returns File class of RawFile.*/
    public File getFile(){
        return rawFile;
    }

    /** Returns complete .raw file path location.*/
    public String getRawFilePath(){
        return rawFile.getAbsolutePath();
    }

    /** Returns boolean whether file exists in file system.*/
    public boolean exists(){
        return rawFile.exists();
    }
}
