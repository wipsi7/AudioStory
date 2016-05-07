package fi.metropolia.audiostory.filestorage;

import android.content.Context;

import java.io.File;


/** Class for creating folder, that is internal to the application and stored on external storage.
 * In case of uninstalling application, all files in folder will be automatically removed also. */
public class Folder {

    private final String FOLDER_NAME = "AudioStoryRecords";
    private final String DEBUG_TAG = "Folder";

    private Context applicationContext;
    private String rootPath;
    private File folder = null;



    public Folder(Context applicationContext){
        this.applicationContext = applicationContext;
        init();
    }

    private void init() {
        // Gets application internal absolute root path on external storage
        rootPath = applicationContext.getExternalFilesDir(null).getAbsolutePath();

        folder = new File(rootPath, FOLDER_NAME);
        if (!folder.exists()) {
            folder.mkdir();
        }

    }

    /** Returns complete folder path location.*/
    public String getFolderPath(){
        return  folder.getAbsolutePath();
    }

    /** Returns array of file names in folder. */
    public String[] getListOfFiles(){
        return folder.list();
    }

}
