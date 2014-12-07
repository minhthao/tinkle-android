package com.hitchlab.tinkle.preference;

public class Preference {
	public static final String PREF_ID = "frenvent";
	
	public static final String USERNAME = "username";
	public static final String UID = "uid";
	public static final String GENDER = "gender";
	
	public static final String SYNC = "sync";
	public static final String SYNC_EVENT_REMINDERS = "event_reminders";
	public static final String SYNC_EVENT_REMINDERS_TIME = "event_reminders_time";
	public static final String SYNC_UPDATE_TIME = "sync_update_time";
	
	public static final String NOTIFICATIONS = "notifications";
	public static final String NOTIFICATION_EVENT_INVITES = "event_invites";
	public static final String NOTIFICATION_EVENT_ACTIVITIES = "event_activities";
	public static final String NOTIFICATION_FRIENDS = "friends";	
	
	public static final String NOTIFICATION_LAST_VIEWED_TIME = "last_view_notif_time";
	
	public static final String DID_MY_EVENTS_INIT = "did_my_events_init";
	public static final String DID_PAST_EVENTS_INIT = "did_past_events_init";
	public static final String DID_FRIENDS_INIT = "did_friends_init";
	public static final String DID_FRIEND_EVENTS_INIT = "did_friends_event_init";
	public static final String DID_NOTIFICATIONS_INIT = "did_notification_init";
	
	public static final String DID_SHARED_EVENT_INIT = "did_shared_event_init";
	
	public static final String REG_ID = "regId";

	//here is a little special, this shared preference will not get destroyed when logout
	//Simply put. this service will get call when we want to start a repeating update service
	public static final String UPDATE = "update";

}
