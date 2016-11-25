package fi.metropolia.audiostory.interfaces;


import fi.metropolia.audiostory.Login.LoginRequest;
import fi.metropolia.audiostory.Login.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginApi {

    @POST("plugins/api_auth/auth.php")
    Call<LoginResponse> getApiKey(@Body LoginRequest loginRequest);
}
