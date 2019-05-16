package com.example.felip.softsong;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomMenuButton;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import jp.android.a.akira.library.okwear.OkWear;
import jp.android.a.akira.library.okwear.listener.WearReceiveListener;

import static android.support.constraint.Constraints.TAG;


//                SharedPreferences.Editor editor = Login_Screen.sharedPref.edit();
//                editor.clear();
//                editor.commit();
public class Home_Screen extends Activity implements DataClient.OnDataChangedListener,
        MessageClient.OnMessageReceivedListener,
        CapabilityClient.OnCapabilityChangedListener{
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
                Wearable.getDataClient(this).addListener(this ).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("xxxxx");
                        syncDataItem();
                    }
                });

                Glide.with(this).load("http://192.168.15.17/pictures/" + Login_Screen.sharedPref.getString("foto_perfil","")).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        b = resource;
                    }
                });

    }

    public void syncDataItem(){

        final PutDataMapRequest putRequest = PutDataMapRequest.create("/PHONE2WEAR");
        final DataMap map = putRequest.getDataMap();
        map.putString("ID", Login_Screen.sharedPref.getString("id", ""));

        Wearable.getDataClient(this).putDataItem(putRequest.asPutDataRequest())
                .addOnSuccessListener(new OnSuccessListener<DataItem>() {
                    @Override
                    public void onSuccess(DataItem dataItem) {
                        System.out.println("yyyyy");
                    }
                });


    }

    @Override
    public void onCapabilityChanged(@NonNull CapabilityInfo capabilityInfo) {

    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {

    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    protected void onResume() {
        super.onResume();
        Wearable.getDataClient(this).addListener(this );
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);

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
    Bitmap b;
    GoogleApiClient mGoogleApiClient;
    int[] picId = {R.drawable.ico_uso, R.drawable.ico_mais,R.drawable.ico_search, R.drawable.ic_send,R.drawable.ico_notification};
}





