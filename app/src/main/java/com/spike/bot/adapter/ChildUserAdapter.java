package com.spike.bot.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.fragments.UserChildListFragment;
import com.spike.bot.listener.SelectCamera;
import com.spike.bot.listener.UserChildAction;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.RoomVO;
import com.spike.bot.model.User;

import java.util.ArrayList;

/**
 * Created by Sagar on 8/3/19.
 * Gmail : jethvasagar2@gmail.com
 */
public class ChildUserAdapter extends RecyclerView.Adapter<ChildUserAdapter.SensorViewHolder> {

    private Context mContext;
    ArrayList<User> userArrayList = new ArrayList<>();
    UserChildAction userChildAction;
    ArrayList<RoomVO> roomList = new ArrayList<>();
    ArrayList<CameraVO> cameraLis = new ArrayList<>();


    public ChildUserAdapter(Context activity, ArrayList<User> userArrayList, UserChildAction userChildAction, ArrayList<RoomVO> roomList, ArrayList<CameraVO> cameraList) {
        this.mContext = activity;
        this.userArrayList = userArrayList;
        this.userChildAction = userChildAction;
        this.cameraLis = cameraList;
        this.roomList = roomList;

    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_user_child, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {

        holder.txtUserName.setText(userArrayList.get(position).getFirstname());

        GridLayoutManager linearLayoutManager = new GridLayoutManager(mContext, 4);
        GridLayoutManager linearLayoutManager2 = new GridLayoutManager(mContext, 4);
        holder.recyclerRoomList.setLayoutManager(linearLayoutManager);
        holder.recyclerCameraList.setLayoutManager(linearLayoutManager2);

        if(userArrayList.get(position).getRoomList()!=null){
            holder.txtRoomList.setVisibility(View.GONE);
            holder.recyclerRoomList.setVisibility(View.VISIBLE);
            SubRoomListAdapter subRoomListAdapter = new SubRoomListAdapter(mContext, userArrayList.get(position).getRoomList());
            holder.recyclerRoomList.setAdapter(subRoomListAdapter);
        }else {
            holder.txtRoomList.setVisibility(View.VISIBLE);
            holder.recyclerRoomList.setVisibility(View.GONE);
        }

        if(userArrayList.get(position).getCameralist()!=null){
            holder.txtNocamera.setVisibility(View.GONE);
            holder.recyclerCameraList.setVisibility(View.VISIBLE);
            SubCameraListAdapter subCameraListAdapter = new SubCameraListAdapter(mContext, userArrayList.get(position).getCameralist());
            holder.recyclerCameraList.setAdapter(subCameraListAdapter);
        }else {
            holder.recyclerCameraList.setVisibility(View.GONE);
            holder.txtNocamera.setVisibility(View.VISIBLE);
        }

       // holder.recyclerCameraList.setVisibility(View.GONE);
        //holder.txtNocamera.setVisibility(View.VISIBLE);
        if (userArrayList.get(position).getIsopen()) {
            holder.imgArrow.setImageDrawable(mContext.getResources().getDrawable(R.drawable.arrow_down_gray));
            holder.linearList.setVisibility(View.VISIBLE);
        } else {
            holder.imgArrow.setImageDrawable(mContext.getResources().getDrawable(R.drawable.arrow_up_gray));
            holder.linearList.setVisibility(View.GONE);
        }

        holder.imgArrow.setId(position);
        holder.imgArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userArrayList.get(position).getIsopen()) {
                    holder.linearList.setVisibility(View.GONE);
                    userArrayList.get(position).setIsopen(false);
                } else {
                    holder.linearList.setVisibility(View.VISIBLE);
                    userArrayList.get(position).setIsopen(true);
                }
                notifyDataSetChanged();
            }
        });

        holder.txtUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imgArrow.performClick();
            }
        });
        holder.linearChildUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imgArrow.performClick();
            }
        });


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
                                userChildAction.actionCHild("update", position, userArrayList.get(position));
                                break;
                            case R.id.action_delete_dots:
                                userChildAction.actionCHild("delete", position, userArrayList.get(position));
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
        return userArrayList.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class SensorViewHolder extends RecyclerView.ViewHolder {

        public TextView txtUserName,txtNocamera,txtRoomList;
        public ImageView imgMore, imgArrow;
        public RecyclerView recyclerCameraList, recyclerRoomList;
        public LinearLayout linearList, linearChildUser;

        public SensorViewHolder(View view) {
            super(view);
            txtUserName = (TextView) itemView.findViewById(R.id.txtUserName);
            recyclerRoomList = itemView.findViewById(R.id.recyclerRoomList);
            recyclerCameraList = itemView.findViewById(R.id.recyclerCameraList);
            imgArrow = itemView.findViewById(R.id.imgArrow);
            imgMore = itemView.findViewById(R.id.imgMore);
            linearList = itemView.findViewById(R.id.linearList);
            linearChildUser = itemView.findViewById(R.id.linearChildUser);
            txtNocamera = itemView.findViewById(R.id.txtNocamera);
            txtRoomList = itemView.findViewById(R.id.txtRoomList);
        }
    }

    public String checkRoomId(String id) {
        String name = "";
        for (int i = 0; i < roomList.size(); i++) {
            if (roomList.get(i).getRoomId().equalsIgnoreCase(id)) {
                name = roomList.get(i).getRoomName();
            }
        }

        return name;
    }

    public String checkCameraId(String id) {
        String name = "";
        for (int i = 0; i < UserChildListFragment.cameraList.size(); i++) {
            if (UserChildListFragment.cameraList.get(i).getCamera_id().equalsIgnoreCase(id)) {
                name = UserChildListFragment.cameraList.get(i).getCamera_name();
            }
        }

        return name;
    }


    public class SubRoomListAdapter extends RecyclerView.Adapter<SubRoomListAdapter.SensorViewHolder> {

        private Context mContext;
        ArrayList<String> userArrayList = new ArrayList<>();


        public SubRoomListAdapter(Context mContext, ArrayList<String> userArrayList) {
            this.mContext = mContext;
            this.userArrayList = userArrayList;
        }

        @Override
        public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_child_user, parent, false);
            return new SensorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SensorViewHolder holder, final int position) {

//            holder.cardView.setPadding(0,0,0,0);
            holder.iv_icon_text.setVisibility(View.GONE);
            holder.text_item.setTextColor(mContext.getResources().getColor(R.color.automation_black));
            holder.text_item.setText(checkRoomId(userArrayList.get(position)));
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

            public LinearLayout cardView;
            public TextView text_item;
            public ImageView iv_icon_text, iv_icon_active_camera, iv_icon;

            public SensorViewHolder(View view) {
                super(view);
                text_item = (TextView) itemView.findViewById(R.id.text_item);
                iv_icon_text = (ImageView) itemView.findViewById(R.id.iv_icon_text);
                iv_icon_active_camera = (ImageView) itemView.findViewById(R.id.iv_icon_active_camera);
                iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
//                cardView =  itemView.findViewById(R.id.cardView);
            }
        }
    }

    public class SubCameraListAdapter extends RecyclerView.Adapter<SubCameraListAdapter.SensorViewHolder> {

        private Context mContext;
        ArrayList<String> userArrayList = new ArrayList<>();


        public SubCameraListAdapter(Context mContext, ArrayList<String> userArrayList) {
            this.mContext = mContext;
            this.userArrayList = userArrayList;
        }

        @Override
        public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_child_user, parent, false);
            return new SensorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SensorViewHolder holder, final int position) {

            holder.iv_icon.setImageResource(Common.getIcon(0,"camera"));
           // holder.cardView.setPadding(0,0,0,0);
            holder.iv_icon_text.setVisibility(View.GONE);
            holder.text_item.setTextColor(mContext.getResources().getColor(R.color.automation_black));
            holder.text_item.setText(checkCameraId(userArrayList.get(position)));
            holder.text_item.setSingleLine(false);
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

            public LinearLayout cardView;
            public TextView text_item;
            public ImageView iv_icon_text, iv_icon_active_camera, iv_icon;

            public SensorViewHolder(View view) {
                super(view);
                text_item = (TextView) itemView.findViewById(R.id.text_item);
                iv_icon_text = (ImageView) itemView.findViewById(R.id.iv_icon_text);
                iv_icon_active_camera = (ImageView) itemView.findViewById(R.id.iv_icon_active_camera);
                iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
               // cardView =  itemView.findViewById(R.id.cardView);
            }
        }
    }

}
