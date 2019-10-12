package com.spike.bot.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.SmartDeviceAdapter.SmartDeviceAdapte;
import com.spike.bot.core.Constants;
import com.spike.bot.listener.LIsterSmartDevice;
import com.spike.bot.model.SmartDeviceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sagar on 23/4/19.
 * Gmail : jethvasagar2@gmail.com
 */
public class SearchSmartDeviceActivity extends AppCompatActivity implements View.OnClickListener, LIsterSmartDevice {

    public boolean isFilterType = false;
    Toolbar toolbar;
    RecyclerView recyclerDevice;
    Button btnUnAssing, btnAssign;
    ArrayList<SmartDeviceModel> arrayList = new ArrayList<>();
    SmartDeviceAdapte smartDeviceAdapte;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serach_smart_device);

        setUiId();
    }

    private void setUiId() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerDevice = findViewById(R.id.recyclerDevice);
        btnAssign = findViewById(R.id.btnAssign);
        btnUnAssing = findViewById(R.id.btnUnAssing);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerDevice.setLayoutManager(linearLayoutManager);
        callgetHueRouterDetails();
        btnUnAssing.setOnClickListener(this);
        btnAssign.setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mood_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuAddSave = menu.findItem(R.id.action_save);
        menuAddSave.setTitle("Add");
        menuAddSave.setIcon(R.drawable.add);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            dataVIsible(true);
            callcreateHueUSer();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        if (v == btnAssign) {
            isFilterType = false;
            updateButton(btnAssign, btnUnAssing);
        } else if (v == btnUnAssing) {
            isFilterType = true;
            updateButton(btnAssign, btnUnAssing);
        }
    }

    private void updateButton(Button btnDeviceDialog, Button btnSensorDialog) {

        if (isFilterType) {
            btnDeviceDialog.setBackground(getResources().getDrawable(R.drawable.drawable_gray_schedule));
            btnSensorDialog.setBackground(getResources().getDrawable(R.drawable.drawable_blue_schedule));
            btnDeviceDialog.setTextColor(getResources().getColor(R.color.txtPanal));
            btnSensorDialog.setTextColor(getResources().getColor(R.color.automation_white));
        } else {
            btnDeviceDialog.setBackground(getResources().getDrawable(R.drawable.drawable_blue_schedule));
            btnSensorDialog.setBackground(getResources().getDrawable(R.drawable.drawable_gray_schedule));

            btnDeviceDialog.setTextColor(getResources().getColor(R.color.automation_white));
            btnSensorDialog.setTextColor(getResources().getColor(R.color.txtPanal));
        }


    }


    public void dataVIsible(boolean flag) {
        if (flag) {
            recyclerDevice.setVisibility(View.GONE);
        } else {
            recyclerDevice.setVisibility(View.VISIBLE);
        }
    }

    private void callgetHueRouterDetails() {
        if (!ActivityHelper.isConnectingToInternet(SearchSmartDeviceActivity.this)) {
            Toast.makeText(SearchSmartDeviceActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(SearchSmartDeviceActivity.this, "Please wait...", false);
        String url = ChatApplication.url + Constants.getHueRouterDetails;
        ChatApplication.logDisplay("url is " + url);
        new GetJsonTask(SearchSmartDeviceActivity.this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        toolbar.setTitle("Search Device");
                        ChatApplication.logDisplay("add hue " + result.toString());
                        /*{"code":200,"message":"Success","data":{"host_ip":"192.168.175.74",
                        "bridge_id":"fV7ZNEHxojK8x2-TWSExQ4C6SF8ZwcXr3p2ZeXnp","smart_device":"Philips Hue"}}*/

                        JSONObject object = new JSONObject(result.toString());
                        JSONObject object1 = object.optJSONObject("data");

                        String smart_device = object1.optString("smart_device");
                        String bridge_id = object1.optString("bridge_id");
                        String host_ip = object1.optString("host_ip");

                        dataVIsible(false);
                        callHueLightsList(bridge_id, host_ip);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(SearchSmartDeviceActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }


    private void callcreateHueUSer() {
        if (!ActivityHelper.isConnectingToInternet(SearchSmartDeviceActivity.this)) {
            Toast.makeText(SearchSmartDeviceActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(SearchSmartDeviceActivity.this, "Please press philips bridge button...", false);
        String url = ChatApplication.url + Constants.createHueUSer;
        ChatApplication.logDisplay("url is " + url);
        new GetJsonTask(SearchSmartDeviceActivity.this, url, "GET", "", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {

                        ChatApplication.logDisplay("add hue " + result.toString());
                        /*{"code":200,"message":"Success","data":{"host_ip":"192.168.175.74",
                        "bridge_id":"fV7ZNEHxojK8x2-TWSExQ4C6SF8ZwcXr3p2ZeXnp","smart_device":"Philips Hue"}}*/

                        JSONObject object = new JSONObject(result.toString());
                        JSONObject object1 = object.optJSONObject("data");

                        String smart_device = object1.optString("smart_device");
                        String bridge_id = object1.optString("bridge_id");
                        String host_ip = object1.optString("host_ip");

                        calladdHueLight(smart_device, bridge_id, host_ip);
                    } else {
                        ChatApplication.showToast(SearchSmartDeviceActivity.this, message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(SearchSmartDeviceActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }


    private void calladdHueLight(String smart_device, String bridge_id, String host_ip) {
        if (!ActivityHelper.isConnectingToInternet(SearchSmartDeviceActivity.this)) {
            Toast.makeText(SearchSmartDeviceActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(SearchSmartDeviceActivity.this, "Please wait...", false);
        String url = ChatApplication.url + Constants.addHueLight;

        JSONObject object = new JSONObject();
        // host_ip, smart_device,  bridge_id
        try {
            object.put("bridge_id", bridge_id);
            object.put("smart_device", smart_device);
            object.put("host_ip", host_ip);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("url is " + url + " " + object);
        new GetJsonTask(SearchSmartDeviceActivity.this, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.logDisplay("add hue is " + result.toString());

                        ChatApplication.showToast(SearchSmartDeviceActivity.this, message);
                        JSONObject object = new JSONObject(result.toString());
                        JSONObject object1 = object.optJSONObject("data");

                        String bridge_id = object1.optString("bridge_id");
                        String host_ip = object1.optString("host_ip");

                        callHueLightsList(bridge_id, host_ip);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(SearchSmartDeviceActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    private void callHueLightsList(final String bridge_id, final String host_ip) {
        if (!ActivityHelper.isConnectingToInternet(SearchSmartDeviceActivity.this)) {
            Toast.makeText(SearchSmartDeviceActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(SearchSmartDeviceActivity.this, "Please wait...", false);
        String url = ChatApplication.url + Constants.HueLightsList;

        final JSONObject object = new JSONObject();
        // host_ip, smart_device,  bridge_id
        try {
            object.put("bridge_id", bridge_id);
            object.put("host_ip", host_ip);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("url is " + url + " " + object.toString());
        new GetJsonTask(SearchSmartDeviceActivity.this, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        if (arrayList != null) {
                            arrayList.clear();
                        }
                        ChatApplication.logDisplay("add hue is HueLightsList " + result.toString());

                        JSONObject object1 = new JSONObject(result.toString());
                        JSONArray jsonArray = object1.optJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object2 = jsonArray.optJSONObject(i);
                            String name = object2.optString("name");
                            JSONObject object3 = object2.optJSONObject("state");
                            String onOff = object3.optString("on");
                            String id = object2.optString("id");
                            SmartDeviceModel smartRemoteModel = new SmartDeviceModel();
                            smartRemoteModel.setName(name);
                            smartRemoteModel.setOnfff(onOff);
                            smartRemoteModel.setId(id);
                            smartRemoteModel.setBridge_id(bridge_id);
                            smartRemoteModel.setHost_ip(host_ip);
                            arrayList.add(smartRemoteModel);
                        }

                        if (arrayList.size() > 0) {
                            setAdapter();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(SearchSmartDeviceActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    private void setAdapter() {
        smartDeviceAdapte = new SmartDeviceAdapte(this, arrayList, this);
        recyclerDevice.setAdapter(smartDeviceAdapte);
        smartDeviceAdapte.notifyDataSetChanged();

    }


    private void callHueLightState(final SmartDeviceModel smartRemoteModel, final int postion) {
        if (!ActivityHelper.isConnectingToInternet(SearchSmartDeviceActivity.this)) {
            Toast.makeText(SearchSmartDeviceActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(SearchSmartDeviceActivity.this, "Please wait...", false);
        String url = ChatApplication.url + Constants.HueLightState;

        JSONObject object = new JSONObject();
        // 	host_ip
        //            bridge_id
        //            status  //1 for on and  0 for off
        //            light_id // id
        try {
            int isflag = 0;
            if (smartRemoteModel.getOnfff().equalsIgnoreCase("true")) {
                isflag = 0;
            } else {
                isflag = 1;
            }
            object.put("bridge_id", smartRemoteModel.getBridge_id());
            object.put("status", isflag);
            object.put("host_ip", smartRemoteModel.getHost_ip());
            object.put("light_id", Integer.parseInt(smartRemoteModel.getId()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("url is " + url + " " + object);
        new GetJsonTask(SearchSmartDeviceActivity.this, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.logDisplay("add hue is " + result.toString());

                        if (smartRemoteModel.getOnfff().equalsIgnoreCase("true")) {
                            arrayList.get(postion).setOnfff("false");
                        } else {
                            arrayList.get(postion).setOnfff("true");
                        }

                        smartDeviceAdapte.notifyDataSetChanged();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(SearchSmartDeviceActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    @Override
    public void listeronoff(SmartDeviceModel smartDeviceModel, int postion) {
        callHueLightState(smartDeviceModel, postion);
    }


}
