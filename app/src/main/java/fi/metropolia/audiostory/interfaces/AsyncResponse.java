package fi.metropolia.audiostory.interfaces;

import fi.metropolia.audiostory.Server.ServerConnection;

public interface AsyncResponse {

    void onProcessFinish(ServerConnection result);

}