package fi.metropolia.audiostory;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;

import fi.metropolia.audiostory.nfc.NfcController;

public class MainActivity extends AppCompatActivity {


    private static String DEBUG_TAG = "MainActivity";
    private static String PACKAGE_NAME = "metropolia.audiostory";

    private PendingIntent pendingIntent;

    private NfcController nfcController;
    private NdefRecord[] ndefRecords;
    private ArrayList<String> records;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                ndefRecords = msgs[0].getRecords();
                if(ndefRecords.length == 5) {
                    records = nfcController.readRecords(ndefRecords);
                    if(records.get(4).equals(PACKAGE_NAME)) {
                        Log.d(DEBUG_TAG, "Correct package");
                        procceed();
                    }
                }
            }
        }
    }

    private void procceed() {

        for (int i = 0; i < records.size(); i++) {
            Log.d(DEBUG_TAG, records.get(i));
        }
    }

    private void init() {

        nfcController = new NfcController(this);

        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
    }
}