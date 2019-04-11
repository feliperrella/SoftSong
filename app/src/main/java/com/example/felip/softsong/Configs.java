package com.example.felip.softsong;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.Statement;

public class Configs extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configs);
        RelativeLayout about = (RelativeLayout) findViewById(R.id.about);
        RelativeLayout kill =  (RelativeLayout) findViewById(R.id.delete);


        kill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            new DoLogin().execute();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(Configs.this);
                builder.setMessage("Voce tem certeza que quer deletar a sua conta?").setPositiveButton("Sim", dialogClickListener)
                        .setNegativeButton("Não", dialogClickListener).show();
        }
        });
    }

    public class DoLogin extends AsyncTask<String, String, String> {
        String message = "";
        Boolean isSuccess = false;
        ClasseConexao conexao = new ClasseConexao();
        @Override
        protected String doInBackground(String... params) {

                try {
                    Connection con = conexao.CONN();
                    if (con == null) {
                        message = "Error in connection with SQL server";
                    } else {
                        String query = "delete from tblUsuario where IDUsuario = " + Login_Screen.sharedPref.getString("id","");
                        Statement stmt = con.createStatement();
                        stmt.executeUpdate(query);
                        message =  "Conta excluida com sucesso :(";
                        SharedPreferences.Editor editor = Login_Screen.sharedPref.edit();
                        editor.clear();
                        editor.commit();
                        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    }
                } catch (Exception ex) {
                    isSuccess = false;
                    message = "Desculpe, algo deu errado.";
                }

            return message;
        }


        @Override
        protected void onPostExecute(String r) {
            Toast.makeText(Configs.this, r, Toast.LENGTH_SHORT).show();

            if (isSuccess) {
                Toast.makeText(Configs.this, r, Toast.LENGTH_SHORT).show();
            }

        }
    }


}


