package com.hitchlab.tinkle.calendar;

import java.util.TimeZone;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;

import com.hitchlab.tinkle.objects.FbEvent;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;
import com.hitchlab.tinkle.supports.TimeFrame;

public class CalendarHelper {
	
	/**
	 * Build cal uid 
	 * @return uri
	 */
	private static Uri buildCalUri(String uid) {
		return Calendars.CONTENT_URI.buildUpon()
				.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
				.appendQueryParameter(Calendars.ACCOUNT_NAME, uid)
				.appendQueryParameter(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
				.build();
	}

	/**
	 * Create a new calendar content value for new account
	 * @param context
	 * @return ContentValues
	 */
	private static ContentValues prepareNewCalContentValues(Context context) {
		final String uid = SharedPreference.getPrefStringValue(context, Preference.UID);
		final String username = SharedPreference.getPrefStringValue(context, Preference.USERNAME);
		ContentValues values = new ContentValues();
		values.put(Calendars.ACCOUNT_NAME, uid);
		values.put(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
		values.put(Calendars.NAME, uid);
		values.put(Calendars.CALENDAR_DISPLAY_NAME, username);
		values.put(Calendars.CALENDAR_COLOR, Color.RED);
		
		//user can only read the calendar.
		values.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_READ);
		values.put(Calendars.OWNER_ACCOUNT, username);
		values.put(Calendars.VISIBLE, 1);
		values.put(Calendars.SYNC_EVENTS, 1);
		return values;
	}
	
	/**
	 * Create a new calendar and insert it to android database
	 * @param context
	 * @return calId
	 */
	public static long createCalendar(Context context) {
		final String uid = SharedPreference.getPrefStringValue(context, Preference.UID);
		ContentResolver cr = context.getContentResolver();
		final ContentValues cv = prepareNewCalContentValues(context);
		Uri  calUri = cr.insert(buildCalUri(uid), cv);//cr.insert(Calendars.CONTENT_URI, cv);
		long calId = Long.parseLong(calUri.getLastPathSegment());
		return calId;
	}
	
	
	/**
	 * Check if a calendar with a given name exists. If it is, then
	 * return the calendar id. Otherwise, do nothing
	 * @param context
	 * @return calId;
	 */
	public static long getCalendar(Context context) {
		String[] projection = new String[] {Calendars._ID, Calendars.NAME};

		final String uid = SharedPreference.getPrefStringValue(context, Preference.UID);
		ContentResolver cr = context.getContentResolver();
		Cursor c = cr.query(Calendars.CONTENT_URI, projection, null, null, null);
		if (c.moveToFirst()) {
			while (c.moveToNext()) {
				String calName = c.getString(1);
				if (calName != null && calName.equals(uid)) {
					long calId = c.getLong(0);
					c.close();
					return calId;
				}
			}
		}
		c.close();
		return -1;
	}
	
	/**
	 * Prepare the sync calendar. If none exist, then create a new one
	 * @param context
	 * @return calId
	 */
	public static long prepareSyncCalendar(Context context) {
		if (getCalendar(context) != -1) return getCalendar(context);
		else return createCalendar(context);
	}
	
	/**
	 * Delete calendar with given calId
	 * @param context
	 * @param calId
	 */
	public static void deleteCalendar(Context context, long calId) {
		ContentResolver cr = context.getContentResolver();
		Uri calUri = ContentUris.withAppendedId(Calendars.CONTENT_URI, calId);
		cr.delete(calUri, null, null);
	}
	
	/**
	 * Create a new event content value to be add into the calendar
	 * @param context
	 * @param fbEvent
	 * @return ContentValues
	 */
	private static ContentValues prepareEventContentValues(Context context, FbEvent event, long calId) {
		
		ContentValues values = new ContentValues();
		values.put(Events.CALENDAR_ID, calId);
		values.put(Events.TITLE, event.getName());
		values.put(Events.DTSTART, event.getStart_time() * 1000L);
		if (event.getEnd_time() != 0) {
			values.put(Events.DTEND, event.getEnd_time() * 1000L);
		} else values.put(Events.DTEND, event.getStart_time() * 1000L + TimeFrame.millisInHour);

		if (!event.getDescription().equals("")) 
			values.put(Events.DESCRIPTION, event.getDescription());
		if (!event.getTimezone().equals("")) {
			values.put(Events.EVENT_TIMEZONE, event.getTimezone());
		} else values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

		if (!event.getLocation().equals("")) {
			values.put(Events.EVENT_LOCATION, event.getLocation());
		}

		boolean hasAlarm = SharedPreference.getPrefBooleanValue(context, Preference.SYNC_EVENT_REMINDERS);
		if (hasAlarm) {
			values.put(Events.HAS_ALARM, true);
		} else values.put(Events.HAS_ALARM, false);

		return values;
	}

	/**
	 * Prepare the reminder content values
	 * @param context
	 * @param the eid
	 * @return ContentValues
	 */
	private static ContentValues prepareEventReminderContentValues(Context context, long eid) {
		ContentValues values = new ContentValues();
		int minutes = SharedPreference.getPrefIntValue(context, Preference.SYNC_EVENT_REMINDERS_TIME);
		values.put(Reminders.MINUTES, minutes);
		values.put(Reminders.EVENT_ID, eid);
		values.put(Reminders.METHOD, Reminders.METHOD_ALERT);
		return values;
	}

	/**
	 * Add an event to calendar
	 * @param context
	 * @param event
	 * @param calId
	 * @return eid
	 */
	public static long addEvent(Context context, FbEvent event, long calId) {
		ContentResolver cr = context.getContentResolver();
		ContentValues contentValues = prepareEventContentValues(context, event, calId);
		Uri uri = cr.insert(Events.CONTENT_URI, contentValues);
		return Long.parseLong(uri.getLastPathSegment());
	}

	/**
	 * add event reminder to the event
	 * @param context
	 * @param eid
	 */
	public static void addEventReminder(Context context, long eid) {
		ContentResolver cr = context.getContentResolver();
		ContentValues reminderValues = prepareEventReminderContentValues(context, eid);
		cr.insert(Reminders.CONTENT_URI, reminderValues);
	}

	/**
	 * disable the reminder of a given event
	 * @param context
	 * @param eid
	 */
	public static void disableReminder(Context context, long eid) {
		ContentResolver cr = context.getContentResolver();
		ContentValues reminder = new ContentValues();
		reminder.put(Events.HAS_ALARM, false);
		Uri updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, eid);
		cr.update(updateUri, reminder, null, null);
	}
	
	/**
	 * enable the reminder of a given event
	 * @param context
	 * @param eid
	 */
	public static void enableeReminder(Context context, long eid) {
		ContentResolver cr = context.getContentResolver();
		ContentValues reminder = new ContentValues();
		reminder.put(Events.HAS_ALARM, true);
		Uri updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, eid);
		cr.update(updateUri, reminder, null, null);
	}

	/**
	 * Update the event
	 * @param context
	 * @param event
	 * @param eid
	 * @param calId
	 */
	public static void updateEvent(Context context, FbEvent event, long eid, long calId) {
		ContentResolver cr = context.getContentResolver();
		ContentValues values = prepareEventContentValues(context, event, calId);
		Uri updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, eid);
		cr.update(updateUri, values, null, null);
	}

	/**
	 * remove an event from calendar
	 * @param context
	 * @param calEid
	 */
	public static void removeEvent(Context context, long eid) {
		ContentResolver cr = context.getContentResolver();
		Uri deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, eid);
		cr.delete(deleteUri, null, null);
	}
}
