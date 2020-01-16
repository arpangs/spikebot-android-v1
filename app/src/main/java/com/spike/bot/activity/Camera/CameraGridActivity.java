package com.spike.bot.activity.Camera;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.Main2Activity;
import com.spike.bot.camera.CameraPlayer;
import com.spike.bot.core.Common;
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

    public int counTab = 0, totalTab = 0;
    public Toolbar toolbar;
    public TabLayout tabLayout;
    public ViewPager recyclerDvr;
    private ArrayList<CameraVO> cameraVOArrayList = new ArrayList<>();

    toolBarImageCapture toolBarImageCapture;
    CameraListFragment cameraListFragment;

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

    /**
     * Get all camera list based on user_id
     */

    public void getAllCameraList() {
        ActivityHelper.showProgressDialog(CameraGridActivity.this, " Please Wait...", false);

        String url = ChatApplication.url + Constants.getAllCameraToken;

        JSONObject object = new JSONObject();
        try {
            object.put("admin", Integer.parseInt(Common.getPrefValue(this, Constants.USER_ADMIN_TYPE)));
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("url is " + url);
        new GetJsonTask(this, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                int code = 0;
                try {
                    code = result.getInt("code");
                    String message = result.getString("message");
                    ChatApplication.logDisplay("result is " + result.toString());
                    if (code == 200) {
                        JSONArray dataObject = result.optJSONArray("data");
                        cameraVOArrayList = JsonHelper.parseCameraArray(dataObject);
                        ChatApplication.logDisplay("cameraVOArrayList is " + cameraVOArrayList.size());

                        if (cameraVOArrayList.size() > 0) {
                            setAdapter();
                        }
                    } else {
                        ChatApplication.showToast(CameraGridActivity.this, "" + message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }

            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                ChatApplication.showToast(CameraGridActivity.this, "No camera found.");
            }
        }).execute();


    }

    /*set view*/
    private void setAdapter() {
        String url = "";
        for (int i = 0; i < cameraVOArrayList.size(); i++) {
            url = "";
            if (Main2Activity.isCloudConnected) {
                url = Constants.CAMERA_DEEP + ":" + cameraVOArrayList.get(i).getCamera_vpn_port() + "" + cameraVOArrayList.get(i).getCamera_url();
            } else {
                String tmpurl = ChatApplication.url + "" + cameraVOArrayList.get(i).getCamera_url();
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

    public interface toolBarImageCapture {

        void imageCapture();
    }
}
