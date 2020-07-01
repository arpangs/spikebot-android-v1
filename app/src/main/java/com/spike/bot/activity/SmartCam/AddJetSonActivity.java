package com.spike.bot.activity.SmartCam;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.kp.core.dialog.ConfirmDialog;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.Repeatar.RepeaterActivity;
import com.spike.bot.adapter.JetSonAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.JetSonModel;
import com.spike.bot.model.RepeaterModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vipul on 10/1/20.
 * Gmail : vipul patel
 */
public class AddJetSonActivity extends AppCompatActivity implements View.OnClickListener , JetSonAdapter.JetsonAction {

    public Toolbar toolbar;
    ImageView empty_add_image;
    TextView txt_empty_text;
    LinearLayout linearNodataFound;
    public RecyclerView recyclerview;
    public JetSonAdapter jetSonAdapter;
    public ArrayList<JetSonModel.Datum> arrayList=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jetson);

        setViewId();
    }

    private void setViewId() {
        toolbar = findViewById(R.id.toolbar);
        empty_add_image = findViewById(R.id.empty_add_image);
        txt_empty_text = findViewById(R.id.txt_empty_text);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Smart Camera");
        recyclerview = findViewById(R.id.recyclerRemoteList);
        linearNodataFound = findViewById(R.id.linearNodataFound);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(linearLayoutManager);

        txt_empty_text.setText("No Jetson Added");
        empty_add_image.setOnClickListener(this);
        txt_empty_text.setOnClickListener(this);

        callGetJetson();
    }

    @Override
    public void onClick(View v) {
        if (v == txt_empty_text || v==empty_add_image) {
            AddJetsonDialog(false,0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        MenuItem menuAdd = menu.findItem(R.id.action_add);
        MenuItem actionEdit = menu.findItem(R.id.actionEdit);
        MenuItem action_save = menu.findItem(R.id.action_save);
        MenuItem menuaddtext = menu.findItem(R.id.action_add_text);
        menuaddtext.setVisible(true);
        menuAdd.setVisible(false);
        action_save.setVisible(false);
        actionEdit.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_text) {
            AddJetsonDialog(false,0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /*view hide use for getting error*/
    public void showView(boolean isflag){
        recyclerview.setVisibility(isflag ? View.VISIBLE : View.GONE);
        linearNodataFound.setVisibility(isflag ? View.GONE : View.VISIBLE);
    }
    /*add jetson dialog.*/
    private void AddJetsonDialog(boolean isFlag,int position) {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_jetson);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        AppCompatButton btnAdd = dialog.findViewById(R.id.btnAdd);
        AppCompatEditText editIpAddress = dialog.findViewById(R.id.editIpAddress);
        AppCompatEditText editDeviceName = dialog.findViewById(R.id.editDeviceName);
        AppCompatImageView imgClose = dialog.findViewById(R.id.imgClose);
        AppCompatTextView txtTitleJetson = dialog.findViewById(R.id.txtTitleJetson);

        if(isFlag){
            txtTitleJetson.setText("Edit Jetson");
            editDeviceName.setText(arrayList.get(position).getJetsonName());
            editIpAddress.setText(arrayList.get(position).getJetsonIp());

        }

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editDeviceName.getText().toString().length()==0){
                    ChatApplication.showToast(AddJetSonActivity.this,"Please enter name address");
                }else if(editIpAddress.getText().toString().length()==0){
                    ChatApplication.showToast(AddJetSonActivity.this,"Please enter ip address");
                }else if(!Patterns.IP_ADDRESS.matcher(editIpAddress.getText()).matches()){
                    ChatApplication.showToast(AddJetSonActivity.this,"Please enter valid ip address");
                }else {
                    callAddJetson(editDeviceName.getText().toString(),editIpAddress.getText().toString(),dialog,isFlag,position);
                }
            }
        });


        dialog.show();

    }

    /*get jetson api call*/
    private void callGetJetson()
    {
        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        SpikeBotApi.getInstance().callGetJetson(new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    ChatApplication.logDisplay("response is "+result);
                    String message = result.getString("message");
                    if (code == 200) {
                        showView(true);
                        JSONObject  object= new JSONObject(String.valueOf(result));
                        JSONArray jsonArray= object.optJSONArray("data");

                        Gson gson=new Gson();
                        arrayList.clear();
                        arrayList = gson.fromJson(jsonArray.toString(), new TypeToken<ArrayList<JetSonModel.Datum>>(){}.getType());
                        if(arrayList.size()>0){
                            setAdapter();
                        }else {
                            showView(false);
                        }

                        ChatApplication.logDisplay("response is "+result);
                    }else {
                        ChatApplication.showToast(AddJetSonActivity.this, message);
                        showView(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ActivityHelper.dismissProgressDialog();

                }
            }

            @Override
            public void onData_FailureResponse() {
                showView(false);
                ActivityHelper.dismissProgressDialog();
            }
        });
    }

    private void setAdapter() {
        jetSonAdapter =new JetSonAdapter(this,this,arrayList);
        recyclerview.setAdapter(jetSonAdapter);
        jetSonAdapter.notifyDataSetChanged();
    }

    /*add jetson api call*/
    private void callAddJetson(String devicename, String ipaddress, Dialog dialog, boolean isFlag,  int position) {
        ActivityHelper.showProgressDialog(this, "Please wait.", false);
        SpikeBotApi.getInstance().callAddJetson(arrayList.get(position).getJetsonId(),devicename,ipaddress,isFlag, new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    ChatApplication.logDisplay("response is "+result);
                    String message = result.getString("message");
                    ChatApplication.showToast(AddJetSonActivity.this, message);
                    if (code == 200) {
                        dialog.dismiss();
                        callGetJetson();
                        ChatApplication.logDisplay("response is "+result);
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
        });
    }

    /*delete jetson*/
    private void deleteJetson(int position) {
        SpikeBotApi.getInstance().deleteJetson(arrayList.get(position).getJetsonId(), new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    ChatApplication.logDisplay("response is "+result);
                    String message = result.getString("message");
                    ChatApplication.showToast(AddJetSonActivity.this, message);
                    if (code == 200) {
                        arrayList.remove(position);
                        setAdapter();
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
        });
    }

    @Override
    public void action(int position, String action) {
        if(action.equalsIgnoreCase("edit")){
            showBottomSheetDialog(position);
        }

    }

    public void showBottomSheetDialog( int postion) {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        TextView txt_bottomsheet_title = view.findViewById(R.id.txt_bottomsheet_title);
        LinearLayout linear_bottom_edit = view.findViewById(R.id.linear_bottom_edit);
        LinearLayout linear_bottom_delete = view.findViewById(R.id.linear_bottom_delete);

        TextView txt_edit = view.findViewById(R.id.txt_edit);

        BottomSheetDialog dialog = new BottomSheetDialog(AddJetSonActivity.this,R.style.AppBottomSheetDialogTheme);
        dialog.setContentView(view);
        dialog.show();

        txt_bottomsheet_title.setText("What would you like to do in" + " " + arrayList.get(postion).getJetsonName() + " " +"?");
        linear_bottom_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                AddJetsonDialog(true,postion);
            }
        });

        linear_bottom_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ConfirmDialog newFragment = new ConfirmDialog("Yes", "No", "Confirm", "Are you sure you want to Delete ?", new ConfirmDialog.IDialogCallback() {
                    @Override
                    public void onConfirmDialogYesClick() {
                        deleteJetson(postion);
                    }

                    @Override
                    public void onConfirmDialogNoClick() {
                    }
                });
                newFragment.show(getFragmentManager(), "dialog");
            }
        });
    }
}
