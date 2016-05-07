package fi.metropolia.audiostory.tasks;

import android.os.AsyncTask;

import fi.metropolia.audiostory.Server.ServerConnection;
import fi.metropolia.audiostory.interfaces.AsyncResponse;

public class LoginTask extends AsyncTask<String, Void, ServerConnection> {
    public AsyncResponse onLoginResult = null;
    private String apiKey = null;

    private String DEBUG_TAG = "LoginTask";

    public void setOnLoginResult(AsyncResponse onLoginResult) {
        this.onLoginResult = onLoginResult;
    }

    protected ServerConnection doInBackground(String ...params){

        ServerConnection serverConnection = new ServerConnection();

        try {
            serverConnection.auth(params[0], params[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return serverConnection;
    }

    protected void onPostExecute(ServerConnection result){
        onLoginResult.onProcessFinish(result);
    }
}