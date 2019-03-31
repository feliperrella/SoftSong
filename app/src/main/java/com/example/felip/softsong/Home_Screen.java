package com.example.felip.softsong;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


//                SharedPreferences.Editor editor = Login_Screen.sharedPref.edit();
//                editor.clear();
//                editor.commit();
public class Home_Screen extends Activity {
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.home_screen);
                plus = findViewById(R.id.plus);
                posts = findViewById(R.id.posts);
                noti = findViewById(R.id.noti);
                ImageView postar = findViewById(R.id.addpost);
                posts = findViewById(R.id.posts);
                postar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent my = new Intent(Home_Screen.this, AddPost.class);
                        startActivity(my);
                        finish();
                    }
                });
                ImageView sear = findViewById(R.id.search);
                sear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent my = new Intent(Home_Screen.this, Search.class);
                        startActivity(my);
                        finish();
                    }
                });
                inte = getIntent();
                plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent my = new Intent(Home_Screen.this, Perfil.class);
                        startActivity(my);
                        finish();
                    }
                });
                noti.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent my = new Intent(Home_Screen.this, NotiSeguidor.class);
                        startActivity(my);
                        finish();
                    }
                });
                new PostDeals.GetMyPosts(this, posts, "all").execute();
                new Not().execute();
    }

    @SuppressLint("StaticFieldLeak")
    class Not extends AsyncTask<String,String,String>
    {
        ClasseConexao conexao = new ClasseConexao();
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection connection = conexao.CONN();
                if(connection != null)
                {
                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery("Select Count(*) from tblSeguir where IDSeguido = " + Login_Screen.sharedPref.getString("id",""));
                    if(rs != null && rs.next())
                    {
                        SharedPreferences.Editor editor = Login_Screen.sharedPref.edit();
                        int n = rs.getInt("Count(*)");
                        int nS = 0;
                        if(Login_Screen.sharedPref.contains("not"))
                            nS = Login_Screen.sharedPref.getInt("not", 0);
                        else
                            editor.putInt("not", 0);
                        sub = n - nS >= 0 ? n -nS : 0;
                        if(n > nS)
                        {
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink_anim_infinit);
                            noti.startAnimation(animation);
                            editor.putInt("not", n);
                            editor.apply();
                        }
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
        }
    }

    public static int sub;
    public static Intent inte;
    ImageView plus, noti;
    ListView posts;

}





