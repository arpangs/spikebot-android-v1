package com.spike.bot.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask2;
import com.kp.core.ICallBack2;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.fragments.UserChildListFragment;
import com.spike.bot.fragments.UserProfileFragment;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.RoomVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    String  TAG = "ProfileActivity";
    boolean isFlagUser=false;
    Activity activity;
    TabLayout tabLayout;
    private ViewPager mViewPager;
    public TextView txtVersionCode;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    ArrayList<Fragment> fragmentList = new ArrayList<>();
    ArrayList<String> strList = new ArrayList<>();
    MenuItem menuAdd,menuAddSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        activity = this;

        mViewPager = (ViewPager) findViewById(R.id.container);
        // mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        txtVersionCode =findViewById(R.id.txtVersionCode);
        tabLayout.setupWithViewPager(mViewPager);

        if(Common.getPrefValue(this, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("1")){
            isFlagUser=true;
        }

        fragmentList.add(UserProfileFragment.newInstance());
        if(isFlagUser){
            fragmentList.add(UserChildListFragment.newInstance());
        }else {
            fragmentList.add(UserProfileFragment.newInstance());
        }

        strList.add("User Details");
        strList.add("Child users");
        getRoomCameraList();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        //mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                //lock second
                if(isFlagUser){
                    if(position==1){
                        // tabLayout.getTabAt(0).select();
                        menuAdd.setVisible(true);
                    }else {
                        menuAdd.setVisible(false);
                    }
                }else {
                    // tabLayout.getTabAt(0).select();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            txtVersionCode.setText("Version code : "+version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        menuAdd = menu.findItem(R.id.action_add);
        menuAddSave = menu.findItem(R.id.action_save);
        menuAddSave.setVisible(false);
        menuAdd.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent intent=new Intent(this,UserChildActivity.class);
            intent.putExtra("modeType","add");
            startActivityForResult(intent,1001);
//            Intent intent=new Intent(this,UserChildActivity.class);

        //    startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position == 0) {
                fragment = new UserProfileFragment();
            } else if (position == 1) {
                if(isFlagUser){
                    fragment = new UserChildListFragment();
                }else {
                    fragment = new UserProfileFragment();
                }

            }
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return strList.get(position);
        }
    }

    public void getRoomCameraList() {

        ActivityHelper.showProgressDialog(this, "Please Wait...", false);
        String url = ChatApplication.url + Constants.getRoomCameraList;

        ChatApplication.logDisplay("url is "+url);
        new GetJsonTask2(this, url, "GET", "", new ICallBack2() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay("getRoomCameraList onSuccess " + result.toString());
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){
                        UserChildListFragment.cameraList.clear();
                        UserChildListFragment.roomList.clear();

                        JSONObject dataObject = result.getJSONObject("data");
                        JSONArray roomArray = dataObject.getJSONArray("roomList");

                        for(int i=0; i<roomArray.length(); i++){

                            JSONObject object=roomArray.getJSONObject(i);

                            RoomVO roomVO=new RoomVO();
                            roomVO.setRoomId(object.optString("room_id"));
                            roomVO.setRoomName(object.optString("room_name"));

                            UserChildListFragment.roomList.add(roomVO);
                        }

                        JSONArray cameraArray = dataObject.getJSONArray("cameradeviceList");
                        if (cameraArray.length() > 0) {

                            for(int i=0; i<cameraArray.length(); i++){

                                JSONObject object=cameraArray.getJSONObject(i);

                                CameraVO roomVO=new CameraVO();
                                roomVO.setCamera_id(object.optString("camera_id"));
                                roomVO.setCamera_name(object.optString("camera_name"));
                                UserChildListFragment.cameraList.add(roomVO);
                            }

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error, int reCode) {
                ActivityHelper.dismissProgressDialog();

            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        ChatApplication.logDisplay("activity is "+requestCode+" "+requestCode);
        if(requestCode==1001 && resultCode == RESULT_OK){
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
//            Fragment fragment = getSupportFragmentManager().getFragments().get(1);
//            fragment.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
