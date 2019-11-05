package com.spike.bot.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatTextView;
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
import com.spike.bot.core.Common;
import com.spike.bot.listener.SelectCamera;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.RepeaterModel;

import java.util.ArrayList;

/**
 * Created by Sagar on 27/11/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class RepeaterAdapter extends RecyclerView.Adapter<RepeaterAdapter.SensorViewHolder>{

    private TempSensorInfoAdapter.OnNotificationContextMenu onNotificationContextMenu;
    private Context mContext;
    ArrayList<RepeaterModel> arrayListLog=new ArrayList<>();
    public MoreOption moreOption;


    public RepeaterAdapter(Context context, ArrayList<RepeaterModel> arrayListLog1,MoreOption moreOption){
        this.mContext=context;
        this.arrayListLog=arrayListLog1;
        this.moreOption=moreOption;
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_repeater,parent,false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {

        holder.txtName.setText(arrayListLog.get(position).getRepeator_name());
        if(arrayListLog.get(position).getIs_active().equals("y")){
            holder.imgRepeatar.setImageResource(R.drawable.repeater);
        }else {
            holder.imgRepeatar.setImageResource(R.drawable.repeater_with_red_cross);
        }

        holder.imgEditRepeater.setId(position);
        holder.imgEditRepeater.setOnClickListener(new View.OnClickListener() {
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
                                moreOption.moreOption(arrayListLog.get(holder.imgEditRepeater.getId()),holder.imgEditRepeater.getId(),1);
                                break;
                            case R.id.action_delete_dots:
                                moreOption.moreOption(arrayListLog.get(holder.imgEditRepeater.getId()),holder.imgEditRepeater.getId(),2);
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

        public AppCompatTextView txtName;
        public ImageView imgEditRepeater,imgRepeatar;

        public SensorViewHolder(View view) {
            super(view);
            txtName =  itemView.findViewById(R.id.txtName);
            imgRepeatar =  itemView.findViewById(R.id.imgRepeatar);
            imgEditRepeater =  itemView.findViewById(R.id.imgEditRepeater);
        }
    }

    public interface MoreOption{
        void moreOption(RepeaterModel repeaterModel,int postion,int type);
    }
}
