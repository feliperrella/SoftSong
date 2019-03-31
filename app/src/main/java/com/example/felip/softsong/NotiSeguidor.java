package com.example.felip.softsong;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class NotiSeguidor extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifi_seguidor);
        p = findViewById(R.id.notiviews);
        new load().execute();
    }

    class load extends AsyncTask<String, String, String>
    {
        ClasseConexao classeConexao = new ClasseConexao();

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection connection = classeConexao.CONN();
                if(connection != null)
                {
                    user.clear();
                    foto.clear();
                    name.clear();
                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery("Select username, nome, caminho_imagem from tblUsuario where IDUsuario in (Select IDSeguidor from tblSeguir where IDSeguido = " + Login_Screen.sharedPref.getString("id", "") + ")");
                    rs.beforeFirst();
                    while(rs.next())
                    {
                        user.add(rs.getString("username"));
                        foto.add(rs.getString("caminho_imagem"));
                        name.add(rs.getString("nome"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            customAdapter adapter = new customAdapter();
            p.setAdapter(adapter);
        }
    }

    class customAdapter extends BaseAdapter
    {


        @Override
        public int getCount() {
            return user.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            final View vieww = getLayoutInflater().inflate(R.layout.searchlist_layout, null);
            TextView nome = vieww.findViewById(R.id.searchname);
            final TextView usu = vieww.findViewById(R.id.searchusu);
            final ImageView per = vieww.findViewById(R.id.seachperfil);
            RelativeLayout rel = vieww.findViewById(R.id.relativeSearch);
            rel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Search.user = usu.getText().toString().replace(" começou a seguir voce", "");
                    Search.cami = "http://192.168.15.17/pictures/" + foto.get(i);
                    Intent my = new Intent(NotiSeguidor.this, Public_Perfil.class);
                    startActivity(my);
                }
            });
            try {
                nome.setText(name.get(i));
                usu.setText(user.get(i)  + " começou a seguir voce");
                Glide.with(getApplicationContext()).load("http://192.168.15.17/pictures/" + foto.get(i)).into(per);
            }
            catch (Exception e){}
            return vieww;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent my = new Intent(NotiSeguidor.this, Home_Screen.class);
            startActivity(my, ActivityOptions.makeSceneTransitionAnimation(NotiSeguidor.this).toBundle());
        }

        return super.onKeyDown(keyCode, event);
    }
    ListView p;
    static ArrayList<String> user = new ArrayList<>(),foto = new ArrayList<>(),name = new ArrayList<>();

}
