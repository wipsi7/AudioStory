package fi.metropolia.audiostory.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fi.metropolia.audiostory.R;
import fi.metropolia.audiostory.filestorage.Folder;
import fi.metropolia.audiostory.filestorage.RawFile;
import fi.metropolia.audiostory.threads.PlayThread;
import fi.metropolia.audiostory.threads.RecordThread;

public class RecordingActivity extends AppCompatActivity {

    private static final int RECORD_PERMISSIONS = 24;
    private ImageView recordView, pauseView, deleteView, saveView;
    private LinearLayout uploadLayout;
    private Button continueBtn;
    private TextView recordTextView;
    private boolean saveEnabled;


    private PlayThread playThread = null;
    private RecordThread recordThread = null;
    private RawFile rawFile = null;
    private Folder folder = null;
    
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode){
            case RECORD_PERMISSIONS:
                if (grantResults.length > 0){
                    for(int i = 0 ; i < grantResults.length; i++){
                        if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                            Toast.makeText(this, "All permissions must be allowed to continue", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                }
                break;
        }
    }

    private void init() {
        folder = new Folder(getApplicationContext());
        rawFile = new RawFile(folder);
    }

    private void initViews() {
        recordView = (ImageView)findViewById(R.id.recordButton);
        pauseView = (ImageView)findViewById(R.id.pauseButton);
        recordTextView = (TextView)findViewById(R.id.record_txt);
        saveView = (ImageView)findViewById(R.id.save_button);
        deleteView = (ImageView)findViewById(R.id.delete_button);
        uploadLayout = (LinearLayout)findViewById(R.id.uploadingLayout);
        continueBtn = (Button)findViewById(R.id.record_continue_button);
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


    public void onRecordingClick(View v){
        //starts recording
        if(!v.isSelected()){
            v.setSelected(true);
            recordTextView.setText(R.string.tap_pause_txt);
            setVisibilitiesOnRecording();
            rawFile.createNewFile();
            recordThread = new RecordThread(rawFile);
            recordThread.start();

        }else
        //continues recording
        {
            recordTextView.setText(R.string.tap_continue_txt);
            setVisibilitiesOnPause();

            if(!recordThread.isRecording()){
                recordThread.start();
            }

        }
    }


    public void onDeleteClick(View v){

        setVisibilityOnFinish();
    }




    public void onSaveClick(View v){
        setVisibilityonSave();
        setVisibilityOnFinish();

        saveEnabled = true;
    }


    public void onPlayClick(View v){
        if(!v.isSelected()){
            v.setSelected(true);
        }else {
            v.setSelected(false);
        }
    }


    /** A pause drawable, to continue recording */
    public void onPausedClick(View v){
        recordTextView.setText(R.string.tap_pause_txt);
        setVisibilitesForContinue();
        if(recordThread.isRecording()){
            recordThread.stopRecording();
        }


    }



    /** restores views to initial */
    private void setVisibilityOnFinish() {
        recordView.setSelected(false);
        recordTextView.setText(R.string.tap_record_txt);
        recordView.setVisibility(View.VISIBLE);
        pauseView.setVisibility(View.INVISIBLE);
        deleteView.setVisibility(View.INVISIBLE);
        saveView.setVisibility(View.INVISIBLE);
    }

    private void setVisibilityonSave(){
        uploadLayout.setVisibility(View.VISIBLE);
        continueBtn.setVisibility(View.VISIBLE);

    }

    /** method called when recording is on Pause */
    private void setVisibilitiesOnPause(){
        recordView.setVisibility(View.INVISIBLE);
        pauseView.setVisibility(View.VISIBLE);
        deleteView.setVisibility(View.VISIBLE);
        saveView.setVisibility(View.VISIBLE);
    }

    /** method called when recordView is selected  */
    private void setVisibilitiesOnRecording() {
        if(saveEnabled){
            uploadLayout.setVisibility(View.INVISIBLE);
            continueBtn.setVisibility(View.INVISIBLE);
        }
    }


    /** called when record is paused and user wants to continue recording */
    private void setVisibilitesForContinue() {
        pauseView.setVisibility(View.INVISIBLE);
        recordView.setVisibility(View.VISIBLE);
        deleteView.setVisibility(View.INVISIBLE);
        saveView.setVisibility(View.INVISIBLE);
    }


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
