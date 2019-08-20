package com.spike.bot.adapter;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.spike.bot.R;
import com.spike.bot.activity.CameraPlayBack;
import com.spike.bot.model.CameraSearchModel;
import com.spike.bot.model.CameraVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 20/3/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class CameraListTempAdapter extends RecyclerView.Adapter {

    private ArrayList<Object>dataSet;
    Context mContext;
    int total_types;

    public static class ParentHolder extends RecyclerView.ViewHolder {

        TextView txtUserName;

        public ParentHolder(View itemView) {
            super(itemView);

            this.txtUserName =  itemView.findViewById(R.id.txtUserName);
        }
    }

    public static class ChildHolder extends RecyclerView.ViewHolder {
        TextView txtCameraLink;

        public ChildHolder(View itemView) {
            super(itemView);
            this.txtCameraLink = (TextView) itemView.findViewById(R.id.txtCameraLink);
        }
    }


    public CameraListTempAdapter(ArrayList<Object>data, Context context) {
        this.dataSet = data;
        this.mContext = context;
        total_types = dataSet.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cameralist_view, parent, false);
                return new ParentHolder(view);
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cameralist_view_sub, parent, false);
                return new ChildHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {

        if(dataSet.get(position) instanceof  CameraVO){
            return 0;
        }else {
            return 1;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int listPosition) {
        int viewType=holder.getItemViewType();
        switch (viewType){
            case 0:

                CameraSearchModel cameraVO= (CameraSearchModel) dataSet.get(listPosition);

                break;
            case 1:

                CameraVO cameraVO1= (CameraVO) dataSet.get(listPosition);

                break;
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
