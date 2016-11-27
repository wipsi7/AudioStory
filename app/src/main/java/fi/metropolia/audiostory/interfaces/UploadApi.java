package fi.metropolia.audiostory.interfaces;


import fi.metropolia.audiostory.upload.UploadResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadApi {

    @Multipart
    @Headers({
            "Cache-Control: no-cache"
    })
    @POST("plugins/api_upload/")
    Call<UploadResponse> upload(
            @Part("key") RequestBody key,
            @Part("resourcetype") RequestBody resourcetype,
            @Part("collection") RequestBody collection,
            @Part("field80") RequestBody artifact,
            @Part("field74") RequestBody tags,
            @Part("field8") RequestBody title,
            @Part("field75") RequestBody category,
            @Part("originalfilename") RequestBody originalFileName,
            @Part MultipartBody.Part file);
}
