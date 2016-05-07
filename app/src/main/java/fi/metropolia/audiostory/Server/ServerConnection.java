package fi.metropolia.audiostory.Server;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class ServerConnection {

    private String DEBUG_TAG = "ServerConnection";

    private String apiKey = null;
    private String authority = "resourcespace.tekniikanmuseo.fi";
    private String path = "plugins";
    public boolean isLogged = false;

    public String getApiKey() {
        return apiKey;
    }
    public void auth(String user, String pass){
        Uri.Builder uri = new Uri.Builder();
        uri.scheme("http");
        uri.authority(authority);
        uri.path(path);

        uri.appendEncodedPath("api_auth/auth.php/");
        String builtUrl = uri.build().toString();
        // parameters to send
        HashMap<String,String> params = new HashMap<>();
        params.put("username", user.trim());
        params.put("password", pass.trim());

        JSONObject response = null;
        try {
            response = new JSONObject(doHttpPostRequest(builtUrl, params));
        }catch (Exception e){
            e.getMessage();
        }


        try {
            this.apiKey = response.getString("api_key");

            Log.d("ServerConnection", apiKey);
            if(this.apiKey.length() > 35){
                this.isLogged = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    /** Makes an POST request using a JSON body. It can't be used to upload a file, use method
     * upload() instead.
     * @param urlString the url of the post request destiny
     * @param params the parameters of the post request payload (will be converted to JSON)*/
    private String doHttpPostRequest(String urlString, HashMap<String,String> params){
        StringBuffer sb;
        String response = "some error happend";

        try {
            Log.d("uriString", urlString);
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true); // This set the request as a post request

            // payload to send
            urlConnection.setChunkedStreamingMode(0);
            OutputStream out = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            String jsonPayload = new JSONObject(params).toString();
            //Log.d(DEBUG_TAG, "json-payload:"+jsonPayload);
            writer.write(jsonPayload);
            writer.flush();
            writer.close();
            out.close();

            // response
            int responseCode=urlConnection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader bf = new BufferedReader(new InputStreamReader(in));
                sb = new StringBuffer("");
                String line;
                while ((line = bf.readLine()) != null) {
                    sb.append(line);
                }
                bf.close();
                response = sb.toString();
            }else{
                response = "No response. code: " + responseCode;
            }
            //Log.d("response", response);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

}