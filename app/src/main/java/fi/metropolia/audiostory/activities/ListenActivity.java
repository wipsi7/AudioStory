package fi.metropolia.audiostory.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import fi.metropolia.audiostory.R;
import fi.metropolia.audiostory.adapters.ListeningAdapter;
import fi.metropolia.audiostory.interfaces.SearchApi;
import fi.metropolia.audiostory.museum.Constant;
import fi.metropolia.audiostory.museum.ListeningList;
import fi.metropolia.audiostory.museum.StoryPlayer;
import fi.metropolia.audiostory.search.SearchResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ListenActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "ListenActivity";

    private String key, id, artifactName;
    private String[] tags;
    private ArrayList<SearchResponse> filteredArrayList;

    private ListView lvList;
    private ListeningAdapter listeningAdapter;
    private  StoryPlayer storyPlayer;
    private int oldPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);

        initViews();
        initRequestFieldsForRetrofit();
        initRetrofit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        storyPlayer.release();
        storyPlayer = null;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        storyPlayer = new StoryPlayer(getApplicationContext(), filteredArrayList);
    }

    private void initViews() {
        lvList = (ListView)findViewById(R.id.listening_listview);
    }

    private void initRequestFieldsForRetrofit() {
        Bundle b = getIntent().getBundleExtra(Constant.EXTRA_BUNDLE_DATA);
        key = b.getString(Constant.BUNDLE_API);
        id = b.getString(Constant.BUNDLE_ID);
        artifactName = b.getString(Constant.BUNDLE_ARTIFACT);
        tags = b.getStringArray(Constant.BUNDLE_FEELINGS);
    }


    private void initRetrofit(){
/*        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);*/

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://resourcespace.tekniikanmuseo.fi/")
                .addConverterFactory(GsonConverterFactory.create())
/*                .client(httpClient.build())*/
                .build();

        SearchApi service = retrofit.create(SearchApi.class);
        Call<SearchResponse[][]> responseCall = service.getDataList(key, id, artifactName, "true");

        responseCall.enqueue(new Callback<SearchResponse[][]>() {
            @Override
            public void onResponse(Call<SearchResponse[][]> call, Response<SearchResponse[][]> response) {
                Log.d(DEBUG_TAG, "Successfully retrieved full list");
                ListeningList listeningList = new ListeningList(response.body(), tags);
                filteredArrayList = listeningList.returnFilteredList();

                Log.i(DEBUG_TAG, "FilteredArrayList size: " + filteredArrayList.size());

                storyPlayer = new StoryPlayer(getApplicationContext(), filteredArrayList);

                listeningAdapter = new ListeningAdapter(getApplicationContext(), filteredArrayList);
                lvList.setAdapter(listeningAdapter);
                lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d(DEBUG_TAG, "onItemClick called, position is " + position + " and id is " + id);
                        Log.d(DEBUG_TAG, "count of child's in parent " + parent.getChildCount());
                        Log.d(DEBUG_TAG, "listeningAdapter" + listeningAdapter.getCount());

                        /*ConstraintLayout layout = (ConstraintLayout) view;
                        ImageView iv = (ImageView)layout.findViewById(R.id.iv_item_playstop);

                        if(oldPosition != position && storyPlayer.isPlaying()){
                            storyPlayer.stop();
                            storyPlayer.setPosition(position);
                            storyPlayer.setView(iv);
                            storyPlayer.start();
                        }else if(!iv.isSelected()) {

                            storyPlayer.setPosition(position);
                            storyPlayer.setView(iv);
                            storyPlayer.start();
                        }else {
                            storyPlayer.stop();
                        }

                        oldPosition = position;*/
                    }
                });
            }

            @Override
            public void onFailure(Call<SearchResponse[][]> call, Throwable t) {
                Log.d(DEBUG_TAG, "Failed: " + t.getMessage());
            }
        });

    }
}
