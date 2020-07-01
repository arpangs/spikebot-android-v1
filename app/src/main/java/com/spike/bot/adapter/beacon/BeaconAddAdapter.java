package com.spike.bot.adapter.beacon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.Beacon.BeaconScannerAddActivity;
import com.spike.bot.R;
import com.spike.bot.adapter.irblaster.IRBlasterAddRemoteList;
import com.spike.bot.model.IRBlasterAddRes;

import java.util.List;

public class BeaconAddAdapter extends RecyclerView.Adapter<BeaconAddAdapter.BeaconViewHolder> {

    List<IRBlasterAddRes.Datum> ScannerList;
    IRBlasterAddRes.Datum scanner;

    private Context mContext;
    private BeaconAction beaconAction;

    public BeaconAddAdapter(List<IRBlasterAddRes.Datum> irList, BeaconScannerAddActivity beaconAction) {
        this.ScannerList = irList;
        this.beaconAction = beaconAction;
    }

    @Override
    public BeaconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_beacon_scanner_list, parent, false);
        mContext = view.getContext();
        return new BeaconViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BeaconViewHolder holder, final int position) {
      /*  ir = irList.get(position);
        holder.mscannerName.setText(ir.getDeviceName());
        if (ir.getRoom() != null) {

            holder.mRoomName.setText("[ " + ir.getRoom().getRoomName() + " ]");
        }

        holder.mbeaconListAdapter = new BeaconScannerAddBeaconList(ir.getRemoteList());
        holder.mbeaconList.setAdapter(holder.mbeaconListAdapter);

        holder.mbeaconEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beaconAction.onEdit(position, ir);
            }
        });

        holder.mbeaconDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beaconAction.onDelete(position, ir);
            }
        });*/

      try {

          scanner = ScannerList.get(position);
          holder.mscannerName.setText(scanner.getDeviceName());
          if (scanner.getRoom() != null) {

              holder.mRoomName.setText("[ " + scanner.getRoom().getRoomName() + " ]");
          }

          if(scanner.getIsActive().equals("n")){
              holder.img_inactive.setVisibility(View.VISIBLE);
          } else{
              holder.img_inactive.setVisibility(View.GONE);
          }

       /*   holder.mbeaconListAdapter = new BeaconScannerAddBeaconList(scanner.getRemoteList());
          holder.mbeaconList.setAdapter(holder.mbeaconListAdapter);*/

         holder.mbeaconEdit.setId(position);
          holder.mbeaconEdit.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                  beaconAction.onEdit(position, ScannerList.get(holder.mbeaconEdit.getId()));
              }
          });

          holder.mbeaconDelete.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  beaconAction.onDelete(position, scanner);
              }
          });
      }catch (Exception e){
          e.printStackTrace();
      }
    }

    @Override
    public int getItemCount() {
        if (ScannerList.size() == 0) {
          return 1;
        } else {
            return ScannerList.size();
        }
    }

    class BeaconViewHolder extends RecyclerView.ViewHolder {

        private TextView mscannerName, mRoomName;
        private ImageView mbeaconEdit, mbeaconDelete,img_inactive;
        private RecyclerView mbeaconList;
        private BeaconScannerAddBeaconList mbeaconListAdapter;

        BeaconViewHolder(View itemView) {
            super(itemView);
            mscannerName = itemView.findViewById(R.id.et_panel);
            mRoomName = itemView.findViewById(R.id.room_name);
            mbeaconEdit = itemView.findViewById(R.id.iv_room_panel_add);
            mbeaconDelete = itemView.findViewById(R.id.iv_room_panel_delete);
            img_inactive = itemView.findViewById(R.id.img_inactive);

            mbeaconList = itemView.findViewById(R.id.list_edit_device);
            mbeaconList.setLayoutManager(new GridLayoutManager(mContext, 4));
        }
    }


    public interface BeaconAction {
        void onEdit(int position, IRBlasterAddRes.Datum ir);

        void onDelete(int position, IRBlasterAddRes.Datum ir);
    }

}
