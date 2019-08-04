package com.example.felip.softsong;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.common.api.GoogleApiClient;
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
public class Home_Screen extends Activity{
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
                Log.i("Feliperrella", Login_Screen.sharedPref.getString("usu", ""));
                new PostDeals.GetMyPosts(this, posts, "all").execute();
                //new Not().execute();
                bmb = findViewById(R.id.bmb);
                for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {
                    SimpleCircleButton.Builder builder = new SimpleCircleButton.Builder().normalImageRes(picId[i]).normalColor(color[i]).listener(new OnBMClickListener() {
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
                                            Intent x = new Intent(Home_Screen.this, ConectionActivity.class);
                                            startActivity(x, ActivityOptions.makeSceneTransitionAnimation(Home_Screen.this).toBundle());
                                        }}, 700);
                                    break;


                            }
                        }
                    });
                    bmb.addBuilder(builder);
                    bmb.bringToFront();
                }
                Glide.with(this).load("http://" + HttpHandler.IP + "/pictures/" + Login_Screen.sharedPref.getString("foto_perfil","")).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        b = resource;
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new PostDeals.GetMyPosts(this, posts, "all").execute();
    }

    public static int sub;
    ImageView noti;
    ListView posts;
    BoomMenuButton bmb;
    Bitmap b;
    GoogleApiClient mGoogleApiClient;
    String NodeID;
    int[] picId = {R.drawable.ico_uso, R.drawable.ico_mais,R.drawable.ico_search, R.drawable.ic_send, R.drawable.watch};
    int[] color = {Color.parseColor("#ebd3df"), Color.parseColor("#cae3f2"), Color.parseColor("#ebd3df"), Color.parseColor("#cae3f2"), Color.parseColor("#cae3f2")};
}





