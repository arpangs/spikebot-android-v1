package com.spike.bot.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.listeners.ColorPickerViewListener;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by Sagar on 16/5/19.
 * Gmail : vipul patel
 */
public class SmartColorPickerActivity extends AppCompatActivity implements View.OnClickListener, ColorPickerViewListener {

    public Toolbar toolbar;
//    private ColorPickerView colorPickerView;
    public top.defaults.colorpicker.ColorPickerView colorPickerView1;
    public SeekBar seekBar;
    public FrameLayout frameLayout;
    Button btnReset, btnDone;
    public String roomDeviceId = "", original_room_device_id = "", smart_device_brightness = "", smart_device_rgb = "";
    public int red = 0, green = 0, blue = 0, progresbar = 50,redTemp=0,greenTemp=0,blueTemp=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_device);

        roomDeviceId = getIntent().getStringExtra("roomDeviceId");
        original_room_device_id = getIntent().getStringExtra("getOriginal_room_device_id");
        setUIId();
    }

    private void setUIId() {
        toolbar = findViewById(R.id.toolbar);
        seekBar = findViewById(R.id.seekbar);
        frameLayout = findViewById(R.id.frameLayout);
        btnDone = findViewById(R.id.btnDone);
        btnReset = findViewById(R.id.btnReset);
        colorPickerView1 = findViewById(R.id.colorPickerView1);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Color picker");
        btnReset.setOnClickListener(this);
        btnDone.setOnClickListener(this);

//        colorPickerView = findViewById(R.id.colorPickerView);
//        colorPickerView.setColorListener(
//                new ColorEnvelopeListener() {
//                    @Override
//                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
//                        ChatApplication.logDisplay("action is click ");
//                        setLayoutColor(envelope);
//                    }
//                });

//        colorPickerView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int action = event.getActionMasked();
//                if (action == 1) {
//                    callHueLightState(color);
//                }
//                ChatApplication.logDisplay("action is " + action);
//                return false;
//            }
//        });

        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // reading.setText("Processing "+progress+"% ");
                progresbar = progress;
            }
        });

        seekBar.setProgress(100);
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();
                if (action == 1) {
                    callHueLightState();
                }
                ChatApplication.logDisplay("action is seekBar " + action);

                return false;
            }
        });


        colorPickerView1.subscribe((color, fromUser, shouldPropagate) -> {

            hex2Rgb1(color);
            colorHex(color);
            if(fromUser && shouldPropagate){
                callHueLightState();
            }

        });

        colorPickerView1.setOnlyUpdateOnTouchEventUp(false);

        getPhilipsHueParams();

    }

    @SuppressLint({"SetTextI18n", "NewAp```````````````````````````````````````````````````i"})
    private void setLayoutColor(ColorEnvelope envelope) {
        hex2Rgb("#" + envelope.getHexCode(), "");
    }

    public void hex2Rgb(String colorStr, String tempColor) {
//        int color1 = 0;
//
//        int color = Color.parseColor("#FF63A9FF");
//        red = Color.red(color);
//        green = Color.green(color);
//        blue = Color.blue(color);
//        color1 = -9532673;
//
//        float[] hsv = new float[3];
//        Color.colorToHSV(color1, hsv);
//        hsv[2] = 0;
//        int startColor = Color.HSVToColor(hsv);
//        hsv[2] = 1;
//        int endColor = Color.HSVToColor(hsv);
//
//        ChatApplication.logDisplay("pure is11 " + startColor + " " + endColor);
//
//        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{startColor, endColor});
//        gd.setCornerRadius(10f);
//        frameLayout.setBackgroundDrawable(gd);

    }

    public void hex2Rgb1(int colorStr) {
        int color1 = colorStr;
        float[] hsv = new float[3];
        Color.colorToHSV(color1, hsv);
        hsv[2] = 0;
        int startColor = Color.HSVToColor(hsv);
        hsv[2] = 1;
        int endColor = Color.HSVToColor(hsv);

        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{startColor, endColor});
        gd.setCornerRadius(10f);
        frameLayout.setBackgroundDrawable(gd);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void colorHex(int color) {
        int a = Color.alpha(color);
       red= Color.red(color);
        green = Color.green(color);
        blue = Color.blue(color);
//        return String.format(Locale.getDefault(), "0x%02X%02X%02X%02X", a, r, g, b);
    }


    private void callHueLightState() {
        if (!ActivityHelper.isConnectingToInternet(SmartColorPickerActivity.this)) {
            Toast.makeText(SmartColorPickerActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

//        ActivityHelper.showProgressDialog(SmartColorPickerActivity.this, "Please wait...", false);
        String url = ChatApplication.url + Constants.changeHueLightState;

        JSONObject object = new JSONObject();

        try {

            String code = "[" + red + "," + green + "," + blue + "]";
            JSONArray jsonArray = new JSONArray(code);

            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put("status", 1);
            object.put("bright", progresbar);
            object.put("is_rgb", 1);//1;
            object.put("rgb_array", jsonArray);
            object.put("room_device_id", roomDeviceId);
//
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("url is " + url + " " + object);
        new GetJsonTask(SmartColorPickerActivity.this, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.logDisplay("add hue is " + result.toString());
                    } else {
                        ChatApplication.showToast(SmartColorPickerActivity.this, message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(SmartColorPickerActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    private void getPhilipsHueParams() {
        //POST: 192.168.175.119/getPhilipsHueParams
        //{
        //     "original_room_device_id": "1564204083339_M3rJKZiVvY"
        //}
        if (!ActivityHelper.isConnectingToInternet(SmartColorPickerActivity.this)) {
            Toast.makeText(SmartColorPickerActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            return;
        }

        ActivityHelper.showProgressDialog(SmartColorPickerActivity.this, "Please wait...", false);
        String url = ChatApplication.url + Constants.getPhilipsHueParams;

        JSONObject object = new JSONObject();

        try {

            object.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            object.put(APIConst.PHONE_TYPE_KEY, APIConst.PHONE_TYPE_VALUE);
            object.put("user_id", Common.getPrefValue(this, Constants.USER_ID));
            object.put("original_room_device_id", original_room_device_id);
//
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatApplication.logDisplay("url is " + url + " " + object);
        new GetJsonTask(SmartColorPickerActivity.this, url, "POST", object.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ActivityHelper.dismissProgressDialog();
                try {
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code == 200) {
                        ChatApplication.logDisplay("result is " + result.toString());
                        JSONObject jsonObject = new JSONObject(result.toString());
                        JSONObject object1 = jsonObject.optJSONObject("data");
                        JSONArray jsonArray = object1.optJSONArray("smart_device_list");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);

                            smart_device_brightness = jsonObject1.optString("smart_device_brightness");
                            smart_device_rgb = jsonObject1.optString("smart_device_rgb");
                        }

//                        hex2Rgb("", smart_device_rgb);

                        String[] separated = smart_device_rgb.replace("[", "").replace("]", "").split(",");
                        redTemp = Integer.parseInt(separated[0]);
                        greenTemp = Integer.parseInt(separated[1]);
                        blueTemp = Integer.parseInt(separated[2]);


//                        final Thread thread = new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    Thread.sleep(1500);
                                    setView();
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                        thread.start();


//                        int rgb = ((red&0x0ff)<<16)|((green&0x0ff)<<8)|(blue&0x0ff);
//
//                        colorPickerView.setPureColor(rgb);
//                        ColorEnvelope colorEnvelope=new ColorEnvelope(rgb);
//                        setLayoutColor(colorEnvelope);
                    } else {
                        ChatApplication.showToast(SmartColorPickerActivity.this, message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(SmartColorPickerActivity.this.getApplicationContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    private void setView() {
        int rgb = ((redTemp&0x0ff)<<16)|((greenTemp&0x0ff)<<8)|(blueTemp&0x0ff);
        colorPickerView1.setInitialColor(rgb);
        colorPickerView1.reset();
    }

    @Override
    public void onClick(View v) {
        if (v == btnDone) {
            this.finish();
        } else if (v == btnReset) {
            setView();
//            colorPickerView1.setInitialColor(0x7F313C93);
//            colorPickerView1.reset();

        }
    }

}
