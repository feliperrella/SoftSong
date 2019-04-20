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
        Glide.with(getApplicationContext()).load("http://192.168.15.17/pictures/" + Login_Screen.sharedPref.getString("foto_perfil","")).into(perfil);
    }


    class Load extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            ClasseConexao conexao = new ClasseConexao();
            Connection conn = null;
            try {
                conn = conexao.CONN();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try (CallableStatement cs = conn.prepareCall("{call }")) {
                boolean resultSetAvailable = false;
                int numberOfResultsProcessed = 0;
                try {
                    resultSetAvailable = cs.execute();
                } catch (SQLException sse) {
                    System.out.printf("Exception thrown on execute: %s%n%n", sse.getMessage());
                    numberOfResultsProcessed++;
                }
                int updateCount = -2;  // initialize to impossible(?) value
                while (true) {
                    boolean exceptionOccurred = true;
                    do {
                        try {
                            if (numberOfResultsProcessed > 0) {
                                resultSetAvailable = cs.getMoreResults();
                            }
                            exceptionOccurred = false;
                            updateCount = cs.getUpdateCount();
                        } catch (SQLException sse) {
                            System.out.printf("Current result is an exception: %s%n%n", sse.getMessage());
                        }
                        numberOfResultsProcessed++;
                    } while (exceptionOccurred);

                    if ((!resultSetAvailable) && (updateCount == -1)) {
                        break;  // we're done
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    ImageView perfil;
    TextView perfilID;
}


