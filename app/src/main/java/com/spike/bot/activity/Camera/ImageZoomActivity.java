package com.spike.bot.activity.Camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kp.core.ActivityHelper;
import com.kp.core.DateHelper;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.BuildConfig;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.customview.PlayStateBroadcatingVideoView;
import com.spike.bot.customview.TouchImageView;
import com.spike.bot.model.CameraRecordingResmodel;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sagar on 25/12/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class ImageZoomActivity extends AppCompatActivity {

    public static boolean isnotification = false, ispause = false;
    public Button btnReport;
    public Bitmap bitmapShare;
    public String imgName = "", imgUrl = "", imgDate = "", home_controller_device_id = "",
            camera_id = "", url = "", file1 = "", file2 = "";
    Toolbar toolbar;
    ProgressBar progressBar, progressBar1;
    TouchImageView imageViewZoom;
    FloatingActionButton fabShare;
    PlayStateBroadcatingVideoView videoView;
    TextView txt_title, txt_time;
    ImageView img_playvideo, img_pausevideo;
    ImageView imgGifVideo;
    FrameLayout placeholder;
    int skiptime, currentposition = 0, bufferpercentage = 0;
    List<String> recoringfiles;
    MediaController mc;
    String gifUrl = "";
    FrameLayout mFramlayout;
    boolean isGifLoad = false;

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


        if (home_controller_device_id.equalsIgnoreCase(""))
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
        progressBar1 = findViewById(R.id.progressBar1);
        fabShare = findViewById(R.id.fabShare);
        btnReport = findViewById(R.id.btnReport);
        txt_title = findViewById(R.id.txt_title);
        txt_time = findViewById(R.id.txt_time);
        img_playvideo = findViewById(R.id.img_playvideo);
        img_pausevideo = findViewById(R.id.img_pausevideo);

        imgGifVideo = findViewById(R.id.img_gifvideo);

        mFramlayout = findViewById(R.id.frameVideo);

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
                        } else {
                            progressBar1.setVisibility(View.VISIBLE);
                            playvideo(skiptime, recoringfiles);
                        }
                    } else {
                        if (gifUrl.length() > 0) {

                            btnReport.setVisibility(View.GONE);

                            isGifLoad = true;

                            imgGifVideo.setVisibility(View.VISIBLE);
                            mFramlayout.setVisibility(View.GONE);
                            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(ImageZoomActivity.this);
                            circularProgressDrawable.setStrokeWidth(7f);
                            circularProgressDrawable.setColorSchemeColors(R.color.blueplus);
                            circularProgressDrawable.setCenterRadius(30f);
                            circularProgressDrawable.start();
                            Glide
                                    .with(ImageZoomActivity.this) // replace with 'this' if it's in activity
//                                .load("https://upload.wikimedia.org/wikipedia/commons/thumb/2/2c/Rotating_earth_(large).gif/200px-Rotating_earth_(large).gif")
                                    .load(gifUrl)
                                    .asGif()
                                    .placeholder(circularProgressDrawable)
                                    .error(R.drawable.img_not_load) // show error drawable if the image is not a gif
                                    .into(imgGifVideo);
                        }

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

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().callReport(imgUrl, home_controller_device_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
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
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(ImageZoomActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(ImageZoomActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void CameraRecordingPlay() {
        if (!ActivityHelper.isConnectingToInternet(ImageZoomActivity.this)) {
            Toast.makeText(ImageZoomActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(ImageZoomActivity.this, "Please wait...", false);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().CameraRecordingPlay(camera_id, imgDate, home_controller_device_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    ChatApplication.logDisplay("getCamerarecoring onSuccess " + result.toString());
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

                    if (code == 200) {
                        gifUrl = "https://beta.spikebot.io:443/generate-gif/" + imgDate + "/" + camera_id + "/" + home_controller_device_id + "/1.gif";
                    } else {
                        ChatApplication.showToast(ImageZoomActivity.this, "Video in progress please check after some time");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(ImageZoomActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(ImageZoomActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void playvideo(int skiptime, List<String> recordingfiles) {
        ChatApplication.logDisplay("video skip time" + " " + skiptime);

        ispause = false;

        String uriPath1 = file1;
        Uri uri1 = Uri.parse(uriPath1);
        videoView.setVideoURI(uri1);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

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
        ChatApplication.logDisplay("url for gif image " + gifUrl);

        Uri bmpUri = null;
        if (CameraGridActivity.checkPermission(this)) {

//            if (isGifLoad) {
//
//                ShareGifImage();
//
//            } else {
//                bmpUri = getLocalBitmapUri(imageViewZoom);
//            }

            bmpUri = getLocalBitmapUri(imageViewZoom);


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

    public void ShareGifImage() {

        Glide.with(ImageZoomActivity.this)
                .load(gifUrl)
                .asGif()
                .into(new Target<GifDrawable>() {
                    @Override
                    public void onLoadStarted(Drawable placeholder) {

                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onResourceReady(GifDrawable resource, GlideAnimation<? super GifDrawable> glideAnimation) {
                        storeImage(resource);
                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {

                    }

                    @Override
                    public void getSize(SizeReadyCallback cb) {

                    }

                    @Override
                    public Request getRequest() {
                        return null;
                    }

                    @Override
                    public void setRequest(Request request) {

                    }

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onStop() {

                    }

                    @Override
                    public void onDestroy() {

                    }
                });


    }

    private void storeImage(GifDrawable image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            return;
        }
        try {
            FileOutputStream output = new FileOutputStream(pictureFile);
            FileInputStream input = new FileInputStream(String.valueOf(image));

            FileChannel inputChannel = input.getChannel();
            FileChannel outputChannel = output.getChannel();

            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            output.close();
            input.close();
//            Toast.makeText(ImageZoomActivity.this, "Image Downloaded", Toast.LENGTH_SHORT).show();

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            Uri uri = Uri.fromFile(pictureFile);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share Image"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Christmas"); /*getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Christmas/c");*/
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs())
                return null;
        }

        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_MERRY_CHRISTMAS_" + Calendar.getInstance().getTimeInMillis() + ".gif");
        return mediaFile;
    }
}
