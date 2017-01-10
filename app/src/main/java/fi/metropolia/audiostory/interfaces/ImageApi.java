package fi.metropolia.audiostory.interfaces;


import fi.metropolia.audiostory.search.ImageResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ImageApi {

    @GET("/plugins/api_audio_search/index.php/")
    Call<ImageResponse[][]> getImageLink(
            @Query("key") String apiKey,
            @Query("collection") String collection,
            @Query("tag") String tag,
            @Query("link") String linkRequired
    );
}
