package com.spike.bot.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.UserChildActivity;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Sagar on 27/2/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class UserProfileFragment extends Fragment implements View.OnClickListener{

    String  TAG = "ProfileActivity",strPassword="";

    EditText et_profile_first_name, et_profile_last_name, et_profile_contact_no, et_profile_email, et_profile_user_name;
    LinearLayout ll_password_view_expand,ll_pass_edittext_view;
    ImageView img_pass_arrow;
    Button btn_save,btnChangePassword;
    EditText edt_new_password,edt_confrim_password;
    Animation slideUpAnimation, slideDownAnimation;

    public UserProfileFragment() {
        super();
    }

    public static UserProfileFragment newInstance() {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        et_profile_first_name = (EditText) view.findViewById(R.id.et_profile_first_name);
        et_profile_last_name = (EditText) view.findViewById(R.id.et_profile_last_name);
        et_profile_contact_no = (EditText) view.findViewById(R.id.et_profile_contact_no);
        et_profile_email = (EditText) view.findViewById(R.id.et_profile_email);
        et_profile_user_name = (EditText) view.findViewById(R.id.et_profile_user_name);
        btn_save  = (Button) view.findViewById(R.id.btn_save);
        ll_password_view_expand = (LinearLayout) view.findViewById(R.id.ll_password_view);
        ll_pass_edittext_view = (LinearLayout) view.findViewById(R.id.ll_pass_edittext_view);
        img_pass_arrow = (ImageView)view.findViewById(R.id.img_pass_arrow);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(this);
        edt_new_password = (EditText)view.findViewById(R.id.et_new_password);
        edt_confrim_password = (EditText)view.findViewById(R.id.et_new_password_confirm);

        et_profile_first_name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        et_profile_last_name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        et_profile_user_name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);


        slideUpAnimation = AnimationUtils.loadAnimation(getContext(),
                R.anim.slide_out_bottom);

        slideDownAnimation = AnimationUtils.loadAnimation(getContext(),
                R.anim.slide_in_bottom);


        ll_password_view_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ll_pass_edittext_view.getVisibility() == View.VISIBLE){
                    //   ll_pass_edittext_view.setAnimation(slideDownAnimation);
                    ll_pass_edittext_view.setVisibility(View.GONE);
                    img_pass_arrow.setImageResource(R.drawable.icn_arrow_right);

                }else{
                    // ll_pass_edittext_view.setAnimation(slideUpAnimation);
                    img_pass_arrow.setImageResource(R.drawable.icn_arrow_down);
                    ll_pass_edittext_view.setVisibility(View.VISIBLE);

                }

            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveProfile();
            }
        });

        getProfile();

        return view;
    }
    public void getProfile(){

        if(!ActivityHelper.isConnectingToInternet(getContext())){
            Toast.makeText(getContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(getContext(),"Please wait.",false);

        String url = ChatApplication.url + Constants.GET_USER_PROFILE_INFO;

        new GetJsonTask(getContext(),url ,"GET","", new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    //{"code":200,"message":"success","data":{"userProfileData":[{"user_email":"test@gmail.com","first_name":"test","last_name":"patel","user_name":"test","user_phone":"123123"}]}}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){
                        JSONObject data = result.getJSONObject("data");
                        JSONArray profileArray = data.getJSONArray("userProfileData");
                        JSONObject obj = profileArray.getJSONObject(0);
                        /*"user_email": "test@gmail.com",
                                "first_name": "test",
                                "last_name": "patel",
                                "user_name": "test",
                                "user_phone": "123123"*/
                        ChatApplication.logDisplay( "getProfile obj " + obj.toString() );
                        String user_email = obj.getString("user_email");
                        String first_name = obj.getString("first_name");
                        String last_name = obj.getString("last_name");
                        String user_name = obj.getString("user_name");
                        String user_phone = obj.getString("user_phone");

                        et_profile_first_name.setText(first_name);
                        et_profile_last_name.setText(last_name);
                        et_profile_contact_no.setText(user_phone);
                        et_profile_email.setText(user_email);
                        et_profile_user_name.setText(user_name);
                        et_profile_first_name.setSelection(et_profile_first_name.getText().length());

                        ActivityHelper.dismissProgressDialog();

                    }else{
                        Toast.makeText(getContext(), message , Toast.LENGTH_SHORT).show();
                    }
                    // Toast.makeText(getActivity().getApplicationContext(), "No New Device detected!" , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    ChatApplication.logDisplay( "Exception getProfile e.getMessage() " + e.getMessage() );
                }
                finally {
                    ActivityHelper.dismissProgressDialog();

                }
            }
            @Override
            public void onFailure(Throwable throwable, String error) {
                ChatApplication.logDisplay( "getProfile onFailure " + error );
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(getContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    public boolean isValidate = false;

    /**
     *
     * @param phone
     * @return
     */

    public static boolean isValidMobile(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() < 10 || phone.length() > 13) {
                check = false;
            } else {
                check = android.util.Patterns.PHONE.matcher(phone).matches();
            }
        } else {
            check = false;
        }
        return check;
    }

    public void saveProfile() {

        if(!ActivityHelper.isConnectingToInternet(getContext())){
            Toast.makeText(getContext(), R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }

        ChatApplication.logDisplay( "saveProfile saveProfile");
        if (TextUtils.isEmpty(et_profile_first_name.getText().toString())) {
            Toast.makeText(getContext(), "Enter First Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(et_profile_last_name.getText().toString())) {
            Toast.makeText(getContext(), "Enter Last Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(et_profile_user_name.getText().toString())) {
            Toast.makeText(getContext(), "Enter User Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(et_profile_contact_no.getText().toString())) {
            Toast.makeText(getContext(), "Enter Phone No", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidMobile(et_profile_contact_no.getText().toString())) {
            Toast.makeText(getContext(), "Invalid Phone No", Toast.LENGTH_SHORT).show();
            return;
        }

        if (et_profile_user_name.getText().toString().trim().matches(".*([ \t]).*")){
            Toast.makeText(getContext(), "Invalid Username", Toast.LENGTH_SHORT).show();
            return;
        }

//        if(!TextUtils.isEmpty(edt_new_password.getText().toString())){
//
//            if(edt_new_password.getText().length()>1){
//                if(edt_new_password.getText().toString().equalsIgnoreCase(edt_confrim_password.getText().toString())){
//                    isValidate = true;
//                }else{
//                    isValidate = false;
//                    Toast.makeText(getContext(), "Password not match..." , Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }else{
//                isValidate = false;
//                Toast.makeText(getContext(), "Invalid Password..." , Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }


        ActivityHelper.showProgressDialog(getContext(),"Please wait.",false);

        JSONObject obj = new JSONObject();
        try {
            obj.put("first_name",et_profile_first_name.getText().toString());
            obj.put("last_name",et_profile_last_name.getText().toString());
            obj.put("user_name",et_profile_user_name.getText().toString());
            obj.put("user_phone",et_profile_contact_no.getText().toString());
            obj.put("user_email",et_profile_email.getText().toString());

            if(strPassword.length()>0){
                obj.put("user_password",""+strPassword);
            }else {
                obj.put("user_password","");
            }

            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ChatApplication.url + Constants.SAVE_USER_PROFILE_DETAILS;

        new GetJsonTask(getContext(),url ,"POST",obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                ChatApplication.logDisplay( "getProfile onSuccess " + result.toString());
                try {
                    //{"code":200,"message":"success","data":{"userProfileData":[{"user_email":"test@gmail.com","first_name":"test","last_name":"patel","user_name":"test","user_phone":"123123"}]}}
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if(code==200){
                        Common.savePrefValue(ChatApplication.getInstance(), "first_name", et_profile_first_name.getText().toString());
                        Common.savePrefValue(ChatApplication.getInstance(), "last_name", et_profile_last_name.getText().toString());

                        Toast.makeText(getContext(), message , Toast.LENGTH_SHORT).show();
                        ActivityHelper.hideKeyboard(getActivity());
                       // finish();
                        ActivityHelper.dismissProgressDialog();
                        ChatApplication.isRefreshUserData = true;

                    }else{
                        ChatApplication.isRefreshUserData = false;
                        Toast.makeText(getContext(), message , Toast.LENGTH_SHORT).show();
                    }
                    // Toast.makeText(getActivity().getApplicationContext(), "No New Device detected!" , Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    ChatApplication.logDisplay( "Exception saveProfile e.getMessage() " + e.getMessage() );
                }
                finally {
                    ActivityHelper.dismissProgressDialog();

                }
            }
            @Override
            public void onFailure(Throwable throwable, String error) {
                ChatApplication.logDisplay( "saveProfile onFailure " + error );
                ActivityHelper.dismissProgressDialog();
                Toast.makeText(getContext(), R.string.disconnect, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    public void addCustomRoom() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_add_custome_room);
        dialog.setCanceledOnTouchOutside(true);

        final TextInputLayout inputRoom = dialog.findViewById(R.id.inputRoom);
        final TextInputLayout inputPassword = dialog.findViewById(R.id.inputPassword);

        final TextInputEditText edtPasswordChild=dialog.findViewById(R.id.edtPasswordChild);
        inputRoom.setVisibility(View.GONE);
        inputPassword.setVisibility(View.VISIBLE);

//        InputFilter[] filterArray = new InputFilter[1];
//        filterArray[0] = new InputFilter.LengthFilter(25);
//        room_name.setFilters(filterArray);

        TextView tv_title =  dialog.findViewById(R.id.tv_title);
        Button btnSave = (Button) dialog.findViewById(R.id.btn_save);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tv_title.setText("Change Password");

//        room_name.setHint("Enter Password");
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtPasswordChild.getText().length()>0){
                    ChatApplication.keyBoardHideForce(getActivity());
                    dialog.cancel();
                    strPassword=edtPasswordChild.getText().toString();
                }else {
                    ChatApplication.showToast(getActivity(), "Please enter password");
                }
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }

        //ActivityHelper.showProgressDialog(getActivity(), "Searching Device attached ", false);

    }

    @Override
    public void onClick(View v) {
        if(v==btnChangePassword){
            addCustomRoom();
        }
    }
}
