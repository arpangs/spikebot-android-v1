package com.spike.bot.activity.Camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kp.core.ActivityHelper;
import com.kp.core.DateHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.BuildConfig;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.customview.PlayStateBroadcatingVideoView;
import com.spike.bot.customview.TouchImageView;
import com.spike.bot.model.CameraRecordingResmodel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sagar on 25/12/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class ImageZoomActivity extends AppCompatActivity{

    Toolbar toolbar;
    ProgressBar progressBar,progressBar1;
    TouchImageView imageViewZoom;
    FloatingActionButton fabShare;
    PlayStateBroadcatingVideoView videoView;
    TextView txt_title, txt_time;
    ImageView img_playvideo, img_pausevideo;
    FrameLayout placeholder;
    public Button btnReport;
    public Bitmap bitmapShare;
    public String imgName = "", imgUrl = "", imgDate = "", home_controller_device_id = "",
            camera_id = "", url = "", file1 = "", file2 = "";
    public static boolean isnotification = false,ispause=false;
    int skiptime, currentposition = 0, bufferpercentage = 0;
    List<String> recoringfiles;
    MediaController mc;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zoom);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        imgUrl = getIntent().getStringExtra("camera_url");
        imgName = getIntent().getStringExtra("imgName");
        imgDate = getIntent().getStringExtra("imgDate");
        camera_id = getIntent().getStringExtra("camera_id");
        isnotification = getIntent().getBooleanExtra("notification", false);
        home_controller_device_id = getIntent().getStringExtra("home_controller_id");
        home_controller_device_id = Common.getPrefValue(ImageZoomActivity.this, Constants.home_controller_id);
        setUi();

        CameraRecordingPlay();
    }

    @Override
    public boolean onSupportNavigateUp() {
        ispause = false;
        onBackPressed();
        return true;
    }

    private void setUi() {

        placeholder = findViewById(R.id.placeholder);
        imageViewZoom = findViewById(R.id.img_slider_item_zoom);
        videoView = findViewById(R.id.videoView);
        progressBar = findViewById(R.id.progressBar);
        progressBar1=  findViewById(R.id.progressBar1);
        fabShare = findViewById(R.id.fabShare);
        btnReport = findViewById(R.id.btnReport);
        txt_title = findViewById(R.id.txt_title);
        txt_time = findViewById(R.id.txt_time);
        img_playvideo = findViewById(R.id.img_playvideo);
        img_pausevideo = findViewById(R.id.img_pausevideo);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

            }
        });

        videoView.setPlayPauseListener(new PlayStateBroadcatingVideoView.PlayPauseListener() {

            @Override
            public void onPlay() {
                System.out.println("Play!");
                img_pausevideo.setVisibility(View.VISIBLE);
                img_playvideo.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPause() {
                System.out.println("Pause!");
                img_playvideo.setVisibility(View.VISIBLE);
                img_pausevideo.setVisibility(View.INVISIBLE);
            }
        });





        // imageViewZoom.setVisibility(View.GONE);
        // videoView.setVisibility(View.VISIBLE);

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to report false image ?", new ConfirmDialog.IDialogCallback() {
                    @Override
                    public void onConfirmDialogYesClick() {
                        callReport();
                    }

                    @Override
                    public void onConfirmDialogNoClick() {
                    }
                });
                newFragment.show(getFragmentManager(), "dialog");

            }
        });

        img_playvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!recoringfiles.isEmpty()) {
                        imageViewZoom.setVisibility(View.INVISIBLE);
                        videoView.setVisibility(View.VISIBLE);
                      //  videoView.setZOrderOnTop(false);
                        img_pausevideo.setVisibility(View.VISIBLE);
                        img_playvideo.setVisibility(View.INVISIBLE);
                        ChatApplication.logDisplay("video play" + " " + ispause);
                        if (ispause) {
                            int videoposition = currentposition;
                            videoView.seekTo(videoposition);
                            videoView.start();
                        } else{
                            progressBar1.setVisibility(View.VISIBLE);
                            playvideo(skiptime, recoringfiles);
                        }

                    } else {
                        ChatApplication.showToast(ImageZoomActivity.this, "Video in progress please check after some time");
                        img_pausevideo.setVisibility(View.INVISIBLE);
                        img_playvideo.setVisibility(View.VISIBLE);
                        imageViewZoom.setVisibility(View.VISIBLE);
                        videoView.setVisibility(View.INVISIBLE);
                      //  videoView.setZOrderOnTop(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        img_pausevideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_pausevideo.setVisibility(View.INVISIBLE);
                img_playvideo.setVisibility(View.VISIBLE);
              //  videoView.setZOrderOnTop(true);
                if (videoView.isPlaying()) {
                    currentposition = videoView.getCurrentPosition();
                    videoView.pause();
                    ispause = true;
                }
            }
        });


        Glide.with(this)
                .load("")
                .asBitmap()
                .load(imgUrl)
                .dontAnimate()
                .override(200, 200)
                .skipMemoryCache(true)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imageViewZoom.setImageBitmap(resource);
                        progressBar.setVisibility(View.GONE);
                        imageViewZoom.setZoom(2.1f);
                        bitmapShare = resource;

                    }
                });

        Date today = null;//2018-01-12 19:40:07
        if (!isnotification) {
            try {
                today = DateHelper.parseDateSimple(imgDate, DateHelper.DATE_YYYY_MM_DD_HH_MM_SS);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


       /* String strDateOfTime = DateHelper.getDayString(today);
        String[] strDateOfTimeTemp = strDateOfTime.split(" ");

        String dateTime = "";
        if (strDateOfTimeTemp.length > 2) {
            dateTime = strDateOfTimeTemp[0] + System.getProperty("line.separator") + strDateOfTimeTemp[1] + " " + strDateOfTimeTemp[2];
        } else {
            dateTime = strDateOfTimeTemp[0] + " " + strDateOfTimeTemp[1];
        }*/

        if (!TextUtils.isEmpty(imgDate)) {
            String dateString = Constants.logConverterDateImagezoom(Long.parseLong(imgDate));
            String[] strDateOfTimeTemp = dateString.split(" ");

            String dateTime = "";
            if (strDateOfTimeTemp[0].equalsIgnoreCase(Constants.getCurrentDate())) {
                dateTime = strDateOfTimeTemp[1] + " " + strDateOfTimeTemp[2];
            } else {
                dateTime = strDateOfTimeTemp[1] + strDateOfTimeTemp[2] + System.getProperty("line.separator") + strDateOfTimeTemp[0];
            }
            if (isnotification) {
                txt_time.setVisibility(View.GONE);
            } else {
                txt_time.setVisibility(View.VISIBLE);
                txt_time.setText("" + dateString);
            }

            //toolbar.setSubtitle("" + dateString);
        }
        if (isnotification) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            img_playvideo.setVisibility(View.GONE);
            txt_title.setText("Detected Photo");
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            img_playvideo.setVisibility(View.VISIBLE);
            txt_title.setText("" + imgName);
        }


        //  toolbar.setTitle("" + imgName);

        toolbar.setSubtitleTextColor(getResources().getColor(R.color.automation_white));

        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cancel, menu);
        MenuItem menucancel = menu.findItem(R.id.action_cancel);
        if (isnotification) {
            menucancel.setVisible(true);
        } else {
            menucancel.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_cancel) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* use for image detection is false found than send to AWS server ..*/
    private void callReport() {

        if (!ActivityHelper.isConnectingToInternet(ImageZoomActivity.this)) {
            Toast.makeText(ImageZoomActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        String imageName = "";

        String[] separated = imgUrl.split("/");

        if (separated != null && separated.length > 0) {
            imageName = separated[separated.length - 1];
        }

        JSONObject object = new JSONObject();
        try {
            /*" {
 "image_name":"out_frame_007__BA-QA-_-1560797169248_DbZww2wfE.png",
 "image_url":"https://spikebot.s3.amazonaws.com/backup/1562238755935_7-4TuHSwb-vip@gmail.com/python_images/20180128/out_frame_007__BA-QA-_-1560797169248_DbZww2wfE.png"
}"*/
            //  object.put("image_name", "" + imageName);
            object.put("image_url", imgUrl);
            object.put("home_controller_device_id", home_controller_device_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ActivityHelper.showProgressDialog(ImageZoomActivity.this, "Please wait...", false);
        String url = Constants.CLOUD_SERVER_URL + Constants.reportFalseImage;

        ChatApplication.logDisplay("Report false image" + " " + url + " " + object.toString());
        new GetJsonTask(ImageZoomActivity.this, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    ChatApplication.showToast(ImageZoomActivity.this, "" + message);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(ImageZoomActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    public void CameraRecordingPlay() {
        if (!ActivityHelper.isConnectingToInternet(ImageZoomActivity.this)) {
            Toast.makeText(ImageZoomActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
       /* object.put("camera_id", "1589017520398_4HMt9ItNu");
        object.put("timestamp", "1589455800");*/

        // {"camera_id":"1589017646842_cZu6CrE-K","timestamp":"1589781664"}

        JSONObject object = new JSONObject();
        try {
            object.put("camera_id", camera_id);
            object.put("timestamp", imgDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ActivityHelper.showProgressDialog(ImageZoomActivity.this, "Please wait...", false);
        url = ChatApplication.url + Constants.camerarecording;
        ChatApplication.logDisplay("Camera Recording" + " " + url + " " + object.toString());


        new GetJsonTask(ImageZoomActivity.this, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    ChatApplication.logDisplay("getCamerarecoring onSuccess " + result.toString());
                    //  ChatApplication.showToast(ImageZoomActivity.this, "" + message);
                    ActivityHelper.dismissProgressDialog();
                    CameraRecordingResmodel camerarecordingres = Common.jsonToPojo(result.toString(), CameraRecordingResmodel.class);
                    skiptime = camerarecordingres.getData().getSkipTime();
                    recoringfiles = camerarecordingres.getData().getCameraFiles();

                    url = ChatApplication.url/* + "/static/storage/volume/pi/1589017520398_4HMt9ItNu-2020-05-14_17-00-01.mp4"*/;
                    if (!recoringfiles.isEmpty()) {
                        for (int i = 0; i < recoringfiles.size(); i++) {
                            if (recoringfiles.size() > 1) {
                                file1 = url + "/" + recoringfiles.get(0);
                            } else {
                                file1 = url + "/" + recoringfiles.get(0);
                            }
                            ChatApplication.logDisplay("Value of element " + file1);
                        }


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    ActivityHelper.dismissProgressDialog();
                } //finally {
//                    ActivityHelper.dismissProgressDialog();
//                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(ImageZoomActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    public void playvideo(int skiptime, List<String> recordingfiles) {
        ChatApplication.logDisplay("video skip time" + " " +  skiptime);

        ispause =false;
       /* if (img_pausevideo.getVisibility() == View.VISIBLE) {
            int videoposition = currentposition;
            videoView.seekTo(videoposition);
            videoView.start();
        } else{*/

       // }
/*
        if (img_pausevideo.getVisibility() == View.VISIBLE) {
            if (!videoView.isPlaying()) {
                int videoposition = currentposition;
                videoView.seekTo(videoposition);
                videoView.start();
            }
        }*/

        /*else {
            int time = (int) TimeUnit.MINUTES.toMillis(skiptime);
            videoView.seekTo(time);
            videoView.start();
        }*/

        String uriPath1 = file1;
        Uri uri1 = Uri.parse(uriPath1);
        videoView.setVideoURI(uri1);
     //   videoView.setZOrderOnTop(true);


        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
              //  videoView.animate().alpha(1);
               // placeholder.setVisibility(View.GONE);

                mc = new MediaController(ImageZoomActivity.this);
                mc.setMediaPlayer(videoView);
                videoView.setMediaController(mc);


                videoView.setBackgroundColor(Color.TRANSPARENT);
                progressBar1.setVisibility(View.GONE);
                int time = (int) TimeUnit.SECONDS.toMillis(skiptime);
                videoView.seekTo(time);
                videoView.start();
                mc.show(0);
            }

        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                img_pausevideo.setVisibility(View.GONE);
                img_playvideo.setVisibility(View.VISIBLE);
                currentposition = 0;
            }
        });


    }

    /*data sharing*/
    private void shareImage() {
        ChatApplication.logDisplay("url is " + imgUrl);
        if (CameraGridActivity.checkPermission(this)) {

            Uri bmpUri = getLocalBitmapUri(imageViewZoom);
            if (bmpUri != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                shareIntent.setType("image/*");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share Image"));

                File fdelete = new File(bmpUri.getPath());
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        System.out.println("file Deleted :" + bmpUri.getPath());
                    } else {
                        System.out.println("file not Deleted :" + bmpUri.getPath());
                    }
                }
            } else {
                ChatApplication.showToast(this, "Invalid image url please check again");

            }
        }
    }

    // Returns the URI path to the Bitmap displayed in specified ImageView
    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            // See https://youtu.be/5xVh-7ywKpE?t=25m25s
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            // **Warning:** This will fail for API >= 24, use a FileProvider as shown below instead.

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                bmpUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
            } else {
                bmpUri = Uri.fromFile(file);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ispause = false;
    }
}
