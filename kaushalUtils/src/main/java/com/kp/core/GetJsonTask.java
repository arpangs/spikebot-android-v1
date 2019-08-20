package com.kp.core;

import java.io.IOException;
import java.net.SocketException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

/**
 * @author kaushal
 *
 */
public class GetJsonTask extends AsyncTask<String, Void, String> {
	private ProgressDialog mProgressDialog;
	private ICallBack activity;
	private Context context;
	private String result = "", url;
	private Map<String, String> parameter;
	String json = "";
	private String error = null;
	private String method = "POST";

	public GetJsonTask(Context context, String url, String method, String json, ICallBack activity) {
		this.activity = activity;
		this.context = context;
		this.url = url;
		this.method = method;
		this.json = json;
	}


	@Override
	protected String doInBackground(String... urls) {

		try {
			return ActivityHelper.CallJSONService(url, json, method);
		} catch (HttpHostConnectException e) {
			error = "Can't connect to server, Problem with server or your internet connection.";
		} catch (SocketException e) {
			error = "Connection problem, check your internet connection";
		} catch (ClientProtocolException e) {
			error = "Protocol Error occured.";
		} catch (IOException e) {
			error = "IO Error occured.";
		} catch (Exception e) {
			error = "Error occured.";
		}
		return null;
	}

	// onPostExecute displays the results of the AsyncTask.
	@Override
	protected void onPostExecute(String result) {
		try {
			// JSONObject soapDatainJsonObject =
			// XML.toJSONObject(result.toString());
			// Log.d("soapDatainJsonObject", "1 soapDatainJsonObject==== " +
			// soapDatainJsonObject.toString());

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
