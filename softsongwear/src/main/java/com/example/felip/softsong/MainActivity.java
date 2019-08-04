package com.example.felip.softsong;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.kinda.alert.KAlertDialog;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainActivity extends WearableActivity{




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Enables Always-on
        setAmbientEnabled();
        txtHertz = findViewById(R.id.txtHertz);
        txtNote = findViewById(R.id.txtNote);
        color = findViewById(R.id.color);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.RECORD_AUDIO)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        recordAudio();
    }

    int bufferSize;

    AudioRecord record;
    final int SAMPLE_RATE = 44100; // The sampling rate
    boolean mShouldContinue = true; // Indicates if recording / playback should stop
    String LOG_TAG = "Feliperrella";

    void recordAudio() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

                // buffer size in bytes
                int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);

                if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                    bufferSize = SAMPLE_RATE * 2;
                }

                short[] audioBuffer = new short[bufferSize / 2];

                AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                        SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize);

                if (record.getState() != AudioRecord.STATE_INITIALIZED) {
                    Log.e(LOG_TAG, "Audio Record can't initialize!");
                    return;
                }
                record.startRecording();

                Log.v(LOG_TAG, "Start recording");

                long shortsRead = 0;
                while (mShouldContinue) {
                    int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length);
                    shortsRead += numberOfShort;
                    calculate(44100, audioBuffer);

                    // Do something with the audioBuffer
                }
                calculate(44100, audioBuffer);
                Log.v(LOG_TAG, String.format("Recording stopped. Samples read: %d", shortsRead));
            }
        }).start();
    }


    public int calculate(int sampleRate, short [] audioData) {

        int numSamples = audioData.length;
        int numCrossing = 0;
        for (int p = 0; p < numSamples - 1; p++) {
            if ((audioData[p] > 0 && audioData[p + 1] <= 0) ||
                    (audioData[p] < 0 && audioData[p + 1] >= 0)) {
                numCrossing++;
            }
        }

        float numSecondsRecorded = (float) numSamples / (float) sampleRate;
        float numCycles = numCrossing / 2;
        final float frequency = numCycles / numSecondsRecorded;
        Log.i(LOG_TAG, (int) frequency + "");
        String note = "";

        if ((int) frequency >= 312 & (int)frequency <= 344) {
            note = "e";
        }
        else if ((int) frequency >= 229 & (int)frequency <= 283){
            note = "B";
        }
        else if ((int) frequency >= 172 & (int)frequency <= 196){
            note = "G";
        }
        else if ((int) frequency >= 123 & (int)frequency <= 147){
            note = "D";
        }
        else if ((int) frequency >= 98 & (int)frequency <= 123){
            note = "A";
        }
        else if ((int) frequency >= 78 & (int)frequency <= 98){
            note = "E";
        }



        final String n = note;
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                txtNote.setText(n);
                if(n.contains("#") || n.contains("b"))
                {
                    color.setBackgroundResource(R.drawable.image_visibility2);
                }
                txtHertz.setText((int)frequency + "Hz");
            }
        });
        return (int)frequency;

    }
    TextView txtHertz, txtNote;
    ConstraintLayout color;
    static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 0;


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    String ID = "";
    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, final Intent intent) {
            //String onMessageReceived = "I just received a  message from the handheld " + receivedMessageNumber++;
            System.out.println(intent.getStringExtra("message"));
            ID = intent.getStringExtra("message");
            String datapath = "/my_path";
            new SendMessage(datapath, "Conexao Aceita").start();

        }
    }

    class SendMessage extends Thread {
        String path;
        String message;

//Constructor///

        SendMessage(String p, String m) {
            path = p;
            message = m;
        }

//Send the message via the thread. This will send the message to all the currently-connected devices//

        public void run() {

//Get all the nodes//

            Task<List<Node>> nodeListTask =
                    Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            try {

//Block on a task and get the result synchronously//

                List<Node> nodes = Tasks.await(nodeListTask);

//Send the message to each device//

                for (Node node : nodes) {
                    Task<Integer> sendMessageTask =
                            Wearable.getMessageClient(MainActivity.this).sendMessage(node.getId(), path, message.getBytes());

                    try {



                        Integer result = Tasks.await(sendMessageTask);


//Handle the errors//

                    } catch (ExecutionException exception) {

//TO DO//

                    } catch (InterruptedException exception) {

//TO DO//

                    }

                }

            } catch (ExecutionException exception) {

//TO DO//

            } catch (InterruptedException exception) {

//TO DO//

            }
        }
    }
}

