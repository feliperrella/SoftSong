package com.example.felip.softsong;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cdflynn.android.library.checkview.CheckView;

import static android.view.View.VISIBLE;

public class Cadastro_Screen extends Activity {

    static final int IMAGE_GALLEY_REQUEST = 20;
    static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
    String pictureDirectoryPath;
    public static String sftpCaminho;
    public static File file;
    public ImageView img;
    public InputStream inputstream;
    String query;
    ResultSet rs;
    public static int count;
    public static String Destino;
    TextView txtNome;
    TextView txtEmail;
    TextView txtSenha, txtNm, tchau;
    ProgressBar pb;
    String nome, nm;
    CheckView check;
    String senha;
    String Email;
    int countt;
    String Verifica_Email;
    String Verifica_User;
    public static Bitmap image;
    AnimationDrawable an;
    CardView btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.cadastro_screen);
        btn = findViewById(R.id.btnCadas);
        Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(fade);
        txtNome = findViewById(R.id.txtNome);
        check = findViewById(R.id.check);
        tchau = findViewById(R.id.txtTchau);
        pb = findViewById(R.id.progress_bar1);
        txtNm = findViewById(R.id.txtNm);
        txtEmail = findViewById(R.id.txtEmail);
        txtSenha = findViewById(R.id.txtSenha);
        img = findViewById(R.id.imagedocad);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Photopicker = new Intent(Intent.ACTION_PICK);
                File PictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                pictureDirectoryPath = PictureDirectory.getPath();
                sftpCaminho = pictureDirectoryPath;
                Uri data = Uri.parse(pictureDirectoryPath);
                Photopicker.setDataAndType(data, "image/*");
                startActivityForResult(Photopicker, IMAGE_GALLEY_REQUEST);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*nome = txtNome.getText().toString();
                Email = txtEmail.getText().toString();
                senha = txtSenha.getText().toString();
                nm = txtNm.getText().toString();
                if(nome.equals("") || Email.equals("") || senha.equals(""))
                {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink_anim);
                    Toast.makeText(getApplicationContext(), "Preencha todos os campos", Toast.LENGTH_LONG).show();
                }
                else
                {
                    width();
                    resto();
                    Cadastra cadastra = new Cadastra();
                    cadastra.execute("");
                }*/
                Intent Home = new Intent(Cadastro_Screen.this, StartActivity.class);
                startActivity(Home, ActivityOptions.makeSceneTransitionAnimation(Cadastro_Screen.this).toBundle());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ActivityCompat.shouldShowRequestPermissionRationale(Cadastro_Screen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_GALLEY_REQUEST) {
                //Uri imageUri = data.getData();
                sftpCaminho = data.getData().getPath().substring(5);
                file = new File(sftpCaminho);
                try {

                    //Toast.makeText(Cadastro.this, data.getData().getPath(), Toast.LENGTH_SHORT).show();
                    inputstream = getContentResolver().openInputStream(data.getData());
                    image = BitmapFactory.decodeStream(inputstream);
                    img.setImageBitmap(image);
                } catch (FileNotFoundException e) {
                    Toast.makeText(this, "Nao foi possivel abrir a foto selecionada.", Toast.LENGTH_LONG).show();
                } catch (IOException e) {

                }
            }
        }
    }

    public class Cadastra extends AsyncTask<String, String, String> {
        String message = "";
        Boolean isSuccess = false;

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
                count += 1;
                //query = "Insert into tblUsuario() values(" + count + ",'" + nome + "','" + senha + "','" + Email + "','2019-02-26',' ',' ',' ','" + nm + "',' ','" + (count + 1) + ".jpg')";
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                //SocketsClientUpload socket = new SocketsClientUpload(sftpCaminho);
                      //socket.run();
//                    ApacheFTPClient apache = new ApacheFTPClient();
//                    apache.run();
//                    apache.Cadastro(file, Cadastro_Screen.this, (count + 1));

                try {
                    HttpHandler sh = new HttpHandler();
                    // Making a request to url and getting response
                    String url = "http://" + HttpHandler.IP +"/InserirCadastro.php?user=" + nome + "&email=" + Email + "&nome=" + nm + "&senha=" + senha + "&caminho=" + (count + 1) + ".jpg";
                    Log.i("Feliperrella", url);
                    String jsonStr = sh.makeServiceCall(url);
                    Log.i("Feliperrella", jsonStr);
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray info = jsonObj.getJSONArray("result");
                    for (int i = 0; i < info.length(); i++) {
                        JSONObject c = info.getJSONObject(i);

                        message = "Cadastro Realizado com sucesso";
                        SharedPreferences.Editor editor = Login_Screen.sharedPref.edit();
                        editor.putString("id", c.getString("is"));
                        editor.putString("usu", nome);
                        idd = c.getString("is");
                        editor.putString("email", Email);
                        editor.putString("foto_perfil", c.getString("is") + ".jpg");
                        editor.apply();
                        Log.i("Feliperrella", Login_Screen.sharedPref.getString("usu",""));
                    }
                    new HTTPServer((idd) + "." + FilenameUtils.getExtension(String.valueOf(file)), encodedImage).execute();

                } catch (Exception e) {
                    System.out.println(e.getMessage().toString());
                    e.printStackTrace();
                }
                return message;

        }



        @Override
        protected void onPostExecute(String r) {

                pb.animate().alpha(0f)
                        .setDuration(250)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                check.check();
                            }
                        })
                        .start();
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Intent Home = new Intent(Cadastro_Screen.this, StartActivity.class);
                        startActivity(Home, ActivityOptions.makeSceneTransitionAnimation(Cadastro_Screen.this).toBundle());
                    }
                }, 1400);

            }

        }

    public void width()
    {
        ValueAnimator anim = ValueAnimator.ofInt(btn.getMeasuredWidth(), 110);

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = btn.getLayoutParams();
                layoutParams.width = val;
                btn.requestLayout();
            }
        });
        anim.setDuration(250);
        anim.start();
    }

    public void resto()
    {
        tchau.animate().alpha(0f)
                .setDuration(250)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        pb.setAlpha(1f);
                        pb.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
                        pb.setVisibility(VISIBLE);
                    }
                })
                .start();
    }
    String idd = "";
    }
