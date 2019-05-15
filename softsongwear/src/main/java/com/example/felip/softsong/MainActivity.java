package com.example.felip.softsong;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.florent37.davinci.DaVinci;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends WearableActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Enables Always-on
        setAmbientEnabled();
        new Load().execute();
        final Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        ((ImageView) findViewById(R.id.bounce)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ImageView) findViewById(R.id.bounce)).startAnimation(anim);
            }
        });
        //new PostDeals.GetMyPosts(this, ((ListView) findViewById(R.id.list)), "all").execute();
    }

    class Load extends AsyncTask<String,String,String>
    {
        @Override
        protected String doInBackground(String... strings) {
            ClasseConexao classeConexao = new ClasseConexao();
            try {
                String sub1 = "(Select Count(*) from tblSeguir where IDSeguidor = " + "5" + ")";
                String sub2 = "(Select Count(*) from tblSeguir where IDSeguindo =" + "5" +")";
                final String sub3 = "(Select caminho_imagem from tblUsuario where IDUsuario = " + "5" + ")";
                String query = "select Count(*)," + sub1 + "," + sub2 + "," + sub3 + "from tblPost where ID_Usuario = " + "5";
                Connection connection = classeConexao.CONN();
                Statement stmt = connection.createStatement();
                final ResultSet rs = stmt.executeQuery(query);
                if(rs.next() && rs != null)
                {
                    ((TextView) findViewById(R.id.POST)).setText(rs.getString("Count(*)"));
                    ((TextView) findViewById(R.id.SEGUINDO)).setText(rs.getString(sub1));
                    ((TextView) findViewById(R.id.SEGUIDORES)).setText(rs.getString(sub2));
                    System.out.println("http://192.168.15.17/pictures/" + rs.getString(sub3));
                    DaVinci.with(getApplicationContext()).load("http://192.168.15.17/pictures/" + rs.getString(sub3)).into(((ImageView) findViewById(R.id.Perfil)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
