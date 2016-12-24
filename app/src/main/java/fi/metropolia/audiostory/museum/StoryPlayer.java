package fi.metropolia.audiostory.museum;


import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import fi.metropolia.audiostory.R;
import fi.metropolia.audiostory.search.SearchResponse;

public class StoryPlayer {

    private Context context;
    private ArrayList<SearchResponse> stories;
    private ImageView iv;
    private int position;

    public StoryPlayer(Context context, ArrayList<SearchResponse> stories){

        this.context = context;
        this.stories = stories;

    }


    public void setView(View v){

        iv = (ImageView)v.findViewById(R.id.iv_item_playstop);

    }

    public void  setPosition(int position){
        this.position = position;
    }
}
