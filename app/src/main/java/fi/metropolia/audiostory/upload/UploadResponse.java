package fi.metropolia.audiostory.upload;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UploadResponse {

    @SerializedName("Success")
    @Expose
    private String response = null;

    public String getResponse() {
        return response;
    }
}
