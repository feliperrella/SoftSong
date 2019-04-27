package com.example.felip.softsong;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

public class ArquivosDiversos {

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

}
