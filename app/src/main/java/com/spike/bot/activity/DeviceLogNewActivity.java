package com.spike.bot.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kp.core.ActivityHelper;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.CloudAdapter;
import com.spike.bot.adapter.DeviceLogNewAdapter;
import com.spike.bot.core.Constants;
import com.spike.bot.listener.OnLoadMoreListener;
import com.spike.bot.model.DeviceLog;
import com.spike.bot.model.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DeviceLogNewActivity extends AppCompatActivity implements OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    Toolbar toolbar;
    RecyclerView rv_device_log;
    RecyclerView rv_month_list;
    SwipeRefreshLayout swipeRefreshLayout;
    private OnLoadMoreListener onLoadMoreListener;

    public List<DeviceLog> deviceLogList = new ArrayList<>();
    DeviceLogNewAdapter deviceLogAdapter;
    ArrayList monthlist = new ArrayList();
    int row_index = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_log_new);

        rv_device_log = findViewById(R.id.rv_device_log_new);
        rv_month_list = findViewById(R.id.rv_monthlist);
        swipeRefreshLayout = findViewById(R.id.swiperefreshnew);

        setView();
    }

    public void setView(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("LOGS");

        setMonthList();
        setLogAdpater();
        setMonthAdpater();

    }

    private void setLogAdpater() {
        rv_device_log.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        onLoadMoreListener = this;
        deviceLogAdapter = new DeviceLogNewAdapter(DeviceLogNewActivity.this, deviceLogList);
        rv_device_log.setAdapter(deviceLogAdapter);
    }

    private void setMonthAdpater()
    {
        rv_month_list.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL) {
            @Override
            public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
                // Do not draw the divider
            }
        });

        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_month_list.setLayoutManager(mLayoutManager);
        row_index= Constants.getCurentMonth();
        MonthPagerAdapter adapter = new MonthPagerAdapter(this, monthlist);
        rv_month_list.setAdapter(adapter);
        rv_month_list.getLayoutManager().scrollToPosition(row_index);
        rv_month_list.setHasFixedSize(true);
    }

    private void setMonthList() {
        monthlist.add("January");
        monthlist.add("February");
        monthlist.add("March");
        monthlist.add("April");
        monthlist.add("May");
        monthlist.add("June");
        monthlist.add("July");
        monthlist.add("August");
        monthlist.add("September");
        monthlist.add("October");
        monthlist.add("November");
        monthlist.add("December");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRefresh() {
    }

    @Override
    public void onLoadMore(int lastVisibleItem) {
    }

    public class MonthPagerAdapter extends RecyclerView.Adapter<MonthPagerAdapter.MonthViewHolder> {

        private Context context;
        public MonthPagerAdapter(Context context, ArrayList months) {
            this.context = context;
            monthlist = months;
        }

        @Override
        public MonthViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MonthViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_month, parent, false));
        }

        @Override
        public void onBindViewHolder(MonthViewHolder holder, final int position) {
            holder.txtmonth.setText(monthlist.get(position).toString());
            holder.txtmonth.setId(position);
            holder.txtmonth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    row_index = holder.txtmonth.getId();
                    notifyDataSetChanged();
                }
            });

            if (row_index == position) {
                holder.txtmonth.setTextColor(getResources().getColor(R.color.automation_black));
                holder.imgdots.setVisibility(View.VISIBLE);
            } else {
                holder.txtmonth.setTextColor(getResources().getColor(R.color.device_button));
                holder.imgdots.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return monthlist.size();
        }

        class MonthViewHolder extends RecyclerView.ViewHolder {

            private TextView txtmonth;
            private ImageView imgdots;

            MonthViewHolder(View itemView) {
                super(itemView);
                txtmonth = itemView.findViewById(R.id.txt_month_name);
                imgdots = itemView.findViewById(R.id.img_dots_month);
                itemView.setTag(this);
            }
        }

    }
}
