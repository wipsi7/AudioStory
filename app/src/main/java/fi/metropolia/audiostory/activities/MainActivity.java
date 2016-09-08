package fi.metropolia.audiostory.activities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import fi.metropolia.audiostory.R;
import fi.metropolia.audiostory.Server.ServerConnection;
import fi.metropolia.audiostory.interfaces.AsyncResponse;
import fi.metropolia.audiostory.museum.Artifact;
import fi.metropolia.audiostory.museum.Constant;
import fi.metropolia.audiostory.museum.Credentials;
import fi.metropolia.audiostory.nfc.NfcController;
import fi.metropolia.audiostory.tasks.LoginTask;

public class MainActivity extends AppCompatActivity {


    private static String DEBUG_TAG = "MainActivity";
    private static String PACKAGE_NAME = "metropolia.audiostory";
    private static final int API_KEY_LENGTH = 128;

    private PendingIntent pendingIntent;
    private NfcController nfcController;

    private Artifact artifact = null;
    private Credentials currentCredentials, newCredentials = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(DEBUG_TAG, "in mainActivity");
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(nfcController.isNfcAvailable()){
            nfcController.getNfcAdapter().enableForegroundDispatch(this, pendingIntent, nfcController.getIntentFilterArray(), nfcController.getTechListArray());
        }else {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        nfcController.getNfcAdapter().disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            NdefMessage[] msgs = nfcController.retrieveNdefMessage(intent);

            if(msgs != null){
                // Currently only one Ndef message is sent
                NdefRecord[] ndefRecords = msgs[0].getRecords();
                if(ndefRecords.length == 5) {
                    ArrayList<String>  records = nfcController.readRecords(ndefRecords);
                    if(records.get(4).equals(PACKAGE_NAME)) {
                        Log.d(DEBUG_TAG, "Correct package");
                        proceed(records);
                    }
                }
            }
        }
    }


    private void proceed(ArrayList<String> records) {
        if(isNetworkAvailable()) {
            artifact.setArtifactName(records.get(Constant.ARTIFACT_INDEX));
            newCredentials = new Credentials(records);

            // if same credentials
            if(currentCredentials != null && currentCredentials.getUserName().equals(newCredentials.getUserName())){
                Log.d(DEBUG_TAG, "Same credentials");
            }
            else {
                acquireKey(records);
            }
        }else {
            Toast.makeText(getBaseContext(), "You are not connected to internet", Toast.LENGTH_SHORT).show();
        }
    }

    //gets apiKey, if failed, credentials will equal to null
    private void acquireKey(ArrayList<String> records) {

        currentCredentials = new Credentials(records);

        LoginTask loginTask = new LoginTask();
        loginTask.setOnLoginResult(new AsyncResponse() {

            @Override
            public void onProcessFinish(ServerConnection result) {
                int length = result.getApiKey().length();

                // If successfully retrieved API KEY
                if(length == API_KEY_LENGTH){

                    currentCredentials.setApiKey(result.getApiKey());
                        //Todo: go foward
                }
                else{
                    Toast.makeText(getBaseContext(), "Wrong username or password", Toast.LENGTH_SHORT).show();
                    currentCredentials = null;
                }
            }
        });
        loginTask.execute(currentCredentials.getUserName(), currentCredentials.getPassword());
    }


    private void init() {

        nfcController = new NfcController(this);
        artifact = new Artifact();

        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void onRecordClick(View v){
        Intent intent = new Intent(this, FeelingsActivity.class);
        startActivity(intent);
    }
}