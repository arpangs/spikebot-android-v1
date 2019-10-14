package com.spike.bot.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kp.core.ActivityHelper;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.Main2Activity;
import com.spike.bot.model.User;

import java.util.List;

/**
 * Created by Sagar on 19/3/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class CloudAdapter extends RecyclerView.Adapter<CloudAdapter.CloudViewHolder>{

    public List<User> userList;
    private Context mcontext;
    private CloudClickListener cloudClickListener;

    public CloudAdapter(Context context,List<User> users,CloudClickListener cloudClickListener){
        this.userList = users;
        this.mcontext = context;
        this.cloudClickListener = cloudClickListener;
    }

    @Override
    public CloudViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CloudViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cloud, parent, false));
    }

    @Override
    public void onBindViewHolder(CloudViewHolder holder, final int position) {

        final User user = userList.get(position);
//        holder.txt_title.setText(user.getFirstname()+" "+user.getLastname());
        holder.txt_title.setText(user.getFirstname());

//        if(user.isActive()){
//            holder.image_cloud.setImageResource(R.drawable.icn_check);
//        }else{
//            holder.image_cloud.setImageResource(R.drawable.icn_round);
//        }

        if(ChatApplication.currentuserId.equalsIgnoreCase(user.getUser_id())){
            holder.image_cloud.setImageResource(R.drawable.icn_check);
        }else{
            holder.image_cloud.setImageResource(R.drawable.icn_round);
        }


        //holder.radioButton.setChecked(user.isActive());

        holder.txt_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityHelper.isConnectingToInternet(mcontext)) {
                    user.setIsActive(true);
                    for(User user1 : userList){
                        if(!user1.getUser_id().equalsIgnoreCase(user.getUser_id())){
                            user1.setIsActive(false);
                        }
                    }
                    notifyDataSetChanged();
                    cloudClickListener.userSelectclick(user);
                }

            }
        });

        holder.image_cloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setIsActive(true);
                for(User user1 : userList){
                    if(!user1.getUser_id().equalsIgnoreCase(user.getUser_id())){
                        user1.setIsActive(false);
                    }
                }
                notifyDataSetChanged();
                cloudClickListener.userSelectclick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class CloudViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_title;
        // private RadioButton radioButton;
        private ImageView image_cloud;

        CloudViewHolder(View itemView) {
            super(itemView);
            txt_title = (TextView)itemView.findViewById(R.id.txt_row_clou_name);
            //  radioButton = (RadioButton)itemView.findViewById(R.id.radio_cloud);
            image_cloud = (ImageView) itemView.findViewById(R.id.image_cloud);
        }
    }

    /**
     * interface for get click event in multiple users session
     * {@link com.spike.bot.activity.Main2Activity#click(User)}
     */
    public interface CloudClickListener{
        void userSelectclick(User user);
    }
}
