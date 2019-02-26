package com.spike.bot.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.activity.VideoVLC2Activity;
import com.spike.bot.core.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kaushal on 27/12/17.
 */

public class CameraAdapter extends RecyclerView.Adapter<CameraAdapter.ViewHolder> {

    Activity mContext;
    private JSONArray cameraArray;
    String camUrl="";
    public CameraAdapter(Activity context, JSONArray cameraArray, String camUrl) {
        this.cameraArray = cameraArray;
        this.mContext =context;
        this.camUrl = camUrl;
    }
    @Override
    public int getItemViewType(int position) {

        return R.layout.row_room_switch_item;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_room_home, parent, false);
        //view.setOnClickListener(MainActivity.myOnClickListener);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;*/
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        /*int width = parent.getMeasuredWidth() / 5;
        view.setMinimumHeight(width);*/

        int width =  parent.getMeasuredWidth();
        //float height = (float) width / Config.ASPECT_RATIO;//(Width/Height)
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        params.width = (int) (width / Constants.CAMERA_NUMBER);
        view.setLayoutParams(params);
        //return new MyViewHolder(itemView);
        return new ViewHolder(view , viewType);
    }
    String url;
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {

      /*  TextView textViewName = holder.textViewName;
        // TextView textViewVersion = holder.textViewVersion;
        ImageView imageView = holder.imageViewIcon;

        textViewName.setText(cameraArray.get(listPosition).getName());
        //textViewVersion.setText(dataSet.get(listPosition).getVersion());
        imageView.setImageResource(cameraArray.get(listPosition).getImage());*/



        try {
            JSONObject obj = cameraArray.getJSONObject(listPosition);
            holder.itemTextView.setText(obj.getString("camera_name"));
            //holder.itemTextView.setBackgroundColor(mContext.getColor(android.R.color.holo_green_light));
            holder.iv_icon.setImageResource(R.drawable.camera_on);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // mItemClickListener.itemClicked(item);

                try {
                    JSONObject obj = cameraArray.getJSONObject(listPosition);
                    //url = camUrl + "/caminfo/";

                    String url =  obj.getString("camera_ip");
                    url = url + obj.getString("camera_videopath");
                    String camera_name =  obj.getString("camera_name");

                    //start camera rtsp player
                    Intent intent = new Intent(mContext, VideoVLC2Activity.class);
                    intent.putExtra("videoUrl",url);
                    intent.putExtra("name",camera_name);
                    mContext.startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return cameraArray.length();
    }
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        //common
        View view;
        int viewType;
        //for item
        TextView itemTextView;
        ImageView iv_icon;
        public ViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            this.view = view;
            itemTextView = (TextView) view.findViewById(R.id.text_item);
            iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
          //  itemTextView.setLayoutParams(new LinearLayout.LayoutParams(mContext));
        }
    }
}