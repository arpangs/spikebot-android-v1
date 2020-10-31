package com.spike.bot.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.Camera.CameraGridActivity;
import com.spike.bot.activity.Main2Activity;
import com.spike.bot.camera.CameraPlayer;
import com.spike.bot.core.Constants;
import com.spike.bot.model.CameraVO;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerDelegate;
import cn.nodemedia.NodePlayerView;

/**
 * Created by Sagar on 31/5/19.
 * Gmail : vipul patel
 */
public class CameraListFragment extends Fragment implements CameraGridActivity.toolBarImageCapture {

    public RecyclerView recyclerView;
    int height = 0, width = 0;
    NodePlayer nodePlayer;
    DVRADAapter dvradAapter;
    ArrayList<CameraVO> cameraVOArrayListTemp = new ArrayList<>();

    public static Fragment newInstance(ArrayList<CameraVO> cameraVOArrayListTemp, int height) {
        CameraListFragment f = new CameraListFragment();
        Bundle b = new Bundle();
        b.putSerializable("cameraVOArrayListTemp", cameraVOArrayListTemp);
        b.putInt("height", height);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.pager_item, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);

        ((CameraGridActivity) getActivity()).setObject(this);
        cameraVOArrayListTemp = (ArrayList<CameraVO>) getArguments().getSerializable("cameraVOArrayListTemp");
        height = getArguments().getInt("height");
        getWidth();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        dvradAapter = new DVRADAapter(getActivity(), cameraVOArrayListTemp);
        recyclerView.setAdapter(dvradAapter);
        return v;
    }

    public void getWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
    }

    @Override
    public void imageCapture() {
        loadView(recyclerView);
    }


    public void loadView(View cardView) {
        try {
            cardView.setDrawingCacheEnabled(true);
//            Bitmap bitmap =  CameraPlayer.saveOpenGL(200,200);

            Bitmap bitmap = Constants.takescreenshotOfRootView(recyclerView, recyclerView);
            cardView.setDrawingCacheEnabled(false);

            String mPath = Environment.getExternalStorageDirectory().toString() + "/camera.jpg";

            File imageFile = new File(mPath);
            FileOutputStream outputStream = new
                    FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        if (nodePlayer != null) {
            nodePlayer.stop();
            nodePlayer.release();
        }

        super.onDestroyView();
    }

    @Override
    public void onPause() {
        if (nodePlayer != null) {
            nodePlayer.stop();
            nodePlayer.release();
        }
        super.onPause();
    }

    public class DVRADAapter extends RecyclerView.Adapter<DVRADAapter.SensorViewHolder> {

        ArrayList<CameraVO> arrayListLog = new ArrayList<>();
        private Context mContext;

        public DVRADAapter(Context context, ArrayList<CameraVO> arrayListLog1) {
            this.mContext = context;
            this.arrayListLog = arrayListLog1;
        }

        @Override
        public DVRADAapter.SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_dvr, parent, false);
            return new DVRADAapter.SensorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final DVRADAapter.SensorViewHolder holder, final int position) {

            int heightis = height / 3;

            holder.txtCameraName.setText(arrayListLog.get(position).getCamera_name());

            if (arrayListLog.get(position).getIsActive() == 1) {

                holder.relCamera.setVisibility(View.VISIBLE);
                holder.relInActiveCamera.setVisibility(View.GONE);

//            holder.player.setMinimumHeight(heightis);

                holder.player.getLayoutParams().height = heightis;
                holder.player.getLayoutParams().width = width;
                holder.player.requestLayout();

                holder.progressBar.setVisibility(View.VISIBLE);
                nodePlayer = new NodePlayer(mContext);


                holder.player.setUIViewContentMode(NodePlayerView.UIViewContentMode.ScaleAspectFill);
                holder.player.setRenderType(NodePlayerView.RenderType.TEXTUREVIEW);

                ChatApplication.logDisplay("url1 is  " + arrayListLog.get(position).getCamera_name() + "  " + arrayListLog.get(position).getLoadingUrl());
                nodePlayer.setInputUrl(arrayListLog.get(position).getLoadingUrl());
                nodePlayer.setAudioEnable(true);
                nodePlayer.setPlayerView(holder.player);
                nodePlayer.setMaxBufferTime(5);
                holder.player.setUIViewContentMode(NodePlayerView.UIViewContentMode.ScaleAspectFill);

                nodePlayer.start();
                nodePlayer.setAudioEnable(false);

                nodePlayer.setNodePlayerDelegate(new NodePlayerDelegate() {
                    @Override
                    public void onEventCallback(NodePlayer player, int event, String msg) {
                        if (player.isPlaying() && msg.equals("NetStream.Buffer.Full")) {
                            try {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.progressBar.setVisibility(View.GONE);
                                    }
                                });

                            } catch (Exception e) {

                            }

                        }
                    }
                });

                holder.player.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), CameraPlayer.class);
                        intent.putExtra("videoUrl", arrayListLog.get(position).getLoadingUrl());
                        intent.putExtra("name", arrayListLog.get(position).getCamera_name());
                        intent.putExtra("isCloudConnect", Main2Activity.isCloudConnected);
                        startActivity(intent);
                    }
                });

                holder.txtCameraName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), CameraPlayer.class);
                        intent.putExtra("videoUrl", arrayListLog.get(position).getLoadingUrl());
                        intent.putExtra("name", arrayListLog.get(position).getCamera_name());
                        intent.putExtra("isCloudConnect", Main2Activity.isCloudConnected);
                        startActivity(intent);
                    }
                });


                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), CameraPlayer.class);
                        intent.putExtra("videoUrl", arrayListLog.get(position).getLoadingUrl());
                        intent.putExtra("name", arrayListLog.get(position).getCamera_name());
                        intent.putExtra("isCloudConnect", Main2Activity.isCloudConnected);
                        startActivity(intent);
                    }
                });
            } else {
                holder.progressBar.setVisibility(View.GONE);
                holder.relCamera.setVisibility(View.GONE);
                holder.relInActiveCamera.setVisibility(View.VISIBLE);

                holder.relInActiveCamera.getLayoutParams().height = heightis;
                holder.relInActiveCamera.getLayoutParams().width = width;
                holder.relInActiveCamera.requestLayout();
            }

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
            NodePlayerView player;
            //            ZoomLayout zoomlayout;
            TextView txtCameraName;
            ProgressBar progressBar;
            View view;
            FrameLayout frameLayout;
            ImageView imgInactiveCamera;
            RelativeLayout relCamera, relInActiveCamera;

            public SensorViewHolder(View view) {
                super(view);
                this.view = view;
                player = view.findViewById(R.id.player);
//                zoomlayout = view.findViewById(R.id.zoomLayout);
                txtCameraName = view.findViewById(R.id.txtCameraName);
                progressBar = view.findViewById(R.id.progressBar);
                frameLayout = view.findViewById(R.id.frameLayout);
                relCamera = view.findViewById(R.id.relCamera);
                imgInactiveCamera = view.findViewById(R.id.imgInactiveCamera);
                relInActiveCamera = view.findViewById(R.id.relInActiveCamera);
            }
        }
    }
}
