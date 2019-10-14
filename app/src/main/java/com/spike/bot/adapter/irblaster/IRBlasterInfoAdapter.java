package com.spike.bot.adapter.irblaster;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.view.ContextThemeWrapper;
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

import com.spike.bot.R;
import com.spike.bot.model.IRBlasterInfoRes;

import java.util.List;

/**
 * Created by Sagar on 31/7/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class IRBlasterInfoAdapter extends RecyclerView.Adapter<IRBlasterInfoAdapter.IRBlasterViewHolder> {

    private Context mContext;
    private IRBlasterInfoClick irBlasterInfoClick;
    private List<IRBlasterInfoRes.Data.IrBlasterList.RemoteList> irBlasterLists;
    IRBlasterInfoRes.Data.IrBlasterList.RemoteList.RemoteCurrentStatusDetails irBlaster;

    public IRBlasterInfoAdapter(List<IRBlasterInfoRes.Data.IrBlasterList.RemoteList> irBlasterLists, IRBlasterInfoClick irBlasterInfoClick) {
        this.irBlasterLists = irBlasterLists;
        this.irBlasterInfoClick = irBlasterInfoClick;
    }

    @Override
    public IRBlasterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_ir_blaster_info, parent, false);
        mContext = view.getContext();
        return new IRBlasterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IRBlasterViewHolder holder, final int position) {

        irBlaster = irBlasterLists.get(position).getRemoteCurrentStatusDetails();

        holder.IRBlasterName.setText(irBlaster.getRemoteName());

        holder.ir_blaster_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irBlasterInfoClick.onIRBlasterClick(irBlasterLists.get(position));
            }
        });

        holder.IRMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayOptionMenu(v, irBlasterLists.get(position), irBlasterInfoClick);
            }
        });

    }

    @Override
    public int getItemCount() {
        return irBlasterLists.size();
    }

    class IRBlasterViewHolder extends RecyclerView.ViewHolder {

        private TextView IRBlasterName;
        private ImageView IRMenu;
        private LinearLayout ir_blaster_root;

        IRBlasterViewHolder(View itemView) {
            super(itemView);

            ir_blaster_root = itemView.findViewById(R.id.ir_blaster_root);
            IRBlasterName = itemView.findViewById(R.id.ir_blaster_name);
            IRMenu = itemView.findViewById(R.id.ir_menu_option);
        }
    }

    public interface IRBlasterInfoClick {
        void onIRBlasterClick(IRBlasterInfoRes.Data.IrBlasterList.RemoteList irBlasterList);

        void onIRBlasterEdit(IRBlasterInfoRes.Data.IrBlasterList.RemoteList irBlasterList);

        void onIRBlasterDelete(IRBlasterInfoRes.Data.IrBlasterList.RemoteList irBlasterList);
    }

    /**
     * Edit, Delete context option menu
     *
     * @param v
     * @param remoteList
     * @param irBlasterInfoClick
     */
    private void displayOptionMenu(View v, final IRBlasterInfoRes.Data.IrBlasterList.RemoteList remoteList,
                                   final IRBlasterInfoClick irBlasterInfoClick) {

        PopupMenu popup = new PopupMenu(mContext, v);
        @SuppressLint("RestrictedApi") Context wrapper = new ContextThemeWrapper(mContext, R.style.PopupMenu);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            popup = new PopupMenu(wrapper, v, Gravity.RIGHT);
        } else {
            popup = new PopupMenu(wrapper, v);
        }
        popup.getMenuInflater().inflate(R.menu.menu_room_edit_option_popup, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_edit:
                        irBlasterInfoClick.onIRBlasterEdit(remoteList);
                        break;
                    case R.id.action_delete:
                        irBlasterInfoClick.onIRBlasterDelete(remoteList);
                        break;
                }
                return true;
            }
        });

        popup.show();
    }
}
