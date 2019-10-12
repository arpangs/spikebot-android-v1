package com.spike.bot.activity.ir.blaster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.irblaster.IRRemoteBrandListAdapter;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.DataSearch;
import com.spike.bot.model.IRRemoteListRes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 2/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class IRRemoteBrandListActivity extends AppCompatActivity implements IRRemoteBrandListAdapter.IRRemoteListClickEvent {

    private LinearLayout linear_progress, ll_sensor_list;
    private RecyclerView mIRListView;
    private String mIrDeviceId, mRemoteName, mIrDeviceType, mRoomId, mIRBlasterModuleId, mIrBlasterId, mRoomName, mBlasterName;
    List<IRRemoteListRes.Data.BrandList> brandLists = new ArrayList<>();

    private IRRemoteBrandListAdapter irRemoteBrandListAdapter;
    private EditText mSearchBrand;
    public static DataSearch arrayList = new DataSearch();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ir_remote_brand_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mRemoteName = getIntent().getStringExtra("REMOTE_NAME");
        mRoomName = getIntent().getStringExtra("BLASTER_NAME");
        mBlasterName = getIntent().getStringExtra("ROOM_NAME");
        mIrBlasterId = getIntent().getStringExtra("SENSOR_ID");
        mRoomId = getIntent().getStringExtra("ROOM_ID");
        mIrDeviceType = getIntent().getStringExtra("IR_DEVICE_TYPE");
        mIrDeviceId = getIntent().getStringExtra("IR_DEVICE_ID");
        mIRBlasterModuleId = getIntent().getStringExtra("IR_BLASTER_MODULE_ID");

        getSupportActionBar().setTitle("Select " + mRemoteName);

        bindView();

        getIRDetailsList();
    }

    private void bindView() {

        mSearchBrand = (EditText) findViewById(R.id.search_brand);

        linear_progress = (LinearLayout) findViewById(R.id.linear_progress);
        ll_sensor_list = (LinearLayout) findViewById(R.id.ll_sensor_list);

        mIRListView = (RecyclerView) findViewById(R.id.list_ir_remote);
        mIRListView.setLayoutManager(new GridLayoutManager(this, 1));

        searchBrand();
    }

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

    private void getIRDetailsList() {
        if (!ActivityHelper.isConnectingToInternet(this)) {
            Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress();
        ActivityHelper.showProgressDialog(IRRemoteBrandListActivity.this, "Please Wait...", false);

        String url = ChatApplication.url + Constants.getIRDeviceTypeBrands + "/" + mIrDeviceId;
        new GetJsonTask(this, url, "GET", "", new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {

                hideProgress();
                ActivityHelper.dismissProgressDialog();

                ActivityHelper.dismissProgressDialog();
                try {

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
            public void onFailure(Throwable throwable, String error) {
                hideProgress();
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }

    /**
     * update adapter when user filter the option
     *
     * @param brandLists
     */
    private void updateAdapter(List<IRRemoteListRes.Data.BrandList> brandLists) {

        irRemoteBrandListAdapter = new IRRemoteBrandListAdapter(brandLists, IRRemoteBrandListActivity.this);
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

        ActivityHelper.showProgressDialog(IRRemoteBrandListActivity.this, "Please Wait...", false);

        String url = ChatApplication.url + Constants.getDeviceBrandRemoteList + "/" + brandList.getBrandId();
        new GetJsonTask(this, url, "GET", "", new ICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    ChatApplication.logDisplay("ir result is " + result.toString());
                    if (code == 200) {

                        if (arrayList != null) {
                            arrayList = null;
                        }
                        arrayList = Common.jsonToPojo(result.getString("data").toString(), DataSearch.class);

                        ChatApplication.logDisplay("ir result is " + arrayList.getDeviceBrandRemoteList().size());

                        if (arrayList.getDeviceBrandRemoteList() != null &&
                                arrayList.getDeviceBrandRemoteList().size() > 0) {

                            Intent intent = new Intent(IRRemoteBrandListActivity.this, IRRemoteConfigActivity.class);
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
            public void onFailure(Throwable throwable, String error) {
                throwable.printStackTrace();
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
    }
}
