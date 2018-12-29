package com.spike.bot.core;

import org.json.JSONObject;

/**
 * Created by kaushal on 2/2/18.
 */

public interface IWebCallBack {
    public void onSuccess(JSONObject result);
    public void onFailure(String error);
}
