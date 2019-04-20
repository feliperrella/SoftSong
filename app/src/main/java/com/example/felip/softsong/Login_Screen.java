package com.example.felip.softsong;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gelitenight.waveview.library.WaveView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static android.view.View.VISIBLE;

public class Login_Screen extends AppCompatActivity {
    private WaveHelper WaveHelper;
    Button btnLogin;
    TextView txtLogin, txtSenha, txtCadastro, text;
    static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
    public static SharedPreferences sharedPref;
    ProgressBar progressBar;
    ImageView lg, sn, logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_login_screen);
        WaveView waveView = findViewById(R.id.wave);
        Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setExitTransition(fade);
            sharedPref = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
            if(!sharedPref.getString("usu", "").equals(""))
            {
                Intent my = new Intent(Login_Screen.this, Home_Screen.class);
                startActivity(my);
                finish();
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
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        if (ContextCompat.checkSelfPermission(Login_Screen.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            // No explanation needed, we can request the permission.
            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            if (ActivityCompat.shouldShowRequestPermissionRationale(Login_Screen.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                ActivityCompat.requestPermissions(Login_Screen.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }
            lg = findViewById(R.id.imgLogin);
            sn = findViewById(R.id.imgSingin);
            text = findViewById(R.id.text);
            btnLogin = findViewById(R.id.btn_Login);
            txtLogin = findViewById(R.id.txtLogin);
            txtSenha = findViewById(R.id.txtSenha);
            txtCadastro = findViewById(R.id.txtCadastro);
            logo = findViewById(R.id.logoSplash);

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
                    Intent x = new Intent(Login_Screen.this, Cadastro_Screen.class);
                    startActivity(x, ActivityOptions.makeSceneTransitionAnimation(Login_Screen.this).toBundle());
                }
            });
        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext() ,R.anim.fadein);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                logo.setAlpha(1f);
                logo.startAnimation(animation);
                logo.setVisibility(VISIBLE);
                txtLogin.setAlpha(1f);
                txtLogin.startAnimation(animation);
                txtLogin.setVisibility(VISIBLE);
                txtSenha.setAlpha(1f);
                txtSenha.startAnimation(animation);
                txtSenha.setVisibility(VISIBLE);
                btnLogin.setAlpha(1f);
                btnLogin.startAnimation(animation);
                btnLogin.setVisibility(VISIBLE);
                lg.setAlpha(1f);
                lg.startAnimation(animation);
                lg.setVisibility(VISIBLE);
                sn.setAlpha(1f);
                sn.startAnimation(animation);
                sn.setVisibility(VISIBLE);
                txtCadastro.setAlpha(1f);
                txtCadastro.startAnimation(animation);
                txtCadastro.setVisibility(VISIBLE);
            }
        }, 1400);
        WaveHelper = new WaveHelper(waveView);
        waveView.setShapeType(WaveView.ShapeType.SQUARE);
        waveView.setWaveColor(
                Color.parseColor("#B079E1"),
                Color.parseColor("#285FBAE6"));

    }

    @Override
    protected void onPause() {
        super.onPause();
        WaveHelper.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        WaveHelper.start();
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
                            editor.apply();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Intent myIntent = new Intent(Login_Screen.this, Home_Screen.class);
                                    View sharedImage = findViewById(R.id.logoSplash);
                                    ActivityOptions activityOptions = (ActivityOptions) ActivityOptions.makeSceneTransitionAnimation(Login_Screen.this, sharedImage, "logo");
                                    startActivity(myIntent, activityOptions.toBundle());
                                }
                            });
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
                finish();
            }

        }
    }


}


