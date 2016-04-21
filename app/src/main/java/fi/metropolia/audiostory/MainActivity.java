package fi.metropolia.audiostory;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private PendingIntent pendingIntent;
    private IntentFilter nDef;
    private IntentFilter[] intentFilterArray;
    private NfcAdapter nfcAdapter;
    private String[][] techListArray;
    private NdefMessage[] msgs;
    private NdefRecord[] ndefRecords;
    private ArrayList<String> records;



    private static String NFC_TAG = "nfcTag";
    private static int TOKEN_INDEX = 0;
    private static int NAME_INDEX = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isNfcAvailable()){

            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilterArray, techListArray);
        }else {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            msgs = retrieveNdefMessage(intent);

            if(msgs != null){
                ndefRecords = msgs[0].getRecords();
                readRecords();
            }

        }

    }

    private void readRecords() {

        records.clear();

        byte[] payload = ndefRecords[0].getPayload();
        byte[] langBytes = getResources().getConfiguration().locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

        Charset textEncoding = Charset.forName("UTF-8");
        records.add(TOKEN_INDEX, new String(payload, langBytes.length + 1, payload.length - langBytes.length - 1, textEncoding));
        Toast.makeText(this, records.get(TOKEN_INDEX), Toast.LENGTH_SHORT).show();

    }

    private NdefMessage[] retrieveNdefMessage(Intent intent){
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage[] ndefMessages;
        if(rawMsgs == null){
            return null;
        }

        ndefMessages = new NdefMessage[rawMsgs.length];
        for(int i = 0; i < rawMsgs.length; i++){
            ndefMessages[i] = (NdefMessage)rawMsgs[i];
        }
        return ndefMessages;
    }

    private void init() {

        records = new ArrayList<String>();

        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        createNdefFilter();

        intentFilterArray = new IntentFilter[]{nDef};
        techListArray = new String[][]{new String[] { Ndef.class.getName()}};

    }

    private boolean isNfcAvailable() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter != null && nfcAdapter.isEnabled()){
            return true;
        }else {
            Toast.makeText(this,"NFC not available", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void createNdefFilter() {
        nDef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            nDef.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            Log.e(NFC_TAG, "error: Malformed data type");
        }
    }
}
