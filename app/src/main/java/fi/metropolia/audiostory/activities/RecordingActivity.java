package fi.metropolia.audiostory.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fi.metropolia.audiostory.R;
import fi.metropolia.audiostory.filestorage.Folder;
import fi.metropolia.audiostory.filestorage.RawFile;
import fi.metropolia.audiostory.filestorage.RawToWavConverter;
import fi.metropolia.audiostory.filestorage.WavFile;
import fi.metropolia.audiostory.museum.Constant;
import fi.metropolia.audiostory.threads.PlayThread;
import fi.metropolia.audiostory.threads.RecordThread;

public class RecordingActivity extends AppCompatActivity {

    private static final int RECORD_PERMISSIONS = 24;
    private static final int MAX_DURATION = 10000;

    private ImageView ivRecord, ivDelete, ivSave, ivPlayStop;
    private LinearLayout llContinue;
    private Button btnContinue;
    private TextView tvMessage;
    private EditText etTitle;

    private Handler uiHandler;
    private Handler maxDurationHandler;

    private Runnable durationRunnable;

    private PlayThread playThread = null;
    private RecordThread recordThread = null;

    private RawToWavConverter rawToWavConverter;
    private RawFile rawFile = null;
    private Folder folder = null;
    private WavFile wavFile = null;
    
    private String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onStart() {
        super.onStart();
        checkForPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isExternalStorageWritable()) {
            Toast.makeText(this, "External storage is not available", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        
        init();
        initViews();
        initHandler();
    }

    private void initHandler() {
        uiHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {

                if(msg.what == Constant.MESSAGE_PLAY_FINISH){
                    ivPlayStop.setSelected(false);
                    changetoDeletePlaySaveState();
                }
                return false;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode){
            case RECORD_PERMISSIONS:
                if (grantResults.length > 0){
                    for (int grantResult : grantResults) {
                        if (grantResult == PackageManager.PERMISSION_DENIED) {
                            Toast.makeText(this, "All permissions must be allowed to continue", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                }
                break;
        }
    }

    private void checkForPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();

        for(String p : permissions){
            result = ContextCompat.checkSelfPermission(this, p);
            if(result != PackageManager.PERMISSION_GRANTED){
                listPermissionsNeeded.add(p);
            }
        }

        if(!listPermissionsNeeded.isEmpty()){
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), RECORD_PERMISSIONS);
        }
    }

    private void init() {
        folder = new Folder(getApplicationContext());
        rawFile = new RawFile(folder);
        rawToWavConverter = new RawToWavConverter(getApplicationContext());
    }

    private void initViews() {
        ivPlayStop = (ImageView)findViewById(R.id.iv_recording_play_stop);
        ivRecord = (ImageView)findViewById(R.id.iv_recording_record);
        tvMessage = (TextView)findViewById(R.id.tv_recording_info);
        ivSave = (ImageView)findViewById(R.id.iv_recording_save);
        ivDelete = (ImageView)findViewById(R.id.iv_recording_delete);
        llContinue = (LinearLayout)findViewById(R.id.ll_recording_continue);
        btnContinue = (Button)findViewById(R.id.btn_recording_continue);
        etTitle = (EditText)findViewById(R.id.et_recording_title);
    }



    public void onRecordingClick(final View v){
        if(!v.isSelected()){
            v.setSelected(true);
            startRecording();
            changeToRecordingState();

            maxDurationHandler = new Handler();

            durationRunnable = new Runnable() {
                @Override
                public void run() {
                    if(recordThread.isRecording())
                        stopRecording(v);
                }
            };

            maxDurationHandler.postDelayed(durationRunnable, MAX_DURATION);

        }else {

            stopRecording(v);
            maxDurationHandler.removeCallbacks(durationRunnable);

        }
    }


    private void stopRecording(View v) {
        v.setSelected(false);
        recordThread.stopRecording();
        changetoDeletePlaySaveState();

    }

    private void startRecording() {

        rawFile.createNewFile();
        recordThread = new RecordThread(rawFile);
        recordThread.start();
    }

    public void onDeleteClick(View v){
        changeToInitialState();

    }


    public void onSaveClick(View v){

        changeToSaveState();

    }

    public void OnContinueClick(View v){
        if(etTitle.getText().length() != 0){
            convWav();
            Bundle b = getIntent().getBundleExtra(Constant.EXTRA_BUNDLE_DATA);
            b.putString(Constant.BUNDLE_STORY_TITLE, etTitle.getText().toString());
            b.putString(Constant.BUNDLE_WAV_PATH, wavFile.getWavFilePath());

            Intent i = new Intent(this, FeelingsActivity.class);
            i.putExtra(Constant.EXTRA_BUNDLE_DATA, b);
            startActivity(i);

        }else {
            Toast.makeText(this, "Enter a title first!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onPlayStopClick(View v){
        //play record
        if(!v.isSelected()){
            v.setSelected(true);
            startPlaying();
            changeToPlayState();
        }else {
            //stop playing record
            v.setSelected(false);
            stopPlaying();
            changetoDeletePlaySaveState();
        }
    }

    private void startPlaying() {
        playThread = new PlayThread(rawFile, uiHandler);
        playThread.start();
    }


    private void stopPlaying(){
        if(playThread.isPlaying()){
            playThread.stopPlaying();
        }
    }


    public void convWav(){
        wavFile = new WavFile(folder, etTitle.getText().toString().replaceAll("\\s+", ""));
        rawToWavConverter.setFilePaths(rawFile.getRawFilePath(),wavFile.getWavFilePath());
        rawToWavConverter.convert();
    }



    /** Changes message and  restores views to initial */
    private void changeToInitialState() {
        tvMessage.setText(R.string.recording_tv_tap_record);

        ivRecord.setVisibility(View.VISIBLE);
        ivDelete.setVisibility(View.INVISIBLE);
        ivPlayStop.setVisibility(View.INVISIBLE);
        ivSave.setVisibility(View.INVISIBLE);
        llContinue.setVisibility(View.INVISIBLE);
    }

    /** Changes message and sets only delete, play, save views visible **/
    private void changetoDeletePlaySaveState() {
        tvMessage.setText(R.string.recording_tv_tap_delete_play_save);

        ivRecord.setVisibility(View.INVISIBLE);
        ivDelete.setVisibility(View.VISIBLE);
        ivPlayStop.setVisibility(View.VISIBLE);
        ivSave.setVisibility(View.VISIBLE);
        llContinue.setVisibility(View.INVISIBLE);
    }

    /** Changes message and sets only record view visible */
    private void changeToRecordingState(){
        tvMessage.setText(R.string.recording_tv_tap_stop);

        ivRecord.setVisibility(View.VISIBLE);
        ivDelete.setVisibility(View.INVISIBLE);
        ivPlayStop.setVisibility(View.INVISIBLE);
        ivDelete.setVisibility(View.INVISIBLE);
        ivSave.setVisibility(View.INVISIBLE);
        llContinue.setVisibility(View.INVISIBLE);
    }

    /** Changes message and sets only play view visible */
    private void changeToPlayState(){
        tvMessage.setText(R.string.recording_tv_playing_stop);

        ivRecord.setVisibility(View.INVISIBLE);
        ivDelete.setVisibility(View.INVISIBLE);
        ivPlayStop.setVisibility(View.VISIBLE);
        ivDelete.setVisibility(View.INVISIBLE);
        ivSave.setVisibility(View.INVISIBLE);
        llContinue.setVisibility(View.INVISIBLE);

    }

    private void changeToSaveState(){
        tvMessage.setText(R.string.recording_tv_tap_record);

        ivRecord.setVisibility(View.VISIBLE);
        ivDelete.setVisibility(View.INVISIBLE);
        ivPlayStop.setVisibility(View.INVISIBLE);
        ivDelete.setVisibility(View.INVISIBLE);
        ivSave.setVisibility(View.INVISIBLE);
        llContinue.setVisibility(View.VISIBLE);
    }


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
