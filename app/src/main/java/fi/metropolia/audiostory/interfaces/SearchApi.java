package fi.metropolia.audiostory.interfaces;


import fi.metropolia.audiostory.search.SearchResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchApi {

    @GET("plugins/api_audio_search/index.php/")
    Call<SearchResponse[][]> getDataList(
            @Query("key") String apiKey,
            @Query("collection") String collection,
            @Query("search") String story,
            @Query("link") boolean linkRequired
    );

}
