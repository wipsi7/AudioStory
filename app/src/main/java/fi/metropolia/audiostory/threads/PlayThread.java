package fi.metropolia.audiostory.threads;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import fi.metropolia.audiostory.filestorage.RawFile;
import fi.metropolia.audiostory.museum.Constant;

/** Thread for playing .raw audio file. Takes input of RawFile class.
 * Playing properties: 44100Hz, 16bit, mono, .raw , streaming*/
public class PlayThread extends Thread {

    private Handler ui;
    private RawFile rawFile;

    private int minBufferSize;
    private boolean playing = false;
    private AudioTrack track;


    public PlayThread(RawFile rawFile, Handler ui) {
        this.rawFile = rawFile;
        this.ui = ui;
        init();
    }

    private void init() {
        minBufferSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
    }


    @Override
    public void run() {
        startPlaying();
    }




    private void startPlaying() {

         track = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                44100,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize,
                AudioTrack.MODE_STREAM
        );

        track.play();
        playing = true;

        byte[] buffer = new byte[minBufferSize];

        try {

            InputStream inputStream = new FileInputStream(rawFile.getFile());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);

            int i;
            while ((i = dataInputStream.read(buffer, 0, minBufferSize)) != -1) {
                track.write(buffer, 0, i);
            }

            dataInputStream.close();

        } catch (IOException e){
            e.printStackTrace();
        }

        playing = false;
        track.stop();
        track.release();

        ui.sendEmptyMessage(Constant.MESSAGE_PLAY_FINISH);


    }

    public boolean isPlaying(){
        return playing;
    }


    public void stopPlaying(){
        track.pause();
        track.flush();
    }
}
