package fi.metropolia.audiostory.activities;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.util.Arrays;

import fi.metropolia.audiostory.R;
import fi.metropolia.audiostory.filestorage.Folder;
import fi.metropolia.audiostory.filestorage.RawFile;
import fi.metropolia.audiostory.filestorage.WavFile;
import fi.metropolia.audiostory.threads.PlayThread;
import fi.metropolia.audiostory.threads.RecordThread;

public class RecordActivity extends AppCompatActivity {

    private final String DEBUG_TAG = "RecordActivity";

    private boolean recordClicked = false;
    
    private TextView titleTextView;

    private RecordThread recordThread = null;
    private PlayThread playThread = null;
    private Folder folder = null;

    private RawFile rawFile = null;
    private WavFile wavFile = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        initViews();
        init();
    }

    private void init() {
        folder = new Folder(getApplicationContext());
        rawFile = new RawFile(folder);
    }

    private void initViews() {
        titleTextView = (TextView)findViewById(R.id.titleText);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!isExternalStorageWritable()) {
            Toast.makeText(this, "External storage is not available", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void onInfoClick(View v){
        Log.d(DEBUG_TAG,"Files in folder: " + Arrays.toString(folder.getListOfFiles()));

    }

    public void onRecordClick(View v) {
        if (!recordClicked) {
            recordClicked = true;

            rawFile.createNewFile();
            recordThread = new RecordThread(rawFile);
            recordThread.start();
            Toast.makeText(this, "Recording", Toast.LENGTH_SHORT).show();
        }
    }

    public void onPlayClick(View v){
        if(rawFile.getFile() != null && rawFile.exists()){
            if(playThread == null || !playThread.isPlaying()){
                playThread = new PlayThread(rawFile);
                playThread.start();
                Toast.makeText(this, "Playing", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onStopClick(View v) {

        if (recordClicked) {
            recordClicked = false;
            recordThread.stopRecording();
            Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
        }
    }


    public void onUploadClick(View v){
        if(rawFile != null && rawFile.exists()){
            convWav();
        }
    }


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }


    public void convWav(){


        wavFile = new WavFile(folder, titleTextView.getText().toString());


        FFmpeg fFmpeg = FFmpeg.getInstance(getApplicationContext());

        try {
            fFmpeg.loadBinary(new LoadBinaryResponseHandler());
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }

        String command = String.format("-f s16le -ar 44.1k -ac 2 -i %s %s", rawFile.getRawFilePath(), wavFile.getWavFilePath());

        try {
            fFmpeg.execute(command, new ExecuteBinaryResponseHandler(){
                @Override
                public void onSuccess(String message) {
                    Log.d(DEBUG_TAG, "SUCCESS: ");
                    Log.d(DEBUG_TAG, "In folder is now files: " + Arrays.toString(folder.getListOfFiles()));
                }
            });



        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }
}