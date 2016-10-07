package fi.metropolia.audiostory.threads;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import fi.metropolia.audiostory.filestorage.RawFile;


/** Thread for recording audio. Takes in a .raw file container and stores recording on it.
 * Audio record properties: 44100HZ, 16bit, mono, .raw.*/
public class RecordThread extends Thread {

    private boolean recordingRunning = false;
    private final String DEBUG_TAG = "RecordThread";
    private RawFile rawFile;


    public RecordThread(RawFile rawFile){
        this.rawFile = rawFile;
    }


    @Override
    public void run() {
        startRecording();
    }

    public void stopRecording(){
        recordingRunning = false;
    }

    public boolean isRecording(){
        return recordingRunning;
    }

    private void startRecording() {
        recordingRunning = true;

        try {
            OutputStream outputStream = new FileOutputStream(rawFile.getFile());
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);

            int minBufferSize = AudioRecord.getMinBufferSize(
                    44100,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
            );

            byte[] audioData = new byte[minBufferSize];

            AudioRecord audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    44100,
                    AudioFormat.CHANNEL_IN_MONO,
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