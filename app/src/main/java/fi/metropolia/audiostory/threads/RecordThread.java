package fi.metropolia.audiostory.threads;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by wipsi on 4/21/2016.
 */
public class RecordThread extends Thread {

    private boolean recordingRunning = false;
    private final String DEBUG_TAG = "RecordThread";
    private File rawFile;

    public RecordThread(File rawFile){
        this.rawFile = rawFile;
    }

    public void setRawFile(File rawFile){
        this.rawFile = rawFile;
    }

    @Override
    public void run() {
        startRecording();
    }

    public void stopRecording(){
        recordingRunning = false;
    }

    private void startRecording() {
        recordingRunning = true;

        try {
            OutputStream outputStream = new FileOutputStream(rawFile);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);

            int minBufferSize = AudioRecord.getMinBufferSize(
                    44100,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT
            );

            byte[] audioData = new byte[minBufferSize];

            AudioRecord audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    44100,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufferSize
            );

            audioRecord.startRecording();


            while(recordingRunning){
                int numofBytes = audioRecord.read(audioData, 0, minBufferSize);
                for(int i = 0; i < numofBytes; i++){
                    dataOutputStream.write(audioData[i]);
                }
            }

            audioRecord.stop();
            dataOutputStream.close();


        } catch (IOException e){
            Log.e(DEBUG_TAG,e.getMessage());
        }

    }
}