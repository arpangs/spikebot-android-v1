package com.spike.bot.core;

import com.spike.bot.ChatApplication;
import com.kp.core.ActivityHelper;

/**
 * Created by Sagar on 23/3/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class APIConst {

    public static final String  PHONE_ID_KEY       = "phone_id";
    public static final String  PHONE_TYPE_KEY     = "phone_type";

//    public static final String PHONE_ID_VALUE      = ActivityHelper.getIMEI(ChatApplication.getInstance());
    public static String PHONE_ID_VALUE      = ActivityHelper.getIMEI(ChatApplication.getInstance());
    public static final String PHONE_TYPE_VALUE    = Constants.ANDROID;
}
