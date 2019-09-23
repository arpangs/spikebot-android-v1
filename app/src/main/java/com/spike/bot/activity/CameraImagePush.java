package com.spike.bot.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.spike.bot.R;

/**
 * Created by Sagar on 15/6/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class CameraImagePush extends AppCompatActivity{

    private ImageView img_cam_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera_url_view);

        /*  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        img_cam_view = (ImageView) findViewById(R.id.img_cam_view);
        //img_cam_view

        String camera_url = getIntent().getStringExtra("camera_url");
        String camera_body = getIntent().getStringExtra("camera_body");

        //Clear.clearCache(Picasso.get());

//        Picasso.with(this).load(camera_url)
//                .placeholder(R.drawable.loader2)
//                .into(img_cam_view);


        Glide.with(this)
                .load(camera_url)
                .error(R.drawable.loader2)
                .placeholder(R.drawable.loader2)
                .into(img_cam_view);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    //6005622234
}
