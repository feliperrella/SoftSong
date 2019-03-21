package com.example.felip.softsong;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class PostDeals {
    public static class GetMyPosts extends AsyncTask<String, String, String> {
        String message = "";
        Boolean isSuccess = false;
        ClasseConexao conexao = new ClasseConexao();
        Context cont;
        String tipoquery;
        public GetMyPosts(Context applicationContext, ListView posts, String type) {
            cont = applicationContext;
            PostDeals.posts = posts;
            tipoquery = type;
        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = conexao.CONN();
                if (con == null) {
                    message = "Error in connection with SQL server";
                } else {
                    String query = "";
                    if(tipoquery.equals("all"))
                        query = "Select (Select count(*) from tblCurtir where ID_Post = post.IDPost),usu.username, post.ID_Usuario,post.IDPost, post.titulo, post.legenda, post.data_horario, midia.caminho_imagem from tblPost as post  left join tblMidiaPost as midiapost on post.IDPost = midiapost.ID_Post left join tblMidia as midia on midiapost.ID_Midia = midia.IDMidia inner join tblUsuario as usu on post.ID_Usuario = usu.IDUsuario where post.ID_Usuario in (Select IDSeguido from tblSeguir where IDSeguidor = " + Login_Screen.sharedPref.getString("id","") + ") group by post.IDPost";
                    else if(tipoquery.equals("my"))
                        query = "Select (Select count(*) from tblCurtir where ID_Post = post.IDPost),usu.username, post.ID_Usuario,post.IDPost, post.titulo, post.legenda, post.data_horario, midia.caminho_imagem from tblPost as post left join tblMidiaPost as midiapost on post.IDPost = midiapost.ID_Post left join tblMidia as midia on midiapost.ID_Midia = midia.IDMidia inner join tblUsuario as usu on post.ID_Usuario = usu.IDUsuario where post.ID_Usuario = " + Login_Screen.sharedPref.getString("id","") + " group by post.IDPost";
                     else if(tipoquery.equals("spec"))

                         query = "Select (Select count(*) from tblCurtir where ID_Post = post.IDPost),usu.username,post.ID_Usuario,post.IDPost, post.titulo, post.legenda, post.data_horario, midia.caminho_imagem from tblPost as post left join tblMidiaPost as midiapost on post.IDPost = midiapost.ID_Post left join tblMidia as midia on midiapost.ID_Midia = midia.IDMidia inner join tblUsuario as usu on post.ID_Usuario = usu.IDUsuario where post.ID_Usuario = (Select IDUsuario from tblUsuario where username = '" + Search.user + "') group by post.IDPost";
                     Statement stmt = con.createStatement();
                     ResultSet rs = stmt.executeQuery(query);

                    String tit = "";
                    String leg = "";
                    String dat = "";
                    String idPost = "";
                    String nome = "";
                    String curt = "";
                    while(rs.next())
                    {
                        curt = curt + rs.getString("(Select count(*) from tblCurtir where ID_Post = post.IDPost)") + ",";
                        nome = nome + rs.getString("username") + ",";
                        idPost = idPost + rs.getString("IDPost") + ",";
                        tit =  tit + rs.getString("titulo") + ",";
                        leg = leg + rs.getString("legenda") + ",";
                        dat = dat + rs.getString("data_horario") + ";";
                    }
                    curt = curt.substring(0, curt.length() - 1);
                    nome = nome.substring(0, nome.length() - 1);
                    idPost = idPost.substring(0, idPost.length() - 1);
                    tit = tit.substring(0, tit.length() - 1);
                    leg = leg.substring(0, leg.length() - 1);
                    dat = dat.substring(0, dat.length() - 1);
                    curtir = curt.split(",");
                    id = idPost.split(",");
                    titulos = tit.split(",");
                    legenda = leg.split(",");
                    data = dat.split(";");
                    nomes = nome.split(",");
                    //rs = null;
                    //isSuccess = true;

                }
            } catch (Exception ex) {
                isSuccess = false;
                message = "Voce Ainda nao tem Posts";
            }
            return message;
        }


        @Override
        protected void onPostExecute(String r) {
            customAdapter customAdapter = new customAdapter(cont);
            posts.setAdapter(customAdapter);
        }
    }

    static class customAdapter extends BaseAdapter
    {

        Context context;
        public customAdapter(Context cont) {
            context = cont;
        }


        @Override
        public int getCount() {
            try {
                return id.length;
            }
            catch (Exception e){return 0;}
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View vieww = inflater.inflate(R.layout.post, null);
                final ViewPager pub = (ViewPager) vieww.findViewById(R.id.pic);
                new picid("Select caminho_imagem from tblMidia where IDMidia in (Select ID_Midia from tblMidiaPost where ID_Post =" + id[i] + ")", pub, context).execute();
                TextView tit = (TextView) vieww.findViewById(R.id.nameee);
                TextView desc = (TextView) vieww.findViewById(R.id.descric);
                TextView hor = (TextView) vieww.findViewById(R.id.horarior);
                TextView nlikes = (TextView) vieww.findViewById(R.id.nlike);
                nlikes.setText(curtir[i]);
            //new likePost(nlikes, "Carregar", id[i]).execute();
            ImageView com = (ImageView) vieww.findViewById(R.id.commentpic);
                com.setImageResource(R.drawable.comment);

                final MediaPlayer mp = (MediaPlayer) MediaPlayer.create(vieww.getContext(), R.raw.likesound);
                final ImageView likes = (ImageView) vieww.findViewById(R.id.likepic);
                likes.setImageResource(R.drawable.like);
                likes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Animation animation = AnimationUtils.loadAnimation(context, R.anim.bounce);
                        likes.startAnimation(animation);
                        if (mp.isPlaying())
                            mp.stop();
                        mp.start();
                        //new likePost(nlikes, "", id[i]).execute();
                    }
                });
                try {
                    tit.setText(nomes[i]);
                    desc.setText(nomes[i] + ": " + legenda[i]);
                    hor.setText(data[i]);
                    vieww.notify();
                } catch (Exception e) {
                }
                return vieww;


        }


    }

    static class picid extends AsyncTask<String, String, String> {
        String message = "";
        Boolean isSuccess = false;
        ClasseConexao conexao = new ClasseConexao();
        String query;
        ViewPager pubs;
        Context cont;

        public picid(String aaa, ViewPager pub, Context context) {
            query = aaa;
            pubs = pub;
            cont = context;
        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            images = null;
            try {
                Connection con = conexao.CONN();
                if (con == null) {
                    message = "Error in connection with SQL server";
                } else {
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    String im = "";
                    rs.beforeFirst();
                    while(rs.next())
                    {
                        im = im + rs.getString("caminho_imagem") + ",";
                    }
                    im = im.substring(0, im.length() - 1);
                    images = im.split(",");
                    isSuccess = true; }
            } catch (Exception ex) { isSuccess = false;message = "Hmm";
            }
            return message; }
        @Override
        protected void onPostExecute(String r) {

                    ViewPageadapt viewPageadapt = new ViewPageadapt((Activity) cont, images);
                    pubs.setAdapter(viewPageadapt);

        }}

    static class ViewPageadapt extends PagerAdapter {

        Activity activity;
        String[] imagess;
        LayoutInflater inflater;
        public ViewPageadapt(Activity activity, String[] imagens)
        {
            this.activity = activity;
            this.imagess = imagens;
        }
        @Override
        public int getCount() {
            try{return imagess.length;}
            catch(Exception e) {return 1;}
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view==object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
                inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View[] itemViews = new View[1];
            final String[] a = new String[1];
            Thread threadd = new Thread() {
                @Override
                public void run() {
                    try {
                        a[0] = imagess[position];
                        System.out.println(a[0]);
                        if(a[0].contains("jpg") || a[0].contains("png")) {
                            final View itemView = inflater.inflate(R.layout.postpics, container, false);
                            final ImageView images = (ImageView) itemView.findViewById(R.id.imgpic);
                            DisplayMetrics dis = new DisplayMetrics();
                            activity.getWindowManager().getDefaultDisplay().getMetrics(dis);
                            images.setMinimumHeight(dis.heightPixels);
                            images.setMinimumWidth(dis.widthPixels);
                            Thread thread = new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        Glide.with(activity.getApplicationContext()).load("http://192.168.15.17/pictures/" + imagess[position]).into(images);
                                    } catch (Exception e) {
                                        //Picasso.with(activity.getApplicationContext()).load(R.drawable.ops).placeholder(R.drawable.ops).into(images);
                                    }
                                }
                            };
                            images.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Glide.with(activity.getApplicationContext()).load("http://192.168.15.17/pictures/" + imagess[position]).into(images);
                                }
                            });
                            thread.run();
                            container.addView(itemView);
                            itemViews[0] = itemView;
                        }
                        else if(a[0].contains("mp4") || a[0].contains("avi"))
                        {
                            inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View itemView = inflater.inflate(R.layout.postvideo, container, false);
                            final VideoView video = (VideoView) itemView.findViewById(R.id.imgvid);
                            try {
                                if(!video.isPlaying()) {
                                    Uri uri = Uri.parse("http://192.168.15.17/pictures/" + imagess[position]);
                                    video.setVideoURI(uri);
                                }

                            }
                            catch (Exception e){}
                            video.requestFocus();
                            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {
                                    video.start();
                                }
                            });
                            video.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    boolean rodando = true;
                                    if(rodando = true)
                                        video.pause();
                                    else
                                        video.start();
                                }
                            });
                            container.addView(itemView);
                            itemViews[0] = itemView;
                        }
                        else if(a[0].contains("mp3") || a[0].contains("wav") || a[0].contains("m4a"))
                        {
                            inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View itemView = inflater.inflate(R.layout.postvideo, container, false);
                            TextView audio = (TextView) itemView.findViewById(R.id.nomeAudio);
                            audio.setText("http://192.168.15.17/pictures/" + imagess[position].substring(("http://192.168.15.17/pictures/" + imagess[position]).lastIndexOf("/") + 1));
                            final MediaPlayer mp = (MediaPlayer) MediaPlayer.create(itemView.getContext(), Uri.parse("http://192.168.15.17/pictures/" + imagess[position]));
                            container.addView(itemView);
                            itemViews[0] = itemView;
                        }
                    }
                    catch (Exception e){}
                }
            };
                threadd.run();
                return itemViews[0];
        }
    }



    public static String[] curtir = null;
    public static String[] nomes = null;
    public static String[] titulos = null;
    public static String[] legenda = null;
    public static String[] data = null;
    public static String[] images = null;
    public static String[] id = null;
    public static String count;
    static ListView posts;
}
