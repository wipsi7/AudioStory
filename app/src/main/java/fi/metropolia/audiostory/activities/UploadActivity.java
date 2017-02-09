package fi.metropolia.audiostory.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import fi.metropolia.audiostory.R;
import fi.metropolia.audiostory.filestorage.ImageStorage;
import fi.metropolia.audiostory.interfaces.UploadApi;
import fi.metropolia.audiostory.museum.ColorPicker;
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
    private ImageView ivArtifact;
    private ProgressDialog progressDialog;


    private UploadData uploadData;
    private String[] tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        init();
        initViews();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    private void initVisualFeelings(String[] tags) {


        ColorPicker picicker = new ColorPicker();
        LinearLayout llVisualLayout = (LinearLayout)findViewById(R.id.ll_upload_visual_feelings);
        int childViewCount = llVisualLayout.getChildCount();
        int tagsCount = tags.length;


        //makes all childViews gone of VisualLayout
        for(int i = 0; i < childViewCount; i++){
            llVisualLayout.getChildAt(i).setVisibility(View.GONE);
        }

        //makes VisualLayout child visibles by depenting number of tags and assign feeling color
        for(int i = 0; i < tagsCount; i++){
            String color = tags[i];
            color = picicker.getMatched(color);
            llVisualLayout.getChildAt(i).setBackgroundColor(Color.parseColor(color));
            llVisualLayout.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }

    private void initViews() {
        tvFeelings = (TextView)findViewById(R.id.tv_upload_feelings);
        tvStoryTitle = (TextView)findViewById(R.id.tv_upload_title);
        btnUpload = (Button)findViewById(R.id.btn_upload_upload);
        cbDisclaimer = (CheckBox)findViewById(R.id.cb_upload_disclaimer);
        ivArtifact = (ImageView)findViewById(R.id.iv_upload_banner);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Uploading ...");



        ImageStorage imageStorage = new ImageStorage(this);
        Bitmap bmArtifact = imageStorage.loadImage();
        if(bmArtifact != null){
            ivArtifact.setImageBitmap(bmArtifact);
        }


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
        uploadData.setDuration(b.getString(Constant.BUNDLE_DURATION));

        tags = b.getStringArray(Constant.BUNDLE_FEELINGS);
        initVisualFeelings(tags);
        uploadData.setTags(tags);
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
        progressDialog.show();
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
        RequestBody storyDuration = RequestBody.create(MediaType.parse("multipart/form-data"), uploadData.getDuration());

        MultipartBody.Part fileBody =
                MultipartBody.Part.createFormData("userfile", uploadData.getUploadFile().getName(), file);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://resourcespace.tekniikanmuseo.fi/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        UploadApi uploadApi = retrofit.create(UploadApi.class);

        Call<UploadResponse> call = uploadApi.upload(key,resourcetype, collection, artifact,tags, title, category, originalFileName,storyDuration , fileBody);

        call.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {

                UploadResponse uploadResponse = response.body();
                Toast.makeText(getApplicationContext(), uploadResponse.getResponse(), Toast.LENGTH_LONG).show();
                Log.d(DEBUG_TAG, uploadResponse.getResponse());

                finishUp();
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                Log.d(DEBUG_TAG, "failed: " + t.getMessage());
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Upload failed ", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /** Called on successfull upload */
    private void finishUp() {
        progressDialog.dismiss();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
