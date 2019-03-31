package com.example.felip.softsong;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Perfil extends Activity {

    public static String[] titulos;
    public static String[] legenda;
    public static String[] data;
    public static String[] id;
    public static  String[] images;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_screen);
        perfil = (ImageView) findViewById(R.id.perfil_);
        extras = (ImageView) findViewById(R.id.extras);
        mypost = (ListView) findViewById(R.id.myposts);
        ImageView back = (ImageView) findViewById(R.id.back);
        mypubs = (TextView) findViewById(R.id.pubs);
        follows = (TextView) findViewById(R.id.follows);
        followings = (TextView) findViewById(R.id.following);
        new GetMyFollows().execute();
        Thread x = new Thread(){
            @Override
            public void run() {
                super.run();
                WizardPagerAdapter adapter = new WizardPagerAdapter();
                ViewPager pager = (ViewPager) findViewById(R.id.segbio);
                pager.setAdapter(adapter);
                TextView bio = (TextView) findViewById(R.id.txtBio);
                bio.setText(Login_Screen.sharedPref.getString("desc",""));
            }
        };
        x.run();

        //final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        Thread t = new Thread(){
            @Override
            public void run() {
                super.run();
                Glide.with(getApplicationContext()).load("http://192.168.15.17/pictures/" + Login_Screen.sharedPref.getString("foto_perfil","")).placeholder(R.drawable.ico_uso).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(perfil);
            }
        };
        t.run();
        new PostDeals.GetMyPosts(this, mypost, "my").execute();
        //new GetMyFollows().execute();
        extras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent my = new Intent(Perfil.this, Dashboard.class);
                startActivity(my);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent my = new Intent(Perfil.this, Home_Screen.class);
                startActivity(my);
            }
        });

        follows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Seguidores_Seguidos.usu = Login_Screen.sharedPref.getString("usu", "");
                Seguidores_Seguidos.Op = "seguidos";
                Intent my = new Intent(Perfil.this, Seguidores_Seguidos.class);
                startActivity(my);
            }
        });
        followings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Seguidores_Seguidos.usu = Login_Screen.sharedPref.getString("usu", "");
                Seguidores_Seguidos.Op = "seguidores";
                Intent my = new Intent(Perfil.this, Seguidores_Seguidos.class);
                startActivity(my);
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent my = new Intent(Perfil.this, Home_Screen.class);
            startActivity(my, ActivityOptions.makeSceneTransitionAnimation(Perfil.this).toBundle());
        }

        return super.onKeyDown(keyCode, event);
    }


    public class GetMyFollows extends AsyncTask<String, String, String> {
        String message = "";
        Boolean isSuccess = false;
        ClasseConexao conexao = new ClasseConexao();
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = conexao.CONN();
                if (con == null) {
                    message = "Error in connection with SQL server";
                } else {
                    String sub1 = "(Select Count(*) from tblSeguir where IDSeguidor = " + Login_Screen.sharedPref.getString("id","") + ")";
                    String sub2 = "(Select Count(*) from tblSeguir where IDSeguido =" + Login_Screen.sharedPref.getString("id","") +")";
                    String query = "select Count(*)," + sub1 + "," + sub2 + "from tblPost where ID_Usuario = " + Login_Screen.sharedPref.getString("id","");
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs != null && rs.next()){
                        a = (rs.getString("Count(*)"));
                        b = (rs.getString(sub1));
                        c = (rs.getString(sub2));
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                mypubs.setText(a);
                                followings.setText(c);
                                follows.setText(b);

                            }
                        });
                        
                    }
                    isSuccess = true;
                }
            } catch (Exception ex) {
                isSuccess = false;
                message = "Voce Ainda nao tem Posts";
            }
            return message;
        }


        @Override
        protected void onPostExecute(String r) {
            if (isSuccess = false) {
                Toast.makeText(Perfil.this, r, Toast.LENGTH_SHORT).show();
            }

        }
    }

    class WizardPagerAdapter extends PagerAdapter {

        public Object instantiateItem(View collection, int position) {

            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.page_one;
                    break;
                case 1:
                    resId = R.id.page_two;
                    break;
            }
            return findViewById(resId);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);
        }
    }


    ImageView perfil, extras;
    ListView mypost;
    TextView mypubs, follows, followings;
    ResultSet rsss;
    String a,b,c;
}






