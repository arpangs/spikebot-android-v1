package com.kp.core;

import org.json.JSONObject;


/**
 * Asynchtask Callback interface 
 * @author kaushal
 *
 */
public interface ICallBackResult 
{
	public void onSuccess(String strData);
	public void onFailure(Throwable throwable,String error);
}
