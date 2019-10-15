package com.spike.bot.adapter.SmartDeviceAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.SmartDevice.PhilipsHueBridgeListActivity;
import com.spike.bot.model.SmartBrandModel;

import java.util.ArrayList;

/**
 * Created by Sagar on 31/7/19.
 * Gmail : vipul patel
 */
public class SmartBrandAdapter extends RecyclerView.Adapter<SmartBrandAdapter.SensorViewHolder> {

    private Context mContext;
    public int type = 0;
    ArrayList<SmartBrandModel> arrayListLog = new ArrayList<>();

    public SmartBrandAdapter(Context context, ArrayList<SmartBrandModel> arrayList, int type) {
        this.mContext = context;
        this.type = type;
        this.arrayListLog = arrayList;
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_brand_list, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {

        holder.txtBrandName.setText(arrayListLog.get(position).getSmart_device_brand_name());

        ChatApplication.logDisplay("img is " + ChatApplication.url + arrayListLog.get(position).getIcon_image());
        Glide.with(mContext).load(ChatApplication.url + arrayListLog.get(position).getIcon_image()).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.imgBrand) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                holder.imgBrand.setImageDrawable(circularBitmapDrawable);
            }
        });
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PhilipsHueBridgeListActivity.class);
                intent.putExtra("brandId", "" + arrayListLog.get(position).getId());
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return arrayListLog.size();
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
            imgBrand = itemView.findViewById(R.id.imgBrand);
        }
    }
}
