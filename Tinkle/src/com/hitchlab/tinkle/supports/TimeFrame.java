package com.hitchlab.tinkle.supports;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class TimeFrame {

	public static long millisInMinute = 1000 * 60;
	public static long millisInHour = millisInMinute * 60;
	public static long millisInDay = millisInHour * 24;
	
	/**
	 * get the standard date time format of an date/time string
	 * @param time string in fb formats
	 * @return time string in format (yyyy-MM-dd HH:mm:ss)
	 */
	public static String getStandardDateTimeFormat(String time) {
		DateFormat format;
		DateFormat standardFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
		Date date = null;
		if (time.length() < 12) // number 12 is just separate 2 way of getting time
			format = new SimpleDateFormat("yyyy-MM-dd");
		else if (time.length() < 20)
			format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		else format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		
		try {
			date = format.parse(time);
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		
		return standardFormat.format(date);
	}
	
	/**
	 * get the time in millis of the time that was written in standard format
	 * @param time in format (yyyy-MM-dd HH:mm:ss)
	 * @return time in millis
	 */
	public static long getTimeInMilli(String time) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(time);
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		return date.getTime();
	}
	
	/**
	 * Get the unix time
	 * @param time in format (yyyy-MM-dd HH:mm:ss)
	 * @return unixTime
	 */
	public static long getUnixTime(String time) {
		return getTimeInMilli(time)/1000L;
	}
	
	/**
	 * Get the display time from unix time
	 * @param unix time
	 * @param time in format of (yyyy-MM-dd HH:mm:ss)
	 */
	public static String getTimeFromUnixTime(long time) {
		Date dateTime = new Date(time * 1000);
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
		return formatter.format(dateTime);
	}
	
	/**
	 * get today date in the standard format
	 * @return today date in format (yyyy-MM-dd 00:00:00)
	 */
	public static String getTodayDate() {
		return new SimpleDateFormat("yyyy-MM-dd' 00:00:00'").format(Calendar.getInstance().getTime());
	}
	
	/**
	 * get the ith day from today
	 * @param the ith date
	 * @return the date in format (yyyy-MM-dd 00:00:00)
	 */
	public static String getIthDate(int i) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd' 00:00:00'");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, i);
		return format.format(cal.getTime());
	}
	
	/**
	 * check whether the time has the hour indicated
	 * @param time in format (yyyy-MM-dd HH:mm:ss)
	 * @return true if the HH:mm is 00:00
	 */
	public static boolean isHourSpecified(String time) {
		return (!time.substring(11, 16).equals("00:00"));
	}
	
	/**
	 * Get the event display time
	 * @param the raw text of time
	 * @param event display time
	 */
	public static String getEventDisplayTime(long startTime) {
		String time = getTimeFromUnixTime(startTime);
		String today = getTodayDate();
		String tomorrow = getIthDate(1);
		boolean hasStartTime = isHourSpecified(time);
		
		if (time.equals(today)) return "TODAY";
		else if (time.equals(tomorrow)) return "TOMORROW";
		else {
			DateFormat targetFormat;
			if (hasStartTime) {
				if (time.substring(0, 10).equals(today.substring(0, 10))) {
					targetFormat = new SimpleDateFormat("'TODAY @ 'h:mm a");
				} else if (time.substring(0, 10).equals(tomorrow.substring(0, 10))) {
					targetFormat = new SimpleDateFormat("'TOMORROW @ 'h:mm a");
				} else if (time.substring(0, 4).equals(today.substring(0, 4)))
					targetFormat = new SimpleDateFormat("EEE, MMM d' @ 'h:mm a");
				else targetFormat = new SimpleDateFormat("EEE, MMM d, yyyy' @ 'h:mm a");
			} else if (time.substring(0, 4).equals(today.substring(0, 4)))
					targetFormat = new SimpleDateFormat("EEE, MMM d");
			else targetFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
			
			Date date = new Date(startTime * 1000L); 
			return targetFormat.format(date).toUpperCase(Locale.getDefault());
		}
	}
	
	/*
	 * In the next few methods. We will try to identify from today to the
	 * next few days the following
	 * 	1. the time frame of the closest weekend
	 *  2. the time frame of today
	 *  3. the time frame of this week, other than the weekend
	 *  4. the time frame of next week
	 *  5. the time frame of this month, other than the above found
	 *  6. other time frame that longer than month
	 */
	
	/**
	 * get the time frame of the today
	 * @return long[2] with the first long be the start time, and second long be the end time
	 */
	public static long[] getTodayTimeFrame() {
		long[] timeFrame = new long[2];
		timeFrame[0] = getUnixTime(getTodayDate());
		timeFrame[1] = getUnixTime(getIthDate(1));
		return timeFrame;
	}
	
	/**
	 * Get the time frame of the this weekend
	 * @return long[2]
	 */
	public static long[] getThisWeekendTimeFrame() {
		long[] timeFrame = new long[2];
		int[] weekendDateIndices = getWeekendDateIndices();
		timeFrame[0] = getUnixTime(getIthDate(weekendDateIndices[0]));
		timeFrame[1] = getUnixTime(getIthDate(weekendDateIndices[1]));
		return timeFrame;
	}
	
	/**
	 * get the time frame of the current week counting only week day
	 * @return long[2]
	 */
	public static long[] getThisWeekTimeFrame() {
		long[] timeFrame = new long[2];
		int[] weekendDateIndices = getWeekendDateIndices();
		timeFrame[0] = getUnixTime(getTodayDate());
		timeFrame[1] = getUnixTime(getIthDate(weekendDateIndices[1]));
		return timeFrame;
	}
	
	/**
	 * get the next week time frame
	 * @return long[2]
	 */
	public static long[] getNextWeekTimeFrame() {
		long[] timeFrame = new long[2];
		int[] weekendDateIndices = getWeekendDateIndices();
		timeFrame[0] = getUnixTime(getIthDate(weekendDateIndices[1]));
		timeFrame[1] = getUnixTime(getIthDate(weekendDateIndices[1]) + 7);
		return timeFrame;
	}
	
	/**
	 * Get this month time frame
	 * @return long[2]
	 */
	public static long[] getThisMonthTimeFrame() {
		int nextMonth = Integer.parseInt(getTodayDate().substring(5, 7)) + 1;
		if (nextMonth == 13) nextMonth = 1;
		long[] timeFrame = new long[2];
		timeFrame[0] = getUnixTime(getTodayDate());
		timeFrame[1] = getUnixTime(getTodayDate().substring(0, 5) + String.format("%02d", nextMonth) + "-01 00:00:00");
		return timeFrame;
	}
	
	/**
	 * get the date name
	 * @return String [MON, TUE, WED, THU, FRI, SAT, SUN]
	 */
	public static String getTodayDateName() {
		return new SimpleDateFormat("EEE").format(Calendar.getInstance().getTime()).toUpperCase();
	}
	
	/**
	 * Get the date indices until the start of the weekend and end of weekend
	 * @return int[2]
	 */
	public static int[] getWeekendDateIndices() {
		String[] dateNames = new String[] {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
		int[] indicesFrame = new int[2];
		String todayName = getTodayDateName();
		for (int i = 0; i < dateNames.length; i++) {
			if (todayName.equals(dateNames[i])) {
				indicesFrame[0] = Math.max(0, 5-i);
				indicesFrame[1] = 7-i;
				break;
			}
		}
		return indicesFrame;
	}
	
	/**
	 * Get the current hour
	 */
	public static int getCurrentHour() {
		return Integer.parseInt(new SimpleDateFormat("H").format(Calendar.getInstance().getTime()));
	}
	
	/**
	 * Get the current min
	 */
	public static int getCurrentMin() {
		return Integer.parseInt(new SimpleDateFormat("mm").format(Calendar.getInstance().getTime()));
	}
}
