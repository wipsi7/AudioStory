package fi.metropolia.audiostory.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import fi.metropolia.audiostory.R;
import fi.metropolia.audiostory.visual.Feeling;

public class FeelingsActivity extends AppCompatActivity {


    private final static  String DEBUG_TAG = "FeelingsActivity";
    private final static  int MAX_SELECTED = 3;
    private LinearLayout goodFeelingLayout;
    private LinearLayout badFeelingLayout;
    private LinearLayout chosedViewsLayout;
    private int selectedCount = 0;
    private ArrayList<Feeling> feelingsList;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feelings);

    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        goodFeelingLayout = (LinearLayout)findViewById(R.id.good_feelings);
        badFeelingLayout = (LinearLayout)findViewById(R.id.bad_feelings);
        chosedViewsLayout = (LinearLayout)findViewById(R.id.choosed_views_layout);


        feelingsList = new ArrayList<>();
        initList();
    }

    private void initList() {
        for(int row_index = 0; row_index < goodFeelingLayout.getChildCount(); row_index++){
            LinearLayout tempGoodFeelingRow = (LinearLayout) goodFeelingLayout.getChildAt(row_index);
            LinearLayout tempBadFeelingRow = (LinearLayout) badFeelingLayout.getChildAt(row_index);
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

        ImageView imageView = (ImageView) chosedViewsLayout.findViewWithTag(v.getTag());
        if(imageView != null) {
            imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.choose));
            imageView.setTag("choose");
            imageView.setSelected(false);
        }else {
            Log.d(DEBUG_TAG, "Tag not found");
        }
    }

    private void addToChoosed(View v) {
        ImageView tempView = (ImageView)v;
        ImageView imageView;

        for(int i = 0; i < MAX_SELECTED; i++){
            imageView = (ImageView) chosedViewsLayout.getChildAt(i);
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
            Toast.makeText(this, R.string.select_toast, Toast.LENGTH_SHORT).show();
            return true;
        }
    }


    public void onContinueClick(View v){
        //TODO start info activity with extras(feelings)
        Intent intent = new Intent(this, RecordingActivity.class);
        startActivity(intent);
    }
}
