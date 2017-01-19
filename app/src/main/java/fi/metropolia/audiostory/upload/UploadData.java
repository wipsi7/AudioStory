package fi.metropolia.audiostory.upload;


import java.io.File;

public class UploadData {

    private String apiKey;
    private String collectionId;
    private String resourceType;
    private File uploadFile;
    private String artifact;
    private String category;
    private String tags;
    private String title;
    private String originalFileName;
    private String duration;

    public UploadData(){

        init();
    }


    private void init() {
        resourceType = "4";
        category = "story";
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getArtifact() {
        return artifact;
    }

    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }

    public String getCategory() {
        return category;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String tag : tags) {
            stringBuilder.append(tag);
            stringBuilder.append(' ');
        }

        this.tags = stringBuilder.toString().trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public File getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(File uploadFile) {
        this.uploadFile = uploadFile;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
