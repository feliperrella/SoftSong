package com.example.felip.softsong;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Public_Perfil extends Activity{

    public static String[] titulos;
    public static String[] legenda;
    public static String[] data;
    public static  String[] images;
    public static String[] id;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.public_perfil);
        seguir = (CardView) findViewById(R.id.cardfollow);
        bloquear = (CardView) findViewById(R.id.cardbloq);
        txtSeguir = (TextView) findViewById(R.id.txtSeguir);
        txtBloq = (TextView) findViewById(R.id.txtBloq);
        seg = (RelativeLayout) findViewById(R.id.follow);
        bloq = (RelativeLayout) findViewById(R.id.bloq);
        perfil = (ImageView) findViewById(R.id.perfil_);
        extras = (ImageView) findViewById(R.id.extras);
        mypost = (ListView) findViewById(R.id.myposts);
        mypubs = (TextView) findViewById(R.id.pubs);
        follows = (TextView) findViewById(R.id.follows);
        followings = (TextView)findViewById(R.id.following);
        try {
            perfil.setImageBitmap(Search.caminho);
        }
        catch (Exception e){}
        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        new GetMyFollows().execute();
        new PostDeals.GetMyPosts(this, mypost, "spec").execute();
        seg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtSeguir.getText().equals("Seguir"))
                {
                    seguir.startAnimation(animation);
                    seg.setBackgroundColor(Color.parseColor("#33CC7A"));
                    txtSeguir.setText("Seguindo");
                    q =  "Insert into tblSeguir values(" + Login_Screen.sharedPref.getString("id", "") + ", " + "(Select IDUsuario from tblUsuario where username ='" + Search.user +"'))";
                    new Segue().execute();
                    new GetMyFollows().execute();
                }
                else if(txtSeguir.getText().equals("Seguindo"))
                {
                    seguir.startAnimation(animation);
                    seg.setBackgroundColor(Color.parseColor("#0099CC"));
                    txtSeguir.setText("Seguir");
                    q =  "Delete from tblSeguir where IDSeguidor = " + Login_Screen.sharedPref.getString("id", "") + " and IDSeguido = " + "(Select IDUsuario from tblUsuario where username ='" + Search.user +"')";
                    new Segue().execute();
                    new GetMyFollows().execute();
                } }
        });

        bloq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtBloq.getText().equals("Bloquear"))
                {
                    bloquear.startAnimation(animation);
                    txtBloq.setText("Bloqueado");
                    q =  "Delete from tblSeguir where IDSeguidor =" + Login_Screen.sharedPref.getString("id", "") + " and IDSeguido = (Select IDUsuario from tblUsuario where username ='" + Search.user + "') ";
                    new Segue().execute();
                    q = "Insert into tblBloqueio values(" + Login_Screen.sharedPref.getString("id", "") + ", " + "(Select IDUsuario from tblUsuario where username ='" + Search.user +"'))";//
                    new GetMyFollows().execute();
                }
                else if(txtBloq.getText().equals("Bloqueado"))
                {
                    bloquear.startAnimation(animation);
                    txtBloq.setText("Bloquear");
                    q =  "Delete from tblBloqueio where IDBloqueador = " + Login_Screen.sharedPref.getString("id", "") + " and IDBloqueado = " + "(Select IDUsuario from tblUsuario where username ='" + Search.user +"')";
                    new Segue().execute();
                    new GetMyFollows().execute();
                }
            }
        });
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(Public_Perfil.this);
                builder.setMessage("Bio: " + f).setPositiveButton("Ok", dialogClickListener).show();
            }
        });
        }


    class Segue extends AsyncTask<String, String, String> {
        String message = "";
        Boolean isSuccess = false;
        ClasseConexao conexao = new ClasseConexao();
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {

                    try {
                        Connection con = conexao.CONN();
                        if (con == null) {
                            message = "Error in connection with SQL server";
                        } else {
                            Statement stmt = con.createStatement();
                            stmt.executeUpdate(q);
                            isSuccess = true; }
                    } catch (Exception ex) { isSuccess = false;message = "Hmm";
                    }
            return message; }
        @Override
        protected void onPostExecute(String r) { }}



    public class GetMyFollows extends AsyncTask<String, String, String> {
        String message = "";
        Boolean isSuccess = false;
        ClasseConexao conexao = new ClasseConexao();
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = conexao.CONN();
                if (con == null) {
                    message = "Error in connection with SQL server";
                } else {
                    String sub1 = "(Select Count(IDSeguidor) from tblSeguir where IDSeguidor = (Select IDUsuario from tblUsuario where username ='" + Search.user + "'))";
                    String sub2 = "(Select Count(IDSeguido) from tblSeguir where IDSeguido = (Select IDUsuario from tblUsuario where username ='" + Search.user +"'))";
                    String sub3 = "(Select IDBloqueado from tblBloqueio where IDBloqueador = " + Login_Screen.sharedPref.getString("id", "") + " and IDBloqueado = (Select IDUsuario from tblUsuario where username ='" + Search.user + "'))";
                    String sub4 = "(Select IDSeguido from tblSeguir where IDSeguidor = " + Login_Screen.sharedPref.getString("id", "") + " and IDSeguido = (Select IDUsuario from tblUsuario where username = '" + Search.user + "'))";
                    String sub5 = "(Select descricao from tblUsuario where username = '" + Search.user + "')";
                    String query = "select Count(*)," + sub1 + "," + sub2 + "," + sub3 + "," + sub4 + "," + sub5 + " from tblPost where ID_Usuario = (Select IDUsuario from tblUsuario where username ='" + Search.user + "')";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs != null && rs.next()){
                        a = (rs.getString("Count(*)"));
                        b = (rs.getString(sub1));
                        c = (rs.getString(sub2));
                        d = (rs.getString(sub3));
                        e = (rs.getString(sub4));
                        f = (rs.getString(sub5));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mypubs.setText(a);
                            }
                        });
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                followings.setText(c);}});
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                follows.setText(b);    }
                        });

                    }
                    isSuccess = true;
                }
            } catch (Exception ex) {
                isSuccess = false;
                message = "Falha ao conectar-se ao banco :(";
            }
            return message;
        }


        @Override
        protected void onPostExecute(String r) {
            //Toast.makeText(Perfil.this, r, Toast.LENGTH_SHORT).show();

            final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
            if(d != null)
            {
                bloquear.startAnimation(animation);
                txtBloq.setText("Bloqueado");
                seg.setEnabled(false);
                seg.setBackgroundColor(Color.parseColor("#BDCCC2"));
            }
            if(e != null)
            {
                seguir.startAnimation(animation);
                seg.setBackgroundColor(Color.parseColor("#33CC7A"));
                txtSeguir.setText("Seguindo");
            }

        }
    }

    ImageView perfil, extras;
    ListView mypost;
    TextView mypubs, follows, followings, txtSeguir, txtBloq;
    CardView seguir, bloquear;
    RelativeLayout seg, bloq;
    String a,b,c,d,e,f,q;
}
