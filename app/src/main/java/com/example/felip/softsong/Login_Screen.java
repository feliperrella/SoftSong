package com.example.felip.softsong;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Login_Screen extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    Button btnLogin;
    TextView txtLogin, txtSenha, txtCadastro;
    static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
    public static SharedPreferences sharedPref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login_screen);
            sharedPref = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
            String x = sharedPref.getString("usu", "");
            if(sharedPref.getString("usu", "") != "")
            {
                Intent my = new Intent(Login_Screen.this, Home_Screen.class);
                startActivity(my);
                finish();
            }
        if (ContextCompat.checkSelfPermission(Login_Screen.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(Login_Screen.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(Login_Screen.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
        if (ContextCompat.checkSelfPermission(Login_Screen.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(Login_Screen.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(Login_Screen.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
            btnLogin = (Button) findViewById(R.id.btn_Login);
            txtLogin = (TextView) findViewById(R.id.txtLogin);
            txtSenha = (TextView) findViewById(R.id.txtSenha);
            txtCadastro = (TextView) findViewById(R.id.txtCadastro);

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DoLogin doLogin = new DoLogin();
                    doLogin.execute("");
                }
            });
            txtCadastro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent my = new Intent(Login_Screen.this, Cadastro_Screen.class);
                    startActivity(my);
                }
            });
    }


    public class DoLogin extends AsyncTask<String, String, String> {
        String message = "";
        Boolean isSuccess = false;
        ClasseConexao conexao = new ClasseConexao();
        String userid = txtLogin.getText().toString();
        String password = txtSenha.getText().toString();
        @Override
        protected String doInBackground(String... params) {
            if (userid.trim().equals("") || password.trim().equals(""))
                message = "Insira seu Usuario e Senha.";
            else {
                try {
                    Connection con = conexao.CONN();
                    if (con == null) {
                        message = "Error in connection with SQL server";
                    } else {
                        String query = "select * from tblUsuario where username='" + userid + "' and senha='" + password + "'";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        if (rs.next()) {
                            message = "Login para " + userid;
                            isSuccess = true;
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("usu",userid);
                            editor.putString("id", rs.getString("IDUsuario"));
                            editor.putString("desc", rs.getString("descricao"));
                            editor.putString("email", rs.getString("email"));
                            editor.putString("foto_perfil", rs.getString("caminho_imagem"));
                            editor.commit();
                            Intent myIntent = new Intent(Login_Screen.this, Home_Screen.class);
                            startActivity(myIntent);
                            finish();
                        } else {
                            message = "Usuario ou senha incorretos.";
                            isSuccess = false;
                        }

                    }
                } catch (Exception ex) {
                    isSuccess = false;
                    message = "Desculpe, algo deu errado.";
                }
            }
            return message;
        }


        @Override
        protected void onPostExecute(String r) {
            Toast.makeText(Login_Screen.this, r, Toast.LENGTH_SHORT).show();

            if (isSuccess) {
                Toast.makeText(Login_Screen.this, r, Toast.LENGTH_SHORT).show();
            }

        }
    }


}


