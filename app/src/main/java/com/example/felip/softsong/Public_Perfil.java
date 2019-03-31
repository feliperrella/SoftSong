package com.example.felip.softsong;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Public_Perfil extends Activity{

    public static String[] id;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.public_perfil);
        seguir = findViewById(R.id.cardfollow);
        bloquear = findViewById(R.id.cardbloq);
        txtSeguir = findViewById(R.id.txtSeguir);
        txtBloq = findViewById(R.id.txtBloq);
        seg = findViewById(R.id.follow);
        bloq = findViewById(R.id.bloq);
        perfil = findViewById(R.id.perfil_);
        extras = findViewById(R.id.extras);
        mypost = findViewById(R.id.myposts);
        mypubs = findViewById(R.id.pubs);
        follows = findViewById(R.id.follows);
        followings = findViewById(R.id.following);
        new GetMyFollows().execute();
        new PostDeals.GetMyPosts(this, mypost, "spec").execute();
        Thread t = new Thread(){
            @Override
            public void run() {
                super.run();
                Glide.with(getApplicationContext()).load(Search.cami).dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(perfil);
            }
        };
        t.run();
        Thread x = new Thread(){
            @Override
            public void run() {
                super.run();
                WizardPagerAdapter adapter = new WizardPagerAdapter();
                ViewPager pager = (ViewPager) findViewById(R.id.seg);
                pager.setAdapter(adapter);
            }
        };
        x.run();
        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
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
        follows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Seguidores_Seguidos.usu = Search.user;
                Seguidores_Seguidos.Op = "seguidos";
                Intent my = new Intent(Public_Perfil.this, Seguidores_Seguidos.class);
                startActivity(my);
            }
        });
        followings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Seguidores_Seguidos.usu = Search.user;
                Seguidores_Seguidos.Op = "seguidores";
                Intent my = new Intent(Public_Perfil.this, Seguidores_Seguidos.class);
                startActivity(my);
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
            TextView bio = findViewById(R.id.txtBioPublic);
            bio.setText(f);
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

    class WizardPagerAdapter extends PagerAdapter {

        public Object instantiateItem(View collection, int position) {

            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.publicperfil1;
                    break;
                case 1:
                    resId = R.id.page_bio;
                    break;
            }
            return findViewById(resId);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);
        }
    }


    ImageView perfil, extras;
    ListView mypost;
    TextView mypubs, follows, followings, txtSeguir, txtBloq;
    CardView seguir, bloquear;
    RelativeLayout seg, bloq;
    String a,b,c,d,e,f,q;
}
