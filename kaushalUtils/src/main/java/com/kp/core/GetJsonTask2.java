package com.kp.core;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

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
	private ICallBack2 activity;
	private Context context;
	private String result = "", url;
	String json = "";
	private String error = null;
	private String method = "POST";
	public static HttpURLConnection con = null;

	public GetJsonTask2(Context context, String url, String method, String json, ICallBack2 activity) {
		this.activity = activity;
		this.context = context;
		this.url = url;
		this.method = method;
		this.json = json;
		if(!this.url.trim().startsWith("http")){
			this.url="http://"+this.url;
		}
	}



	@Override
	protected String doInBackground(String... urls) {
		try {
			StringBuilder params = new StringBuilder("");
			String result = "";
			String url1 = url;

			URL obj = new URL(url1);

			HttpURLConnection con = httpConnection(obj, method);
			if (method.equalsIgnoreCase("POST")) {
				con.setDoOutput(true);
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.getOutputStream());
				outputStreamWriter.write(json);
				outputStreamWriter.flush();
			}

			BufferedReader in;//= new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			//con.getErrorStream() for error

			if (con.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else {
				in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}

			System.out.println("Response Code getErrorStream : " + con.getErrorStream());
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine + "\n");
			}
			in.close();
			result = response.toString();
			return result;
		}catch (Exception e) {
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
			if(!TextUtils.isEmpty(result) && result.startsWith("Maintenance is")){
				error=result;
			}else if(!TextUtils.isEmpty(result) && result.startsWith("<!DOCT")){
				error=result;
			}


			Log.d("System out","result is " + " " + url + " " +result);
			activity.onFailure(e, error,500);
		}
	}
	/*
	 * @Override protected void onCancelled() { }
	 */

	public static HttpURLConnection httpConnection(URL obj, String method){

		try {
			con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod(method);
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.setRequestProperty("Accept-Language", "UTF-8");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);
			con.setUseCaches( false );
		} catch (ProtocolException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return con;
	}
}
