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

    public RawFile(Folder folder){
        this.folder = folder;

    }

    /** Creates new .raw file. Also removes old .raw file if it existed.*/
    public void createNewFile() {
        rawFile = new File(folder.getFolderPath(), RAW_FILE);

        if (rawFile.exists()) {
            rawFile.delete();
        }

        try {
            if (rawFile.createNewFile()) {
                Log.d(DEBUG_TAG, "Raw file created");
            } else {
                Log.d(DEBUG_TAG, "Raw file already exists");
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
