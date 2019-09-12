package com.kp.core;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kaushalprajapti.util.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author kaushal Prajapati (kaushal2406@gmail.com)
 *
 */
public class ActivityHelper {
	public static String TAG = "ActivityHelper";

	// Dialog Action
	public static final int NO_ACTION = 1;
	public static final int CLOSE_ACTIVITY = 2;

	public static void hideKeyboard(final Activity p_context) {
		try {
			InputMethodManager imm = (InputMethodManager) p_context.getSystemService(Context.INPUT_METHOD_SERVICE);
			// imm.hideSoftInputFromWindow(((EditText) p_view).getWindowToken(),
			// 0);

			imm.hideSoftInputFromWindow(p_context.getCurrentFocus().getWindowToken(), 0);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}

	// Progress dialog
	public static ProgressDialog m_progressDialog;

	public static void showProgressDialog(final Context p_context, final String p_loadingMessage,
			final boolean p_isCancelable) {
		// m_progressDialog = null;
		if (m_progressDialog != null && m_progressDialog.isShowing()) {
			// m_progressDialog.isShowing();
			return;
		}
		m_progressDialog = new ProgressDialog(p_context);
		m_progressDialog.setMessage(p_loadingMessage);
		m_progressDialog.setCancelable(p_isCancelable);
		// m_progressDialog.findViewById(android.R.id)
		if (!((Activity) p_context).isFinishing()) {
			m_progressDialog.show();
		}
	}

	public static void dismissProgressDialog() {
		try {
			if (m_progressDialog != null && m_progressDialog.isShowing()) {
				m_progressDialog.dismiss();
			}
			m_progressDialog = null;
		} catch (Throwable e) {
		}
	}

	/**
	 * This is used to check the given URL is valid or not.
	 * @param url
	 * @return
     */
	public static boolean isValidUrl(String url) {
		String regex = "^(https?|ftp|file|rtmp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"; //Add rtsp
		Pattern mPattern = Pattern.compile(regex);
		Matcher matcher = mPattern.matcher(url);
		return matcher.matches();
	}

	public static void showDialog(final Context p_context,String title, String p_message, final int p_action) {

		AlertDialog.Builder m_builder = new AlertDialog.Builder(p_context);

		//m_builder.setTitle(title);
		m_builder.setMessage(p_message);
		m_builder.setCancelable(false);
		m_builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface p_dialog, int p_id) {

				if (p_action == NO_ACTION) {

				} else if (p_action == CLOSE_ACTIVITY) {
					if (p_context instanceof Activity) {
						((Activity) p_context).finish();
					}
				}

			}
		});

		AlertDialog m_alertDialog = m_builder.create();
		m_alertDialog.show();
	}


	public static boolean isConnectingToInternet(final Context context) {
		
		 boolean haveConnectedWifi = false;
		    boolean haveConnectedMobile = false;
		    
		try {
			
			
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
//				NetworkInfo[] info = connectivity.getAllNetworkInfo();
//				if (info != null)
//					for (int i = 0; i < info.length; i++)
//						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
//							return true;
//						}
				
//				getActiveNetworkInfo
				 NetworkInfo[] netInfo = connectivity.getAllNetworkInfo();
				    for (NetworkInfo ni : netInfo) {
//				    	ni.getState()
				        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				            if (ni.isConnected())
				                haveConnectedWifi = true;
				        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				            if (ni.isConnected())
				                haveConnectedMobile = true;
				    }
				    return haveConnectedWifi || haveConnectedMobile;
			}

		} catch (Exception e) {
			// Toast.makeText(context, " isConnectingToInternet = "
			// +e.getMessage() , Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	public static void showMenuPermanant(Context context) {
		try {
			ViewConfiguration config = ViewConfiguration.get(context);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
		}
	}

	// To animate view slide out from bottom to top
	public static void slideToTop(final View view) {
		// TranslateAnimation animate = new
		// TranslateAnimation(0,0,0,-view.getHeight());
		// animate.setDuration(500);
		//// animate.setFillAfter(true);
		// view.startAnimation(animate);
		// view.setVisibility(View.GONE);
		final int initialHeight = view.getMeasuredHeight();

		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				if (interpolatedTime == 1) {
					view.setVisibility(View.GONE);
				} else {
					view.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
					view.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int) (initialHeight / view.getContext().getResources().getDisplayMetrics().density));
		view.startAnimation(a);
	}

	// To animate view slide out from top to bottom
	public static void slideToBottom(final View view) {
		// TranslateAnimation animate = new
		// TranslateAnimation(0,0,0,view.getHeight());
		// animate.setDuration(500);
		//// animate.setFillAfter(true);
		// view.startAnimation(animate);
		// view.setVisibility(View.VISIBLE);
		view.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		final int targetHeight = view.getMeasuredHeight();

		// Older versions of android (pre API 21) cancel animations for views
		// with a height of 0.
		view.getLayoutParams().height = 1;
		view.setVisibility(View.VISIBLE);
		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				view.getLayoutParams().height = interpolatedTime == 1 ? LayoutParams.WRAP_CONTENT
						: (int) (targetHeight * interpolatedTime);
				view.requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};
		// 1dp/ms
		a.setDuration((int) (targetHeight / view.getContext().getResources().getDisplayMetrics().density));
		view.startAnimation(a);
	}

	@SuppressLint("MissingPermission")
	public static String getIMEI(Context context) {
		String number = "";
		try {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
			number = tm.getDeviceId();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return number;
	}

	public static String getDeviceId(Context context) {
		// TelephonyManager tm =
		// (TelephonyManager)context.getSystemService(context.TELEPHONY_SERVICE);
		// String number = tm.getDeviceId();
		String androidId = android.provider.Settings.Secure.getString(context.getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);
		return androidId;
	}
	 public static void webViewSettings(WebView wv_msg_body) {

		    WebSettings webSettings = wv_msg_body.getSettings();
	      webSettings.setLoadWithOverviewMode(true);
	      webSettings.setUseWideViewPort(true);
	      webSettings.setBuiltInZoomControls(true);
	      webSettings.setSupportZoom(true);
	      webSettings.setDisplayZoomControls(false);
	      webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); 
	      webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
	  //    wv_docket_msg_body.setInitialScale(110);
	      wv_msg_body.setWebChromeClient(new WebChromeClient());///////////////////////
	      wv_msg_body.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
	      wv_msg_body.setScrollbarFadingEnabled(false);
	      wv_msg_body.clearCache(true); 
	      
	  }
	 public static String CallJSONService(String url,String json,String method) throws Exception{
			
		 StringBuilder params=new StringBuilder("");
		    String result="";
		    String url1 =url ;
		    
		    UtilsConstants.logDisplay("CallXMLService  url  " + url1 );
		    URL obj = new URL(url1);
		    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		    con.setRequestMethod(method);
		    con.setRequestProperty("User-Agent", "Mozilla/5.0");
		    con.setRequestProperty("Accept-Language", "UTF-8");
		    con.setRequestProperty("Content-Type", "application/json");
		    con.setRequestProperty("Accept", "application/json");

		    con.setConnectTimeout(60000);
		    con.setReadTimeout(60000);
		    con.setUseCaches( false );
		    if(method.equalsIgnoreCase("POST")){
//		    	JSONObject json = new JSONObject();
//		        json.putAll( parameter );
		    	con.setDoOutput(true);
			    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.getOutputStream());
			    outputStreamWriter.write(json);
			    outputStreamWriter.flush();
		    }
		    int responseCode = con.getResponseCode();
		    UtilsConstants.logDisplay("\nSending 'POST' request to URL : " + url);
		    UtilsConstants.logDisplay("Post parameters : " + params);
		    UtilsConstants.logDisplay("Response Code : " + responseCode);
		    UtilsConstants.logDisplay("CallJSONService  json  " + json );
		    UtilsConstants.logDisplay("CallJSONService  responseCode  " + responseCode );
		    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		    String inputLine;
		    StringBuffer response = new StringBuffer();

		    while ((inputLine = in.readLine()) != null) {
		        response.append(inputLine + "\n");
		    }
		    in.close();
		        result = response.toString();

		 con.disconnect();
		    	return  result;

		 
	}


	public static String CallJSONService1(String url,String json,String method) throws Exception{

		StringBuilder params=new StringBuilder("");
		String result="";
		String url1 =url ;

		UtilsConstants.logDisplay("CallXMLService  url  " + url1 );
		URL obj = new URL(url1);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod(method);
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		con.setRequestProperty("Accept-Language", "UTF-8");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");

		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		con.setUseCaches( false );
		if(method.equalsIgnoreCase("POST")){
//		    	JSONObject json = new JSONObject();
//		        json.putAll( parameter );
			con.setDoOutput(true);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.getOutputStream());
			outputStreamWriter.write(json);
			outputStreamWriter.flush();
		}
		int responseCode = con.getResponseCode();
		UtilsConstants.logDisplay("\nSending 'POST' request to URL : " + url);
		UtilsConstants.logDisplay("Post parameters : " + params);
		UtilsConstants.logDisplay("Response Code : " + responseCode);
		UtilsConstants.logDisplay("CallJSONService  json  " + json );
		UtilsConstants.logDisplay("CallJSONService  responseCode  " + responseCode );
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine + "\n");
		}
		in.close();
		result = response.toString();
		con.disconnect();
		return  result;
	}


	public static String CallJSONService2(String url,String json,String method) throws Exception{

		StringBuilder params=new StringBuilder("");
		String result="";
		String url1 =url ;

		UtilsConstants.logDisplay("CallXMLService  url  " + url1 );
		URL obj = new URL(url1);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod(method);
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		con.setRequestProperty("Accept-Language", "UTF-8");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");

		con.setConnectTimeout(2000);
		con.setReadTimeout(2000);
		con.setUseCaches( false );
		if(method.equalsIgnoreCase("POST")){
//		    	JSONObject json = new JSONObject();
//		        json.putAll( parameter );
			con.setDoOutput(true);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.getOutputStream());
			outputStreamWriter.write(json);
			outputStreamWriter.flush();
		}
		int responseCode = con.getResponseCode();
		UtilsConstants.logDisplay("\nSending 'POST' request to URL : " + url);
		UtilsConstants.logDisplay("Post parameters : " + params);
		UtilsConstants.logDisplay("Response Code : " + responseCode);
		UtilsConstants.logDisplay("CallJSONService  json  " + json );
		UtilsConstants.logDisplay("CallJSONService  responseCode  " + responseCode );
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine + "\n");
		}
		in.close();
		result = response.toString();
		con.disconnect();

		return  result;


	}

	public static String CallJSONService3(String url,String json,String method) throws Exception{

		StringBuilder params=new StringBuilder("");
		String result="";
		String url1 =url ;

		UtilsConstants.logDisplay("CallXMLService  url  " + url1 );
		URL obj = new URL(url1);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod(method);
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		con.setRequestProperty("Accept-Language", "UTF-8");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");

		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		con.setUseCaches( false );
		if(method.equalsIgnoreCase("POST")){
//		    	JSONObject json = new JSONObject();
//		        json.putAll( parameter );
			con.setDoOutput(true);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.getOutputStream());
			outputStreamWriter.write(json);
			outputStreamWriter.flush();
		}
		int responseCode = con.getResponseCode();
		UtilsConstants.logDisplay("\nSending 'POST' request to URL : " + url);
		UtilsConstants.logDisplay("Post parameters : " + params);
		UtilsConstants.logDisplay("Response Code : " + responseCode);
		UtilsConstants.logDisplay("CallJSONService  json  " + json );
		UtilsConstants.logDisplay("CallJSONService  responseCode  " + responseCode );
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine + "\n");
		}
		in.close();
		result = response.toString();

//		con.disconnect();
		return  result;


	}
	 
		public static void setLocale(Context context) {

	       SharedPreferences sharedPreferences = context.getSharedPreferences("App", context.MODE_PRIVATE);
		   String language  = sharedPreferences.getString(LANGUAGE, "ru");
	       Resources res = context.getResources();
	       DisplayMetrics dm = res.getDisplayMetrics();
		   android.content.res.Configuration conf = res.getConfiguration();
		   conf.locale = new Locale(language.toLowerCase());
		   res.updateConfiguration(conf, dm);
		   
//		   FontsOverride.setDefaultFont(context, "normal", "fonts/SanFranciscoText-Regular.ttf");
//		   FontsOverride.setDefaultFont(context, "monospace", "fonts/Proxima_Nova_Light.otf"); //DEFAULT
//		   FontsOverride.setDefaultFont(context, "monospace", "fonts/SanFranciscoText-Medium.ttf"); 
		   FontsOverride.setDefaultFont(context, "DEFAULT", "fonts/SanFranciscoText-Regular.ttf");
	       FontsOverride.setDefaultFont(context, "MONOSPACE", "fonts/SanFranciscoText-Medium.ttf");
//	       FontsOverride.setDefaultFont(context, "BOLD", "fonts/SanFranciscoText-Bold.ttf");
//	       FontsOverride.setDefaultFont(context, "SANS_SERIF", "fonts/SanFranciscoText-Light.ttf");
	       new FontsOverride(context).loadFonts();
	    }
		public static final String PARAM_DEV_TOKEN = "PARAM_DEV_TOKEN";
		public static final String PARAM_IS_ENABLE = "PARAM_IS_ENABLE";
		public static final String PHONETYPE = "ANDROID";
		public static final String LANGUAGE = "LANGUAGE";
		
	public String getEncoded64ImageStringFromBitmap(Bitmap bitmap,int sampling) {
	    ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    bitmap.compress(CompressFormat.PNG, sampling, stream);
	    byte[] byteFormat = stream.toByteArray();
	    // get the base 64 string
	    String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
	    return imgString;
	}
	public static void generateSpinnerList(final Context mContext,final Spinner spinner,final ArrayList<String> headerList){
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, headerList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}
	public static Date parseTimeSimple(String date, String format) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(format);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(formatter.parse(date));
		calendar.setTimeZone(TimeZone.getDefault());

		return calendar.getTime();
	}

	/* hmZero make double digit hour and minute value
	*  @htMnt : 1
	*  return : 01
	*/
	public static String hmZero(int hrMnt) {
		return (hrMnt >=10)? Integer.toString(hrMnt):String.format("0%s",Integer.toString(hrMnt));
	}

	public static String hourMinute(String hour,String mnts){
		return String.format("%02d:%02d", hour, mnts);
	}

	/*
	* make double format hour:minute string
	* @hour : 1
	* @minute : 2
	* return : 01:02
	* */

	public static String hourMinuteZero(int hour,int mnts){
		String hourZero = (hour >=10)? Integer.toString(hour):String.format("0%s",Integer.toString(hour));
		String minuteZero = (mnts >=10)? Integer.toString(mnts):String.format("0%s",Integer.toString(mnts));
		return hourZero+":"+minuteZero;
	}

	private String putTimeInXX(String inputDescription,String pTime) {
		return inputDescription.replace("XX",pTime);
	}
}
