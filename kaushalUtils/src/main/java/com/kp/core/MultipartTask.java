package com.kp.core;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.Map;

/**
 * @author kaushal Prajapati (kaushal2406@gmail.com)
 *
 */
public class MultipartTask extends AsyncTask<String, Void, String> {
	String Tag="FileUploadTask";

    private ICallBack callBack;
    private Context context;
    private String result="",url;
    private String error="";
	private Map<String,String> parameter;
    URL connectURL;

	public MultipartTask(Context context, String url, Map<String,String> parameter , ICallBack callBack) {
    	this.callBack = callBack;
    	this.context = context;
    	try {
			this.connectURL = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    	this.url = url;
    	this.parameter = parameter;
	}
    @Override
    protected String doInBackground(String... urls) {
      
		try {
			
//			return new HttpFileUpload(url, key,userId,fileName,realPath).Send_Now();
			 Log.d("Send_Now","Starting Http File doInBackground " + url);

//	          String iFileName = "ovicam_temp_vid.mp4";
	          String lineEnd = "\r\n";
	          String twoHyphens = "--";
	          String boundary = "*****";
	          
//	          try
//	          {
			/*for (String key : parameter.keySet()) {
			 	//parameter.get(key);
				Log.d(Tag, key + " Starting result  keySet => " + parameter.get(key));
			}*/
;
	                // Open a HTTP connection to the URL
	                HttpURLConnection conn = (HttpURLConnection)connectURL.openConnection();
	      
	                // Allow Inputs
	                conn.setDoInput(true);
	                // Allow Outputs
	                conn.setDoOutput(true);
	                // Don't use a cached copy.
	                conn.setUseCaches(false);
	                // Use a post method.
	                conn.setRequestMethod("POST");
	               // conn.setRequestProperty("Connection", "Keep-Alive");
	                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Accept-Language", "UTF-8");
	                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
	      //          conn.setRequestProperty("uploadedfile", iFileName); 
	              //  conn.setChunkedStreamingMode(1024*1024);
                    conn.setConnectTimeout(60000);
                    conn.setReadTimeout(60000);

                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

					for (String key : parameter.keySet()) {
						//parameter.get(key);
						Log.d(Tag, key + " Starting result  keySet => " + parameter.get(key));
						dos.writeBytes(twoHyphens + boundary + lineEnd);
						dos.writeBytes("Content-Disposition: form-data; name=\""+key+"\""+ lineEnd);
						dos.writeBytes(lineEnd);
						dos.writeBytes(parameter.get(key));
						dos.writeBytes(lineEnd);
					}

	                /*dos.writeBytes(twoHyphens + boundary + lineEnd);
	                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName +"\"" + lineEnd);
	                dos.writeBytes(lineEnd);*/
	      
	                Log.d(Tag,"Headers are written");
	              /*
	                int maxBufferSize = 1024*1024;
	                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
	                byte[ ] buffer = new byte[bufferSize];
	      
	                // read file and write it into form...
	                int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	      
	                while (bytesRead > 0)
	                {
	                  try {
	                        dos.write(buffer, 0, bufferSize);
	                    } catch (OutOfMemoryError e) {
	                        e.printStackTrace();
	                        String response = "outofmemoryerror";
	                        return response;
	                    }
	      //            Log.d(Tag, "bytesRead ===-==-=-=-=-=- " + bytesRead);
	                    bytesAvailable = fileInputStream.available();
	                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
	                    bytesRead = fileInputStream.read(buffer, 0,bufferSize);
	                }*/
	              //  dos.writeBytes(lineEnd);
	                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    dos.flush();
                    dos.close();
                    dos =null;

	                Log.d(Tag,"File Sent, Response: "+String.valueOf(conn.getResponseCode()));
	                int serverResponseCode = conn.getResponseCode();
	                String serverResponseMessage = conn.getResponseMessage();
	                
	                Log.d("uploadFile", "HTTP Response is : "  + serverResponseMessage + ": " + serverResponseCode);
	                
	               // InputStream is = conn.getInputStream();
	                // retrieve the response from server
	                /*int ch;
	      
	                StringBuffer b =new StringBuffer();
	                while( ( ch = is.read() ) != -1 ){ b.append( (char)ch ); }
	                String s=b.toString();
	                Log.i("Response",s);*/

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine + "\n");
                    }
                    in.close();
                    result = response.toString();
                    Log.d("CallXMLService ", "CallXMLService  result  " + result );

	             // close streams
	               // fileInputStream.close();
	                /*dos.flush();
	                dos.close();
	                dos =null;*/
            return  result;
		}catch (HttpHostConnectException e) {
			//	 Log.d("Exception","HttpHostConnectException Exception" + e.getMessage());
			error = "Can't connect to server, Problem with server or your internet connection.";
		}
    	catch (SocketException e) {
    	   //  Log.d("Exception","SocketException Exception" + e.getMessage());
    	    error = "Connection problem, check your internet connection";
    	}
        catch (ClientProtocolException e) {
        	// Log.d("ClientProtocolException  ","ClientProtocolException  =  " + e.getMessage() );
        	error = "Protocol Error occured.";
        } catch (IOException e) {
        	// Log.d("IOException  ","IOException  =  " + e.getMessage() );
        	error = "IO Error occured.";
        }
		catch (Exception e) {
			error = "Error occured.";
			 Log.d("Exception ", e.toString());
		}
		error = "Can't connect to server, Problem with server or your internet connection.";
		return error;
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
    	Log.d("onPostExecute  "," onPostExecute  result json =  " + result );
        try
        {
//           	JSONObject soapDatainJsonObject = XML.toJSONObject(result.toString());
//           	Log.d("soapDatainJsonObject", "1 soapDatainJsonObject==== " + soapDatainJsonObject.toString());

            JSONObject json = new JSONObject(result);
            callBack.onSuccess(json);
        }
        catch (Throwable e) {
            Log.d("onPostExecute", " Exception " + e.getMessage());
            callBack.onFailure(e,error);
        }
   }
	/*@Override
	protected void onCancelled() {
	}*/
}
