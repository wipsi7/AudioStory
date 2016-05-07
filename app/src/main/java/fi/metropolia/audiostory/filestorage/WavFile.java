package fi.metropolia.audiostory.filestorage;

import android.util.Log;

import java.io.File;

/** Class for .wav container. If same title name wav exists in file system, it is then deleted  */
public class WavFile {

    private String DEBUG_TAG = "WavFile";

    private Folder folder;
    private String title;
    private File wavFile;

    private boolean result = false;

    public WavFile(Folder folder, String title){
        this.folder = folder;
        this.title = title + ".wav";
        createFile();
        removeOld();
    }

    private void createFile() {

        wavFile = new File(folder.getFolderPath(), title);
    }


    /** removes old .wav file with same title if exists*/
    private void removeOld(){
        if(wavFile.exists()){
            result = wavFile.delete();

            if(result)
                Log.d(DEBUG_TAG, "Old wav file removed");
            else
                Log.d(DEBUG_TAG,"Old wav file not removed");
        }
    }

    public File getFile(){
        return  wavFile;
    }

    /** Returns complete .wav file path location.*/
    public String getWavFilePath(){
        return wavFile.getAbsolutePath();
    }

}
