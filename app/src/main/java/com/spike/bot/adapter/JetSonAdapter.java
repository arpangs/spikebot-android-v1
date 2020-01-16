package com.spike.bot.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.activity.SmartCam.AddJetSonActivity;
import com.spike.bot.activity.SmartCam.SmartCameraActivity;
import com.spike.bot.core.Common;
import com.spike.bot.listener.SelectCamera;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.JetSonModel;

import java.util.ArrayList;

/**
 * Created by Sagar on 27/11/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class JetSonAdapter extends RecyclerView.Adapter<JetSonAdapter.SensorViewHolder>{

    private Context mContext;
    JetsonAction jetsonAction;
    ArrayList<JetSonModel.Datum> arrayListLog=new ArrayList<>();

    public JetSonAdapter(Context context,JetsonAction jetsonAction, ArrayList<JetSonModel.Datum> arrayList) {
        this.mContext=context;
        this.jetsonAction=jetsonAction;
        this.arrayListLog=arrayList;
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_add_jetson,parent,false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {

        holder.txtNameJetson.setText(arrayListLog.get(position).getDeviceName());

        holder.txtNameJetson.setId(position);
        holder.txtNameJetson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, SmartCameraActivity.class);
                mContext.startActivity(intent);
            }
        });

        holder.imgMoreJetjson.setId(position);
        holder.imgMoreJetjson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mContext, v);
                @SuppressLint("RestrictedApi") Context wrapper = new ContextThemeWrapper(mContext, R.style.PopupMenu);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    popup = new PopupMenu(wrapper, v, Gravity.RIGHT);
                } else {
                    popup = new PopupMenu(wrapper, v);
                }
                popup.getMenuInflater().inflate(R.menu.menu_dots, popup.getMenu());
                popup.getMenu().findItem(R.id.action_log).setVisible(false);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_edit_dots:
                                jetsonAction.action(holder.imgMoreJetjson.getId(),"edit");
                                break;
                            case R.id.action_delete_dots:
                                jetsonAction.action(holder.imgMoreJetjson.getId(),"delete");
                                break;

                        }
                        return true;
                    }
                });

                popup.show();
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

    public class SensorViewHolder extends RecyclerView.ViewHolder{

        public TextView txtNameJetson;
        public AppCompatImageView imgMoreJetjson;

        public SensorViewHolder(View view) {
            super(view);
            txtNameJetson =  itemView.findViewById(R.id.txtNameJetson);
            imgMoreJetjson =  itemView.findViewById(R.id.imgMoreJetjson);
        }
    }

    public interface JetsonAction {

        public void action(int position , String action);
    }
}


