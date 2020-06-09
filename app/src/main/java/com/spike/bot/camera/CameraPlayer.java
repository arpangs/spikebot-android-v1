package com.spike.bot.camera;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerDelegate;
import cn.nodemedia.NodePlayerView;


public class CameraPlayer extends AppCompatActivity implements View.OnClickListener{

   public static String[] permissions = new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};

    NodePlayerView player;
    NodePlayer nodePlayer;
    ZoomLayout zoomlayout;
    RelativeLayout relativeLayout;
    ProgressDialog progressDialog;
    String mMediaUrl;
    boolean isMute=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_player);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //rtmp://home.deepfoods.net:11111/live/livestream3
        mMediaUrl = getIntent().getExtras().getString("videoUrl");
        String name = getIntent().getExtras().getString("name");
        setTitle(name);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        player = findViewById(R.id.player);
        player.setUIViewContentMode(NodePlayerView.UIViewContentMode.ScaleAspectFit);
        player.setRenderType(NodePlayerView.RenderType.TEXTUREVIEW);

        relativeLayout = findViewById(R.id.relativeLayout);

        nodePlayer = new NodePlayer(this);

       //  nodePlayer.setInputUrl("rtsp://192.168.175.68:554/user=admin&password=spike123&channel=1&stream=0.sdp?real_stream--rtp-caching=100");
        nodePlayer.setInputUrl(mMediaUrl);
        nodePlayer.setAudioEnable(true);
        nodePlayer.setPlayerView(player);
        nodePlayer.setMaxBufferTime(5);

        ChatApplication.logDisplay("media is "+mMediaUrl);

        nodePlayer.setNodePlayerDelegate(new NodePlayerDelegate() {
            @Override
            public void onEventCallback(NodePlayer player, int event, String msg) {
                if(player.isPlaying() && msg.equals("NetStream.Buffer.Full")){
                    player.seekTo(1);
                    progressDialog.dismiss();
                }
            }
        });


        zoomlayout=findViewById(R.id.zoomLayout);
        zoomlayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                zoomlayout.init(CameraPlayer.this);
                return false;
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        nodePlayer.start();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nodePlayer.stop();
        nodePlayer.release();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        nodePlayer.stop();
        nodePlayer.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        nodePlayer.stop();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_playback, menu);
        MenuItem muteIcon = menu.findItem(R.id.action_mute);

        Drawable drawable1 = menu.findItem(R.id.action_screenshot).getIcon();

        Drawable drawable = DrawableCompat.wrap(drawable1);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this,R.color.automation_white));
        menu.findItem(R.id.action_screenshot).setIcon(drawable);

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
                saveScreenShot2();
            }
            return true;
        }else if(id == R.id.action_mute){

            isMute =!isMute;
            nodePlayer.setAudioEnable(!isMute);
            invalidateOptionsMenu();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveScreenShot2(){
        loadView(player);
    }

    public void loadView(View cardView){
        try {
            cardView.setDrawingCacheEnabled(true);

            //Define a bitmap with the same size as the view
            Bitmap returnedBitmap = Bitmap.createBitmap(player.getRenderView().getWidth(), player.getRenderView().getHeight(),Bitmap.Config.ARGB_8888);
            //Bind a canvas to it
            Canvas canvas = new Canvas(returnedBitmap);
            //Get the view's background
            Drawable bgDrawable =player.getRootView().getRootView().getBackground();
            if (bgDrawable!=null)
                bgDrawable.draw(canvas);
            else
                canvas.drawColor(Color.WHITE);
            player.getRenderView().draw(canvas);

            Bitmap bitmap = Constants.takescreenshotOfRootView(player.getRenderView().getRootView(),null);
            cardView.setDrawingCacheEnabled(false);

            String mPath = Environment.getExternalStorageDirectory().toString() + "/"+ System.currentTimeMillis()+"camera.jpg";

            File imageFile = new File(mPath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            ChatApplication.showToast(this,"Saved.");

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
