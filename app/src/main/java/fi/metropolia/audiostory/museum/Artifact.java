package fi.metropolia.audiostory.museum;

import java.util.ArrayList;

import fi.metropolia.audiostory.Security.Encrypter;

public class Artifact {

    private String userName;
    private String password;
    private String collectionID;
    private String artifactName;
    private String apiKey;


    public Artifact(ArrayList<String> records){

        Encrypter encrypter = new Encrypter();
        userName = encrypter.decrypt(records.get(0));
        password = encrypter.decrypt(records.get(1));
        collectionID = records.get(2);
        artifactName = records.get(3);
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getCollectionID() {
        return collectionID;
    }

    public String getArtifactName() {
        return artifactName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
