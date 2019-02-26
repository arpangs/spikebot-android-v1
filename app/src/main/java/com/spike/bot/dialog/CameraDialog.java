package com.spike.bot.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.spike.bot.R;

/**
 * Created by kaushal on 27/12/17.
 */

public class CameraDialog extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes;
    public WebView wv_camera;
    public String url="";

    public CameraDialog(Activity a,String url) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.url = url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

//        getWindow().setLayout(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.dialog_camera);

        yes = (Button) findViewById(R.id.btn_yes);
        wv_camera = (WebView) findViewById(R.id.wv_camera );
        yes.setOnClickListener(this);

        WebSettings webSettings = wv_camera.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //wv_camera.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        wv_camera.setHorizontalScrollBarEnabled(true);

        wv_camera.getSettings().setAllowFileAccess(true);
        wv_camera.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wv_camera.getSettings().setSupportMultipleWindows(true);
        wv_camera.getSettings().setPluginState(WebSettings.PluginState.ON);
        //for zoomout
        wv_camera.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        wv_camera.getSettings().setUseWideViewPort(true);
        wv_camera.setInitialScale(1);


        webSettings.setLoadWithOverviewMode(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        wv_camera.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        wv_camera.setScrollbarFadingEnabled(false);

        wv_camera.clearCache(true);
        wv_camera.requestFocusFromTouch();
        //wv_camera.getSettings().setW

        //wv_camera.setWebChromeClient(new MyWebChromeClient());///////////////////////
          wv_camera.setWebViewClient(new WebViewClient());
      //  wv_camera.setWebViewClient(new MyWebViewClient());

        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);

        // Enable remote debugging via chrome://inspect
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        // AppRTC requires third party cookies to work
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(wv_camera, true);
        }
        wv_camera.loadUrl(url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                dismiss();
                break;
            default:
                break;
        }
    }
}