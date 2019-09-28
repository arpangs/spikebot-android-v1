package com.spike.bot.activity.TTLock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.activity.Main2Activity;

import java.util.ArrayList;

/**
 * Created by Sagar on 2/9/19.
 * Gmail : vipul patel
 */
public class LockBrandActivity extends AppCompatActivity {

    Toolbar toolbar;
    FloatingActionButton fab;
    RecyclerView recyclerViewLock;
    SmartLockBrandAdapter smartLockBrandAdapter;
    SmartLockOptionBrandAdapter smartLockOptionBrandAdapter;

    ArrayList<String> stringArrayList=new ArrayList<>();
    public boolean isFlagClick=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_device_list);

        setviewId();
    }

    private void setviewId() {
        toolbar=findViewById(R.id.toolbar);
        fab=findViewById(R.id.fab);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Select Your Brand");

        fab.setVisibility(View.GONE);
        recyclerViewLock=findViewById(R.id.recyclerSmartDevice);

        setAdapter();

        stringArrayList.add("Bridge");
        stringArrayList.add("Lock");
    }

    private void setAdapter() {
        recyclerViewLock.setLayoutManager(new LinearLayoutManager(this));
        smartLockBrandAdapter=new SmartLockBrandAdapter(this);
        recyclerViewLock.setAdapter(smartLockBrandAdapter);
        smartLockBrandAdapter.notifyDataSetChanged();
    }

    private void setAdapter1() {
        recyclerViewLock.setLayoutManager(new LinearLayoutManager(this));
        smartLockOptionBrandAdapter=new SmartLockOptionBrandAdapter(this,stringArrayList);
        recyclerViewLock.setAdapter(smartLockOptionBrandAdapter);
        smartLockOptionBrandAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public class SmartLockBrandAdapter extends RecyclerView.Adapter<SmartLockBrandAdapter.SensorViewHolder>{

        private Context mContext;
        public int type=0;

        public SmartLockBrandAdapter(Context context) {
            this.mContext=context;
        }

        @Override
        public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_brand_list,parent,false);
            return new SensorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SensorViewHolder holder, final int position) {
            //SmartLockBrandAdapter
            holder.imgBrand.setImageResource(R.drawable.smart_lock_icon_brand);
            holder.txtBrandName.setText("TT Lock");

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isFlagClick=true;
                    setAdapter1();
                }
            });
        }

        @Override
        public int getItemCount() {
            return 1;
        }


        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class SensorViewHolder extends RecyclerView.ViewHolder{

            public View  view;
            public ImageView imgBrand;
            public TextView txtBrandName;

            public SensorViewHolder(View view) {
                super(view);
                this.view=view;
                txtBrandName =  itemView.findViewById(R.id.txtBrandName);
                imgBrand =  itemView.findViewById(R.id.imgBrand);
            }
        }
    }


    public class SmartLockOptionBrandAdapter extends RecyclerView.Adapter<SmartLockOptionBrandAdapter.SensorViewHolder>{

        private Context mContext;
        public int type=0;

        public SmartLockOptionBrandAdapter(Context context, ArrayList<String> stringArrayList) {
            this.mContext=context;
        }

        @Override
        public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_brand_list,parent,false);
            return new SensorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SensorViewHolder holder, final int position) {
            //SmartLockBrandAdapter
            if(position==0){
                holder.imgBrand.setImageResource(R.drawable.smart_lock_icon_bridge_small_icon);
            }else {
                holder.imgBrand.setImageResource(R.drawable.smart_lock_icon_brand);
            }

            holder.txtBrandName.setText(stringArrayList.get(position));
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    if(position==0){
                        intent=new Intent(mContext,GatewayTypeActivity.class);
                    }else {
                        intent=new Intent(mContext,TTLockListActivity.class);
                    }

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

        public class SensorViewHolder extends RecyclerView.ViewHolder{

            public View  view;
            public ImageView imgBrand;
            public TextView txtBrandName;

            public SensorViewHolder(View view) {
                super(view);
                this.view=view;
                txtBrandName =  itemView.findViewById(R.id.txtBrandName);
                imgBrand =  itemView.findViewById(R.id.imgBrand);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(isFlagClick){
            isFlagClick=false;
            setAdapter();
        }else {
            super.onBackPressed();
        }
    }
}
