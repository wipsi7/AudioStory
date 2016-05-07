package fi.metropolia.audiostory.threads;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import fi.metropolia.audiostory.filestorage.RawFile;

/** Thread for playing .raw audio file. Takes input of RawFile class.
 * Playing properties: 44100Hz, 16bit, mono, .raw , streaming*/
public class PlayThread extends Thread {

    private RawFile rawFile;

    private int minBufferSize;
    private boolean playing = false;

    public PlayThread(RawFile rawFile) {
        this.rawFile = rawFile;
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

        AudioTrack track = new AudioTrack(
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
    }

    public boolean isPlaying(){
        return playing;
    }
}
