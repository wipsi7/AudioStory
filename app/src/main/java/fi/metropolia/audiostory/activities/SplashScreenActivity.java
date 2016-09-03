package fi.metropolia.audiostory.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import fi.metropolia.audiostory.MainActivity;
import fi.metropolia.audiostory.R;

public class SplashScreenActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        int timeInMillSec = 2000;
        Intent intent = new Intent(this, MainActivity.class);

        startActivityAfterDelay(intent, timeInMillSec);
    }


    /** Starts activity after given time and finishes current activity.
     * @param intent Intent which contains activity to start.
     * @param timeInMillSec Time in milliseconds to delay
     */
    private void startActivityAfterDelay(final Intent intent ,int timeInMillSec) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, timeInMillSec);
    }
}
