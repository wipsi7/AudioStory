package fi.metropolia.audiostory.activities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import fi.metropolia.audiostory.Login.LoginResponse;
import fi.metropolia.audiostory.R;
import fi.metropolia.audiostory.filestorage.ImageStorage;
import fi.metropolia.audiostory.museum.Artifact;
import fi.metropolia.audiostory.museum.Connectivity;
import fi.metropolia.audiostory.museum.Constant;
import fi.metropolia.audiostory.museum.Credentials;
import fi.metropolia.audiostory.nfc.NfcController;
import fi.metropolia.audiostory.retrofit.ImageRetrofit;
import fi.metropolia.audiostory.retrofit.LoginRetrofit;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {


    private static final String DEBUG_TAG = "MainActivity";
    private static final String PACKAGE_NAME = "metropolia.audiostory";
    private static final int API_KEY_LENGTH = 128;

    private PendingIntent pendingIntent;
    private NfcController nfcController;

    private LinearLayout llButtonsContainer;
    private TextView tvArtifactTitle;
    private ImageView iv_main_artifact_image;
    private AVLoadingIndicatorView indicatorView;
    private GifImageView gifImageView;

    private LoginRetrofit loginRetrofit;
    private ImageRetrofit imageRetrofit;

    private ImageStorage imageStorage;
    private Artifact artifact = null;
    private Credentials currentCredentials = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        init();
        initLoginRetrofit();
        initImageRetrofit();
    }

    /** Deals with image returned from server**/
    private void initImageRetrofit() {
        imageRetrofit = new ImageRetrofit(getApplicationContext());
        imageRetrofit.setResponseListener(new ImageRetrofit.ResponseListener() {
            @Override
            public void onResponse(Bitmap bm) {
                indicatorView.hide();
                tvArtifactTitle.setText(artifact.getArtifactName());
                iv_main_artifact_image.setImageBitmap(bm);
                llButtonsContainer.setVisibility(View.VISIBLE);

                imageStorage.store(bm);
            }
        });
    }

    //gets apiKey, if failed, credentials will equal to null
    private void initLoginRetrofit() {
        loginRetrofit = new LoginRetrofit(getApplicationContext());
        loginRetrofit.setResponseListener(new LoginRetrofit.ResponseListener() {

            @Override
            public void onResponse(LoginResponse loginResponse) {
                indicatorView.hide();
                tvArtifactTitle.setText(artifact.getArtifactName());

                // If successfully retrieved API KEY, make buttons visible and set api key
                if(loginResponse.getApi_key().length() == API_KEY_LENGTH){
                    currentCredentials.setApiKey(loginResponse.getApi_key());

                    imageRetrofit.setKeyAndId(currentCredentials.getApiKey(),currentCredentials.getCollectionID());
                    imageRetrofit.start();
                }else {

                    Toast.makeText(getApplicationContext(), "Wrong username or password on Tag", Toast.LENGTH_SHORT).show();
                    currentCredentials = null;
                    llButtonsContainer.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void initViews() {
        iv_main_artifact_image = (ImageView) findViewById(R.id.iv_main_artifact_image);
        llButtonsContainer = (LinearLayout)findViewById(R.id.ll_main_buttons_container);
        tvArtifactTitle = (TextView)findViewById(R.id.tv_main_artifact);
        indicatorView = (AVLoadingIndicatorView)findViewById(R.id.avi_main_indicator);
        gifImageView = (GifImageView)findViewById(R.id.gif_main_nfc);
    }


    private void init() {
        nfcController = new NfcController(this);
        artifact = new Artifact();
        imageStorage = new ImageStorage(this);

        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
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



    /** Checks if internet is working, if is sets title of artifact. Acquires new API key if credentials are not same */
    private void proceed(ArrayList<String> records) {
        if(Connectivity.isNetworkAvailable(getSystemService(Context.CONNECTIVITY_SERVICE))) {
            artifact.setArtifactName(records.get(Constant.ARTIFACT_INDEX));




            Credentials newCredentials = new Credentials(records);

            // if same credentials
            if(areSameCredentials(currentCredentials, newCredentials)){
                Log.d(DEBUG_TAG, "Same credentials");
                if(!currentCredentials.getCollectionID().equals(newCredentials.getCollectionID())){
                    Log.d(DEBUG_TAG, "Different Collection ID, updating current ID");
                    startLoading();
                    currentCredentials.setCollectionID(newCredentials.getCollectionID());
                    imageRetrofit.setCollectionId(currentCredentials.getCollectionID());
                    imageRetrofit.start();
                }
            }
            else {
                Log.d(DEBUG_TAG, "Different credentials");
                startLoading();
                acquireKey(records);
            }
        }else {
            Toast.makeText(getBaseContext(), "You are not connected to internet", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean areSameCredentials(Credentials currentCredentials, Credentials newCredentials) {
        return currentCredentials != null
                && currentCredentials.getUserName().equals(newCredentials.getUserName())
                && currentCredentials.getPassword().equals(newCredentials.getPassword());
    }



    private void acquireKey(ArrayList<String> records) {

        Log.d(DEBUG_TAG, "Records size is: " + records.size());
        currentCredentials = new Credentials(records);

        loginRetrofit.setLoginCredentials(currentCredentials.getUserName(), currentCredentials.getPassword());
        loginRetrofit.start();

    }


    public void onRecordClick(View v){
        Bundle bundle = createBundle(Constant.BUNDLE_RECORD);

        Intent i = new Intent(this, LoginActivity.class);
        i.putExtra(Constant.EXTRA_BUNDLE_DATA, bundle);
        startActivity(i);
    }

    public void onListenClick(View v){
        Bundle bundle = createBundle(Constant.BUNDLE_LISTEN);

        Intent i = new Intent(this, FeelingsActivity.class);
        i.putExtra(Constant.EXTRA_BUNDLE_DATA, bundle);
        startActivity(i);
    }

    @NonNull
    private Bundle createBundle(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.BUNDLE_TYPE, type);
        bundle.putString(Constant.BUNDLE_USER, currentCredentials.getUserName());
        bundle.putString(Constant.BUNDLE_PASS, currentCredentials.getPassword());
        bundle.putString(Constant.BUNDLE_ID, currentCredentials.getCollectionID());
        bundle.putString(Constant.BUNDLE_API, currentCredentials.getApiKey());
        bundle.putString(Constant.BUNDLE_ARTIFACT, artifact.getArtifactName());

        return bundle;
    }

    public void startLoading(){
        tvArtifactTitle.setText("Please wait...");
        indicatorView.smoothToShow();
        gifImageView.setVisibility(View.INVISIBLE);
    }

}