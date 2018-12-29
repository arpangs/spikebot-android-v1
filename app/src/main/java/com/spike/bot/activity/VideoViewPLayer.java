package com.spike.bot.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.spike.bot.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import tcking.github.com.giraffeplayer2.VideoView;

/**
 * Created by Sagar on 22/3/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class VideoViewPLayer extends AppCompatActivity{

    VideoView videoView;
    String videoUrl = "",name="";
    boolean isMute=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        videoUrl =  getIntent().getExtras().getString("videoUrl");
        name =  getIntent().getExtras().getString("name");

        setTitle(name);
        //String link = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";

        videoView = (VideoView) findViewById(R.id.video_view);
        videoView.setVideoPath(videoUrl).getPlayer().start();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_playback, menu);;
        MenuItem muteIcon = menu.findItem(R.id.action_mute);
        if(isMute)
            muteIcon.setIcon(R.drawable.icn_mute);
        else
            muteIcon.setIcon(R.drawable.icn_unmute);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_screenshot) {

            if (checkPermission()) {

                loadView(videoView);
            }
            /*Bitmap bitmap = SavePixels(0,0,videoView.getWidth(),videoView.getHeight());*/

            return true;
        }else if(id == R.id.action_mute){

            isMute =!isMute;
            videoView.getPlayer().setMute(!isMute);
            invalidateOptionsMenu();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    String[] permissions = new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};

    private boolean checkPermission() {
        List arrayList = new ArrayList();
        for (String str : this.permissions) {
            if (ContextCompat.checkSelfPermission(this, str) != 0) {
                arrayList.add(str);
            }
        }
        if (arrayList.isEmpty()) {
            return true;
        }
        ActivityCompat.requestPermissions(this, (String[]) arrayList.toArray(new String[arrayList.size()]), 100);
        return false;
    }

    public void loadView(VideoView cardView){

        try {

            cardView.setDrawingCacheEnabled(true);
           // Bitmap bitmap =  loadBitmapFromView(cardView);
            Bitmap bitmap =  videoView.getDrawingCache();
            cardView.setDrawingCacheEnabled(false);


            String mPath =
                    Environment.getExternalStorageDirectory().toString() + "/camera.jpg";

            File imageFile = new File(mPath);
            FileOutputStream outputStream = new
                    FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
