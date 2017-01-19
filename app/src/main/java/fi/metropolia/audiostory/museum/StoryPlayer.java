package fi.metropolia.audiostory.museum;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import fi.metropolia.audiostory.R;
import fi.metropolia.audiostory.search.SearchResponse;


/**Class where all method of media playing is performed, aswell responsible to change play/stop imageges accordingly**/
public class StoryPlayer {

    private static final String DEBUG_TAG = "StoryPlayer";
    private Context context;
    private ArrayList<SearchResponse> stories;
    private ImageView iv;
    private TextView tvTime;
    private int position;
    private MediaPlayer player;

    private MediaPlayer.OnCompletionListener completionListener;
    private MediaPlayer.OnPreparedListener preparedListener;

    private boolean preperad, preparing;

    public StoryPlayer(Context context, ArrayList<SearchResponse> stories){

        this.context = context;
        this.stories = stories;

        initPlayer();

    }

    private void initPlayer(){
        player = new MediaPlayer();

        completionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                iv.setSelected(false);
                player.reset();
            }
        };

        preparedListener = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                preperad = true;
                preparing = false;
                iv.setSelected(true);
                player.start();

            }
        };

    }

    public void setView(View v){

        iv = (ImageView)v.findViewById(R.id.iv_item_playstop);
        tvTime = (TextView)v.findViewById(R.id.tv_item_length);

    }

    public void  setPosition(int position){
        this.position = position;
    }


    public void start(){

        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(context, Uri.parse(stories.get(position).getDownloadLink()));
        } catch (IOException e) {
            Log.e(DEBUG_TAG, e.getMessage());
        }

        player.setOnCompletionListener(completionListener);
        player.setOnPreparedListener(preparedListener);

        preparing = true;
        player.prepareAsync();
    }

    public void release(){
        if(iv != null){
            iv.setSelected(false);
        }
        player.reset();
        player.release();
    }

    public void stop(){
        iv.setSelected(false);
        player.stop();
        player.reset();
        preperad = false;
    }


    public boolean isPlaying(){
        if(preperad){
            if(player.isPlaying()){
                return true;
            }
        }

        return false;
    }

    public boolean isPreparing(){
        return preparing;
    }
}
