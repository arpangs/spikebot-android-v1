package com.spike.bot.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.R;
import com.spike.bot.fragments.UserChildListFragment;
import com.spike.bot.listener.UserChildAction;
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
    UserChildListFragment fragment;

    public ChildUserAdapter(Context activity, ArrayList<User> userArrayList, UserChildAction userChildAction, UserChildListFragment userchildlistfragment) {
        this.mContext = activity;
        this.userArrayList = userArrayList;
        this.userChildAction = userChildAction;
        this.fragment = userchildlistfragment;
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
        holder.linearChildUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // userChildAction.actionCHild("update", position, userArrayList.get(position));
            }
        });
        holder.imgMore.setId(position);
        holder.imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   fragment.BackgroundBlurred();
             //   holder.linearChildUser.setAlpha(0.50f);

                userChildAction.actionCHild("update", position, userArrayList.get(position), true);

                /*PopupMenu popup = new PopupMenu(mContext, v);

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
                                userChildAction.actionCHild("update", position, userArrayList.get(position), true);
                                break;
                            case R.id.action_delete_dots:
                                userChildAction.actionCHild("delete", position, userArrayList.get(position), true);
                                break;

                        }
                        return true;
                    }
                });

                popup.show();*/
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

        public TextView txtUserName, txtNocamera, txtRoomList;
        public ImageView imgMore, imgArrow;
        public RecyclerView recyclerCameraList, recyclerRoomList;
        public LinearLayout linearList, linearChildUser;
        FrameLayout frameLayout;

        public SensorViewHolder(View view) {
            super(view);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            recyclerRoomList = itemView.findViewById(R.id.recyclerRoomList);
            recyclerCameraList = itemView.findViewById(R.id.recyclerCameraList);
            imgArrow = itemView.findViewById(R.id.imgArrow);
            imgMore = itemView.findViewById(R.id.imgMore);
            linearList = itemView.findViewById(R.id.linearList);
            linearChildUser = itemView.findViewById(R.id.linearChildUser);
            txtNocamera = itemView.findViewById(R.id.txtNocamera);
            txtRoomList = itemView.findViewById(R.id.txtRoomList);
            frameLayout = itemView.findViewById(R.id.frame_layout);

            imgMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemView.setAlpha(0.5f);
                }
            });

        }
    }
}
