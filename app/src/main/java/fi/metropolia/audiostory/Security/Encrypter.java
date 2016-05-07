package fi.metropolia.audiostory.Security;

public class Encrypter {

    private int userKey = 7;


    public String encrypt(String message){

        StringBuilder msg = new StringBuilder(message);

        for(int i = 0; i < msg.length(); i++){
            msg.setCharAt(i, (char)(msg.charAt(i) + userKey));
        }

        return msg.toString();
    }

    public String decrypt(String encryptedMessage){

        StringBuilder msg = new StringBuilder(encryptedMessage);

        for(int i = 0; i < msg.length(); i++){
            msg.setCharAt(i, (char)(msg.charAt(i) - userKey));
        }

        return msg.toString();
    }
}