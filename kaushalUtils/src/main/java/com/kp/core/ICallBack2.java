package com.kp.core;

import org.json.JSONObject;


/**
 * Asynchtask Callback interface 
 * @author kaushal
 *
 */
public interface ICallBack2
{
	public void onSuccess(JSONObject result);
	public void onFailure(Throwable throwable, String error,int responseErrorCode);
}
