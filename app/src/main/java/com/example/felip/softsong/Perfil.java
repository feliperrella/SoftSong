package com.example.felip.softsong;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Perfil extends Activity {

    public static String[] titulos;
    public static String[] legenda;
    public static String[] data;
    public static String[] id;
    public static  String[] images;
    public static int[] backs = {R.drawable.image_visibility, R.drawable.image_visibility1, R.drawable.image_visibility2};

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.perfil_screen);
        perfil = findViewById(R.id.perfil_);
        extras = findViewById(R.id.extras);
        nome = findViewById(R.id.nome);
        bt0 = findViewById(R.id.btnPostar);
        bt1 = findViewById(R.id.btnFollowers);
        bt2 = findViewById(R.id.btnFollowings);
        Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        //ImageView back = (ImageView) findViewById(R.id.back);
        mypubs = (TextView) findViewById(R.id.pubs);
        follows = (TextView) findViewById(R.id.follows);
        followings = (TextView) findViewById(R.id.following);
        new GetMyFollows().execute();
        final Thread x = new Thread(){
            @Override
            public void run() {
                super.run();
                WizardPagerAdapter adapter = new WizardPagerAdapter();
                ViewPager pager = (ViewPager) findViewById(R.id.segbio);
                pager.setAdapter(adapter);
                TextView bio = findViewById(R.id.txtBio);
                bio.setText(Login_Screen.sharedPref.getString("desc",""));
                ImageView visibility = findViewById(R.id.visibility);
                visibility.setImageResource(backs[(int) (3*Math.random())]);
            }
        };
        x.run();

        //final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        Thread t = new Thread(){
            @Override
            public void run() {
                super.run();
                Glide.with(getApplicationContext()).load("http://192.168.15.17/pictures/" + Login_Screen.sharedPref.getString("foto_perfil","")).into(perfil);
            }
        };
        t.run();
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
        {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        //new GetMyFollows().execute();
        extras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent x = new Intent(Perfil.this, Dashboard.class);
                startActivity(x, ActivityOptions.makeSceneTransitionAnimation(Perfil.this).toBundle());
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Seguidores_Seguidos.usu = Login_Screen.sharedPref.getString("usu", "");
                Seguidores_Seguidos.Op = "seguidos";
                Intent my = new Intent(Perfil.this, Seguidores_Seguidos.class);
                startActivity(my);
            }
        });
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Seguidores_Seguidos.usu = Login_Screen.sharedPref.getString("usu", "");
                Seguidores_Seguidos.Op = "seguidores";
                Intent my = new Intent(Perfil.this, Seguidores_Seguidos.class);
                startActivity(my);
            }
        });

        bt0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Perfil.this, Posts_Holder.class);
                i.putExtra("type", "my");
                startActivity(i);

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
                    String sub2 = "(Select Count(*) from tblSeguir where IDSeguindo =" + Login_Screen.sharedPref.getString("id","") +")";
                    String sub3 = "(Select nome from tblUsuario where IDUsuario = " + Login_Screen.sharedPref.getString("id","") + ")";
                    String query = "select Count(*)," + sub1 + "," + sub2 + "," + sub3 + "from tblPost where ID_Usuario = " + Login_Screen.sharedPref.getString("id","");
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs != null && rs.next()){
                        a = (rs.getString("Count(*)"));
                        b = (rs.getString(sub1));
                        c = (rs.getString(sub2));
                        d = (rs.getString(sub3));
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                mypubs.setText(a);
                                followings.setText(c);
                                follows.setText(b);
                                nome.setText(d);
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
    String a,b,c,d;
    LinearLayout bt0, bt1, bt2;
    ConstraintLayout x;
}






