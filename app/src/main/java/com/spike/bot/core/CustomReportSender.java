package com.spike.bot.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.acra.ACRA;
import org.acra.ACRAConstants;
import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


// this ReportSender class used for create a mail for Crash Report.
public class CustomReportSender implements ReportSender {

    public static final String TAG = "CustomReportSender";
    private final Map<ReportField, String> mMapping = new HashMap<ReportField, String>() ;
    private FileOutputStream crashReport = null;
    //private LoginVO loginVo;
    SharedPreferences prefs;
    String username="",fullName="",userID="";
    Context mContext;
    public CustomReportSender(Context ctx) {
        // the destination
        try {
            this.mContext = ctx;
            crashReport = ctx.openFileOutput("crashReport", Context.MODE_PRIVATE);
//        loginVo = ActivityHelper.getLoginData(ctx);
            prefs=mContext.getSharedPreferences("App", mContext.MODE_PRIVATE);
           // username=prefs.getString(AppConstants.TTCHAT_USERNAME,"");
          //  fullName=prefs.getString(AppConstants.TTCHAT_FULLNAME,"");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            infoLog("IO ERROR"+ e.getMessage());
        }
    }

    @Override
    public void send(CrashReportData report) throws ReportSenderException {

        final Map<String, String> finalReport = remap(report);

        try {
            OutputStreamWriter osw = new OutputStreamWriter(crashReport);

            Set set = finalReport.entrySet();
            Iterator i = set.iterator();

            while (i.hasNext()) {
                Map.Entry<String,String> me = (Map.Entry) i.next();
                osw.write("[" + me.getKey() + "]=" + me.getValue());
            }

            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
            infoLog("IO ERROR" + e.getMessage());
        }

        //Unknown SMTP host: mail.gochetak.com;
        //SendMail eUnknown SMTP host: mail.gochetak.com
        JSONObject obj=new JSONObject(finalReport);
        Log.d(TAG,"Report JSON : " + obj.toString());
        try {
            System.out.println("time GMailSender    "  );
       /* GMailSender sender = new GMailSender("appcrash@gochetak.com", "app@%CRASH"); //app@)!%CRASH
        sender.sendMail("MongooseIM - appcrash From TTCHAT Android",
        		obj.toString()+",("+username+","+fullName+","+userID+")",
                "appcrash@gochetak.com",
                "sagardj@tasktower.com");*/ //deepkiraninc@gmail.com //prakashaj@tasktower.com
            GMailSender sender = new GMailSender("ttchatcrash@gmail.com", "ttchat@123"); //app@)!%CRASH
            sender.sendMail("Deep Automation - CrashReport",
                    obj.toString()+",("+username+","+fullName+","+userID+")",
                    "ttchatcrash@gmail.com",
                    "ttchatcrash@gmail.com");
        } catch (Exception e) {
            e.printStackTrace();
//    	System.out.println("SendMail demo "+ e.getMessage());
        }
    }

    private Map<String, String> remap(Map<ReportField, String> report) {

        ReportField[] fields = ACRA.getConfig().customReportContent();
        if (fields.length == 0) {
            fields = ACRAConstants.DEFAULT_REPORT_FIELDS;
        }

        final Map<String, String> finalReport = new HashMap<String, String>(
                report.size());
        for (ReportField field : fields) {
            if (mMapping == null || mMapping.get(field) == null) {
                finalReport.put(field.toString(), report.get(field));
            } else {
                finalReport.put(mMapping.get(field), report.get(field));
            }
        }
        return finalReport;
    }

    private static void infoLog(String data) {
            Log.i(TAG, data);
    }

}