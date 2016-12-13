package fi.metropolia.audiostory.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import fi.metropolia.audiostory.R;
import fi.metropolia.audiostory.museum.Constant;

public class LoginActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "remember_prefs";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String CHECKED = "checked";


    private EditText etUser, etPass;
    private CheckBox cbRemember;

    private String user;
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        initViews();
        init();

        loadPreferences();
    }

    private void init() {
        Bundle bundle = getIntent().getBundleExtra(Constant.EXTRA_BUNDLE_DATA);
        user = bundle.getString(Constant.BUNDLE_USER);
        pass = bundle.getString(Constant.BUNDLE_PASS);

    }

    private void initViews() {
        etUser = (EditText)findViewById(R.id.et_login_user);
        etPass = (EditText)findViewById(R.id.et_login_pass);
        cbRemember = (CheckBox)findViewById(R.id.cb_login_remember);
    }


    public void onSignClick(View v){
        if(isFormValid()){
            saveMe();
            if(isAccountCorrect()){
                signIn();
            }
        }
    }

    private void saveMe() {
        if(cbRemember.isChecked()){
            savePreferences(user, pass, true);
        }else {
            savePreferences(null, null, false);
        }
    }

    private void signIn() {
        Bundle bundle = getIntent().getBundleExtra(Constant.EXTRA_BUNDLE_DATA);
        Intent i = new Intent(this, RecordingActivity.class);
        i.putExtra(Constant.EXTRA_BUNDLE_DATA, bundle);

        startActivity(i);
    }

    private boolean isAccountCorrect() {
        if(user.equals(etUser.getText().toString().trim()) && pass.equals(etPass.getText().toString().trim())){
            return true;
        }
        else {
            Toast.makeText(getApplicationContext(),R.string.login_toast_wrong_user_or_pass, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private boolean isFormValid(){
        boolean isUserEmpty = etUser.getText().toString().isEmpty();
        boolean isPassEmpty = etPass.getText().toString().isEmpty();
        if(isUserEmpty){
            etUser.setError(getString(R.string.login_error_field_emty));
        }
        if(isPassEmpty){
            etPass.setError(getString(R.string.login_error_field_emty));
        }
        return !(isPassEmpty || isUserEmpty);
    }

    public void savePreferences(String user, String pass , boolean remember_checkbox){
        getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
                .edit()
                .putString(PREF_USERNAME, user)
                .putString(PREF_PASSWORD, pass)
                .putBoolean(CHECKED, remember_checkbox)
                .apply();
    }

    private void loadPreferences() {
        SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        String username = pref.getString(PREF_USERNAME, null);
        String password = pref.getString(PREF_PASSWORD, null);
        boolean checked = pref.getBoolean(CHECKED, false);

        if(checked){
            etUser.setText(username);
            etPass.setText(password);
            cbRemember.setChecked(true);
        }
    }
}
