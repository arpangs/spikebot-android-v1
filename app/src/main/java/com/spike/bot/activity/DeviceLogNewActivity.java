package com.spike.bot.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.spike.bot.R;
import com.spike.bot.adapter.DeviceLogAdapter;
import com.spike.bot.adapter.DeviceLogNewAdapter;
import com.spike.bot.listener.OnLoadMoreListener;
import com.spike.bot.model.DeviceLog;

import java.util.ArrayList;
import java.util.List;

public class DeviceLogNewActivity extends AppCompatActivity implements OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    Toolbar toolbar;
    RecyclerView rv_device_log;

    SwipeRefreshLayout swipeRefreshLayout;
    private OnLoadMoreListener onLoadMoreListener;
    ViewPager monthpager;
    private TabLayout tabLayout;

    public List<DeviceLog> deviceLogList = new ArrayList<>();
    DeviceLogNewAdapter deviceLogAdapter;

    private String[] tabs = { "January", "Fabruary", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_log_new);

        rv_device_log = findViewById(R.id.rv_device_log_new);
        swipeRefreshLayout = findViewById(R.id.swiperefreshnew);
        monthpager = findViewById(R.id.month_pager);
        tabLayout = findViewById(R.id.tabs_container);
        tabLayout.setupWithViewPager(monthpager);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("LOGS");
        setupViewPager(monthpager);

        rv_device_log.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        onLoadMoreListener = this;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rv_device_log.getLayoutManager();

        deviceLogAdapter = new DeviceLogNewAdapter(DeviceLogNewActivity.this, deviceLogList);
        rv_device_log.setAdapter(deviceLogAdapter);

        monthpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void setupViewPager(ViewPager viewPager) {
        MonthPagerAdapter adapter = new MonthPagerAdapter();
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoadMore(int lastVisibleItem) {

    }

    public class MonthPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 12;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return false;
        }
    }
}
