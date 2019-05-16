package com.example.felip.softsong;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class MainActivity extends WearableActivity implements DataClient.OnDataChangedListener {

    private  ImageView perfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Enables Always-on
        setAmbientEnabled();
        perfil = findViewById(R.id.Perfil);
        final Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        (findViewById(R.id.bounce)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (findViewById(R.id.bounce)).startAnimation(anim);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();


        Wearable.getDataClient(this).addListener(this).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("sucesso");
            }
        });
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);

    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEventBuffer);
        for(DataEvent event : events) {
                final DataMap map = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                // read your values from map:
                String id = map.getString("ID");
                System.out.println("foi");
                Log.v("Feliperrella", "ID recebido: " + id);
        }
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
}
