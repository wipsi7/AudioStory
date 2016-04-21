package fi.metropolia.audiostory.activities;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import fi.metropolia.audiostory.R;

public class RecordActivity extends AppCompatActivity {

    private final String FOLDER_NAME = "AudioStoryRecords";
    private final String TEMP_FILE = "record_temp.raw";
    private final String DEBUG_TAG = "RecordActivity";

    private boolean recordRunning = false;
    private boolean playing = false;

    private File folder = null;
    private File rawFile = null;
    private File wavFile = null;

    private TextView titleTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

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

    public void onRecordClick(View v) {

        if (!recordRunning) {
            Thread recordThread = new Thread() {
                @Override
                public void run() {
                    recordRunning = true;
                    startRecording();
                }
            };
            Toast.makeText(this, "Recording", Toast.LENGTH_SHORT).show();
            recordThread.start();
        }
    }

    public void onStopClick(View v) {
        if (recordRunning) {
            recordRunning = false;
            Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
        }
    }

    public void onPlayClick(View v){
        if(rawFile != null && rawFile.exists()){
            if(!playing){
                Thread playThread = new Thread(){
                    @Override
                    public void run() {
                       startPlaying();
                    }
                };
                playThread.start();
            }
        }
    }

    public void onUploadClick(View v){
        if(rawFile != null && rawFile.exists()){
            convWav();

        }

    }

    private void startPlaying(){

        int minBufferSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        AudioTrack track = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                44100,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize,
                AudioTrack.MODE_STREAM
        );

        playing = true;
        track.play();

        int i = 0;
        byte[] buffer = new byte[minBufferSize];

        try {

            InputStream inputStream = new FileInputStream(rawFile);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);

            while ((i = dataInputStream.read(buffer, 0, minBufferSize)) != -1){
                track.write(buffer,0,i);
            }

            dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        track.stop();
        track.release();
        playing = false;

    }

    private void startRecording() {
        rawFile = createRawOnExternalStorage();
        Log.d(DEBUG_TAG, "rawFile path: " + rawFile.getAbsolutePath());

        try {
            OutputStream outputStream = new FileOutputStream(rawFile);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);

            int minBufferSize = AudioRecord.getMinBufferSize(
                    44100,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT
            );

            byte[] audioData = new byte[minBufferSize];

            AudioRecord audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    44100,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufferSize
            );
            audioRecord.startRecording();


            while(recordRunning){
                int numofBytes = audioRecord.read(audioData, 0, minBufferSize);
                for(int i = 0; i < numofBytes; i++){
                    dataOutputStream.write(audioData[i]);
                }
            }

            audioRecord.stop();
            dataOutputStream.close();


        } catch (IOException e){
            Log.e(DEBUG_TAG,e.getMessage());
        }


    }

    private File createRawOnExternalStorage() {
        String rootPath = getExternalFilesDir(null).getAbsolutePath();
        folder = new File(rootPath, FOLDER_NAME);
        if (!folder.exists()) {
            folder.mkdir();
        }
        String[] names = getExternalFilesDir(null).list();
        Log.d(DEBUG_TAG, "List of files: " + Arrays.toString(names));

        File tempFile = new File(folder, TEMP_FILE);


        Log.d(DEBUG_TAG, "Folder absolute path:" + folder.getAbsolutePath());

        if (tempFile.exists()) {
            tempFile.delete();
        }


        try {
            if (tempFile.createNewFile()) {
                Log.d(DEBUG_TAG, "File created");
            } else {
                Log.d(DEBUG_TAG, "File already exists");
            }
        } catch (IOException e) {
            Log.e(DEBUG_TAG, e.getMessage());
        }

        String files[] = folder.list();
        Log.d(DEBUG_TAG, "Files in folder:" + Arrays.toString(files));
        return tempFile;
    }



    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    public void convWav(){

        String wav;
        if(titleTextView.getTextSize() == 0) {
            wav = "random.wav";
        }else {
            wav = titleTextView.getText() + ".wav";
        }

        wavFile = new File(folder, wav);
        if(wavFile.exists()){
            wavFile.delete();
        }

/*        try {
            wavFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        FFmpeg fFmpeg = FFmpeg.getInstance(getApplicationContext());

        try {
            fFmpeg.loadBinary(new LoadBinaryResponseHandler());
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }

        String command = String.format("-f s16le -ar 44.1k -ac 2 -i %s %s", rawFile.getAbsolutePath(), wavFile.getAbsolutePath());

        try {
            fFmpeg.execute(command, new ExecuteBinaryResponseHandler(){
                @Override
                public void onSuccess(String message) {
                    Log.d(DEBUG_TAG, "SUCCESS: ");
                    Log.d(DEBUG_TAG, "In folder is now files: " + Arrays.toString(folder.list()));
                }
            });



        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }
}
