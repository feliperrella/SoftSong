package com.example.felip.softsong;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
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

    private  ImageView perfil;
    private TextView textView;
    Button talkButton;
    int receivedMessageNumber = 1;
    int sentMessageNumber = 1;
    SharedPreferences sharedPreferences;
    public static int[] backs = {R.drawable.image_visibility, R.drawable.image_visibility1, R.drawable.image_visibility2};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Enables Always-on
        setAmbientEnabled();
        sharedPreferences = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        if(sharedPreferences.getString("id", "") != null)
        {
            ID = sharedPreferences.getString("id", "");
            new Load().execute();
        }
        perfil = findViewById(R.id.Perfil);
        textView = findViewById(R.id.textView3);
        final Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        (findViewById(R.id.bounce)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (findViewById(R.id.bounce)).startAnimation(anim);
            }
        });
        IntentFilter newFilter = new IntentFilter(Intent.ACTION_SEND);
        Receiver messageReceiver = new Receiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, newFilter);
        FrameLayout visibility = findViewById(R.id.visibility);
        visibility.setBackground(getDrawable(backs[(int) (3*Math.random())]));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class Load extends AsyncTask<String,String,String>
    {
        @Override
        protected String doInBackground(String... strings) {
            ClasseConexao classeConexao = new ClasseConexao();
            try {
                String sub1 = "(Select Count(*) from tblSeguir where IDSeguidor = " + ID + ")";
                String sub2 = "(Select Count(*) from tblSeguir where IDSeguindo =" + ID +")";
                final String sub3 = "(Select caminho_imagem from tblUsuario where IDUsuario = " + ID + ")";
                String query = "select Count(*)," + sub1 + "," + sub2 + "," + sub3 + "from tblPost where ID_Usuario = " + ID;
                Connection connection = classeConexao.CONN();
                Statement stmt = connection.createStatement();
                final ResultSet rs = stmt.executeQuery(query);
                if(rs.next() && rs != null)
                {
                    ((TextView) findViewById(R.id.POST)).setText(rs.getString("Count(*)"));
                    ((TextView) findViewById(R.id.SEGUINDO)).setText(rs.getString(sub1));
                    ((TextView) findViewById(R.id.SEGUIDORES)).setText(rs.getString(sub2));
                    System.out.println("http://192.168.15.17/pictures/" + rs.getString(sub3));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    String ID = "";
    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String onMessageReceived = "I just received a  message from the handheld " + receivedMessageNumber++;
            System.out.println(intent.getStringExtra("message"));
            ID = intent.getStringExtra("message");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("id", ID);
            editor.commit();
            new Load().execute();
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

