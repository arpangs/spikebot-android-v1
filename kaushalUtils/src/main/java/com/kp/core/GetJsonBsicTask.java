package com.kp.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class GetJsonBsicTask extends AsyncTask<String, Void, String> {
	private ProgressDialog mProgressDialog;
    private ICallBack activity;
    private Context context;
    private String result="",url;
    private Map<String,String> parameter;
    String json="";
    private String error=null;
    private String method="POST";
	public GetJsonBsicTask(Context context,String url,String method,String json ,ICallBack activity) {
    	this.activity = activity;
    	this.context = context;
    	this.url = url;
    	this.method = method;
//    	this.parameter = parameter;
    	this.json=json;
	}
	@Override
    protected String doInBackground(String... urls) {
          
      
		try {
			SharedPreferences sharedPreferences = context.getSharedPreferences("App", context.MODE_PRIVATE);
//		    String pin = sharedPreferences.getString(AppConstants.KEY_PIN, "");
//		    String mobileToken = sharedPreferences.getString(AppConstants.KEY_MOBILETOKEN, "");
		    
//		    Log.d("getBalance", mobileToken + " mobileToken getBalance pin " +pin);
		    
			 StringBuilder params=new StringBuilder("");
			    String result="";
			    String url1 =url ;
			    
			    URL obj = new URL(url1);
			    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			    con.setRequestMethod(method);
			    con.setRequestProperty("User-Agent", "Mozilla/5.0");
			    con.setRequestProperty("Accept-Language", "UTF-8");
			    con.setRequestProperty("Content-Type", "application/json");
//			    String encoded =Base64.encodeToString((mobileToken + ":" + pin).getBytes("UTF-8"), Base64.NO_WRAP); 
//			    con.setRequestProperty("Authorization", ActivityHelper.getBasicAuthentication(context));
			    con.setConnectTimeout(60000); 
			    con.setReadTimeout(60000); 
			    con.setUseCaches( false );
			    if(method.equalsIgnoreCase("POST")){
//			    	JSONObject json = new JSONObject();
//			        json.putAll( parameter );
			    	con.setDoOutput(true);
				    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.getOutputStream());
				    outputStreamWriter.write(json);
				    outputStreamWriter.flush();
			    }
			    int responseCode = con.getResponseCode();

			    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			    String inputLine;
			    StringBuffer response = new StringBuffer();

			    while ((inputLine = in.readLine()) != null) {
			        response.append(inputLine + "\n");
			    }
			    in.close();
			        result = response.toString();
//			    } catch (UnsupportedEncodingException e) {
//			        e.printStackTrace();
//			    } catch (MalformedURLException e) {
//			        e.printStackTrace();
//			    } catch (ProtocolException e) {
//			        e.printStackTrace();
//			    } catch (IOException e) {
//			        e.printStackTrace();
//			    }catch (Exception e) {
//			        e.printStackTrace();
//			    }finally {
			    	return  result;
//			    }
		}catch (HttpHostConnectException e) {
			error = "Can't connect to server, Problem with server or your internet connection.";
		}
    	catch (SocketException e) {
    	    error = "Connection problem, check your internet connection";
    	}
        catch (ClientProtocolException e) {
        	error = "Protocol Error occured.";
        } catch (IOException e) {
        	Log.d("IOException  ","IOException  =  " + e.getMessage() );
        	error = "IO Error occured.";
        } 
        catch (Exception e) {
			error = "Error occured.";
			Log.d("Exception ", e.toString());
		}
		return null;
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
    	try
		{	
    		JSONObject json = new JSONObject(result);
    		activity.onSuccess(json);
		}
		catch (Throwable e) {
			activity.onFailure(e,error);
		}
   }

}