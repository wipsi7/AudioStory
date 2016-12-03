package fi.metropolia.audiostory.Login;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("api_key")
    @Expose
    private String api_key = null;

    public String getApi_key() {
        return api_key;
    }
}
