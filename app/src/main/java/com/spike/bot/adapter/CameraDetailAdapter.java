package com.spike.bot.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.customview.recycle.ItemClickListener;
import com.spike.bot.listener.SelectCamera;
import com.spike.bot.model.CameraCounterModel;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.PanelVO;

import java.util.ArrayList;
import java.util.List;

public class CameraDetailAdapter extends RecyclerView.Adapter<CameraDetailAdapter.ViewHolder> {

    private Context mContext;
    private ClickListener mItemClickListener;
    private CameraClickListener mCameraClickListener;
    private JetsonClickListener jetsonClickListener;
    ArrayList<CameraVO> arrayListCamera;

    List<CameraCounterModel.Data.CameraCounterList> counterlist = new ArrayList<>();
    CameraCounterModel.Data counterres = new CameraCounterModel.Data();

    public List<CameraCounterModel.Data.CameraCounterList> getCounterlist() {
        return counterlist;
    }

    public void setCounterlist(List<CameraCounterModel.Data.CameraCounterList> counterlist) {
        this.counterlist = counterlist;
    }

    public CameraCounterModel.Data getCounterres() {
        return counterres;
    }

    public void setCounterres(CameraCounterModel.Data counterres) {
        this.counterres = counterres;
    }


    public CameraDetailAdapter(Context context, ArrayList<CameraVO> arrayListCamera1) {
        this.mContext = context;
        this.arrayListCamera = arrayListCamera1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_camera_listview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        getCameracounter();
        final CameraVO cameraVO = (CameraVO) arrayListCamera.get(position);
        holder.itemTextView.setText(cameraVO.getCamera_name());

        holder.ll_room_item.setOnClickListener(null);

        if (cameraVO.getJetson_device_id().startsWith("JETSON-")) {
            holder.frame_camera_alert.setVisibility(View.VISIBLE);
        } else {
            try {
                holder.frame_camera_alert.setVisibility(View.INVISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (cameraVO.getIsActive() == 0) {
            holder.iv_icon.setImageResource(Common.getIconInActive(1, cameraVO.getCamera_icon()));
        } else {
            holder.iv_icon.setImageResource(Common.getIcon(1, cameraVO.getCamera_icon()));
        }

        if (!TextUtils.isEmpty(cameraVO.getIs_unread()) && !cameraVO.getIs_unread().equalsIgnoreCase("0")) {
            holder.txtCameraCount.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(cameraVO.getIs_unread()) && Integer.parseInt(cameraVO.getIs_unread()) > 99) {
                holder.txtCameraCount.setText("99+");
            } else {
                holder.txtCameraCount.setVisibility(View.VISIBLE);
                holder.txtCameraCount.setText("" + cameraVO.getIs_unread());
            }

        } else {
            holder.txtCameraCount.setVisibility(View.INVISIBLE);
        }

        ChatApplication.logDisplay("SectionExpandableGridAdapter count " + cameraVO.getIs_unread());

        holder.iv_icon_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraClickListener.itemClicked(cameraVO, "editcamera");
            }
        });

        holder.iv_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraClickListener != null && cameraVO.getIsActive() == 1) {
                    mCameraClickListener.itemClicked(cameraVO, "editclick_true");
                } else {
                    Common.showToast("Camera not active");
                }
            }
        });
        holder.itemTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraClickListener != null && cameraVO.getIsActive() == 1) {
                    mCameraClickListener.itemClicked(cameraVO, "editclick_true");
                } else {
                    Common.showToast("Camera not active");
                }
            }
        });
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraClickListener != null && cameraVO.getIsActive() == 1) {
                    mCameraClickListener.itemClicked(cameraVO, "editclick_true");
                } else {
                    Common.showToast("Camera not active");
                }
            }
        });


        holder.imgLogCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraVO.getJetson_device_id().startsWith("JETSON-")) {
                    jetsonClickListener.jetsonClicked(null, "showjetsoncameraLog", cameraVO.getJetson_device_id(), cameraVO.getCamera_id());
                } else {
                    mItemClickListener.itemClicked(cameraVO, "cameraLog");
                }

            }
        });
    }


    @Override
    public int getItemCount() {
        return arrayListCamera.size();
    }

    public interface CameraClickListener {
        void itemClicked(CameraVO item, String action);
    }

    public interface JetsonClickListener {
        void jetsonClicked(PanelVO panelVO, String action, String jetsonid, String camera_id);
    }

    public interface ClickListener {
        void itemClicked(CameraVO item, String action);
    }

    public void setClickListener(ClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void setCameraClickListener(CameraClickListener mCameraClickListener) {
        this.mCameraClickListener = mCameraClickListener;
    }

    public void setJetsonClickListener(JetsonClickListener jetsonClickListener) {
        this.jetsonClickListener = jetsonClickListener;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView text_item, itemTextView, txtCameraCount;
        public ImageView iv_icon_text, iv_icon, imgLogCamera;
        LinearLayout ll_room_item;
        FrameLayout frame_camera_alert;
        View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            itemTextView = view.findViewById(R.id.text_item);
            iv_icon = view.findViewById(R.id.iv_icon);
            iv_icon_text = view.findViewById(R.id.iv_icon_text);
            ll_room_item = view.findViewById(R.id.ll_room_item);
            imgLogCamera = view.findViewById(R.id.imgLogCamera);
            txtCameraCount = view.findViewById(R.id.txtCameraCount);
            frame_camera_alert = view.findViewById(R.id.frame_camera_alert);
        }
    }

    public void getCameracounter() {

        for (int i = 0; i < counterlist.size(); i++) {
            String cameras_id = counterlist.get(i).getCameraId();
            String jetson_device_id = counterlist.get(i).getJetsonDeviceId();
            int total_unread = counterlist.get(i).getTotalUnread();

        /*    ChatApplication.logDisplay("getCameracounter camera_id" + " " + cameras_id);
            ChatApplication.logDisplay("getCameracounter jetson_device_id " + " " + jetson_device_id);
            ChatApplication.logDisplay("getCameracounter total_unread " + " " + total_unread);*/


            for (int l = 0; l < arrayListCamera.size(); l++) {
                if (arrayListCamera.get(l) instanceof CameraVO) {
                    CameraVO cameraVO1 = (CameraVO) arrayListCamera.get(l);
                    ChatApplication.logDisplay("getCameracounter total unread id" + " " + cameraVO1.getCamera_id());

                    if (cameraVO1 != null && cameraVO1.getJetson_device_id().startsWith("JETSON-")) {
                        if (cameraVO1.getCamera_id().equalsIgnoreCase(cameras_id) && cameraVO1.getJetson_device_id().equalsIgnoreCase(jetson_device_id)) {
                            cameraVO1.setIs_unread(String.valueOf(total_unread));
                            break;
                        }
                    }

                    if (cameraVO1 != null && cameraVO1.getCamera_id().equalsIgnoreCase(cameras_id)) {
                        cameraVO1.setIs_unread(String.valueOf(total_unread));
                        // mSectionedExpandableGridAdapter.notifyItemChanged(l);
                        break;
                        //  mSectionedExpandableGridAdapter.notifyItemChanged(k);
                        //   break;
                    }
                }
            }


        }

    }
}
