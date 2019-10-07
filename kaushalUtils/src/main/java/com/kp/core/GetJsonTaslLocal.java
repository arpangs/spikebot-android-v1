package com.kp.core;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Map;

/**
 * @author kaushal
 *
 */
public class GetJsonTaslLocal extends AsyncTask<String, Void, String> {
	private ICallBack activity;
	private Context context;
	private String result = "", url;
	String json = "";
	private String error = null;
	private String method = "POST";

	public GetJsonTaslLocal(Context context, String url, String method, String json, ICallBack activity) {
		this.activity = activity;
		this.context = context;
		this.url = url;
		this.method = method;
		this.json = json;
	}


	@Override
	protected String doInBackground(String... urls) {

		try {
			return ActivityHelper.CallJSONService3(url, json, method);
		} catch (Exception e) {
			error = "Error occured.";
		}
		return null;
	}

	// onPostExecute displays the results of the AsyncTask.
	@Override
	protected void onPostExecute(String result) {
		try {
			JSONObject json = new JSONObject(result);
			activity.onSuccess(json);
		} catch (Throwable e) {
			activity.onFailure(e, error);
		}
	}
	/*
	 * @Override protected void onCancelled() { }
	 */
}
