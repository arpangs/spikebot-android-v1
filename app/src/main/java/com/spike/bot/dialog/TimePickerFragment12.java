package com.spike.bot.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.TimePicker;

import com.kp.core.ActivityHelper;
import com.kp.core.DateHelper;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Time Picker Dialog for Time Display in application
 * @author kaushal prajapati
 *
 */
public class TimePickerFragment12 extends DialogFragment implements OnTimeSetListener {
	private Activity activity;
	private Date selectedDate;
	private ICallback callback;
	private boolean is24Hour = false;


	public TimePickerFragment12(Activity activity, String selectedDate, ICallback callback) {

		this.activity = activity;
		this.selectedDate = new Date();
		try {
			this.selectedDate = ActivityHelper.parseTimeSimple(selectedDate, DateHelper.DATE_FROMATE_HH_MM_AMPM);
		} catch (ParseException e) {
		}
		
		this.callback = callback;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		final Calendar c = Calendar.getInstance();
		c.setTime(selectedDate);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int am = c.get(Calendar.AM_PM);

		return new TimePickerDialog(getActivity(), this, hour, minute,is24Hour);
	}
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		if (view.isShown()) {
			Calendar timeCal = Calendar.getInstance();
			timeCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
			timeCal.set(Calendar.MINUTE, minute);
			String time="";
			try {
				time =  DateHelper.formateDate(timeCal.getTime(), DateHelper.DATE_FROMATE_HH_MM_AMPM);
			} catch (ParseException e) {
			}
			callback.onSuccess( time );
		}
	}
}