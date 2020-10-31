package com.spike.bot.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.spike.bot.R;
import com.spike.bot.fragments.ScheduleFragment;

public class ScheduleListActivity extends AppCompatActivity {
    String TAG = "DeviceLogActivity";
    Activity activity;
    FrameLayout container;

    String moodId3 = "", roomId = "";
    int selection = 0;
    String roomName = "", isActivityType = "", isRoomMainFm = "";
    boolean isMoodAdapter = false;
    String from = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_list);
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Schedule");
        activity = this;
        container = findViewById(R.id.container);

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
        isRoomMainFm = getIntent().getExtras().getString("isRoomMainFm");
        if (TextUtils.isEmpty(isRoomMainFm)) {
            isRoomMainFm = "";
        }
        from = getIntent().getExtras().getString("from");

        // create a FragmentManager
        FragmentManager fm = getSupportFragmentManager();
// create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
// replace the FrameLayout with new Fragment

        //set room name title on sch list
        if (!TextUtils.isEmpty(moodId3) && !TextUtils.isEmpty(roomName)) {
            toolbar.setTitle(roomName);
        }

        fragmentTransaction.replace(R.id.container, ScheduleFragment.newInstance(true, moodId, moodId2, moodId3, selection, roomId, isMoodAdapter, isActivityType, isRoomMainFm, from));
        fragmentTransaction.commit(); // save the changes
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {

        if (!from.equals("")) {
            this.finish();
            startActivity(new Intent(ScheduleListActivity.this, Main2Activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!TextUtils.isEmpty(moodId3)) {
            menu.findItem(R.id.action_add).setVisible(false);
            menu.findItem(R.id.action_save).setVisible(false);
        } else {
            menu.findItem(R.id.action_save).setVisible(false);
            menu.findItem(R.id.action_add).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }
}
