package fi.metropolia.audiostory.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

import fi.metropolia.audiostory.R;
import fi.metropolia.audiostory.adapters.ListeningAdapter;
import fi.metropolia.audiostory.interfaces.SearchApi;
import fi.metropolia.audiostory.museum.Constant;
import fi.metropolia.audiostory.museum.ListeningList;
import fi.metropolia.audiostory.search.SearchResponse;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ListenActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "ListenActivity";

    private String key, id, artifactName;
    private String[] tags;
    private ArrayList<SearchResponse> filteredList;

    private ListView lvList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);

        initViews();
        initFields();
        initRetrofit();
    }

    private void initViews() {
        lvList = (ListView)findViewById(R.id.listening_listview);
    }

    private void initFields() {
        Bundle b = getIntent().getBundleExtra(Constant.EXTRA_BUNDLE_DATA);
        key = b.getString(Constant.BUNDLE_API);
        id = b.getString(Constant.BUNDLE_ID);
        artifactName = b.getString(Constant.BUNDLE_ARTIFACT);
        tags = b.getStringArray(Constant.BUNDLE_FEELINGS);
    }


    private void initRetrofit(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://resourcespace.tekniikanmuseo.fi/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        SearchApi service = retrofit.create(SearchApi.class);

        Call<SearchResponse[][]> responseCall = service.getDataList(key, id, artifactName, "true");

        responseCall.enqueue(new Callback<SearchResponse[][]>() {
            @Override
            public void onResponse(Call<SearchResponse[][]> call, Response<SearchResponse[][]> response) {
                Log.d(DEBUG_TAG, "Succeed");
                ListeningList listeningList = new ListeningList(response.body(), tags);
                //TODO create UI for play
                //TODO get playList and implement in listview
                filteredList = new ArrayList<SearchResponse>();
                filteredList = listeningList.getList();

                ListeningAdapter listeningAdapter = new ListeningAdapter(getApplicationContext(), filteredList);
                lvList.setAdapter(listeningAdapter);
            }

            @Override
            public void onFailure(Call<SearchResponse[][]> call, Throwable t) {
                Log.d(DEBUG_TAG, "Failed: " + t.getMessage());
            }
        });

    }
}
