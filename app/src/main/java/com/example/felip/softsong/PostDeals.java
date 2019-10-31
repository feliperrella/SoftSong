package com.example.felip.softsong;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.relex.circleindicator.CircleIndicator;

public class PostDeals {
    public static class GetMyPosts extends AsyncTask<String, String, String> {
        String message = "";
        Boolean isSuccess = false;
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
                HttpHandler sh = new HttpHandler();
                    String url = "";
                    if(tipoquery.equals("all"))
                        url = "http://" + HttpHandler.IP +"/PostDeals.php?id=" + (Login_Screen.sharedPref.getString("id",""));
                    else if(tipoquery.equals("my"))
                        url = "http://" + HttpHandler.IP +"/PostDealsMy.php?id=" + (Login_Screen.sharedPref.getString("id",""));
                    else if(tipoquery.equals("spec"))
                        url = "http://" + HttpHandler.IP +"/PostDealsSpec.php?id=" + (Login_Screen.sharedPref.getString("id","")) + "&username=" + Search.user;
                    String jsonStr = sh.makeServiceCall(url);
                    Log.i("Feliperrella", jsonStr);
                    curtir.clear();
                    nomes.clear();
                    id.clear();
                    legenda.clear();
                    likou.clear();
                    data.clear();
                    picture.clear();

                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray info = jsonObj.getJSONArray("posts");
                        for (int i = 0; i < info.length(); i++) {
                            JSONObject c = info.getJSONObject(i);
                            picture.add(c.getString("caminho_imagem"));
                            curtir.add(c.getString("curtidas"));
                            nomes.add(c.getString("username"));
                            id.add(c.getString("IDPost"));
                            legenda.add(c.getString("legenda"));
                            data.add(c.getString("data_horario"));
                            likou.add(c.getString("likou"));
                        }
                    }
                    catch (Exception e){}
                }
                    //rs = null;
                    //isSuccess = true;


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
                return id.size();
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
            final ViewHolder holder;
            if(view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.post, null);
                holder = new ViewHolder();
                holder.perfil = view.findViewById(R.id.postperfil);
                holder.comment = view.findViewById(R.id.commentpic);
                holder.tit = view.findViewById(R.id.nameee);
                holder.desc = view.findViewById(R.id.descric);
                holder.hor = view.findViewById(R.id.horarior);
                holder.nlikes = view.findViewById(R.id.nlike);
                holder.com = view.findViewById(R.id.commentpic);
                holder.likes = view.findViewById(R.id.likepic);
                view.setTag(holder);
            }
            else {
                holder = (ViewHolder) view.getTag();
            }
            final ViewPager pub = view.findViewById(R.id.pic);
            Thread t = new Thread(){
                @Override
                public void run() {
                    super.run();
                    new picid(id.get(i), pub, context).execute();
                }
            };
            t.run();
            indicator = view.findViewById(R.id.indicator);
            Glide.with(view.getContext()).load("http://" + HttpHandler.IP + "/pictures/" + picture.get(i)).signature(new StringSignature(String.valueOf(System.currentTimeMillis()))).into(holder.perfil);
            holder.nlikes.setText(Integer.parseInt(curtir.get(i)) == 1 ? curtir.get(i) + " curtida" : curtir.get(i) + " curtidas");
            holder.com.setImageResource(R.drawable.comment);
            if(likou.get(i).equals("1"))
                holder.likes.setImageResource(R.drawable.heart);
            else
                holder.likes.setImageResource(R.drawable.heart_white);
            holder.likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(likou.get(i).equals("1"))
                    {
                        Animation s = AnimationUtils.loadAnimation(context, R.anim.fadein);
                        holder.likes.startAnimation(s);
                        holder.likes.setImageResource(R.drawable.heart_white);
                        String g = (id.get(i));
                        new LikeDeslike(g, holder.nlikes).execute();
                    }
                    else
                    {
                        Animation s = AnimationUtils.loadAnimation(context, R.anim.fadein);
                        holder.likes.startAnimation(s);
                        holder.likes.setImageResource(R.drawable.heart);
                        String g = (id.get(i));
                        new LikeDeslike(g, holder.nlikes).execute();
                    }
                }
            });
            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater inf = (LayoutInflater)  context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View v = inf.inflate(R.layout.comments, null);
                    final EditText com = v.findViewById(R.id.WriteComment);
                    final ImageView send = v.findViewById(R.id.send);
                    final ListView comments = v.findViewById(R.id.comments);
                    new getComments("get", id.get(i), comments, context, com.getText().toString()).execute();
                    send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Animation animation = AnimationUtils.loadAnimation(context, R.anim.fadein);
                            send.startAnimation(animation);
                            new getComments("set", id.get(i), comments, context, com.getText().toString()).execute();
                        }
                    });
                    send.setImageResource(R.drawable.ic_send);
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setView(v);
                    alert.create();
                    alert.show();
                }
            });
            try {
                holder.tit.setText(nomes.get(i));
                holder.desc.setText(nomes.get(i) + ": " + legenda.get(i));
                holder.hor.setText(data.get(i));
                view.notify();
            } catch (Exception e) {
            }
            return view;


        }

        static class ViewHolder {
            private ImageView perfil;
            private ImageView comment;
            private TextView tit;
            private TextView desc;
            private TextView hor;
            private TextView nlikes;
            private ImageView com;
            private ImageView likes;
        }


    }

    static class picid extends AsyncTask<String, String, String> {
        String message = "";
        Boolean isSuccess = false;
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
                String url = "http://" + HttpHandler.IP + "/getPicturesPost.php?id="+query;
                Log.i("Feliperrella", url);
                HttpHandler sh = new HttpHandler();
                String jsonStr = sh.makeServiceCall(url);
                Log.i("Feliperrella", jsonStr);
                if (jsonStr != null) {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray info = jsonObj.getJSONArray("data");
                    String im = "";
                    for (int i = 0; i < info.length(); i++) {
                        JSONObject c = info.getJSONObject(i);
                        im = im + c.getString("caminho_imagem") + ",";
                    }
                    im = im.substring(0, im.length() - 1);
                    images = im.split(",");
                    isSuccess = true;
                }
            } catch (Exception ex) { isSuccess = false;message = "Hmm";
            }
            return message; }
        @Override
        protected void onPostExecute(String r) {

                    ViewPageadapt viewPageadapt = new ViewPageadapt((Activity) cont, images);
                    pubs.setAdapter(viewPageadapt);
                    indicator.setViewPager(pubs);
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
                                        Thread x = new Thread(){
                                            @Override
                                            public void run() {
                                                super.run();
                                                Glide.with(activity.getApplicationContext()).load("http://" + HttpHandler.IP + "/pictures/" + imagess[position]).override(600, 200).into(images);
                                            }
                                        };
                                        x.run();
                                    } catch (Exception e) {
                                        //Picasso.with(activity.getApplicationContext()).load(R.drawable.ops).placeholder(R.drawable.ops).into(images);
                                    }
                                }
                            };
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
                                    Uri uri = Uri.parse("http://" + HttpHandler.IP + "/pictures/" + imagess[position]);
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
                            final View itemView = inflater.inflate(R.layout.postaudio, container, false);
                            TextView audio = (TextView) itemView.findViewById(R.id.nomeAudio);
                            final ImageView play = (ImageView) itemView.findViewById(R.id.playbutton);
                            audio.setText(imagess[position]);
                            final MediaPlayer[] mp = new MediaPlayer[1];
                            Thread x = new Thread(){
                                @Override
                                public void run() {
                                    super.run();
                                    mp[0] = (MediaPlayer) MediaPlayer.create(itemView.getContext(), Uri.parse("http://" + HttpHandler.IP + "/pictures/" + imagess[position]));
                                }
                            };
                            x.run();
                            play.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(!mp[0].isPlaying()) {
                                        mp[0].start();
                                    }
                                    else
                                        mp[0].pause();
                                }
                            });

                            container.addView(itemView);
                            itemViews[0] = itemView;
                        }
                        else
                        {
                            inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View itemView = inflater.inflate(R.layout.postarquivos, container, false);
                            final TextView name = (TextView) itemView.findViewById(R.id.nomeArquivo);
                            final ImageView pic = (ImageView) itemView.findViewById(R.id.doc);
                            final String nome = (imagess[position]);
                            pic.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    new DownloadFileFromURL("http://" + HttpHandler.IP + "/pictures/" + imagess[position], activity).execute();
                                }
                            });
                            Thread t = new Thread()
                            {
                                @Override
                                public void run() {
                                    name.setText(nome);
                                    //pic.setImageResource(R.drawable.ico_doc);
                                }
                            };
                            t.run();
                            //Toast.makeText(getApplicationContext(), "A", Toast.LENGTH_LONG).show();
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

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            ((ViewPager) container).removeView((View) object);
        }
    }

    static class DownloadFileFromURL extends AsyncTask<String, String, String> {

        String x;
        String y;
        Activity activit;
        public DownloadFileFromURL(String s, Activity activity) {
            x = s;
            activit = activity;
        }

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            y = x.substring(x.lastIndexOf("/"), x.length());
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(x);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream
                OutputStream output = new FileOutputStream(Environment
                        .getExternalStorageDirectory().toString()
                        + "/Download/" + y);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            System.out.println("Foi");
            Toast.makeText(activit, "Arquivo baixado com sucesso, cheque a pasta Downloads", Toast.LENGTH_LONG).show();
        }



    }

    static class LikeDeslike extends AsyncTask<String, String, String>
    {
        String ID;
        String n;
        TextView nlikess;
        public LikeDeslike(String idd, TextView nlikes) {
            ID = idd;
            nlikess = nlikes;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                HttpHandler sh = new HttpHandler();
                String url = "http://" + HttpHandler.IP + "/LikeDeslike.php?id=" + Login_Screen.sharedPref.getString("id", "") + "&post=" + ID;
                String ret = sh.makeServiceCall(url);
                if (ret != null) {
                        n = ret;
                    Thread t = new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            nlikess.setText(Integer.parseInt(n) == 1 ? n + " curtida" : n + " curtidas");
                        }
                    };
                    t.run();
                    return n;
                }
            }
            catch (Exception e){}
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }

    static class getComments extends AsyncTask<String,String,String>
    {
        String idhere, o, come;
        ListView l;
        Context contextx;
        int count;
        public getComments(String s1, String s, ListView comments, Context context, String com) {
            o = s1;
            idhere = s;
            l = comments;
            contextx = context;
            come = com;
        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... strings) {
            try {
                final HttpHandler sh = new HttpHandler();
                    comments.clear();
                    data_horario.clear();
                    username.clear();
                    caminho_.clear();
                    if(o == "get") {
                        String jsonStr = sh.makeServiceCall("http://" + HttpHandler.IP + "/getComments.php?id=" + idhere);
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray info = jsonObj.getJSONArray("data");
                        for (int i = 0; i < info.length(); i++) {
                            JSONObject c = info.getJSONObject(i);
                            comments.add(c.getString("comentario"));
                            data_horario.add(c.getString("data_horario"));
                            username.add(c.getString("username"));
                            caminho_.add(c.getString("caminho_imagem"));
                        }
                        customA adapter = new customA(contextx);
                        l.setAdapter(adapter);
                    }
                    else
                    {
                        final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        final Date date = new Date();
                        Thread t = new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    sh.makeServiceCall("http://" + HttpHandler.IP + "/addComment.php?id=" + (Login_Screen.sharedPref.getString("id", "")) + "&post=" + idhere + "&comment=" + come);
                                    new getComments("get", idhere, l, contextx, come).execute();
                                }
                                catch (Exception e){}
                            }
                        };
                        t.run();

                    }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    static class customA extends BaseAdapter{
        Context context;
        public customA(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return comments.size();
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
            if(view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.individual_comment, null);
            }
            final TextView txt = view.findViewById(R.id.comm);
            final ImageView img = view.findViewById(R.id.perfilComment);
            final TextView hor = view.findViewById(R.id.commhor);
            Glide.with(view.getContext()).load("http://" + HttpHandler.IP + "/pictures/" + caminho_.get(i)).placeholder(R.drawable.ico_uso).into(img);
            Thread x = new Thread(){
                @Override
                public void run() {
                    super.run();
                    String a = "http://"+ HttpHandler.IP + "/pictures/" + caminho_.get(i);
                    txt.setText(username.get(i) + ": " + comments.get(i));
                    hor.setText(data_horario.get(i));
                }
            };
            x.run();
            return view;
        }
    }


    private static ArrayList<String> picture = new ArrayList<>(),curtir = new ArrayList<>(), nomes = new ArrayList<>(),legenda = new ArrayList<>(),id = new ArrayList<>();
    static ArrayList<String> data = new ArrayList<>();
    static ArrayList<String> comments = new ArrayList<>(), data_horario = new ArrayList<>(), username = new ArrayList<>(), caminho_ = new ArrayList<>(), likou = new ArrayList<>();
    private static String[] images = null;
    @SuppressLint("StaticFieldLeak")
    private static CircleIndicator indicator;
    @SuppressLint("StaticFieldLeak")
    private static ListView posts;
    static ImageView perfil, comment,com, likes;
    static TextView tit, desc,hor,nlikes;
    static MediaPlayer mp = null;
    static ViewPager pub;
}
