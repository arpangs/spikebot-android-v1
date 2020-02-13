package com.spike.bot.adapter.filter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.kp.core.DateHelper;
import com.spike.bot.R;
import com.spike.bot.adapter.CameraNotiListAdapter;
import com.spike.bot.adapter.TempSensorInfoAdapter;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.customview.OnSwipeTouchListener;
import com.spike.bot.listener.UpdateCameraAlert;
import com.spike.bot.model.CameraAlertList;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.CameraViewModel;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sagar on 26/11/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class CameraNotificationAdapter extends RecyclerView.Adapter<CameraNotificationAdapter.SensorViewHolder> {

    private TempSensorInfoAdapter.OnNotificationContextMenu onNotificationContextMenu;
    private Context mContext;
    ArrayList<CameraAlertList> arrayListLog = new ArrayList<>();
    ArrayList<CameraVO> getCameraList = new ArrayList<>();
    ArrayList<CameraViewModel> arrayList;
    List<String> myList;

    public GridLayoutManager gridLayoutManager;
    public UpdateCameraAlert updateCameraAlert;


    public CameraNotificationAdapter(Context context, GridLayoutManager gridLayoutManager
            , ArrayList<CameraAlertList> arrayListLog, ArrayList<CameraVO> getCameraList, UpdateCameraAlert updateCameraAlert) {
        this.onNotificationContextMenu = onNotificationContextMenu;
        this.mContext = context;
        this.arrayListLog = arrayListLog;
        this.getCameraList = getCameraList;
        this.gridLayoutManager = gridLayoutManager;
        this.updateCameraAlert = updateCameraAlert;

    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_camera_nitification, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {

        try {
            holder.txtStartTime.setText("" + DateHelper.formateDate(DateHelper.parseTimeSimple(arrayListLog.get(position).getStartTime(),
                    DateHelper.DATE_FROMATE_HH_MM_TEMP), DateHelper.DATE_FROMATE_H_M_AMPM));


            holder.txtEndTime.setText("" + DateHelper.formateDate(DateHelper.parseTimeSimple(arrayListLog.get(position).getEndTime(),
                    DateHelper.DATE_FROMATE_HH_MM_TEMP), DateHelper.DATE_FROMATE_H_M_AMPM));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (position == 0) {
            holder.viewLine.setVisibility(View.GONE);
        } else {
            holder.viewLine.setVisibility(View.VISIBLE);
        }


        if (arrayListLog.get(position).getCameraIds() != null)
        {
            myList = new ArrayList<String>(Arrays.asList(arrayListLog.get(position).getCameraIds().split(",")));
            if (myList.size() > 0)
            {
                arrayList = new ArrayList<>();
                for (int j = 0; j < myList.size(); j++) {
                    for (int i = 0; i < getCameraList.size(); i++) {
                        if (getCameraList.get(i).getCamera_id().equalsIgnoreCase(myList.get(j))) {
                            CameraViewModel cameraViewModel = new CameraViewModel();
                            cameraViewModel.setId(getCameraList.get(i).getCamera_id());
                            cameraViewModel.setName(getCameraList.get(i).getCamera_name());
                            cameraViewModel.setIsActivite("" + getCameraList.get(i).getIsActive());
                            arrayList.add(cameraViewModel);
                            arrayListLog.get(position).setCameraViewModels(arrayList);
                        }
                    }
                }

                if (arrayListLog.get(position).getCameraViewModels() != null) {
                    if (arrayListLog.get(position).getCameraViewModels().size() > 0) {
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, Constants.SWITCH_NUMBER);
                        holder.recyclerCamera.setLayoutManager(gridLayoutManager);
                        CameraNotiListAdapter cameraNotiListAdapter = new CameraNotiListAdapter(mContext, arrayListLog.get(position).getCameraViewModels());
                        holder.recyclerCamera.setAdapter(cameraNotiListAdapter);
                    }
                }

                if (arrayListLog.get(position).getIsOpen()) {
                    holder.imgArrow.setImageDrawable(mContext.getResources().getDrawable(R.drawable.arrow_down_gray));
                    holder.recyclerCamera.setVisibility(View.VISIBLE);
                } else {
                    holder.imgArrow.setImageDrawable(mContext.getResources().getDrawable(R.drawable.arrow_up_gray));
                    holder.recyclerCamera.setVisibility(View.GONE);
                }

                if (arrayListLog.get(position).getIsActive() == 1) {
                    holder.switchAlert.setChecked(true);
                } else {
                    holder.switchAlert.setChecked(false);
                }

            }
        }


        holder.linearCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayListLog.get(position).getIsOpen()) {
                    arrayListLog.get(position).setIsOpen(false);
                    holder.recyclerCamera.setVisibility(View.GONE);
                } else {
                    arrayListLog.get(position).setIsOpen(true);
                    holder.recyclerCamera.setVisibility(View.VISIBLE);
                }
                notifyDataSetChanged();
            }
        });

        if (Common.getPrefValue(mContext, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")) {
            if (Common.getPrefValue(mContext, Constants.USER_ID).equalsIgnoreCase(arrayListLog.get(position).getUser_id())) {
                holder.imgMore.setVisibility(View.VISIBLE);
            } else {
                holder.imgMore.setVisibility(View.INVISIBLE);
            }
        }

        holder.imgMore.setOnClickListener(new View.OnClickListener() {
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
                                updateCameraAlert.updatecameraALert("update", arrayListLog.get(position), holder.switchAlert, position);
                                break;
                            case R.id.action_delete_dots:
                                updateCameraAlert.updatecameraALert("delete", arrayListLog.get(position), holder.switchAlert, position);
                                break;

                        }
                        return true;
                    }
                });

                popup.show();
            }
        });

        holder.switchAlert.setOnTouchListener(new OnSwipeTouchListener(mContext) {
            @Override
            public void onClick() {
                super.onClick();
                holder.switchAlert.setChecked(holder.switchAlert.isChecked());
                updateCameraAlert.updatecameraALert("switch", arrayListLog.get(position), holder.switchAlert, position);
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                holder.switchAlert.setChecked(holder.switchAlert.isChecked());
                updateCameraAlert.updatecameraALert("switch", arrayListLog.get(position), holder.switchAlert, position);
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                holder.switchAlert.setChecked(holder.switchAlert.isChecked());
                updateCameraAlert.updatecameraALert("switch", arrayListLog.get(position), holder.switchAlert, position);
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

        public AppCompatImageView imgArrow, imgMore;
        public AppCompatTextView txtStartTime, txtEndTime;
        public SwitchCompat switchAlert;
        public LinearLayout linearCamera;
        public RecyclerView recyclerCamera;
        public View viewLine;

        public SensorViewHolder(View view) {
            super(view);
            imgArrow =  itemView.findViewById(R.id.imgArrow);
            txtStartTime =  itemView.findViewById(R.id.txtStartTime);
            txtEndTime =  itemView.findViewById(R.id.txtEndTime);
            switchAlert =  itemView.findViewById(R.id.switchAlert);
            imgMore =  itemView.findViewById(R.id.imgMore);
            linearCamera =  itemView.findViewById(R.id.linearCamera);
            recyclerCamera =  itemView.findViewById(R.id.recyclerCamera);
            viewLine =  itemView.findViewById(R.id.viewLine);
        }
    }
}
