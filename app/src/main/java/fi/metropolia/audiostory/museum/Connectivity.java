package fi.metropolia.audiostory.museum;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Connectivity {

    public static boolean isNetworkAvailable(Object connectivityManager) {
        ConnectivityManager manager = (ConnectivityManager)connectivityManager;
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
