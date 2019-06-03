package com.example.felip.softsong;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gelitenight.waveview.library.WaveView;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.ExecutionException;

import cdflynn.android.library.checkview.CheckView;

import static android.view.View.VISIBLE;

public class ConectionActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_connection);
        t = ((ImageView) findViewById(R.id.logo));
        if(Login_Screen.sharedPref.getString("wear", "") != "")
        {
            ((Button) findViewById(R.id.btnConnect)).setEnabled(false);
            ((Button) findViewById(R.id.btnConnect)).setText("Relogio ja conectado");
            Glide.with(this).load(R.drawable.bt).into(((ImageView) findViewById(R.id.logo)));
            //((ImageView) findViewById(R.id.logo)).setImageDrawable(getDrawable(R.drawable.bt));
        }
        else {
            ((ImageView) findViewById(R.id.logo)).bringToFront();
            ((ImageView) findViewById(R.id.logo)).setImageAlpha(R.drawable.ic_logo2);
        }
        WaveView waveView = findViewById(R.id.wavee);
        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext() ,R.anim.fadein);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                ((ImageView) findViewById(R.id.watch)).startAnimation(animation);
                ((ImageView) findViewById(R.id.watch)).setVisibility(VISIBLE);
                ((ImageView) findViewById(R.id.logo)).startAnimation(animation);
                ((ImageView) findViewById(R.id.logo)).setVisibility(VISIBLE);
            }
        }, 1000);
        check = findViewById(R.id.check);
        WaveHelper = new WaveHelper(waveView);
        waveView.setShapeType(WaveView.ShapeType.SQUARE);
        waveView.setWaveColor(
                Color.parseColor("#B079E1"),
                Color.parseColor("#285FBAE6"));

        talkbutton = findViewById(R.id.btnConnect);
        textview = findViewById(R.id.textView);

        myHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Bundle stuff = msg.getData();
                messageText(stuff.getString("messageText"));
                return true;
            }
        });


        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        Receiver messageReceiver = new Receiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        WaveHelper.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        WaveHelper.start();
    }
    private WaveHelper WaveHelper;
    public void messageText(String newinfo) {
        if (newinfo.compareTo("") != 0) {
            textview.append("\n" + newinfo);
        }
    }


    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = "Conexao realizada com sucesso";
            textview.setText(message);
            check.check();
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext() ,R.anim.fadein);
            Glide.with(getApplicationContext()).load(R.drawable.bt).into(t);
            t.startAnimation(animation);
            SharedPreferences.Editor editor = Login_Screen.sharedPref.edit();
            editor.putString("wear", "Conectado");
            editor.commit();
        }
    }


    public void talkClick(View v) {
        String message = Login_Screen.sharedPref.getString("id", "");
        //textview.setText(message);
        new NewThread("/my_path", message).start();

    }


    public void sendmessage(String messageText) {
        Bundle bundle = new Bundle();
        bundle.putString("messageText", messageText);
        Message msg = myHandler.obtainMessage();
        msg.setData(bundle);
        myHandler.sendMessage(msg);

    }


    class NewThread extends Thread {
        String path;
        String message;

        NewThread(String p, String m) {
            path = p;
            message = m;
        }


        public void run() {

            Task<List<Node>> wearableList =
                    Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            try {

                List<Node> nodes = Tasks.await(wearableList);
                for (Node node : nodes) {
                    Task<Integer> sendMessageTask =
                            Wearable.getMessageClient(ConectionActivity.this).sendMessage(node.getId(), path, message.getBytes());

                    try {

                        Integer result = Tasks.await(sendMessageTask);
                        sendmessage("I just sent the wearable a message " + sentMessageNumber++);

                    } catch (ExecutionException exception) {

                        //TO DO: Handle the exception//


                    } catch (InterruptedException exception) {

                    }

                }

            } catch (ExecutionException exception) {

                //TO DO: Handle the exception//

            } catch (InterruptedException exception) {

                //TO DO: Handle the exception//
            }

        }
    }
    CheckView check;
    Button talkbutton;
    TextView textview;
    protected Handler myHandler;
    int receivedMessageNumber = 1;
    int sentMessageNumber = 1;
    ImageView t;
}
