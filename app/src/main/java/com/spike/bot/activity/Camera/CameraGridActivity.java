package com.spike.bot.activity.Camera;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.kp.core.ActivityHelper;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.Main2Activity;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.camera.CameraPlayer;
import com.spike.bot.core.Constants;
import com.spike.bot.core.JsonHelper;
import com.spike.bot.fragments.CameraListFragment;
import com.spike.bot.model.CameraVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 30/5/19.
 * Gmail : vipul patel
 */
public class CameraGridActivity extends AppCompatActivity {

    public static boolean isCamera = false;
    public int counTab = 0, totalTab = 0;
    public Toolbar toolbar;
    public TabLayout tabLayout;
    public ViewPager recyclerDvr;
    String jetson_id;
    toolBarImageCapture toolBarImageCapture;
    CameraListFragment cameraListFragment;
    private ArrayList<CameraVO> cameraVOArrayList = new ArrayList<>();
    private ArrayList<CameraVO> jetsonArrayList = new ArrayList<>();

    /*checkPermission */
    public static boolean checkPermission(Activity context) {
        List arrayList = new ArrayList();
        for (String str : CameraPlayer.permissions) {
            if (ContextCompat.checkSelfPermission(context, str) != 0) {
                arrayList.add(str);
            }
        }
        if (arrayList.isEmpty()) {
            return true;
        }
        ActivityCompat.requestPermissions(context, (String[]) arrayList.toArray(new String[arrayList.size()]), 100);
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_grid);

        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabDots);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // roomVO = (RoomVO) getIntent().getExtras().getSerializable("room");
        try {
            isCamera = getIntent().getExtras().getBoolean("isshowGridCamera");
            jetson_id = getIntent().getExtras().getString("jetson_device_id");

            // jetson_id = new ArrayList<>();
/*
            if (!jetsonArrayList.isEmpty()) {
                for (CameraVO cameraVO : jetsonArrayList) {
                    jetson_id = cameraVO.getJetson_device_id();
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        setUi();

    }

    private void setUi() {
        toolbar.setTitle("Camera List");
        recyclerDvr = findViewById(R.id.recyclerDvr);
        getAllCameraList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_playback, menu);
        MenuItem muteIcon = menu.findItem(R.id.action_mute);
        muteIcon.setVisible(false);

        Drawable drawable1 = menu.findItem(R.id.action_screenshot).getIcon();

        Drawable drawable = DrawableCompat.wrap(drawable1);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.automation_white));
        menu.findItem(R.id.action_screenshot).setIcon(drawable);
        return true;
    }

    public void setObject(toolBarImageCapture toolBarImageCapture) {
        this.toolBarImageCapture = toolBarImageCapture;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_screenshot) {
            if (checkPermission(this)) {
                toolBarImageCapture.imageCapture();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get all camera list based on user_id
     */
    public void getAllCameraList() {
        ActivityHelper.showProgressDialog(CameraGridActivity.this, "Please Wait...", false);

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().getAllCameraList(jetson_id, isCamera, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    int code = 0;
                    ActivityHelper.dismissProgressDialog();
                    JSONObject result = new JSONObject(stringResponse);
                    code = result.getInt("code");
                    String message = result.getString("message");
                    ChatApplication.logDisplay("result is " + result.toString());
                    if (code == 200) {
                        JSONArray dataObject = result.optJSONArray("data");
                        cameraVOArrayList = JsonHelper.parseCameraArray(dataObject);
                        ChatApplication.logDisplay("cameraVOArrayList is " + cameraVOArrayList.size());

                        if (cameraVOArrayList.size() > 0) {
                            setAdapter(ChatApplication.url);
                        }
                    } else {
                        ChatApplication.showToast(CameraGridActivity.this, "" + message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(CameraGridActivity.this, "No camera found.");
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(CameraGridActivity.this, "No camera found.");
            }
        });
    }

    /*set view*/
    private void setAdapter(String ChatApplicationurl) {
        String url = "";
        for (int i = 0; i < cameraVOArrayList.size(); i++) {
            url = "";
            if (Main2Activity.isCloudConnected) {
//                url = Constants.CAMERA_DEEP + ":" + cameraVOArrayList.get(i).getCamera_vpn_port() + "" + cameraVOArrayList.get(i).getCamera_url();
                url = Constants.CAMERA_DEEP_VPN + ":" + cameraVOArrayList.get(i).getCamera_vpn_port() + "" + cameraVOArrayList.get(i).getCamera_url();  // change on 15 oct 2020 as per web developer instruction
            } else {

                if (ChatApplication.url == null) {
                    ChatApplication.url = ChatApplicationurl;
                }
                String tmpurl = "http://" + ChatApplication.url + "" + cameraVOArrayList.get(i).getCamera_url();
                url = tmpurl.replace("http", "rtmp").replace(":80", ""); //replace port number to blank String
            }
            cameraVOArrayList.get(i).setLoadingUrl(url);
        }


        /*total count set*/
        for (int i = 0; i < cameraVOArrayList.size(); i++) {
            if (i == counTab) {
                totalTab = totalTab + 1;
                counTab = counTab + 3;
            }
            cameraVOArrayList.get(i).setCameraPostion(totalTab);
        }

        recyclerDvr.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(recyclerDvr, true);

    }

    public interface toolBarImageCapture {

        void imageCapture();
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            ArrayList<CameraVO> cameraVOArrayListTemp = new ArrayList<>();
            for (int i = 0; i < cameraVOArrayList.size(); i++) {
                if (cameraVOArrayList.get(i).getCameraPostion() == position + 1) {
                    cameraVOArrayListTemp.add(cameraVOArrayList.get(i));
                }
            }
            return cameraListFragment.newInstance(cameraVOArrayListTemp, recyclerDvr.getHeight());
        }

        @Override
        public int getCount() {
            return totalTab;
        }
    }
}
