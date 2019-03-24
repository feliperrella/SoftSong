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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

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
        perfil = (ImageView) findViewById(R.id.perfil);
        user = (EditText) findViewById(R.id.Auser);
        nome = (EditText) findViewById(R.id.Anome);
        senha = (EditText) findViewById(R.id.Asenha);
        bio = (EditText) findViewById(R.id.abio);
        email = (EditText) findViewById(R.id.Aemail);
        celular = (EditText) findViewById(R.id.Acelular);
        Button update = (Button) findViewById(R.id.btnAtualizar);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new update().execute();
            }
        });
        Glide.with(getApplicationContext()).load("http://192.168.15.17/pictures/" + Login_Screen.sharedPref.getString("foto_perfil","")).placeholder(R.drawable.ico_uso).into(perfil);
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
                Connection connection = conexao.CONN();
                if(connection != null)
                {
                    Statement stmt = connection.createStatement();
                    final ResultSet rs = stmt.executeQuery("Select * from tblUsuario where IDUsuario = " + Login_Screen.sharedPref.getString("id",""));
                    Thread thread = new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                if(rs != null && rs.next()) {
                                    user.setText(rs.getString("username"));
                                    nome.setText(rs.getString("nome"));
                                    senha.setText(rs.getString("senha"));
                                    email.setText(rs.getString("email"));
                                    celular.setText(rs.getString("tel"));
                                    bio.setText(rs.getString("descricao"));
                                }
                            } catch (SQLException e) {
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
                Connection connection = conexao.CONN();
                if(connection != null)
                {
                    Statement stmt = connection.createStatement();
                    String teste = "Update tblUsuario set username='" + user.getText() + "', nome='" + nome.getText() + "', senha='" + senha.getText() + "', email='" + email.getText() + "', tel='" + celular.getText() + "', caminho_imagem='" + Login_Screen.sharedPref.getString("foto_perfil","") + "' where IDUsuario=" + Login_Screen.sharedPref.getString("id","");
                    stmt.executeUpdate("Update tblUsuario set username='" + user.getText() + "', nome='" + nome.getText() + "', senha='" + senha.getText() + "', email='" + email.getText() + "', tel='" + celular.getText() + "', caminho_imagem='" + Login_Screen.sharedPref.getString("foto_perfil","") + "', descricao='" + bio.getText() + "' where IDUsuario=" + Login_Screen.sharedPref.getString("id",""));
                    SharedPreferences.Editor editor = Login_Screen.sharedPref.edit();
                    editor.putString("usu", String.valueOf(user.getText()));
                    editor.putString("email", String.valueOf(email.getText()));
                    editor.putString("desc", String.valueOf(bio.getText()));
                    String[] newpath = Login_Screen.sharedPref.getString("foto_perfil", "").split("-");
                    System.out.println(newpath[0] + file.getName());
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                    String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                    new HTTPServer(newpath[0] + file.getName(), encodedImage).execute();
                    editor.putString("foto_perfil", newpath[0] + file.getName());
                    editor.commit();
                }
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
    ClasseConexao conexao = new ClasseConexao();
    static Bitmap image;
    InputStream inputstream;
    static File file;
    static String sftpCaminho;
    static final int IMAGE_GALLEY_REQUEST = 20;
    String pictureDirectoryPath;
    ImageView perfil;
    EditText user,nome,senha, email, celular, bio;
}
