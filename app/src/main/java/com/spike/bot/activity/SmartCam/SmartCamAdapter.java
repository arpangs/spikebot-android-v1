package com.spike.bot.activity.SmartCam;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.adapter.JetSonAdapter;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.JetSonModel;

import java.util.ArrayList;

/**
 * Created by vipul on 20/1/20.
 * Gmail : vipul patel
 */
public class SmartCamAdapter extends RecyclerView.Adapter<SmartCamAdapter.SensorViewHolder>{

    private Context mContext;
    JetSonAdapter.JetsonAction jetsonAction;
    ArrayList<CameraVO> arrayListLog=new ArrayList<>();

    public SmartCamAdapter(Context context, JetSonAdapter.JetsonAction jetsonAction, ArrayList<CameraVO> arrayList) {
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

        holder.txtNameJetson.setText(arrayListLog.get(position).getCamera_name());

        if(arrayListLog.get(position).getIsActive()==0){
            holder.imgView.setImageResource(R.drawable.camera_off_inactive);
        }else {
            holder.imgView.setImageResource(R.drawable.camera_on);
        }

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
        public AppCompatImageView imgMoreJetjson,imgView;

        public SensorViewHolder(View view) {
            super(view);
            txtNameJetson =  itemView.findViewById(R.id.txtNameJetson);
            imgMoreJetjson =  itemView.findViewById(R.id.imgMoreJetjson);
            imgView =  itemView.findViewById(R.id.imgView);
        }
    }

}
