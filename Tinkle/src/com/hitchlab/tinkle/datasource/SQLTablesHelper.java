package com.hitchlab.tinkle.datasource;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class SQLTablesHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "event.db";
	public static final int D_VERSION = 2;

	public static final String CALENDAR_EVENT_TABLE_NAME = "CalendarEventTable";
	public static final String CALENDAR_EVENT_CALENDAR_ID = "CID";
	public static final String CALENDAR_EVENT_FB_ID = "FBID";
	public static final String CALENDAR_EVENT_LAST_MODIFIED = "lastModified";
	public static final String CALENDAR_EVENT_START_TIME = "startTime";

	public static final String FRIEND_EVENT_TABLE_NAME = "EventFeedTable";
	public static final String FRIEND_EVENT_EID = "EID";
	public static final String FRIEND_EVENT_NAME = "Name";
	public static final String FRIEND_EVENT_PICTURE = "Picture";
	public static final String FRIEND_EVENT_START_TIME = "StartTime";
	public static final String FRIEND_EVENT_END_TIME = "EndTime";
	public static final String FRIEND_EVENT_LOCATION = "Location";
	public static final String FRIEND_EVENT_LONGITUDE = "Longitude";
	public static final String FRIEND_EVENT_LATITUDE = "Latitude";
	public static final String FRIEND_EVENT_FRIENDS_ATTENDING = "FriendsAttending";
	
	public static final String MY_EVENT_TABLE_NAME = "MyEventTable";
	public static final String MY_EVENT_EID = "EID";
	public static final String MY_EVENT_NAME = "Name";
	public static final String MY_EVENT_PICTURE = "Picture";
	public static final String MY_EVENT_RSVP = "Rsvp";
	public static final String MY_EVENT_START_TIME = "StartTime";
	public static final String MY_EVENT_END_TIME = "EndTime";
	public static final String MY_EVENT_LOCATION = "Location";
	public static final String MY_EVENT_LONGITUDE = "Longitude";
	public static final String MY_EVENT_LATITUDE = "Latitude";
	public static final String MY_EVENT_FRIENDS_ATTENDING = "FriendsAttending";
	
	public static final String PUBLIC_EVENT_TABLE_NAME = "PublicEventTable";
	public static final String PUBLIC_EVENT_EID = "EID";
	public static final String PUBLIC_EVENT_NAME = "Name";
	public static final String PUBLIC_EVENT_PICTURE = "Picture";
	public static final String PUBLIC_EVENT_START_TIME = "StartTime";
	public static final String PUBLIC_EVENT_END_TIME = "EndTime";
	public static final String PUBLIC_EVENT_LOCATION = "Location";
	public static final String PUBLIC_EVENT_LONGITUDE = "Longitude";
	public static final String PUBLIC_EVENT_LATITUDE = "Latitude";
	public static final String PUBLIC_EVENT_HOST = "Host";
	
	public static final String SHARED_EVENT_TABLE_NAME = "SharedEventTable";
	public static final String SHARED_EVENT_FROM_UID = "fromUid";
	public static final String SHARED_EVENT_FROM_NAME = "fromName";
	public static final String SHARED_EVENT_TIME_POST = "timePost";
	public static final String SHARED_EVENT_EID = "eid";
	public static final String SHARED_EVENT_NAME = "name";
	public static final String SHARED_EVENT_PICTURE = "picture";
	public static final String SHARED_EVENT_START_TIME = "startTime";
	public static final String SHARED_EVENT_END_TIME = "endTime";
	public static final String SHARED_EVENT_LOCATION = "location";
	public static final String SHARED_EVENT_LONGITUDE = "longitude";
	public static final String SHARED_EVENT_LATITUDE = "latitude";
	public static final String SHARED_EVENT_HOST = "host";
	
	public static final String ATTENDEE_TABLE_NAME = "UserTable";
	public static final String ATTENDEE_UID = "UID";
	public static final String ATTENDEE_EID = "EID";
	
	public static final String FRIEND_TABLE_NAME = "FriendTable";
	public static final String FRIEND_UID = "UID";
	public static final String FRIEND_NAME = "Name";
	public static final String FRIEND_NUM_EVENTS = "NumEvents";
	
	public static final String NOTIFICATION_TABLE_NAME = "NotificationTable";
	public static final String NOTIFICATION_UID = "uid";
	public static final String NOTIFICATION_TYPE = "type";
	public static final String NOTIFICATION_MESSAGE = "message";
	public static final String NOTIFICATION_MESSAGE_EXTRA1 = "message_extra1";
	public static final String NOTIFICATION_MESSAGE_EXTRA2 = "message_extra2";
	public static final String NOTIFICATION_EXTRA_INFO = "extra_info";
	public static final String NOTIFICATION_VIEWED = "viewed";
	public static final String NOTIFICATION_TIME = "time";
	public static final String NOTIFICATION_CLICKED = "clicked";
	
	public static final String SHARE_TABLE_NAME = "ShareTableName";
	public static final String SHARE_EID = "EID";
	
	public static final String VIEW_TABLE_NAME = "ViewTableName";
	public static final String VIEW_EID = "EID";
	
	private final String TABLE_NOTIFICATION_CREATE = "create table if not exists " + NOTIFICATION_TABLE_NAME + "( "
			+ NOTIFICATION_UID + " text, "
			+ NOTIFICATION_TYPE + " integer, "
			+ NOTIFICATION_MESSAGE + " text, " 
			+ NOTIFICATION_MESSAGE_EXTRA1 + " text, "
			+ NOTIFICATION_MESSAGE_EXTRA2 + " text, "
			+ NOTIFICATION_EXTRA_INFO + " text, "
			+ NOTIFICATION_VIEWED + " integer, "
			+ NOTIFICATION_TIME + " integer, "
			+ NOTIFICATION_CLICKED + " integer);";
	
	private final String TABLE_CALENDAR_EVENT_CREATE = "create table if not exists " + CALENDAR_EVENT_TABLE_NAME + "( "
			+ CALENDAR_EVENT_CALENDAR_ID + " integer, " 
			+ CALENDAR_EVENT_FB_ID + " text, " 
			+ CALENDAR_EVENT_LAST_MODIFIED + " integer, " 
			+ CALENDAR_EVENT_START_TIME + " integer);";

	private final String TABLE_FRIEND_EVENT_CREATE = "create table if not exists " + FRIEND_EVENT_TABLE_NAME + "( "
			+ FRIEND_EVENT_EID + " text, "
			+ FRIEND_EVENT_NAME + " text, " 
			+ FRIEND_EVENT_PICTURE + " text, "
			+ FRIEND_EVENT_START_TIME + " integer, "
			+ FRIEND_EVENT_END_TIME + " integer, "
			+ FRIEND_EVENT_LOCATION + " text, "
			+ FRIEND_EVENT_LONGITUDE + " real, "
			+ FRIEND_EVENT_LATITUDE + " real, "
			+ FRIEND_EVENT_FRIENDS_ATTENDING + " text);";

	private final String TABLE_MY_EVENT_CREATE = "create table if not exists " + MY_EVENT_TABLE_NAME + "( "
			+ MY_EVENT_EID + " text, "
			+ MY_EVENT_NAME + " text, "
			+ MY_EVENT_PICTURE + " text, "
			+ MY_EVENT_RSVP + " text, "
			+ MY_EVENT_START_TIME + " integer, "
			+ MY_EVENT_END_TIME + " integer, "
			+ MY_EVENT_LOCATION + " text, "
			+ MY_EVENT_LONGITUDE + " real, "
			+ MY_EVENT_LATITUDE + " real, "
			+ MY_EVENT_FRIENDS_ATTENDING + " text);";
	
	private final String TABLE_PUBLIC_EVENT_CREATE = "create table if not exists " + PUBLIC_EVENT_TABLE_NAME + "( "
			+ PUBLIC_EVENT_EID + " text,"
			+ PUBLIC_EVENT_NAME + " text, "
			+ PUBLIC_EVENT_PICTURE + " text, "
			+ PUBLIC_EVENT_START_TIME + " integer, "
			+ PUBLIC_EVENT_END_TIME + " integer, "
			+ PUBLIC_EVENT_LOCATION + " text, "
			+ PUBLIC_EVENT_LONGITUDE + " real, "
			+ PUBLIC_EVENT_LATITUDE + " real, "
			+ PUBLIC_EVENT_HOST + " text);";
	
	private final String TABLE_SHARED_EVENT_CREATE = "create table if not exists " + SHARED_EVENT_TABLE_NAME + "( "
			+ SHARED_EVENT_FROM_UID + " text, "
			+ SHARED_EVENT_FROM_NAME + " text, "
			+ SHARED_EVENT_EID + " text, "
			+ SHARED_EVENT_NAME + " text, "
			+ SHARED_EVENT_PICTURE + " text, "
			+ SHARED_EVENT_START_TIME + " integer, "
			+ SHARED_EVENT_END_TIME + " integer, "
			+ SHARED_EVENT_LOCATION + " text, "
			+ SHARED_EVENT_LONGITUDE + " real, "
			+ SHARED_EVENT_LATITUDE + " real, "
			+ SHARED_EVENT_HOST + " text);";
	
	private final String TABLE_ATTENDEE_CREATE = "create table if not exists " + ATTENDEE_TABLE_NAME + "( "
			+ ATTENDEE_UID + " text, "
			+ ATTENDEE_EID + " text);";

	private final String TABLE_FRIEND_CREATE = "create table if not exists " + FRIEND_TABLE_NAME + "( "
			+ FRIEND_UID + " text, "
			+ FRIEND_NAME + " text, "
			+ FRIEND_NUM_EVENTS + " integer);";
	
	private final String TABLE_VIEW_CREATE = "create table if not exists " + VIEW_TABLE_NAME + "( " + VIEW_EID + " text);";
	
	private final String TABLE_SHARE_CREATE = "create table if not exists " + SHARE_TABLE_NAME + "( " + SHARE_EID + " text);";
	
	//default constructor
	public SQLTablesHelper(Context context) {
		super(context, DATABASE_NAME, null, D_VERSION);
	}

	//constructor, very self-explanatory
	public SQLTablesHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CALENDAR_EVENT_CREATE);
		db.execSQL(TABLE_FRIEND_EVENT_CREATE);
		db.execSQL(TABLE_MY_EVENT_CREATE);
		db.execSQL(TABLE_FRIEND_CREATE);
		db.execSQL(TABLE_ATTENDEE_CREATE);
		db.execSQL(TABLE_PUBLIC_EVENT_CREATE);
		db.execSQL(TABLE_NOTIFICATION_CREATE);
		db.execSQL(TABLE_VIEW_CREATE);
		db.execSQL(TABLE_SHARE_CREATE);
		db.execSQL(TABLE_SHARED_EVENT_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}
}
