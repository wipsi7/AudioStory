package fi.metropolia.audiostory.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import fi.metropolia.audiostory.R;
import fi.metropolia.audiostory.visual.Feeling;

public class FeelingsActivity extends AppCompatActivity {


    private final static  String DEBUG_TAG = "FeelingsActivity";
    private LinearLayout goodFeelingLayout, badFeelingLayout;
    private ArrayList<Feeling> feelingsList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feelings);
        init();
    }

    private void init() {
        goodFeelingLayout = (LinearLayout)findViewById(R.id.good_feelings);
        badFeelingLayout = (LinearLayout)findViewById(R.id.bad_feelings);

        feelingsList = new ArrayList<Feeling>();
        initList();
    }

    private void initList() {
        for(int i = 0; i < goodFeelingLayout.getChildCount(); i++){
            Feeling feeling = new Feeling(getApplicationContext(), (ImageView)goodFeelingLayout.getChildAt(i), (ImageView)badFeelingLayout.getChildAt(i));
            feelingsList.add(feeling);
        }
    }

    public void flipViews(View v){
        for(int i = 0; i < feelingsList.size(); i++){
            feelingsList.get(i).flip();

        }

    }

    public void onFeelingClick(View v){

        Log.d(DEBUG_TAG, "Tag is: " + v.getTag().toString());
        if(!v.isSelected()){
            v.setSelected(true);
        }else {
            v.setSelected(false);
        }
    }
}
