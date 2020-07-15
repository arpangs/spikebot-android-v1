package com.spike.bot.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kp.core.ActivityHelper;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.Constants;
import com.spike.bot.model.IRDeviceDetailsRes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 2/4/19.
 * Gmail : jethvasagar2@gmail.com
 */
public class SmartRemoteAdapter extends RecyclerView.Adapter<SmartRemoteAdapter.SensorViewHolder> {

    List<IRDeviceDetailsRes.Data> arrayListLog = new ArrayList<>();
    String url = ChatApplication.url + Constants.SAVE_EDIT_SWITCH;
    private TempSensorInfoAdapter.OnNotificationContextMenu onNotificationContextMenu;
    private Activity mContext;

    public SmartRemoteAdapter(Activity context, List<IRDeviceDetailsRes.Data> arrayListLog1) {
        this.mContext = context;
        this.arrayListLog = arrayListLog1;
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_smart_remote, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {

        holder.txtNameRemote.setText(arrayListLog.get(position).getDeviceName());

        holder.imgEditRemote.setId(position);
        holder.imgEditRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showBottomSheetDialog(arrayListLog.get(v.getId()).getDeviceId(), v.getId(), arrayListLog.get(v.getId()).getDeviceName());
                showBottomSheetDialog(arrayListLog.get(position).getDeviceId(), position, arrayListLog.get(position).getDeviceName(), position); // dev arpan update this
            }
        });


        holder.imgDeleteRemote.setId(position);
        holder.imgDeleteRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {


            }
        });
    }

    public void showBottomSheetDialog(String module_id, final int id, String smart_remote_name, int position) {
        View view = mContext.getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);


        BottomSheetDialog dialog = new BottomSheetDialog(mContext, R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        dialog.show();

        txt_bottomsheet_title.setText("What would you like to do in" + " " + arrayListLog.get(id).getDeviceName() + " " + "?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                dialogRemoteshow(v.getId());
                dialogRemoteshow(position);
            }
        });

        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete ?", new ConfirmDialog.IDialogCallback() {
                    @Override
                    public void onConfirmDialogYesClick() {
                        if (arrayListLog != null && arrayListLog.size() > 0) {
//                            deleteRemote(arrayListLog.get(v.getId()).getDeviceId(), v.getId(), arrayListLog.get(v.getId()).getDeviceName());
                            deleteRemote(arrayListLog.get(position).getDeviceId(), position, arrayListLog.get(position).getDeviceName());
                        } else {
                            Toast.makeText(mContext, "No Smart Remote found", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onConfirmDialogNoClick() {

                    }
                });
                newFragment.show(mContext.getFragmentManager(), "dialog");
            }
        });
    }

    private void dialogRemoteshow(final int b) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_remote_key);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);


        final AppCompatEditText editKeyValue = dialog.findViewById(R.id.editKeyValue);
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView txtTitalMood = dialog.findViewById(R.id.txtTitalMood);
        ImageView iv_close = dialog.findViewById(R.id.iv_close);

        btnCancel.setVisibility(View.GONE);

        txtTitalMood.setText("Smart Remote");
        editKeyValue.setFilters(new InputFilter[]{ChatApplication.filter, new InputFilter.LengthFilter(30)});

        editKeyValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatApplication.keyBoardHideForce(mContext);
                dialog.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editKeyValue.getText().toString().length() == 0) {
                    ChatApplication.showToast(mContext, "Please enter smart remote name");
                } else {
                    ChatApplication.keyBoardHideForce(mContext);
                    saveSensor(dialog, arrayListLog.get(b), editKeyValue.getText().toString(), b);
                }
            }
        });
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    public void saveSensor(final Dialog dialog, IRDeviceDetailsRes.Data smartRemoteModel, final String name, final int position) {

        if (!ActivityHelper.isConnectingToInternet(mContext)) {
            ChatApplication.showToast(mContext.getApplicationContext(), "" + R.string.disconnect);
            return;
        }

        ActivityHelper.showProgressDialog(mContext, "Please wait...", false);

       /* JSONObject obj = new JSONObject();
        try {
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            obj.put("user_id", Common.getPrefValue(mContext, Constants.USER_ID));
            obj.put("device_id", smartRemoteModel.getDeviceId());
            obj.put("device_name", name);

        } catch (JSONException e) {
            e.printStackTrace();
        }*/
       /* new GetJsonTask(mContext, url, "POST", obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        dialog.dismiss();
                        arrayListLog.get(position).setDeviceName(name);
                        notifyDataSetChanged();
                    } else {
                        ChatApplication.showToast(mContext, message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();

                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();*/
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().SaveSmartRemote(smartRemoteModel.getDeviceId(), name, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {

                    JSONObject result = new JSONObject(stringResponse);
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        dialog.dismiss();
                        arrayListLog.get(position).setDeviceName(name);
                        notifyDataSetChanged();
                    } else {
                        ChatApplication.showToast(mContext, message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();

                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
            }
        });


    }

    /**
     * Delete individual remote
     */
    public void deleteRemote(String module_id, final int id, String smart_remote_name) {

        if (!ActivityHelper.isConnectingToInternet(mContext)) {
            Toast.makeText(mContext, R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(mContext, "Please wait...", false);
/*
        JSONObject object = new JSONObject();
        try {
            object.put("device_id", module_id);
            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(mContext, Constants.USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        String url = ChatApplication.url + Constants.DELETE_MODULE;

       /* ChatApplication.logDisplay("url is " + url + "  " + object);
        new GetJsonTask(mContext, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();

                try {
                    int code = result.getInt("code");
                    if (code == 200) {
                        arrayListLog.remove(id);
                        notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                Toast.makeText(mContext, R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();*/

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().DeleteRemote(module_id, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();

                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    if (code == 200) {
                        arrayListLog.remove(id);
                        notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(mContext, R.string.disconnect, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(mContext, R.string.disconnect, Toast.LENGTH_SHORT).show();
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

        public TextView txtNameRemote;
        public ImageView imgDeleteRemote, imgEditRemote;
        public LinearLayout ll_sensor_view;

        public SensorViewHolder(View view) {
            super(view);
            txtNameRemote = itemView.findViewById(R.id.txtNameRemote);
            imgEditRemote = itemView.findViewById(R.id.imgEditRemote);
            imgDeleteRemote = itemView.findViewById(R.id.imgDeleteRemote);
            ll_sensor_view = itemView.findViewById(R.id.ll_sensor_view);
        }
    }
}
