package com.example.felip.softsong;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ListView;

public class Posts_Holder extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.posts_holder);
        ListView mypost = findViewById(R.id.posts);
        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("type") != null)
        {
            new PostDeals.GetMyPosts(this, mypost, bundle.getString("type")).execute();
        }
    }
}
