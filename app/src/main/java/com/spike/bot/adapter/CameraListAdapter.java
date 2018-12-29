package com.spike.bot.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.model.CameraVO;

import java.util.List;

/**
 * Created by Sagar on 20/3/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class CameraListAdapter extends RecyclerView.Adapter<CameraListAdapter.ListViewHolder>{

    List<CameraVO> cameraVOs;
    private CameraClick cameraClick;

    SparseBooleanArray sparseBooleanArray;

    public void setSelectedIds(int id){
        sparseBooleanArray.put(id,true);
    }

    public SparseBooleanArray getSelectedIds() {
        return sparseBooleanArray;
    }

    public CameraListAdapter(List<CameraVO> arrayList, CameraClick cameraClick){
        this.cameraVOs = arrayList;
        this.cameraClick = cameraClick;
        sparseBooleanArray = new SparseBooleanArray();
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_camera_list,parent,false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, final int position) {

        final CameraVO cameraVO = cameraVOs.get(position);

        holder.txt_camera.setText(cameraVO.getCamera_name()+"_"+cameraVO.getCamera_videopath());

        /*
        * change background color of selected single row item in adapter
        * default value of is ADAPTER_POSITION = -1
        *
        * */

        if(ChatApplication.ADAPTER_POSITION == position)
            holder.ll_main.setBackgroundColor(Color.parseColor("#40808080"));
        else
            holder.ll_main.setBackgroundColor(Color.WHITE);

        holder.ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatApplication.ADAPTER_POSITION = position;
                cameraClick.onCameraClick(position,cameraVO);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cameraVOs.size();
    }

    class ListViewHolder extends RecyclerView.ViewHolder{

        TextView txt_camera,txt_header;
        LinearLayout ll_main;

        public ListViewHolder(View itemView) {
            super(itemView);
            txt_camera = (TextView)itemView.findViewById(R.id.txt_camera);
            ll_main = (LinearLayout) itemView.findViewById(R.id.ll_main);
        }
    }
    public interface CameraClick{
        void onCameraClick(int position, CameraVO cameraTime);
    }

}
