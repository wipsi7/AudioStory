package fi.metropolia.audiostory.retrofit;


import android.content.Context;
import android.support.annotation.CallSuper;
import android.widget.Toast;

import fi.metropolia.audiostory.Login.LoginRequest;
import fi.metropolia.audiostory.Login.LoginResponse;
import fi.metropolia.audiostory.interfaces.LoginApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginRetrofit {

    public interface ResponseListener{
        void onResponse(LoginResponse loginResponse);
    }

    private Call<LoginResponse> loginResponseCall;
    private Callback<LoginResponse> loginResponseCallback;
    private LoginApi service;
    private LoginRequest loginRequest;
    private LoginResponse loginResponse;
    private ResponseListener responseListener;

    private Context context;

    public void setResponseListener(ResponseListener responseListener){
        this.responseListener = responseListener;
    }


    public LoginRetrofit(Context context){

        this.context = context;
        loginRequest = new LoginRequest();

        initRetrofitCall();
        initRetrofitCallback();
    }


    @CallSuper
    public void setLoginCredentials(String username, String password ){
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
    }


    private void initRetrofitCallback() {
        // data coming back from server is handled here
        loginResponseCallback = new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                loginResponse = response.body();
                responseListener.onResponse(loginResponse);

            }

            @Override /** Called when connection to server fails **/
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(context, "Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void initRetrofitCall() {

/*      HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);*/

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://resourcespace.tekniikanmuseo.fi/")
                .addConverterFactory(GsonConverterFactory.create())
/*                .client(httpClient.build())*/
                .build();
        service = retrofit.create(LoginApi.class);
    }

    public void start(){
        loginResponseCall = service.getApiKey(loginRequest);

        if(loginResponseCall.isExecuted()){
            loginResponseCall.clone();
        }


        // Starts networking with server Asynchronously
        loginResponseCall.enqueue(loginResponseCallback);
    }
}
