package com.example.felip.softsong;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static com.example.felip.softsong.Perfil.backs;

public class Public_Perfil extends Activity{

    public static String[] id;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.public_perfil);
        seguir = findViewById(R.id.btnSeguir);
        bloquear = findViewById(R.id.btnBloquear);
        perfil = findViewById(R.id.perfil_);
        extras = findViewById(R.id.extras);
        bt0 = findViewById(R.id.btnPostar);
        bt1 = findViewById(R.id.btnFollowers);
        bt2 = findViewById(R.id.btnFollowings);
        nome = findViewById(R.id.nome);
        mypubs = findViewById(R.id.pubs);
        follows = findViewById(R.id.follows);
        followings = findViewById(R.id.following);
        new GetMyFollows().execute();
        Thread t = new Thread(){
            @Override
            public void run() {
                super.run();
                Glide.with(getApplicationContext()).load(Search.cami).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(perfil);
            }
        };
        t.run();
        Thread x = new Thread(){
            @Override
            public void run() {
                super.run();
                WizardPagerAdapter adapter = new WizardPagerAdapter();
                ViewPager pager = (ViewPager) findViewById(R.id.segbio);
                pager.setAdapter(adapter);
                ImageView visibility = findViewById(R.id.visibility);
                //visibility.setImageResource(backs[(int) (3*Math.random())]);
            }
        };
        x.run();
        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
        seguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(seguir.getText().toString().equals("S E G U I R"))
                {
                    seguir.setText("S E G U I N D O");
                    seguir.startAnimation(animation);
                    String q =  "http://" + HttpHandler.IP + "/Interacoes.php?type=follow&user=" + Search.user + "&id=" + Login_Screen.sharedPref.getString("id", "");
                    new Segue(q).execute();
                    new GetMyFollows().execute();
                }
                else if(seguir.getText().toString().equals("S E G U I N D O"))
                {
                    seguir.setText("S E G U I R");
                    seguir.startAnimation(animation);
                    String q =  "http://" + HttpHandler.IP + "/Interacoes.php?type=unfollow&user=" + Search.user + "&id=" + Login_Screen.sharedPref.getString("id", "");
                    new Segue(q).execute();
                    new GetMyFollows().execute();
                } }
        });

        bloquear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //HttpHandler sh = new HttpHandler();
                if(bloquear.getText().toString().equals("B L O Q U E A R"))
                {
                    bloquear.startAnimation(animation);
                    bloquear.setText("B L O Q U E A D O");
                    String q =  "http://" + HttpHandler.IP + "/blockProcedure.php?user=" + Search.user + "&id=" + Login_Screen.sharedPref.getString("id", "");
                    new Segue(q).execute();
                    new GetMyFollows().execute();
                }
                else if(bloquear.getText().toString().equals("B L O Q U E A D O"))
                {
                    bloquear.startAnimation(animation);
                    bloquear.setText("B L O Q U E A R");
                    seguir.setEnabled(true);
                    seguir.setBackgroundResource(R.drawable.button_follow_background);
                    //sh.makeServiceCall("http://" + HttpHandler.IP + "/Interacoes.php?type=desbloquear&user=" + Search.user + "&id=" + Login_Screen.sharedPref.getString("id", ""));
                    String q = "http://" + HttpHandler.IP + "/Interacoes.php?type=desbloquear&user=" + Search.user + "&id=" + Login_Screen.sharedPref.getString("id", "");
                    new Segue(q).execute();
                    new GetMyFollows().execute();
                }
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Seguidores_Seguidos.usu = Search.user;
                Seguidores_Seguidos.Op = "seguidos";
                Intent my = new Intent(Public_Perfil.this, Seguidores_Seguidos.class);
                startActivity(my);
            }
        });
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Seguidores_Seguidos.usu = Search.user;
                Seguidores_Seguidos.Op = "seguidores";
                Intent my = new Intent(Public_Perfil.this, Seguidores_Seguidos.class);
                startActivity(my);
            }
        });
        bt0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Public_Perfil.this, Posts_Holder.class);
                i.putExtra("type", "spec");
                startActivity(i);
            }
        });
        }


    class Segue extends AsyncTask<String, String, String> {
        String url;
        public Segue(String q) {
            url = q;
        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {

                    try {
                        HttpHandler sh = new HttpHandler();
                        sh.makeServiceCall(url);
                    }
                    catch (Exception ex) {

                    }
                return null;
        }
        @Override
        protected void onPostExecute(String r) { }}



    public class GetMyFollows extends AsyncTask<String, String, String> {
        String message = "";
        Boolean isSuccess = false;
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            try {
                HttpHandler sh = new HttpHandler();
                    String jsonStr = sh.makeServiceCall("http://" + HttpHandler.IP + "/PublicPerfil.php?id=" + Login_Screen.sharedPref.getString("id", "") + "&user=" + Search.user);
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    Log.i("Feliperrella", jsonStr);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject rs = jsonArray.getJSONObject(i);
                        a = (rs.getString("Count(*)"));
                        b = (rs.getString("seguindo"));
                        c = (rs.getString("seguidores"));
                        d = (rs.getInt("bloqueado"));
                        e = (rs.getInt("sigo"));
                        f = (rs.getString("descr"));
                        h = (rs.getString("nome"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mypubs.setText(a);
                                nome.setText(h);
                            }
                        });
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                followings.setText(c);}});
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                follows.setText(b);    }
                        });

                    }
                    isSuccess = true;

            } catch (Exception ex) {
                isSuccess = false;
                message = "Falha ao conectar-se ao banco :(";
            }
            return message;
        }


        @Override
        protected void onPostExecute(String r) {
            //Toast.makeText(Perfil.this, r, Toast.LENGTH_SHORT).show();
            TextView bio = findViewById(R.id.txtBio);
            bio.setText(f);
            final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
            if(d != 0)
            {
                    bloquear.startAnimation(animation);
                    bloquear.setText("B L O Q U E A D O");
                    seguir.setEnabled(false);
            }
            if(e != 0)
            {

                    seguir.startAnimation(animation);
                    seguir.setText("S E G U I N D O");

            }

        }
    }

    class WizardPagerAdapter extends PagerAdapter {

        public Object instantiateItem(View collection, int position) {

            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.linearLayout2;
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
    TextView mypubs, follows, followings, nome;
    Button seguir, bloquear;
    LinearLayout bt0, bt1, bt2;
    String a,b,c,f,h;
    int d,e;
}
