package com.spike.bot.activity.TTLock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.R;

import java.util.ArrayList;

/**
 * Created by Sagar on 3/9/19.
 * Gmail : vipul patel
 */
public class GatewayTypeActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerSmartDevice;
    SmartLockOptionBrandAdapter smartLockOptionBrandAdapter;
    ArrayList<String> stringArrayList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_device_list);

        setviewId();
    }

    private void setviewId() {
        toolbar = findViewById(R.id.toolbar);
        recyclerSmartDevice = findViewById(R.id.recyclerSmartDevice);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Select Bridge Type");

        stringArrayList.add("G1");
        stringArrayList.add("G2");

        setAdapter();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setAdapter() {
        recyclerSmartDevice.setLayoutManager(new LinearLayoutManager(this));
        smartLockOptionBrandAdapter = new SmartLockOptionBrandAdapter(this, stringArrayList);
        recyclerSmartDevice.setAdapter(smartLockOptionBrandAdapter);
        smartLockOptionBrandAdapter.notifyDataSetChanged();
    }

    public class SmartLockOptionBrandAdapter extends RecyclerView.Adapter<SmartLockOptionBrandAdapter.SensorViewHolder> {

        private Context mContext;
        public int type = 0;

        public SmartLockOptionBrandAdapter(Context context, ArrayList<String> stringArrayList) {
            this.mContext = context;
        }

        @Override
        public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_brand_list, parent, false);
            return new SensorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SensorViewHolder holder, final int position) {
            //SmartLockBrandAdapter
            holder.imgBrand.setImageResource(R.drawable.smart_lock_icon_bridge_small_icon);

            holder.txtBrandName.setText(stringArrayList.get(position));
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, GateWayListActivity.class);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return stringArrayList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class SensorViewHolder extends RecyclerView.ViewHolder {

            public View view;
            public ImageView imgBrand;
            public TextView txtBrandName;

            public SensorViewHolder(View view) {
                super(view);
                this.view = view;
                txtBrandName = itemView.findViewById(R.id.txtBrandName);
                imgBrand = itemView.findViewById(R.id.img_Brand);
            }
        }
    }

}
