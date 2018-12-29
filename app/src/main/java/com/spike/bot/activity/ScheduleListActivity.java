package com.spike.bot.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.widget.FrameLayout;

import com.spike.bot.R;
import com.spike.bot.fragments.ScheduleFragment;

public class ScheduleListActivity extends AppCompatActivity {
    String TAG = "DeviceLogActivity";
    Activity activity;
    FrameLayout container;

    String moodId3 = "",roomId = "";
    int  selection = 0;
    String roomName = "",isActivityType="";
    boolean isMoodAdapter = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Schedule");
        activity = this;
        container = (FrameLayout) findViewById(R.id.container);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String moodId = getIntent().getExtras().getString("moodId");
        String moodId2 = getIntent().getExtras().getString("moodId2"); //MoodFragmetn add schedule coming
        moodId3 = getIntent().getExtras().getString("moodId3");
        roomId = getIntent().getExtras().getString("roomId");
        roomName = getIntent().getExtras().getString("roomName");
        selection = getIntent().getExtras().getInt("selection");
        isMoodAdapter = getIntent().getExtras().getBoolean("isMoodAdapter");
        isActivityType = getIntent().getExtras().getString("isActivityType");

        Log.d("AddSchedule","moodId " + moodId);
        // create a FragmentManager
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
// create a FragmentTransaction to begin the transaction and replace the Fragment
        android.support.v4.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
// replace the FrameLayout with new Fragment

        //set room name title on sch list
        if(!TextUtils.isEmpty(moodId3) && !TextUtils.isEmpty(roomName)){
            toolbar.setTitle(roomName);
        }

        Log.d("Roomselection","Fragment selection ; " + selection + " id : " + moodId3);
        fragmentTransaction.replace(R.id.container,ScheduleFragment.newInstance(true,moodId,moodId2,moodId3,selection,roomId,isMoodAdapter,isActivityType));
        fragmentTransaction.commit(); // save the changes
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(!TextUtils.isEmpty(moodId3)){
            menu.findItem(R.id.action_add).setVisible(false);
            menu.findItem(R.id.action_save).setVisible(false);
        }else{
            menu.findItem(R.id.action_save).setVisible(false);
            menu.findItem(R.id.action_add).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }


}
