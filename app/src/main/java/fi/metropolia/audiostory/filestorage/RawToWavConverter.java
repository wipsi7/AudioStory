package fi.metropolia.audiostory.filestorage;

import android.content.Context;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

public class RawToWavConverter {

    private final String DEBUG_TAG = "RawToWavConverter";

    private Context applicationContext;
    private String sourcePath;
    private String destinationPath;
    private String command;

    private FFmpeg fFmpeg;

    public RawToWavConverter(Context applicationContext){
        this.applicationContext = applicationContext;

    }

    public void setFilePaths(String sourcePath, String destinationPath){
        this.sourcePath = sourcePath;
        this.destinationPath = destinationPath;
    }

    public void convert(){

        fFmpeg = FFmpeg.getInstance(applicationContext);

        try {
            fFmpeg.loadBinary(new LoadBinaryResponseHandler());
        } catch (FFmpegNotSupportedException e) {
            Log.d(DEBUG_TAG, "ffmpeg not supported");
            e.printStackTrace();
        }

        command = String.format("-f s16le -ar 44.1k -ac 1 -i %s %s", sourcePath, destinationPath);

        try {
            fFmpeg.execute(command, new ExecuteBinaryResponseHandler(){
                @Override
                public void onSuccess(String message) {
                    Log.d(DEBUG_TAG, "SUCCESSFULLY CONVERTED RAW TO WAV");
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }
}
