package com.example.felip.softsong;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

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

public class atualizar_perfil extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atualizar_usuario);
        perfil = findViewById(R.id.perfilAtualizar);
        user = findViewById(R.id.Auser);
        nome = findViewById(R.id.Anome);
        senha = findViewById(R.id.Asenha);
        bio = findViewById(R.id.abio);
        email = findViewById(R.id.Aemail);
        celular = findViewById(R.id.Acelular);
        Button update = findViewById(R.id.btnAtualizar);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new update().execute();
            }
        });
        Thread foto = new Thread(){
            @Override
            public void run() {
                super.run();
                Glide.with(getApplicationContext()).load("http://" + HttpHandler.Media + "/pictures/" + Login_Screen.sharedPref.getString("foto_perfil","")).placeholder(R.drawable.ico_uso).into(perfil);
            }
        };
        foto.run();
        new Load().execute();
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Photopicker = new Intent(Intent.ACTION_PICK);
                File PictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                pictureDirectoryPath = PictureDirectory.getPath();
                sftpCaminho = pictureDirectoryPath;
                Uri data = Uri.parse(pictureDirectoryPath);
                Photopicker.setDataAndType(data, "image/*");
                startActivityForResult(Photopicker, IMAGE_GALLEY_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ActivityCompat.shouldShowRequestPermissionRationale(atualizar_perfil.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_GALLEY_REQUEST) {
                //Uri imageUri = data.getData();
                sftpCaminho = data.getData().getPath().substring(5);
                file = new File(sftpCaminho);
                try {

                    //Toast.makeText(Cadastro.this, data.getData().getPath(), Toast.LENGTH_SHORT).show();
                    inputstream = getContentResolver().openInputStream(data.getData());
                    image = BitmapFactory.decodeStream(inputstream);
                    perfil.setImageBitmap(image);
                } catch (FileNotFoundException e) {
                    Toast.makeText(this, "Nao foi possivel abrir a foto selecionada.", Toast.LENGTH_LONG).show();
                } catch (IOException e) {

                }
            }
        }
    }

    class Load extends AsyncTask<String, String, String>
    {
        @Override
        protected String doInBackground(String... strings) {
            try {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall("http://" + HttpHandler.IP + "/loadInfo.php?id=" + Login_Screen.sharedPref.getString("id", ""));
            Log.i("Feliperrella", jsonStr);
            if(jsonStr != null)
                {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    final JSONArray jsonArray = jsonObject.getJSONArray("info");
                    Thread thread = new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                for (int i=0; i < jsonArray.length(); i++) {
                                    JSONObject c = jsonArray.getJSONObject(i);
                                    user.setText(c.getString("username"));
                                    nome.setText(c.getString("nome"));
                                    senha.setText(c.getString("senha"));
                                    email.setText(c.getString("email"));
                                    celular.setText(c.getString("tel"));
                                    bio.setText(c.getString("descricao"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.run();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


    }

    class update extends AsyncTask<String,String,String>
    {
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... strings) {
            try {
                HttpHandler sh = new HttpHandler();
                    SharedPreferences.Editor editor = Login_Screen.sharedPref.edit();
                    editor.putString("usu", String.valueOf(user.getText()));
                    editor.putString("email", String.valueOf(email.getText()));
                    editor.putString("desc", String.valueOf(bio.getText()));
                    String newpath = Login_Screen.sharedPref.getString("id", "");
                    System.out.println(newpath + file.getName());
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                    String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                    //new HTTPServer(newpath + "-" + file.getName(), encodedImage).execute();
                sh.makeServiceCall("http://" + HttpHandler.IP + "/SavePicture.php?name=" + newpath + "-" + file.getName() + "&image=" + encodedImage);

                editor.putString("foto_perfil", newpath + "-" + file.getName());
                    editor.apply();
                    Glide.get(getApplicationContext()).clearMemory();
                    String call = "http://" + HttpHandler.IP + "/updateUser.php?id=" + Login_Screen.sharedPref.getString("id", "") +  "&user=" + user.getText() + "&nome=" + nome.getText() + "&email=" + email.getText() + "&senha=" + senha.getText() +  "&telefone=" + email.getText() + "&caminho_imagem=" + Login_Screen.sharedPref.getString("foto_perfil", "") + "&descricao=" + bio.getText();
                    sh.makeServiceCall(call);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(), "Informaçoes atualizadas com sucesso", Toast.LENGTH_LONG).show();
        }
    }
    static Bitmap image;
    InputStream inputstream;
    static File file;
    static String sftpCaminho;
    static final int IMAGE_GALLEY_REQUEST = 20;
    String pictureDirectoryPath;
    ImageView perfil;
    EditText user,nome,senha, email, celular, bio;
}
