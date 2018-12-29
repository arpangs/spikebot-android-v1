package com.spike.cameraapp;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kaushal on 27/12/17.
 */

public class CameraAdapter extends RecyclerView.Adapter<CameraAdapter.ViewHolder> {

    Activity mContext;
    private JSONArray cameraArray;
    private  ItemClick itemClick;

    public ItemClick getItemClick() {
        return itemClick;
    }

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public JSONArray getCameraArray() {
        return cameraArray;
    }

    public void setCameraArray(JSONArray cameraArray) {
        this.cameraArray = cameraArray;
    }

    public CameraAdapter(Activity context, JSONArray cameraArray) {
        this.cameraArray = cameraArray;
        this.mContext =context;
    }
    @Override
    public int getItemViewType(int position) {

        return R.layout.row_room_edit_switch_item;
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
        //RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
       // params.width = (int) (width / Constants.CAMERA_NUMBER);
        //Log.d("","width = " + width );
        //view.setLayoutParams(params);
        //return new MyViewHolder(itemView);
        return new ViewHolder(view , viewType);
    }
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
            final String url = obj.getString("url");
            final String name = obj.getString("name");

            holder.itemTextView.setText(name);
            //holder.itemTextView.setBackgroundColor(mContext.getColor(android.R.color.holo_green_light));
            holder.iv_icon.setImageResource(R.drawable.camera_on);

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // mItemClickListener.itemClicked(item);

                    Log.d(""," setOnClickListener url = " + url);
                    Log.d(""," setOnClickListener name = " + name);
                    /*Intent intent = new Intent(mContext,VideoVLC2Activity.class);
                    intent.putExtra("videoUrl",url);
                    mContext.startActivity(intent);*/
                    itemClick.cameraClick(listPosition);
                    // CameraDialog dialog = new CameraDialog(mContext,url);
                    //dialog.show();
                }
            });
            holder.iv_icon_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // mItemClickListener.itemClicked(item);

                    Log.d(""," setOnClickListener iv_icon_edit = " + url);
                    Log.d(""," setOnClickListener name = " + name);
                    itemClick.editClick(listPosition);
                }
            });
            holder.iv_icon_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // mItemClickListener.itemClicked(item);

                    Log.d(""," setOnClickListener iv_icon_delete = " + url);
                    Log.d(""," setOnClickListener name = " + name);

                    itemClick.deleteClick(listPosition);
                }
            });
          /*  holder.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Log.d(""," onLongClick  url = " + url);
                    Log.d(""," onLongClick  name = " + name);
                    return true;
                }
            });*/



        } catch (JSONException e) {
            e.printStackTrace();
        }


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
        ImageView iv_icon_delete;
        ImageView iv_icon_edit;
        public ViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            this.view = view;
            itemTextView = (TextView) view.findViewById(R.id.text_item);
            iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            iv_icon_delete = (ImageView) view.findViewById(R.id.iv_icon_delete );
            iv_icon_edit = (ImageView) view.findViewById(R.id.iv_icon_edit);
          //  itemTextView.setLayoutParams(new LinearLayout.LayoutParams(mContext));
        }
    }
    public interface ItemClick{
        public void deleteClick(int Position);
        public void editClick(int Position);
        public void cameraClick(int Position);
    }
}