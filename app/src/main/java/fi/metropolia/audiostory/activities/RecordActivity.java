package fi.metropolia.audiostory.activities;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import fi.metropolia.audiostory.R;
import fi.metropolia.audiostory.filestorage.Folder;
import fi.metropolia.audiostory.filestorage.RawFile;
import fi.metropolia.audiostory.filestorage.RawToWavConverter;
import fi.metropolia.audiostory.filestorage.WavFile;
import fi.metropolia.audiostory.threads.PlayThread;
import fi.metropolia.audiostory.threads.RecordThread;

public class RecordActivity extends AppCompatActivity {

    private final String DEBUG_TAG = "RecordActivity";

    private boolean recordClicked = false;

    private TextView titleTextView;

    private RawToWavConverter rawToWavConverter;
    private RecordThread recordThread = null;

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
        rawToWavConverter = new RawToWavConverter(getApplicationContext());
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
        PlayThread playThread = null;
        if(rawFile.getFile() != null && rawFile.exists()){
            if(!playThread.isPlaying()){
                //playThread = new PlayThread(rawFile);
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
        if(titleTextView.getText().length() != 0){
            wavFile = new WavFile(folder, titleTextView.getText().toString());
            rawToWavConverter.setFilePaths(rawFile.getRawFilePath(),wavFile.getWavFilePath());
            rawToWavConverter.convert();
        }else {
            Toast.makeText(this, "Enter a title first!", Toast.LENGTH_SHORT).show();
        }
    }
}