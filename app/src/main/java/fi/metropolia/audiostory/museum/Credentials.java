package fi.metropolia.audiostory.museum;

import java.util.ArrayList;

import fi.metropolia.audiostory.Security.Encrypter;

public class Credentials{

    private String userName, password, collectionID, apiKey = null;

    public Credentials(ArrayList<String> records){
        Encrypter encrypter = new Encrypter();
        userName = encrypter.decrypt(records.get(Constant.USERNAME_INDEX));
        password = encrypter.decrypt(records.get(Constant.PASSWORD_INDEX));
        collectionID = records.get(Constant.COLLECTION_ID_INDEX);
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

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
