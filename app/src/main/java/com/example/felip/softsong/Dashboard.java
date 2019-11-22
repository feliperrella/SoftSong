package com.example.felip.softsong;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class Dashboard extends Activity {
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
            setContentView(R.layout.dashboard);
            Login_Screen.sharedPref = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
            usu = (TextView) findViewById(R.id.usu);
            perfil = (ImageView) findViewById(R.id.imageView5);
            Glide.with(getApplicationContext()).load("http://" + HttpHandler.Media + "/pictures/" + Login_Screen.sharedPref.getString("foto_perfil","")).into(perfil);
            email = (TextView) findViewById(R.id.email);
            usuario =  findViewById(R.id.relativeUsu);
            logout = findViewById(R.id.relativeLogout);
            configs =  findViewById(R.id.relativeConfigs);
            notifications = findViewById(R.id.relativeNotifications);
            email.setText(Login_Screen.sharedPref.getString("email",""));
            usu.setText(Login_Screen.sharedPref.getString("usu", ""));
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    SharedPreferences.Editor editor = Login_Screen.sharedPref.edit();
                                    editor.clear();
                                    editor.apply();
                                    Log.i("Feliperrella", Login_Screen.sharedPref.getString("id", ""));
                                    Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                    finish();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
                    builder.setMessage("Voce tem certeza que quer sair?").setPositiveButton("Sim", dialogClickListener)
                            .setNegativeButton("Não", dialogClickListener).show();
                }
            });

            configs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent my = new Intent(Dashboard.this, Configs.class);
                    startActivity(my);
                }
            });

            usuario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent my = new Intent(Dashboard.this, atualizar_perfil.class);
                    startActivity(my);
                }
            });

        }







    TextView usu,email;
    ConstraintLayout usuario, logout, configs, notifications;
    ImageView perfil;
    }
