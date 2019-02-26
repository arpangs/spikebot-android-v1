package com.kp.core;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URL;
import java.util.Map;

/**
 * @author kaushal
 *
 */
public class GetJsonTask2 extends AsyncTask<String, Void, String> {
	private ProgressDialog mProgressDialog;
	private ICallBack2 activity;
	private Context context;
	private String result = "", url;
	private Map<String, String> parameter;
	String json = "";
	private String error = null;
//	private int responseErrorCode = 0;
	private String method = "POST";
	public static HttpURLConnection con = null;

	public GetJsonTask2(Context context, String url, String method, String json, ICallBack2 activity) {
		this.activity = activity;
		this.context = context;
		this.url = url;
		this.method = method;
		// this.parameter = parameter;
		this.json = json;
	}



	@Override
	protected String doInBackground(String... urls) {

		try {

			StringBuilder params=new StringBuilder("");
			String result="";
			String url1 =url ;

			URL obj = new URL(url1);

			HttpURLConnection con =httpConnection(obj,method);
//			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//			con.setRequestMethod(method);
//			con.setRequestProperty("User-Agent", "Mozilla/5.0");
//			con.setRequestProperty("Accept-Language", "UTF-8");
//			con.setRequestProperty("Content-Type", "application/json");
//			con.setRequestProperty("Accept", "application/json");
//			con.setConnectTimeout(60000);
//			con.setReadTimeout(60000);
//			con.setUseCaches( false );
			if(method.equalsIgnoreCase("POST")){
//		    	JSONObject json = new JSONObject();
//		        json.putAll( parameter );
				con.setDoOutput(true);
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.getOutputStream());
				outputStreamWriter.write(json);
				outputStreamWriter.flush();
			}
			//int responseCode = con.getResponseCode();
			UtilsConstants.logDisplay("\nSending 'POST' request to URL : " + url);
			UtilsConstants.logDisplay("Post parameters : " + params);
			//System.out.println("Response Code : " + responseCode);

 			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine + "\n");
			}
			in.close();
			result = response.toString();
			return result;

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
			//activity.onFailure(e, error,responseErrorCode);
		}
	}
	/*
	 * @Override protected void onCancelled() { }
	 */

	public static HttpURLConnection httpConnection(URL obj, String method){

		try {
			try {
//				if(con==null){
					con = (HttpURLConnection) obj.openConnection();
//				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			con.setRequestMethod(method);
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.setRequestProperty("Accept-Language", "UTF-8");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setConnectTimeout(60000);
			con.setReadTimeout(60000);
			con.setUseCaches( false );
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
		return con;
	}
}
