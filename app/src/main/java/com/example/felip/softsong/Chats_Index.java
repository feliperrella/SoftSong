package com.example.felip.softsong;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class Chats_Index extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.chats_index);
        Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(fade);
        perfil = findViewById(R.id.perfil);
        perfilID = findViewById(R.id.idUsuario);
        perfilID.setText(Login_Screen.sharedPref.getString("usu",""));
        Glide.with(getApplicationContext()).load("http://" + HttpHandler.IP + "/pictures/" + Login_Screen.sharedPref.getString("foto_perfil","")).into(perfil);
    }
    ImageView perfil;
    TextView perfilID;
}


