package fi.metropolia.audiostory.search;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchResponse {

    @SerializedName("Title")
    @Expose
    private String title;

    @SerializedName("Original filename")
    @Expose
    private String originalFilename;

    @SerializedName("Tags")
    @Expose
    private String tags;

    @SerializedName("Category")
    @Expose
    private String category;

    @SerializedName("Source")
    @Expose
    private String source;

    @SerializedName("Creation date")
    @Expose
    private String creationDate;

    @SerializedName("File extension")
    @Expose
    private String fileExtension;

    @SerializedName("File size(KB)")
    @Expose
    private String fileSizeKB;

    @SerializedName("Created by")
    @Expose
    private String createdBy;

    @SerializedName("Collection name")
    @Expose
    private String collectionName;

    @SerializedName("Collection ID")
    @Expose
    private String collectionID;

    @SerializedName("Download link")
    @Expose
    private String downloadLink;

    @SerializedName("Length (sec)")
    @Expose
    private String storyDuration;

    public String getTitle() {
        return title;
    }

    public String getFeelingsTags() {
        return tags;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public String getStoryDuration() {
        return storyDuration;
    }
}
