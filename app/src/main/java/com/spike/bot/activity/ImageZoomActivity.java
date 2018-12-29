package com.spike.bot.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.kp.core.DateHelper;
import com.spike.bot.R;
import com.spike.bot.core.Log;
import com.spike.bot.customview.TouchImageView;
import com.spike.bot.model.CameraVO;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Sagar on 25/12/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class ImageZoomActivity extends AppCompatActivity{

    Toolbar toolbar;
    ProgressBar progressBar;
    TouchImageView imageViewZoom;

    public String imgName="",imgUrl="",imgDate="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zoom);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        imgUrl=getIntent().getStringExtra("imgUrl");
        imgName=getIntent().getStringExtra("imgName");
        imgDate=getIntent().getStringExtra("imgDate");

        setUi();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setUi() {

        imageViewZoom=findViewById(R.id.img_slider_item_zoom);
        progressBar=findViewById(R.id.progressBar);


        //Get Image
        Glide.with(this)
                .load(imgUrl)
                .dontAnimate()
                .override(500,400)
                .skipMemoryCache(true)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        imageViewZoom.setZoom(2.3f);
                        return false;
                    }
                })
                .into(imageViewZoom);


        Date today = null;//2018-01-12 19:40:07
        try {
            today = DateHelper.parseDateSimple(imgDate,DateHelper.DATE_YYYY_MM_DD_HH_MM_SS);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String strDateOfTime=DateHelper.getDayString(today);
        String []strDateOfTimeTemp=strDateOfTime.split(" ");

        String dateTime="";
        if(strDateOfTimeTemp.length>2){
             dateTime = strDateOfTimeTemp[0] + System.getProperty("line.separator") + strDateOfTimeTemp[1]+ " "+strDateOfTimeTemp[2];
        }else {
            dateTime = strDateOfTimeTemp[0]+" "+strDateOfTimeTemp[1];
        }

        toolbar.setTitle(""+imgName);
        toolbar.setSubtitle(""+dateTime);
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.automation_white));
    }
}
