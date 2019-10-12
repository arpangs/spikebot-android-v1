package com.spike.bot.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kp.core.ActivityHelper;
import com.kp.core.DateHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.BuildConfig;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.Constants;
import com.spike.bot.customview.TouchImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by Sagar on 25/12/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class ImageZoomActivity extends AppCompatActivity {

    Toolbar toolbar;
    ProgressBar progressBar;
    TouchImageView imageViewZoom;
    FloatingActionButton fabShare;
    public Button btnReport;
    public Bitmap bitmapShare;
    public String imgName = "", imgUrl = "", imgDate = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zoom);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        imgUrl = getIntent().getStringExtra("imgUrl");
        imgName = getIntent().getStringExtra("imgName");
        imgDate = getIntent().getStringExtra("imgDate");

        setUi();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setUi() {

        imageViewZoom = findViewById(R.id.img_slider_item_zoom);
        progressBar = findViewById(R.id.progressBar);
        fabShare = findViewById(R.id.fabShare);
        btnReport = findViewById(R.id.btnReport);


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
                        imageViewZoom.setZoom(2.3f);
                        bitmapShare = resource;

                    }
                });

        Date today = null;//2018-01-12 19:40:07
        try {
            today = DateHelper.parseDateSimple(imgDate, DateHelper.DATE_YYYY_MM_DD_HH_MM_SS);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String strDateOfTime = DateHelper.getDayString(today);
        String[] strDateOfTimeTemp = strDateOfTime.split(" ");

        String dateTime = "";
        if (strDateOfTimeTemp.length > 2) {
            dateTime = strDateOfTimeTemp[0] + System.getProperty("line.separator") + strDateOfTimeTemp[1] + " " + strDateOfTimeTemp[2];
        } else {
            dateTime = strDateOfTimeTemp[0] + " " + strDateOfTimeTemp[1];
        }

        toolbar.setTitle("" + imgName);
        toolbar.setSubtitle("" + dateTime);
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.automation_white));

        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareImage();
            }
        });

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
    }

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
            object.put("image_name", "" + imageName);
            object.put("image_url", imgUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ActivityHelper.showProgressDialog(ImageZoomActivity.this, "Please wait...", false);
        String url = ChatApplication.url + Constants.reportFalseImage;
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
}
