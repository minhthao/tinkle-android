package com.hitchlab.tinkle.dbrequest;

public class Table {
	public static final String FOLLOW_TABLE_NAME = "follow";
	public static final String FOLLOW_FROM_ID = "fromId";
	public static final String FOLLOW_TO_ID = "toId";
	public static final String FOLLOW_VISIBILITY = "visibility";
	public static final String FOLLOW_FROM_NAME = "fromName";
	public static final String FOLLOW_TO_NAME = "toName";
	
	public static final String USER_TABLE_NAME = "user";
	public static final String USER_REG_ID = "regId";
	public static final String USER_UID = "uid";
	public static final String USER_USERNAME = "username";
	public static final String USER_NUM_USER_EVENTS = "numEvents";
	public static final String USER_NUM_FRIENDS_EVENTS = "numFriendsEvent";
	public static final String USER_TIMESTAMP = "timestamp";
	
	public static final String DEVICE_TABLE_NAME = "device";
	public static final String DEVICE_ID = "deviceId";
	public static final String DEVICE_REG_ID = "regId";
	public static final String DEVICE_UID = "uid";
	
	public static final String FBUSER_TABLE_NAME = "fbuser";
	public static final String FBUSER_UID = "uid";
	public static final String FBUSER_NAME = "name";
	public static final String FBUSER_INFO = "info";
	
	public static final String EVENT_TABLE_NAME = "event";
	public static final String EVENT_EID = "eid";
	public static final String EVENT_NAME = "name";
	public static final String EVENT_PICTURE = "picture";
	public static final String EVENT_START_TIME = "start_time";
	public static final String EVENT_END_TIME = "end_time";
	public static final String EVENT_PRIVACY = "privacy";
	public static final String EVENT_LOCATION = "location";
	public static final String EVENT_LONGITUDE = "longitude";
	public static final String EVENT_LATITUDE = "latitude";
	public static final String EVENT_NUM_INTERESTS = "num_interest";
	public static final String EVENT_TIMESTAMP = "timestamp";
	public static final String EVENT_HOST = "host";
	public static final String EVENT_DISTANCE = "distance";
	
	public static final String SHARED_EVENT_FROM_UID = "fromUid";
	public static final String SHARED_EVENT_FROM_NAME = "fromName";
	public static final String SHARED_EVENT_TO_UID = "toUid";
	public static final String SHARED_EVENT_TIME_POST = "timePost";
	
	public static final String NOTIFICATION_UID = "uid";
	public static final String NOTIFICATION_TYPE = "type";
	public static final String NOTIFICATION_MESSAGE = "message";
	public static final String NOTIFICATION_MESSAGE_EXTRA1 = "message_extra1";
	public static final String NOTIFICATION_MESSAGE_EXTRA2 = "message_extra2";
	public static final String NOTIFICATION_EXTRA_INFO = "extra_info";
	public static final String NOTIFICATION_VIEWED = "viewed";
	public static final String NOTIFICATION_TIME = "time";
	
	public static final String PUBLIC_EVENT_TIME_FRAME_BEGIN = "begin";
	public static final String PUBLIC_EVENT_TIME_FRAME_END = "end";
	public static final String PUBLIC_EVENT_LOWER_LONGITUDE = "lower_long";
	public static final String PUBLIC_EVENT_UPPER_LONGITUDE = "upper_long";
	public static final String PUBLIC_EVENT_LOWER_LATITUDE = "lower_latitude";
	public static final String PUBLIC_EVENT_UPPER_LATITUDE = "upper_latitude";
}
