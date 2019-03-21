package com.example.felip.softsong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;


//                SharedPreferences.Editor editor = Login_Screen.sharedPref.edit();
//                editor.clear();
//                editor.commit();
public class Home_Screen extends Activity {
            public static ImageView imageView;
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.home_screen);
                plus = (ImageView) findViewById(R.id.plus);
                posts = (ListView) findViewById(R.id.posts);
                ImageView postar = (ImageView) findViewById(R.id.addpost);
                posts = (ListView) findViewById(R.id.posts);
                //Glide.with(getApplicationContext()).load(R.drawable.plus).placeholder(R.drawable.ico_uso).into(plus);
                postar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent my = new Intent(Home_Screen.this, AddPost.class);
                        startActivity(my);
                    }
                });
                ImageView sear = (ImageView) findViewById(R.id.search);
                sear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent my = new Intent(Home_Screen.this, Search.class);
                        startActivity(my);
                        finish();
                    }
                });
                inte = getIntent();
                plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent my = new Intent(Home_Screen.this, Perfil.class);
                        startActivity(my);
                        finish();
                    }
                });
                new PostDeals.GetMyPosts(this, posts, "all").execute();
    }




    public static Intent inte;
    public static String[] nomes;
    public static String[] titulos;
    public static String[] legenda;
    public static String[] data;
    public static String[] images;
    public static String[] id;
    ImageView plus;

    ListView posts;

}





