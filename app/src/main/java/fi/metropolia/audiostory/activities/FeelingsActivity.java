package fi.metropolia.audiostory.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fi.metropolia.audiostory.R;
import fi.metropolia.audiostory.museum.Constant;
import fi.metropolia.audiostory.visual.Feeling;

public class FeelingsActivity extends AppCompatActivity {


    private final static String DEBUG_TAG = "FeelingsActivity";
    private final static String CHOOSE = "choose";
    private final static  int MAX_SELECTED = 3;

    private LinearLayout llGoodFeeling, llBadFeeling, llChosed;
    private TextView tvStoryAbout;
    private int selectedCount = 0;
    private ArrayList<Feeling> feelingsList;
    private ArrayList<String> choosedList;
    private Bundle b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feelings);

        initviews();
        init();



    }

    private void initviews() {
        llGoodFeeling = (LinearLayout)findViewById(R.id.good_feelings);
        llBadFeeling = (LinearLayout)findViewById(R.id.bad_feelings);
        llChosed = (LinearLayout)findViewById(R.id.ll_feelings_choose);
        tvStoryAbout = (TextView)findViewById(R.id.tv_feelings_story_about);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void init() {

        b = getIntent().getBundleExtra(Constant.EXTRA_BUNDLE_DATA);
        int type = b.getInt(Constant.BUNDLE_TYPE);
        switch(type){
            case Constant.BUNDLE_RECORD:
                tvStoryAbout.setText(R.string.feelings_tv_story_about);
                break;

            case Constant.BUNDLE_LISTEN:
                tvStoryAbout.setText(R.string.feelings_tv_story_listen);
                break;
        }

        feelingsList = new ArrayList<>();
        choosedList = new ArrayList<>();
        initList();
    }

    private void initList() {
        for(int row_index = 0; row_index < llGoodFeeling.getChildCount(); row_index++){
            LinearLayout tempGoodFeelingRow = (LinearLayout) llGoodFeeling.getChildAt(row_index);
            LinearLayout tempBadFeelingRow = (LinearLayout) llBadFeeling.getChildAt(row_index);
            for(int view_index = 0; view_index < tempGoodFeelingRow.getChildCount(); view_index++){
                Feeling feeling = new Feeling(this, (ImageView) tempGoodFeelingRow.getChildAt(view_index), (ImageView) tempBadFeelingRow.getChildAt(view_index));
                feelingsList.add(feeling);
            }
        }
    }

    public void flipViews(View v){
        for(int i = 0; i < feelingsList.size(); i++){
            feelingsList.get(i).flip();

        }

    }

    public void onFeelingClick(View v){
        Log.d(DEBUG_TAG, "is activated: " + v.isActivated());
        Log.d(DEBUG_TAG, "is selected: " + v.isSelected());

        if(!v.isSelected()){
            if(!isMaxReached()) {
                selectedCount++;

                //changing state to selected
                v.setSelected(true);
                addToChoosed(v);
            }

        } else{
            selectedCount--;

            //changing state to not selected
            v.setSelected(false);

            removeFromChoosed(v);

        }
    }

    private void removeFromChoosed(View v) {

        ImageView imageView = (ImageView) llChosed.findViewWithTag(v.getTag());
        if(imageView != null) {
            imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.choose));
            imageView.setTag(CHOOSE);
            imageView.setSelected(false);
        }else {
            Log.d(DEBUG_TAG, "Tag not found");
        }
    }

    private void addToChoosed(View v) {
        ImageView tempView = (ImageView)v;
        ImageView imageView;

        for(int i = 0; i < MAX_SELECTED; i++){
            imageView = (ImageView) llChosed.getChildAt(i);
            if(!imageView.isSelected()){
                imageView.setImageDrawable(tempView.getDrawable());
                imageView.setTag(tempView.getTag());
                imageView.setSelected(true);
                break;
            }
        }

    }

    private boolean isMaxReached() {
        if( selectedCount < MAX_SELECTED){
            return false;
        }else {
            Toast.makeText(this, R.string.feelings_toast_select_max, Toast.LENGTH_SHORT).show();
            return true;
        }
    }


    /** Method called on continue button click**/
    public void onContinueClick(View v){
        if(selectedCount != 0){
            String[] selectedStringArray = getSelectedStringArray();


            b.putStringArray(Constant.BUNDLE_FEELINGS, selectedStringArray);

            int type = b.getInt(Constant.BUNDLE_TYPE);
            switch(type){
                case Constant.BUNDLE_RECORD:
                    Intent recordIntent = new Intent(this, UploadActivity.class);
                    recordIntent.putExtra(Constant.EXTRA_BUNDLE_DATA, b);
                    startActivity(recordIntent);
                    break;

                case Constant.BUNDLE_LISTEN:
                    Intent listenIntent = new Intent(this, ListenActivity.class);
                    listenIntent.putExtra(Constant.EXTRA_BUNDLE_DATA, b);
                    startActivity(listenIntent);
                    break;
            }


        }else {
            Toast.makeText(this, R.string.feelings_toast_select_min, Toast.LENGTH_SHORT).show();
        }

    }

    private String[] getSelectedStringArray() {
        String[] selectedArray = new String[selectedCount];
        int addIndex = 0;
        String temp;


        for(int i = 0; i < MAX_SELECTED; i++){
            temp = llChosed.getChildAt(i).getTag().toString();
            if(!temp.equals(CHOOSE)){
                selectedArray[addIndex] = temp;
                addIndex++;
            }
        }
        return selectedArray;
    }
}
