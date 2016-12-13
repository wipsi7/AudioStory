package fi.metropolia.audiostory.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import fi.metropolia.audiostory.R;
import fi.metropolia.audiostory.interfaces.UploadApi;
import fi.metropolia.audiostory.museum.Constant;
import fi.metropolia.audiostory.upload.UploadData;
import fi.metropolia.audiostory.upload.UploadResponse;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "UploadActivity";

    private TextView tvFeelings, tvStoryTitle;
    private Button btnUpload;
    private CheckBox cbDisclaimer;

    private UploadData uploadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        init();
        initViews();
    }

    private void initViews() {
        tvFeelings = (TextView)findViewById(R.id.tv_upload_feelings);
        tvStoryTitle = (TextView)findViewById(R.id.tv_upload_title);
        btnUpload = (Button)findViewById(R.id.btn_upload_upload);
        cbDisclaimer = (CheckBox)findViewById(R.id.cb_upload_disclaimer);

        btnUpload.setEnabled(false);

        tvFeelings.setText(uploadData.getTags());
        tvStoryTitle.setText(uploadData.getTitle());

        cbDisclaimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox)v;
                if(cb.isChecked()){
                    btnUpload.setEnabled(true);
                }else {
                    btnUpload.setEnabled(false);
                }
            }
        });
    }

    private void init() {
        Bundle b = getIntent().getBundleExtra(Constant.EXTRA_BUNDLE_DATA);
        uploadData = new UploadData();
        uploadData.setApiKey(b.getString(Constant.BUNDLE_API));
        uploadData.setCollectionId(b.getString(Constant.BUNDLE_ID));
        uploadData.setArtifact(b.getString(Constant.BUNDLE_ARTIFACT));
        uploadData.setTags(b.getStringArray(Constant.BUNDLE_FEELINGS));
        uploadData.setTitle(b.getString(Constant.BUNDLE_STORY_TITLE));

        String path = b.getString(Constant.BUNDLE_WAV_PATH);

        File file = new File(path);
        uploadData.setUploadFile(file);
        uploadData.setOriginalFileName(file.getName());


    }


    public void onUploadClick(View v){
        Log.d(DEBUG_TAG, "API is: " + uploadData.getApiKey());
        Log.d(DEBUG_TAG, "Collection ID is: " + uploadData.getCollectionId());
        Log.d(DEBUG_TAG, "File path is " + uploadData.getUploadFile().getAbsolutePath());

        upload();
    }

    private void upload() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        RequestBody key = RequestBody.create(MediaType.parse("multipart/form-data"), uploadData.getApiKey());
        RequestBody file = RequestBody.create(MediaType.parse("audio/wav"), uploadData.getUploadFile());
        RequestBody collection = RequestBody.create(MediaType.parse("multipart/form-data"), uploadData.getCollectionId());
        RequestBody resourcetype = RequestBody.create(MediaType.parse("multipart/form-data"), uploadData.getResourceType());
        RequestBody artifact = RequestBody.create(MediaType.parse("multipart/form-data"), uploadData.getArtifact());
        RequestBody tags = RequestBody.create(MediaType.parse("multipart/form-data"), uploadData.getTags());
        RequestBody title = RequestBody.create(MediaType.parse("multipart/form-data"), uploadData.getTitle());
        RequestBody category = RequestBody.create(MediaType.parse("multipart/form-data"), uploadData.getCategory());
        RequestBody originalFileName = RequestBody.create(MediaType.parse("multipart/form-data"), uploadData.getOriginalFileName());

        MultipartBody.Part fileBody =
                MultipartBody.Part.createFormData("userfile", uploadData.getUploadFile().getName(), file);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://resourcespace.tekniikanmuseo.fi/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        UploadApi uploadApi = retrofit.create(UploadApi.class);

        Call<UploadResponse> call = uploadApi.upload(key,resourcetype, collection, artifact,tags, title, category, originalFileName, fileBody);

        call.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {

                UploadResponse uploadResponse = response.body();
                Toast.makeText(getApplicationContext(), uploadResponse.getResponse(), Toast.LENGTH_SHORT).show();
                Log.d(DEBUG_TAG, uploadResponse.getResponse());
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                Log.d(DEBUG_TAG, "failed: " + t.getMessage());
            }
        });

    }
}
