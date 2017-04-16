package fi.metropolia.audiostory.filestorage;

import android.content.Context;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

public class RawToWavConverter {

    private final String DEBUG_TAG = "RawToWavConverter";

    private Context applicationContext;
    private String sourcePath;
    private String destinationPath;


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
            fFmpeg.loadBinary(new LoadBinaryResponseHandler(){
                @Override
                public void onFailure() {
                    Log.i(DEBUG_TAG, "Failed loading LoadBinaryResponseHandler");
                }

                @Override
                public void onSuccess() {
                    Log.i(DEBUG_TAG, "Successfully loaded LoadBinaryResponseHandler");
                }


            });
        } catch (FFmpegNotSupportedException e) {
            Log.d(DEBUG_TAG, "ffmpeg not supported");
            e.printStackTrace();
        }

        String temp;
        temp = String.format("-f s16le -ac 1 -i %s %s", sourcePath, destinationPath);
        String[] command = temp.split(" ");

        try {
            fFmpeg.execute(command, new FFmpegExecuteResponseHandler(){
                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {

                }

                @Override
                public void onSuccess(String message) {
                    Log.d(DEBUG_TAG, "SUCCESSFULLY CONVERTED RAW TO WAV: " + message);
                }

                @Override
                public void onProgress(String message) {
                    Log.d(DEBUG_TAG, message);
                }

                @Override
                public void onFailure(String message) {
                    Log.d(DEBUG_TAG, "FAILED CONVERTING RAW TO WAV: " + message);
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }
}
