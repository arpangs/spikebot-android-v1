package com.spike.bot.camera;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowInsets;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.Constants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerDelegate;
import cn.nodemedia.NodePlayerView;


public class CameraPlayer extends AppCompatActivity implements View.OnClickListener{

   public static String[] permissions = new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};

    NodePlayerView player;
    NodePlayer nodePlayer;
    ZoomLayout zoomlayout;
    boolean isMute=false;
    private boolean isCloudConnect;
    //LinearLayout ll_player;

    RelativeLayout relativeLayout;
    String mMediaUrl;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_player);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //rtmp://home.deepfoods.net:11111/live/livestream3
        mMediaUrl = getIntent().getExtras().getString("videoUrl");
        String name = getIntent().getExtras().getString("name");
        boolean isCloudConnect = getIntent().getExtras().getBoolean("isCloudConnect");
        setTitle(name);

        //   ll_player = (LinearLayout)findViewById(R.id.ll_player);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        player = (NodePlayerView)findViewById(R.id.player);
        player.setUIViewContentMode(NodePlayerView.UIViewContentMode.ScaleAspectFit);
        player.setRenderType(NodePlayerView.RenderType.TEXTUREVIEW);

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        nodePlayer = new NodePlayer(this);


        //String url = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
        //String url = "rtmp://13.127.153.122:1936/live/livestream1";
       //  nodePlayer.setInputUrl("rtsp://192.168.175.68:554/user=admin&password=spike123&channel=1&stream=0.sdp?real_stream--rtp-caching=100");
        nodePlayer.setInputUrl(mMediaUrl);
        nodePlayer.setAudioEnable(true);
        nodePlayer.setPlayerView(player);
        nodePlayer.setMaxBufferTime(5);

        nodePlayer.setNodePlayerDelegate(new NodePlayerDelegate() {
            @Override
            public void onEventCallback(NodePlayer player, int event, String msg) {
                if(player.isPlaying() && msg.equals("NetStream.Buffer.Full")){
                    ChatApplication.logDisplay("camera log is dispaly "+event+" "+msg);
                    ChatApplication.logDisplay("camera log is dispaly "+ player.getDuration());
                    ChatApplication.logDisplay("camera log is dispaly "+ nodePlayer.isLive());
                    ChatApplication.logDisplay("camera log is dispaly buffer "+ nodePlayer.getBufferPercentage());
                    ChatApplication.logDisplay("camera log is dispaly buffer "+ nodePlayer.getBufferPercentage());
                    ChatApplication.logDisplay("camera log is dispaly buffer isPlaying "+ nodePlayer.isPlaying());

                    progressDialog.dismiss();
                }
            }
        });

        zoomlayout=(ZoomLayout)findViewById(R.id.zoomLayout);
        zoomlayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                zoomlayout.init(CameraPlayer.this);
                return false;
            }
        });

        //ll_player.setOnTouchListener(new TouchHandler());
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
    Bitmap b = null;
    public void saveScreenShot(){
        //  View v1 = getWindow().getDecorView().getRootView();
        b = ScreenshotUtils.getScreenShot(player.getRenderView()); //player.getRenderView());

        // player.getRenderView().setDrawingCacheEnabled(false);

        //If bitmap is not null
        if (b != null) {
        }

        //  showScreenShotImage(b);//show bitmap over imageview
        File saveFile = ScreenshotUtils.getMainDirectoryName(this);//get the path to save screenshot
        File file = ScreenshotUtils.store(b, "screenshot" + new Date().getTime() + ".jpg", saveFile);//save the screenshot to selected path
        //shareScreenshot(file);//finally share screenshot

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
        //if(nodePlayer.isLive() || nodePlayer.isPlaying()) {
        nodePlayer.stop();
        //}
    }

    @Override
    protected void onResume() {
        super.onResume();
        nodePlayer.start();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        player.setUIViewContentMode(NodePlayerView.UIViewContentMode.ScaleAspectFit);
//        nodePlayer
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

                //saveScreenShot();
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
           // Bitmap bitmap =  loadBitmapFromView(cardView);
           // Bitmap bitmap =  SavePixels(100,100,200,200);
//            Bitmap bitmap =  saveOpenGL(200,200);
            Bitmap bitmap = Constants.takescreenshotOfRootView(this.getWindow().getDecorView().getRootView(),null);
            // Bitmap bitmap =  player.getDrawingCache();
            cardView.setDrawingCacheEnabled(false);

            String mPath = Environment.getExternalStorageDirectory().toString() + "/camera.jpg";

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

    /**
     *
     * @param v
     * @return
     */
    public Bitmap loadBitmapFromView(View v) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        v.measure(View.MeasureSpec.makeMeasureSpec(dm.widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(dm.heightPixels, View.MeasureSpec.EXACTLY));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        Bitmap returnedBitmap = Bitmap.createBitmap(v.getMeasuredWidth(),
                v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(returnedBitmap);
        v.draw(c);

        return returnedBitmap;
    }

    /**
     * SavePixels
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     */
    public static Bitmap SavePixels(int x, int y, int w, int h) {
        EGL10 egl = (EGL10) EGLContext.getEGL();
        GL10 gl = (GL10) egl.eglGetCurrentContext().getGL();
        int b[] = new int[w * (y + h)];
        int bt[] = new int[w * h];
        IntBuffer ib = IntBuffer.wrap(b);
        ib.position(0);
        gl.glReadPixels(x, 0, w, y + h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ib);
        for (int i = 0, k = 0; i < h; i++, k++) {
            for (int j = 0; j < w; j++) {
                int pix = b[i * w + j];
                int pb = (pix >> 16) & 0xff;
                int pr = (pix << 16) & 0x00ff0000;
                int pix1 = (pix & 0xff00ff00) | pr | pb;
                bt[(h - k - 1) * w + j] = pix1;
            }
        }

        return Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);
    }


    /**
     *
     * @param width
     * @param height
     * @return
     */

    public static Bitmap saveOpenGL(int width,int height){

        EGL10 egl = (EGL10) EGLContext.getEGL();
        GL10 gl = (GL10) egl.eglGetCurrentContext().getGL();

        int screenshotSize = width * height;
        ByteBuffer bb = ByteBuffer.allocateDirect(screenshotSize * 4);
        bb.order(ByteOrder.nativeOrder());
        gl.glReadPixels(0, 0, width, height, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, bb);
        int pixelsBuffer[] = new int[screenshotSize];
        bb.asIntBuffer().get(pixelsBuffer);

        for (int i = 0; i < screenshotSize; ++i) {
            pixelsBuffer[i] = ((pixelsBuffer[i] & 0xff00ff00)) | ((pixelsBuffer[i] & 0x000000ff) << 16) | ((pixelsBuffer[i] & 0x00ff0000) >> 16);
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixelsBuffer, screenshotSize-width, -width, 0, 0, width, height);
        return bitmap;
    }


    /**
     *
     * @param CamView
     */
    public void TakeScreenshot(View CamView){    //THIS METHOD TAKES A SCREENSHOT AND SAVES IT AS .jpg
        Random num = new Random();
        int nu=num.nextInt(1000); //PRODUCING A RANDOM NUMBER FOR FILE NAME
        CamView.setDrawingCacheEnabled(true); //CamView OR THE NAME OF YOUR LAYOUR
        CamView.buildDrawingCache(true);
        Bitmap bmp = Bitmap.createBitmap(CamView.getDrawingCache());
        CamView.setDrawingCacheEnabled(false); // clear drawing cache
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapdata = bos.toByteArray();
        ByteArrayInputStream fis = new ByteArrayInputStream(bitmapdata);

        String picId=String.valueOf(nu);
        String myfile="Ghost"+picId+".jpeg";

        File dir_image = new  File(Environment.getExternalStorageDirectory()+//<---
                File.separator+"Ultimate Entity Detector");          //<---
        dir_image.mkdirs();                                                  //<---
        //^IN THESE 3 LINES YOU SET THE FOLDER PATH/NAME . HERE I CHOOSE TO SAVE
        //THE FILE IN THE SD CARD IN THE FOLDER "Ultimate Entity Detector"

        try {
            File tmpFile = new File(dir_image,myfile);
            FileOutputStream fos = new FileOutputStream(tmpFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = fis.read(buf)) > 0) {
                fos.write(buf, 0, len);
            }
            fis.close();
            fos.close();
            Toast.makeText(getApplicationContext(),
                    "The file is saved at :SD/Ultimate Entity Detector", Toast.LENGTH_LONG).show();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
