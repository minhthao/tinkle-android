package com.hitchlab.tinkle.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.facebook.Session;
import com.hitchlab.tinkle.calendar.CalendarHelper;
import com.hitchlab.tinkle.calendar.EventCalendarItem;
import com.hitchlab.tinkle.datasource.CalendarEventDataSource;
import com.hitchlab.tinkle.fbquery.QuerySyncEvents;
import com.hitchlab.tinkle.objects.FbEvent;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;
import com.hitchlab.tinkle.supports.Internet;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
public class CheckSyncCalendarService extends IntentService {
	
	public static final String TYPE = "type";
	public static final String TYPE_UPDATE_ALARM = "updateAlarm";
	public static final String TYPE_REGULAR_SERVICE = "regularService";
	public static final String TYPE_ADD_EVENT = "addEvent";
	public static final String TYPE_REMOVE_EVENT = "removeEvent";
	
	public static final String EVENT_DATA = "eventData";
	public static final String ALARM_ON_OFF = "alarm";

	private Context context;
	private QuerySyncEvents querySyncEvents; 
	private CalendarEventDataSource calendarEventDataSource;
	private long calId;
	
	public CheckSyncCalendarService() {
		super("CheckSyncCalendarService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		this.context = this;
		this.calendarEventDataSource= new CalendarEventDataSource(context);
		if (SharedPreference.containKey(context, Preference.SYNC)) {
			String intentType = intent.getStringExtra(TYPE);
			if (intentType != null) {
				if (intentType.equals(TYPE_REGULAR_SERVICE)) doRegularService();
				if (intentType.equals(TYPE_UPDATE_ALARM)) updateAlarm(intent);
				if (intentType.equals(TYPE_ADD_EVENT)) addEvent(intent);
				if (intentType.equals(TYPE_REMOVE_EVENT)) removeEvent(intent);
			}
		}
	}
	
	/**
	 * Remove an event from the phone calendar
	 * @param intent
	 */
	private void removeEvent(Intent intent) {
		if (SharedPreference.getPrefBooleanValue(this, Preference.SYNC)) {
			FbEvent event = intent.getParcelableExtra(EVENT_DATA);
			EventCalendarItem calendarItem = calendarEventDataSource.getEventItem(event.getId());
			if (calendarItem != null) {
				CalendarHelper.removeEvent(context, calendarItem.getCalendarEid());
				calendarEventDataSource.removeEventWithFbId(calendarItem.getEid());
			}
		}
	}
	
	/**
	 * Add an event to the phone calendar
	 * @param intent
	 */
	private void addEvent(Intent intent) {
		if (SharedPreference.getPrefBooleanValue(this, Preference.SYNC)) {
			this.calId = CalendarHelper.prepareSyncCalendar(context);
			FbEvent event = intent.getParcelableExtra(EVENT_DATA);
			addEvent(event);
		}
	}
	
	/**
	 * Update the alarm by either turn it on or off
	 * @param intent
	 */
	private void updateAlarm(Intent intent) {
		boolean alarm = intent.getBooleanExtra(ALARM_ON_OFF, false);
		ArrayList<EventCalendarItem> items = calendarEventDataSource.getFutureEventItems();
		for (EventCalendarItem item : items) {
			if (alarm) CalendarHelper.disableReminder(context, item.getCalendarEid());
			else {
				CalendarHelper.addEventReminder(context, item.getCalendarEid());
				CalendarHelper.enableeReminder(context, item.getCalendarEid());
			}
		}
	}
	
	/**
	 * Do the regular service that will check with fb server that our calendar is up to date
	 */
	private void doRegularService() {
		boolean hasInternet = Internet.hasActiveInternetConnection(context);
		if (hasInternet && SharedPreference.getPrefBooleanValue(this, Preference.SYNC)) {
			Session session = Session.getActiveSession();
			if (session == null) 
				try {
					session = Session.openActiveSessionFromCache(context);
				} catch (UnsupportedOperationException e) {
					Log.e("fbSession", "error open fb session");
				}
			if (session != null && session.isOpened()) {
				this.calId = CalendarHelper.prepareSyncCalendar(context);
				initQuerySyncEvents();
				querySyncEvents.queryEvents(session);
				SharedPreference.updateSharedPref(context, Preference.SYNC_UPDATE_TIME, System.currentTimeMillis());
			}
		}
	}
	
	
	/**
	 * Init the query sync events
	 */
	private void initQuerySyncEvents() {
		querySyncEvents = new QuerySyncEvents() {
			@Override
			public void onQueryCompleted(ArrayList<FbEvent> events) {
				Map<String, FbEvent> eidToEvent = new HashMap<String, FbEvent>();
				for (int i = 0; i < events.size(); i++) {
					FbEvent event = events.get(i);
					eidToEvent.put(event.getId(), event);
					addEvent(event);
				}
				
				//we first remove all the events that is no longer active and in sync
				ArrayList<EventCalendarItem> calendarItems = calendarEventDataSource.getFutureEventItems();
				for (EventCalendarItem calendarItem : calendarItems) {
					if (!eidToEvent.containsKey(calendarItem.getEid())) {
						CalendarHelper.removeEvent(context, calendarItem.getCalendarEid());
						calendarEventDataSource.removeEventWithFbId(calendarItem.getEid());
					}
				}
			}
		};
	}
	
	/**
	 * add an event to to calendar
	 * @param FbEvent
	 */
	private void addEvent(FbEvent event) {
		//check if event exist in the calendar
		EventCalendarItem calendarItem = calendarEventDataSource.getEventItem(event.getId());
		if (calendarItem != null) {
			if (calendarItem.getLastModified() != event.getUpdated_time()) {
				CalendarHelper.updateEvent(context, event, calendarItem.getCalendarEid(), calId);
				calendarEventDataSource.updateEvent(event);
			}
		} else {
			long calEid = CalendarHelper.addEvent(context, event, calId);
			EventCalendarItem myItem = new EventCalendarItem();
			myItem.setCalendarEid(calEid);
			myItem.setLastModified(event.getUpdated_time());
			myItem.setStartTime(event.getStart_time() * 1000L);
			myItem.setEid(event.getId());
			calendarEventDataSource.addEvent(myItem);
			
			//check if event reminder is needed
			boolean reminder = SharedPreference.getPrefBooleanValue(context, Preference.SYNC_EVENT_REMINDERS);
			if (reminder) CalendarHelper.addEventReminder(context, calEid);
		}
	}

}
