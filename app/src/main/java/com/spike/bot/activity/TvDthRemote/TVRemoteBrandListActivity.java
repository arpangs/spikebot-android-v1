package com.spike.bot.activity.TvDthRemote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kp.core.ActivityHelper;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.ir.blaster.IRRemoteBrandListActivity;
import com.spike.bot.activity.ir.blaster.IRRemoteConfigActivity;
import com.spike.bot.adapter.irblaster.IRRemoteBrandListAdapter;
import com.spike.bot.api_retrofit.DataResponseListener;
import com.spike.bot.api_retrofit.SpikeBotApi;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.DataSearch;
import com.spike.bot.model.IRRemoteListRes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TVRemoteBrandListActivity extends AppCompatActivity implements IRRemoteBrandListAdapter.IRRemoteListClickEvent {

    public static DataSearch arrayList = new DataSearch();
    List<IRRemoteListRes.Data.BrandList> brandLists = new ArrayList<>();
    private LinearLayout linear_progress;
    private RecyclerView mIRListView;
    private EditText mSearchBrand;
    private String mIrDeviceId, mRemoteName, mIrDeviceType, mRoomId, mIRBlasterModuleId, mIrBlasterId, mRoomName, mBlasterName;
    private IRRemoteBrandListAdapter irRemoteBrandListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ir_remote_brand_list);

        mRemoteName = getIntent().getStringExtra("REMOTE_NAME");
        mRoomName = getIntent().getStringExtra("BLASTER_NAME");
        mBlasterName = getIntent().getStringExtra("ROOM_NAME");
        mIrBlasterId = getIntent().getStringExtra("SENSOR_ID");
        mRoomId = getIntent().getStringExtra("ROOM_ID");
        mIrDeviceType = getIntent().getStringExtra("IR_DEVICE_TYPE");
        mIrDeviceId = getIntent().getStringExtra("IR_DEVICE_ID");
        mIRBlasterModuleId = getIntent().getStringExtra("IR_BLASTER_MODULE_ID");

        bindView();
        getIRDetailsList();
    }

    private void bindView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Select TV brand");

        mSearchBrand = findViewById(R.id.search_brand);
        linear_progress = findViewById(R.id.linear_progress);
        mIRListView = findViewById(R.id.list_ir_remote);
        mIRListView.setLayoutManager(new GridLayoutManager(this, 1));

        searchBrand();
    }

    /*serach view */
    private void searchBrand() {
        mSearchBrand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterAdapter(mSearchBrand.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /**
     * filter adapter
     *
     * @param filterAdapter
     */
    private void filterAdapter(String filterAdapter) {

        final List<IRRemoteListRes.Data.BrandList> tmpBrandLists = new ArrayList<>();

        for (IRRemoteListRes.Data.BrandList brandList : brandLists) {
            if (brandList.getBrandType().toLowerCase().contains(filterAdapter.toLowerCase())) {
                tmpBrandLists.add(brandList);
            }
        }
        updateAdapter(tmpBrandLists);

    }

    /*call for details*/
    private void getIRDetailsList() {
        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress();
        ActivityHelper.showProgressDialog(TVRemoteBrandListActivity.this, "Please Wait...", false);
        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");
        SpikeBotApi.getInstance().getIRDetailsList(new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                hideProgress();
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    ChatApplication.logDisplay("url is res " + result);
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if (code == 200) {

                        ChatApplication.logDisplay("remote res : " + result.toString());
                        IRRemoteListRes irRemoteListRes = Common.jsonToPojo(result.toString(), IRRemoteListRes.class);
                        brandLists = irRemoteListRes.getData().getBrandList();
                        updateAdapter(brandLists);

                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onData_FailureResponse() {
                hideProgress();
                ActivityHelper.dismissProgressDialog();
            }

            @Override
            public void onData_FailureResponse_with_Message(String error) {
                hideProgress();
                ActivityHelper.dismissProgressDialog();
            }
        });
    }

    /**
     * update adapter when user filter the option
     *
     * @param brandLists
     */
    private void updateAdapter(List<IRRemoteListRes.Data.BrandList> brandLists) {

        irRemoteBrandListAdapter = new IRRemoteBrandListAdapter(brandLists, TVRemoteBrandListActivity.this);
        mIRListView.setAdapter(irRemoteBrandListAdapter);
        irRemoteBrandListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProgress() {

        linear_progress.setVisibility(View.VISIBLE);
        mIRListView.setVisibility(View.GONE);
    }

    private void hideProgress() {
        linear_progress.setVisibility(View.GONE);
        mIRListView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClickRemoteList(IRRemoteListRes.Data.BrandList brandList) {
        getIRRemoteDetails(brandList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REMOTE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }

    private void getIRRemoteDetails(final IRRemoteListRes.Data.BrandList brandList) {
        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(TVRemoteBrandListActivity.this, "Please Wait...", false);

        if (ChatApplication.url.contains("http://"))
            ChatApplication.url = ChatApplication.url.replace("http://", "");

        SpikeBotApi.getInstance().getIRRemoteDetails(brandList.getBrandId(), new DataResponseListener() {
            @Override
            public void onData_SuccessfulResponse(String stringResponse) {
                ActivityHelper.dismissProgressDialog();
                try {
                    JSONObject result = new JSONObject(stringResponse);
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {

                        if (arrayList != null) {
                            arrayList = null;
                        }
                        arrayList = Common.jsonToPojo(result.getString("data").toString(), DataSearch.class);

                        ChatApplication.logDisplay("ir result is " + arrayList.getDeviceBrandRemoteList().size());

                        if (arrayList.getDeviceBrandRemoteList() != null &&
                                arrayList.getDeviceBrandRemoteList().size() > 0) {

                            Intent intent = new Intent(TVRemoteBrandListActivity.this, IRRemoteConfigActivity.class);
                            intent.putExtra("BRAND_NAME", brandList.getBrandType());
                            intent.putExtra("BLASTER_NAME", mBlasterName);
                            intent.putExtra("ROOM_ID", mRoomId);
                            intent.putExtra("ROOM_NAME", mRoomName);
                            intent.putExtra("SENSOR_ID", mIrBlasterId);
                            intent.putExtra("IR_BRAND_TYPE", brandList.getBrandType());
                            intent.putExtra("IR_DEVICE_TYPE", mIrDeviceType);
                            intent.putExtra("IR_DEVICE_ID", mIrDeviceId);
                            intent.putExtra("BRAND_ID", "" + brandList.getBrandId());
                            intent.putExtra("IR_BLASTER_MODULE_ID", "" + mIRBlasterModuleId);
                            startActivityForResult(intent, Constants.REMOTE_REQUEST_CODE);
                        } else {
                            Toast.makeText(getApplicationContext(), "No remote available", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    ActivityHelper.dismissProgressDialog();
                    e.printStackTrace();
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
}
