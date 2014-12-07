package com.hitchlab.tinkle.service;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScheduleServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		startRepeatingCalendarEventService(context);
		startRepeatingNotificationService(context);
		startUpdatingService(context);
	}

	/**
	 * create and start service that will responsible to call a repeating service every
	 * 12 hours to update the list of calendar events to be displayed
	 * @param context
	 */
	public static void startRepeatingCalendarEventService(Context context) {
		Intent intent = new Intent(context, CheckSyncCalendarServiceReceiver.class);
		PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Calendar cal = Calendar.getInstance();
		//start 10 minutes after the boot is completed or after called
		cal.add(Calendar.MINUTE, 10);
		//fetch in exactly 12 hours
		service.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_HALF_DAY, pending);
	}
	
	/**
	 * Create and start service that will responsible to call a repeating service every
	 * 30 minutes to update the list of notifications
	 * @param context
	 */
	public static void startRepeatingNotificationService(Context context) {
		Intent intent = new Intent(context, CheckNotificationServiceReceiver.class);
		PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Calendar cal = Calendar.getInstance();
		//start 10 minutes after the boot is completed or after called
		cal.add(Calendar.MINUTE, 10);
		//fetch in exactly half an hour
		service.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_HOUR, pending);
	}
	
	/**
	 * Create and start service that will update all the store data every hour
	 * @param context
	 */
	public static void startUpdatingService(Context context) {
		Intent intent = new Intent(context, CheckUpdatingServiceReceiver.class);
		PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Calendar cal = Calendar.getInstance();
		//start 10 minutes after the boot is completed or after called
		cal.add(Calendar.MINUTE, 10);
		//fetch in exactly 12 hours
		service.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_HOUR, pending);
	}
}
