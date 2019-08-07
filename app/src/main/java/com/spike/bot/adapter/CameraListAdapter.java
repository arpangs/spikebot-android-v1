package com.spike.bot.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.model.CameraSearchModel;
import com.spike.bot.model.CameraVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 20/3/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class CameraListAdapter extends RecyclerView.Adapter<CameraListAdapter.ListViewHolder> {

    List<CameraSearchModel> cameraVOs;
    private CameraClick cameraClick;
    Context context;
    SparseBooleanArray sparseBooleanArray;

    public void setSelectedIds(int id) {
        sparseBooleanArray.put(id, true);
    }

    public SparseBooleanArray getSelectedIds() {
        return sparseBooleanArray;
    }

    public CameraListAdapter(Context context, ArrayList<CameraSearchModel> arrayList, CameraClick cameraClick) {
        this.context = context;
        this.cameraVOs = arrayList;
        this.cameraClick = cameraClick;
        sparseBooleanArray = new SparseBooleanArray();
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_camera_list,parent,false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cameralist_view, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListViewHolder holder, final int position) {

        final CameraSearchModel cameraVO = cameraVOs.get(position);

//        holder.txtUserName.setText(cameraVO.getCamera_name());

        String styledText = "<u><font color='#0098C0'>"+cameraVO.getCamera_name()+"</font></u>";
        holder.txtUserName.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);

        if (cameraVO.isOpen()) {
//            holder.linearList.setVisibility(View.VISIBLE);
//                    notifyDataSetChanged();
        } else {

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            holder.recyclerRoomList.setLayoutManager(linearLayoutManager);

            SubCameraListAdapter subCameraListAdapter = new SubCameraListAdapter(context, cameraVOs.get(position).getArrayList(), cameraClick);
            holder.recyclerRoomList.setAdapter(subCameraListAdapter);
            subCameraListAdapter.notifyDataSetChanged();

//                    notifyItemChanged(holder.txtUserName.getId());
        }

        holder.sectionToggleButton.setId(position);
        holder.sectionToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

//                ChatApplication.ADAPTER_POSITION = position;
                if (cameraVO.isOpen()) {
                    cameraVOs.get(holder.sectionToggleButton.getId()).setOpen(false);
                    holder.linearList.setVisibility(View.GONE);
                } else {
                    cameraVOs.get(holder.sectionToggleButton.getId()).setOpen(true);
                    holder.linearList.setVisibility(View.VISIBLE);
                }
                notifyDataSetChanged();
            }
        });
//        holder.sectionToggleButton.setChecked(cameraVO.isOpen());

        holder.txtUserName.setId(position);
        holder.txtUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ChatApplication.ADAPTER_POSITION = position;
                if (cameraVO.isOpen()) {
                    holder.sectionToggleButton.setChecked(false);
                    cameraVOs.get(holder.txtUserName.getId()).setOpen(false);
                    holder.linearList.setVisibility(View.GONE);
                } else {
                    holder.sectionToggleButton.setChecked(true);
                    cameraVOs.get(holder.txtUserName.getId()).setOpen(true);
                    holder.linearList.setVisibility(View.VISIBLE);
                }
                notifyDataSetChanged();
            }
        });

        holder.linearMain.setId(position);
        holder.linearMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ChatApplication.ADAPTER_POSITION = position;
                if (cameraVO.isOpen()) {
                    holder.sectionToggleButton.setChecked(false);
                    cameraVOs.get(holder.linearMain.getId()).setOpen(false);
                    holder.linearList.setVisibility(View.GONE);
                } else {
                    cameraVOs.get(holder.linearMain.getId()).setOpen(true);
                    holder.sectionToggleButton.setChecked(true);
                    holder.linearList.setVisibility(View.VISIBLE);
                }
                notifyDataSetChanged();
            }
        });


        /*
         * change background color of selected single row item in adapter
         * default value of is ADAPTER_POSITION = -1
         *
         * */

//        if(ChatApplication.ADAPTER_POSITION == position)
//            holder.ll_main.setBackgroundColor(Color.parseColor("#40808080"));
//        else
//            holder.ll_main.setBackgroundColor(Color.WHITE);
//
//        holder.ll_main.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ChatApplication.ADAPTER_POSITION = position;
//                cameraClick.onCameraClick(position,cameraVO);
//                notifyDataSetChanged();
//            }
//        });
    }

    private void callExpaned(int position, CameraSearchModel cameraVO, LinearLayout linearList) {

    }

    @Override
    public int getItemCount() {
        return cameraVOs.size();
    }

    class ListViewHolder extends RecyclerView.ViewHolder {

        TextView txtUserName;
        LinearLayout linearChildUser, linearList;
        RecyclerView recyclerRoomList;
        ToggleButton sectionToggleButton;
        LinearLayout linearMain;

        public ListViewHolder(View itemView) {
            super(itemView);
            recyclerRoomList = itemView.findViewById(R.id.recyclerRoomList);
            linearChildUser = itemView.findViewById(R.id.linearChildUser);
            linearList = itemView.findViewById(R.id.linearList);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            sectionToggleButton = itemView.findViewById(R.id.toggle_button_section);
            linearMain = itemView.findViewById(R.id.linearMain);
        }
    }

    public interface CameraClick {
        void onCameraClick(int position, CameraVO cameraTime);
    }

    public class SubCameraListAdapter extends RecyclerView.Adapter<SubCameraListAdapter.SensorViewHolder> {

        private Context mContext;
        List<CameraVO> userArrayList = new ArrayList<>();
        CameraClick cameraClick;


        public SubCameraListAdapter(Context mContext, List<CameraVO> userArrayList, CameraClick cameraClick) {
            this.mContext = mContext;
            this.cameraClick = cameraClick;
            this.userArrayList = userArrayList;
        }

        @Override
        public SubCameraListAdapter.SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_camera_list, parent, false);
            return new SubCameraListAdapter.SensorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SubCameraListAdapter.SensorViewHolder holder, final int position) {

            holder.txt_camera.setText(userArrayList.get(position).getCamera_ip());

            holder.txt_camera.setId(position);
            holder.txt_camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callCameraClick(v.getId(), userArrayList.get(holder.txt_camera.getId()));
                }
            });

            holder.linearCamera.setId(position);
            holder.linearCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callCameraClick(v.getId(), userArrayList.get(holder.linearCamera.getId()));
                }
            });
        }


        @Override
        public int getItemCount() {
            return userArrayList.size();
        }


        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class SensorViewHolder extends RecyclerView.ViewHolder {

            public TextView txt_camera;
            public LinearLayout linearCamera;

            public SensorViewHolder(View view) {
                super(view);
                linearCamera = itemView.findViewById(R.id.linearCamera);
                txt_camera = itemView.findViewById(R.id.txt_camera);
            }
        }
    }

    private void callCameraClick(int id, CameraVO cameraVO) {
        cameraClick.onCameraClick(id, cameraVO);
        notifyDataSetChanged();
    }

}
