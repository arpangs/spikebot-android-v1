package com.spike.bot.activity.SmartDevice;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.R;
import com.spike.bot.core.Constants;
import com.spike.bot.model.CameraSearchModel;
import com.spike.bot.model.CameraVO;
import com.spike.bot.receiver.ExpandableRecyclerView;

import java.util.ArrayList;


public class ExpandableTestAdapter extends ExpandableRecyclerView.Adapter<ExpandableTestAdapter.ChildViewHolder, ExpandableRecyclerView.SimpleGroupViewHolder, String, String> {

    Context context;
    CameraClick cameraPlayBack;
    ArrayList<CameraSearchModel> arrayList = new ArrayList<>();

    public ExpandableTestAdapter(Context context, ArrayList<CameraSearchModel> arrayList, CameraClick cameraPlayBack) {
        this.context = context;
        this.arrayList = arrayList;
        this.cameraPlayBack = cameraPlayBack;
    }


    @Override
    public int getGroupItemCount() {
        return arrayList.size() - 1;
    }

    @Override
    public int getChildItemCount(int group) {
        return arrayList.get(group).getArrayList().size();
    }

    @Override
    public String getGroupItem(int position) {
        return "group :" + position;
    }

    @Override
    public String getChildItem(int group, int position) {
        return "group : " + group + " item" + position;
    }

    @Override
    protected ExpandableRecyclerView.SimpleGroupViewHolder onCreateGroupViewHolder(ViewGroup parent) {
        return new ExpandableRecyclerView.SimpleGroupViewHolder(parent.getContext());
    }

    @Override
    protected ChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_cameralist_view_sub, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public int getChildItemViewType(int group, int position) {
        return arrayList.get(group).getArrayList().size();
    }

    @Override
    public void onBindGroupViewHolder(ExpandableRecyclerView.SimpleGroupViewHolder holder, int group) {
        super.onBindGroupViewHolder(holder, group);
        String styledText = "<font color='#0279e1'>" + arrayList.get(group).getCamera_name() + "</font>";
        holder.txtUserName.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
    }

    @Override
    public void onBindChildViewHolder(final ChildViewHolder holder, final int group, final int position) {
        super.onBindChildViewHolder(holder, group, position);

        /*original*/
//        holder.txtCameraLink.setText(arrayList.get(group).getArrayList().get(position).getCamera_name() + " " + arrayList.get(group).getArrayList().get(position).getCamera_ip());

        /*change by arpan - 20 oct 2020*/

        String mMainvideoUrl = arrayList.get(group).getArrayList().get(position).getLoadingUrl();

        try {

            String MainName = mMainvideoUrl.substring(0, mMainvideoUrl.length() - 4);


            String arr[] = MainName.split("_");
            String timestamps = arr[arr.length - 1];

            holder.txtCameraLink.setText(Constants.getDate(timestamps) + " [" + Constants.getStartTime(timestamps).toLowerCase() + " TO " + Constants.getEndTime(timestamps).toLowerCase() + "] ");

        } catch (Exception e) {
            e.printStackTrace();
            holder.txtCameraLink.setText(arrayList.get(group).getArrayList().get(position).getCamera_name());
        }


        holder.view.setId(group);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraPlayBack.onCameraClick(holder.view.getId(), position, arrayList.get(holder.view.getId()).getArrayList().get(position));
            }
        });
    }

    public interface CameraClick {
        void onCameraClick(int group, int child, CameraVO cameraTime);
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtCameraLink;
        public View view;

        public ChildViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            txtCameraLink = itemView.findViewById(R.id.txtCameraLink);
        }
    }

}
