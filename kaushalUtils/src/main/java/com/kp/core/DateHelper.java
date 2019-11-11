package com.kp.core;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author kaushal Prajapati (kaushal2406@gmail.com)
 *
 */
public class DateHelper {
	public static String TAG = "DateHelper";

	public static final String DATE_FROMATE_DD_MMM_YYYY = "dd-MMM-yyyy";
	public static final String DATE_FROMATE_D_MMM_YY = "d-MMM-yy";
	public static final String DATE_MM_DD_YYYY = "MM/dd/yyyy";
	public static final String DATE_DD_MMM_YY = "dd MMM yy";
	public static final String DATE_DD_MMM = "dd-MMM";
	public static final String DATE_D_MMM = "d-MMM";
	public static final String DATE_FROMATE_HH_MM_AMPM = "hh:mm a";
	public static final String DATE_FROMATE_H_M_AMPM = "h:mm a";
	public static final String DATE_FROMATE_H_M_AMPM1 = "yyyy-MM-dd HH:mm";
	public static final String DATE_FROMATE_H_M_AMPM12 = "MMM dd, yyyy hh:mm aa";
	public static final String DATE_FROMATE_HH_MM= "HH:mm";
	public static final String DATE_FROMATE_HH_MM_TEMP= "HH:mm";
	public static final String DATE_DD_MMM_YYYY_HH_MM_AMPM = "dd-MMM-yyyy hh:mm a";
	public static final String DATE_MM_DD_YYYY_HH_MM_SS_AMPM = "MM/dd/yyyy hh:mm:ss a";
	public static final String DATE_D_MMM_YY_H_MM_AMPM = "dd-MMM-yy h:mm a";
	public static final String DATE_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_HH_MM_AMPM_DD_MM_YY= "hh:mm a dd-MMM-yy";

	private static boolean isToday(Date dateTime) {
		Date today = removeTime(new Date());
		return today.compareTo(removeTime(dateTime)) == 0;
	}

	private static boolean isYesterday(Date dateTime) {
		Date yesterday = removeTime(addDaysToDate(new Date(), -1));
		// Log.d("isToday", "isToday isYesterday " +
		// removeTime(dateTime).toLocaleString() );
		return yesterday.compareTo(removeTime(dateTime)) == 0;
	}

	public static String getDayString(Date date) {
		String s = "";

		if (isToday(date)) {
			//s = "Today";
			try {
				s = formateDate(date, DATE_FROMATE_HH_MM_AMPM);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}/* else if (isYesterday(date)) {
			s = "Yesterday";
		} */else {
			try {
				s = formateDate(date, DATE_D_MMM_YY_H_MM_AMPM);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return s;
	}

	public static String formateDate(Date date, String format) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		formatter.setTimeZone(TimeZone.getDefault());
		return formatter.format(date);
	}

	public static Date removeTime(final Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date addDaysToDate(Date today, int days) {
		final Calendar c = Calendar.getInstance();
		c.setTime(today);
		c.add(Calendar.DAY_OF_MONTH, days);
		// Log.w("DATE:",String.valueOf(c.getTime()));
		return c.getTime();
	}

	public static Date addHoursToDate(Date today, int hours) {
		final Calendar c = Calendar.getInstance();
		c.setTime(today);
		c.add(Calendar.HOUR_OF_DAY, hours);
		// Log.w("DATE:",String.valueOf(c.getTime()));
		return c.getTime();
	}

	public static Date addMinutesToDate(Date today, int minutes) {
		final Calendar c = Calendar.getInstance();
		c.setTime(today);
		c.add(Calendar.MINUTE, minutes);
		// Log.w("DATE:",String.valueOf(c.getTime()));
		return c.getTime();
	}

	public static Date addYearsToDate(Date today, int years) {
		final Calendar c = Calendar.getInstance();
		c.setTime(today);
		c.add(Calendar.YEAR, years);
		// Log.w("DATE:",String.valueOf(c.getTime()));
		return c.getTime();
	}

	public static String getCurrentTimeStamp(String seprator) {
		Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR);
		int mMonth = c.get(Calendar.MONTH);
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		int mHours = c.get(Calendar.HOUR_OF_DAY);
		int mMinute = c.get(Calendar.MINUTE);
		int mSecond = c.get(Calendar.SECOND);
		StringBuilder s = new StringBuilder();
		s.append(mDay + seprator + (mMonth + 1) + seprator + mYear + seprator + mHours + seprator + mMinute + seprator
				+ mSecond);
		Log.d(TAG, "getCurrentTimeStamp " + s.toString());
		return s.toString();
	}

	public static Date getMonthFirstDate(Date today) {

		Calendar calendar = Calendar.getInstance(); // this takes current date
		calendar.setTime(today);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		// System.out.println(c.getTime());
		return calendar.getTime();
	}

	public static Date getMonthLastDate(Date today) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(today);
		// calendar.add(Calendar.MONTH, 1);
		// calendar.set(Calendar.DAY_OF_MONTH, 1);
		// calendar.add(Calendar.DATE, -1);
		calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
		Date lastDayOfMonth = calendar.getTime();

		// calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		// DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// System.out.println("Today : " + sdf.format(today));
		// System.out.println("Last Day of Month: " +
		// sdf.format(lastDayOfMonth));
		return lastDayOfMonth;
	}

	public static String formateDateToUtc(Date date, String format) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

		Log.d("formateDayli ",
				" formateDaylightDateToUtc formateDateToUtc  " + date.getTime());
		// TimeZone timezone = TimeZone.getDefault();
		// int TimeZoneOffset = timezone.getRawOffset() / (60 * 60 * 1000);
		// Log.d("","AM DayLight DayLightSaving: TimeZoneOffset " +
		// TimeZoneOffset);
		// double hoursDiff = timezone.getDSTSavings() / (60 * 60 * 1000);

		// return new Date(calendar.getTime().getTime() -
		// timezone.getDSTSavings());
		// return formatter.format(new Date(date.getTime() -
		// timezone.getDSTSavings()));
		Log.d("formateD ",
				" formateDaylightDateToUtc formateDateToUtc  " + formatter.format(date));
		return formatter.format(date);
	}

	public static boolean isDateAfter(String startDate, String endDate, String myFormatString) {
		try {
			SimpleDateFormat df = new SimpleDateFormat(myFormatString);
			Date endingDate = df.parse(endDate);
			Date startingDate = df.parse(startDate);

			if (endingDate.after(startingDate)
					|| endingDate.toLocaleString().equalsIgnoreCase(startingDate.toLocaleString()))
				return true;
			else
				return false;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isTimeAfter(String startDate, String endDate, String myFormatString) {
		try {
			SimpleDateFormat df = new SimpleDateFormat(myFormatString);
			Date endingDate = df.parse(endDate);
			Date startingDate = df.parse(startDate);
			Log.d("isTimeAfter", "isTimeAfter startingDate " + startingDate.toLocaleString());
			Log.d("isTimeAfter", "isTimeAfter endingDate " + endingDate.toLocaleString());
			// Log.d("endDate", "endDate " +
			// endDate.toLocaleString() + " " + formateDate(endDate,
			// "dd/MM/yyyy") + " " + formateDate(today, "dd/MM/yyyy"));
			if (endingDate.after(startingDate))
				return true;
			else
				return false;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isDateBefore(Date today, Date endDate) {
		try {
			// Log.d("today", "today " +
			// today.toLocaleString());
			// Log.d("endDate", "endDate " +
			// endDate.toLocaleString() + " " + formateDate(endDate,
			// "dd/MM/yyyy") + " " + formateDate(today, "dd/MM/yyyy"));

			if (endDate.before(today)
					&& !formateDate(endDate, "dd/MM/yyyy").equalsIgnoreCase(formateDate(today, "dd/MM/yyyy")))
				return true;
			else
				return false;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isDateGreater(Date today, Date endDate) {
		try {
			Calendar dateCalFrom = Calendar.getInstance();
			dateCalFrom.setTime(today);
			dateCalFrom.set(Calendar.HOUR_OF_DAY, 0);
			dateCalFrom.set(Calendar.MINUTE, 1);
			dateCalFrom.set(Calendar.SECOND, 1);

			Calendar dateCalTo = Calendar.getInstance();
			dateCalTo.setTime(endDate);
			dateCalTo.set(Calendar.HOUR_OF_DAY, 0);
			dateCalTo.set(Calendar.MINUTE, 1);
			dateCalTo.set(Calendar.SECOND, 1);

			// Log.d("today", "isDateGreater today " +
			// dateCalFrom.getTime().toLocaleString());
			// Log.d("endDate", "isDateGreater endDate "
			// + dateCalTo.getTime().toLocaleString() + " " +
			// formateDate(endDate, "dd/MM/yyyy") + " " + formateDate(today,
			// "dd/MM/yyyy"));

			if (dateCalFrom.after(dateCalTo)
					&& !formateDate(endDate, "dd/MM/yyyy").equalsIgnoreCase(formateDate(today, "dd/MM/yyyy"))) {
				// Log.d("today", "today isDateGreater
				// from date === yes" );
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isDateGreaterEquals(Date today, Date endDate) {
		try {
			Calendar dateCalFrom = Calendar.getInstance();
			dateCalFrom.setTime(today);
			dateCalFrom.set(Calendar.HOUR_OF_DAY, 0);
			dateCalFrom.set(Calendar.MINUTE, 1);
			dateCalFrom.set(Calendar.SECOND, 1);

			Calendar dateCalTo = Calendar.getInstance();
			dateCalTo.setTime(endDate);
			dateCalTo.set(Calendar.HOUR_OF_DAY, 0);
			dateCalTo.set(Calendar.MINUTE, 1);
			dateCalTo.set(Calendar.SECOND, 1);

			// Log.d("today", "isDateGreaterEquals today
			// " + today.toLocaleString());
			// Log.d("endDate", "isDateGreaterEquals
			// endDate " + endDate.toLocaleString() + " " + formateDate(endDate,
			// "dd/MM/yyyy") + " " + formateDate(today, "dd/MM/yyyy"));

			if (dateCalFrom.getTime().after(dateCalTo.getTime())
					|| formateDate(endDate, "dd/MM/yyyy").equalsIgnoreCase(formateDate(today, "dd/MM/yyyy"))) {
				// Log.d("today", "today
				// isDateGreaterEquals from date === yes" );
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isDateLessEquals(Date today, Date endDate) {
		try {
			Calendar dateCalFrom = Calendar.getInstance();
			dateCalFrom.setTime(today);
			dateCalFrom.set(Calendar.HOUR_OF_DAY, 0);
			dateCalFrom.set(Calendar.MINUTE, 1);
			dateCalFrom.set(Calendar.SECOND, 1);

			Calendar dateCalTo = Calendar.getInstance();
			dateCalTo.setTime(endDate);
			dateCalTo.set(Calendar.HOUR_OF_DAY, 0);
			dateCalTo.set(Calendar.MINUTE, 1);
			dateCalTo.set(Calendar.SECOND, 1);

			// Log.d("today", "today " +
			// today.toLocaleString());
			// Log.d("endDate", "endDate " +
			// endDate.toLocaleString() + " " + formateDate(endDate,
			// "dd/MM/yyyy") + " " + formateDate(today, "dd/MM/yyyy"));

			if (dateCalFrom.getTime().before(dateCalTo.getTime())
					|| formateDate(endDate, "dd/MM/yyyy").equalsIgnoreCase(formateDate(today, "dd/MM/yyyy"))) {
				// Log.d("today", "today
				// isDateLessEquals from date === yes" );
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isTimeLessEquals(Date today, Date endDate) {
		try {
			Calendar dateCalFrom = Calendar.getInstance();
			dateCalFrom.setTime(today);

			Calendar dateCalTo = Calendar.getInstance();
			dateCalTo.setTime(endDate);

			Log.d("today", "today " + today.toLocaleString());
			Log.d("endDate", "endDate " + endDate.toLocaleString() + "   "+ formateDate(endDate, "dd/MM/yyyy") + " " + formateDate(today, "dd/MM/yyyy"));

			if (!dateCalFrom.getTime().after(dateCalTo.getTime())) {
				Log.d("today", "today isTimeLessEquals from date === yes");
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	public static Date parseTimeSimple(String date, String format) throws ParseException
	{
		SimpleDateFormat formatter = new SimpleDateFormat(format);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(formatter.parse(date));
		calendar.setTimeZone(TimeZone.getDefault());

		return calendar.getTime();
	}


	public static Date parseDateSimple(String date, String format) throws ParseException
	{
		SimpleDateFormat formatter = new SimpleDateFormat(format);

		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.setTime(formatter.parse(date));
//			calendar.setTimeZone(TimeZone.getDefault());

//
//			TimeZone timezone = TimeZone.getDefault();
//			int TimeZoneOffset = timezone.getRawOffset() / (60 * 60 * 1000);
//			CustomLogHandler.printInfolog("","AM DayLight DayLightSaving: TimeZoneOffset " + TimeZoneOffset);
//			double hoursDiff = timezone.getDSTSavings() / (60 * 60 * 1000);
//

//		    return new Date(calendar.getTime().getTime() + timezone.getDSTSavings());

		return calendar.getTime();
	}
}
