package com.example.felip.softsong;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomMenuButton;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


//                SharedPreferences.Editor editor = Login_Screen.sharedPref.edit();
//                editor.clear();
//                editor.commit();
public class Home_Screen extends Activity {
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                requestWindowFeature(1);
                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                    getWindow().setStatusBarColor(Color.TRANSPARENT);
                }
                setContentView(R.layout.home_screen);
                Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.fade);
                getWindow().setEnterTransition(fade);
                posts = findViewById(R.id.posts);
                new PostDeals.GetMyPosts(this, posts, "all").execute();
                new Not().execute();
                bmb = findViewById(R.id.bmb);
                for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {
                    SimpleCircleButton.Builder builder = new SimpleCircleButton.Builder().normalImageRes(picId[i]).listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(final int index) {
                            BoomButton b;
                            View bm;
                            Intent myIntent;
                            ActivityOptions activityOptions;
                            switch (index)
                            {
                                case 0:
                                    new Handler().postDelayed(new Runnable()
                                    {
                                        @Override
                                        public void run() {
                                            Intent x = new Intent(Home_Screen.this, Perfil.class);
                                            startActivity(x, ActivityOptions.makeSceneTransitionAnimation(Home_Screen.this).toBundle());
                                        }}, 700);
                                    break;
                                case 1:
                                    new Handler().postDelayed(new Runnable()
                                    {
                                        @Override
                                        public void run() {
                                            Intent x = new Intent(Home_Screen.this, AddPost.class);
                                            startActivity(x, ActivityOptions.makeSceneTransitionAnimation(Home_Screen.this).toBundle());
                                        }}, 700);
                                break;
                                case 2:
                                    new Handler().postDelayed(new Runnable()
                                    {
                                        @Override
                                        public void run() {
                                            Intent x = new Intent(Home_Screen.this, Search.class);
                                            startActivity(x, ActivityOptions.makeSceneTransitionAnimation(Home_Screen.this).toBundle());
                                        }}, 700);
                                    break;
                                case 3:
                                    new Handler().postDelayed(new Runnable()
                                    {
                                        @Override
                                        public void run() {
                                            Intent x = new Intent(Home_Screen.this, Chats_Index.class);
                                            startActivity(x, ActivityOptions.makeSceneTransitionAnimation(Home_Screen.this).toBundle());
                                        }}, 700);
                                    break;
                                case 4:
                                    new Handler().postDelayed(new Runnable()
                                    {
                                        @Override
                                        public void run() {
                                            Intent x = new Intent(Home_Screen.this, NotiSeguidor.class);
                                            startActivity(x, ActivityOptions.makeSceneTransitionAnimation(Home_Screen.this).toBundle());
                                        }}, 700);
                                    break;



                            }
                        }
                    });
                    bmb.addBuilder(builder);
                    bmb.bringToFront();
                }
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
                    ResultSet rs = stmt.executeQuery("Select Count(*) from tblSeguir where IDSeguindo = " + Login_Screen.sharedPref.getString("id",""));
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
    BoomMenuButton bmb;
    RelativeLayout test;
    int[] picId = {R.drawable.ico_uso, R.drawable.ico_mais,R.drawable.ico_search, R.drawable.ic_send,R.drawable.ico_notification};
}





