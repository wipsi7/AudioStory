package fi.metropolia.audiostory.retrofit;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;

import fi.metropolia.audiostory.interfaces.ImageApi;
import fi.metropolia.audiostory.museum.Constant;
import fi.metropolia.audiostory.search.ImageResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImageRetrofit {

    public interface ResponseListener{
        void onResponse(Bitmap bm);
    }

    private final static String DEBUG_TAG = "ImageRetrofit";

    private ResponseListener responseListener;
    private Callback<ImageResponse[][]> imageResponseCallback;
    private Context context;
    private String apiKey, collectionId;

    private ImageApi imageApi;
    private Call<ImageResponse[][]> imageResponseCall;

    public void setResponseListener(ResponseListener responseListener){
        this.responseListener = responseListener;
    }

    public ImageRetrofit(Context context){
        this.context = context;

        initRetrofitCall();
    }

    public void setKeyAndId(String apiKey, String collectionId) {
        this.apiKey = apiKey;
        this.collectionId = collectionId;
    }

    public void setCollectionId(String collectionId){
        this.collectionId = collectionId;
    }


    private void initRetrofitCall() {
/*        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);*/

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
/*                .client(httpClient.build())*/
                .build();

        imageApi = retrofit.create(ImageApi.class);


        initImageresponseCallback();
    }


    private void initImageresponseCallback(){
        imageResponseCallback = new Callback<ImageResponse[][]>() {
            @Override
            public void onResponse(Call<ImageResponse[][]> call, Response<ImageResponse[][]> response) {
                ImageResponse[][] imageResponse = response.body();
                Log.d(DEBUG_TAG, "ImageResponse length is " + response.body().length);
                if (imageResponse.length == 1) {

                    new DownloadImageTask().execute(imageResponse[0][0].getDownloadLink());
                } else {
                    Log.e(DEBUG_TAG, "Images do not exists or more than one image exists");
                }
            }

            @Override
            public void onFailure(Call<ImageResponse[][]> call, Throwable t) {
                Log.d(DEBUG_TAG, "ERROR: " + t.getMessage());
            }
        };

    }

    public void start(){
        imageResponseCall = imageApi.getImageLink(apiKey, collectionId, "image", "true");

        if(imageResponseCall.isExecuted()){
            Log.d(DEBUG_TAG, "imageResponseCall already executed, cloning");
            imageResponseCall = imageResponseCall.clone();
        }


        imageResponseCall.enqueue(imageResponseCallback);
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bm = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bm = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bm;
        }

        protected void onPostExecute(Bitmap result) {


            Log.d(DEBUG_TAG, "Image Task Completed");
            responseListener.onResponse(result);
        }

    }
}
