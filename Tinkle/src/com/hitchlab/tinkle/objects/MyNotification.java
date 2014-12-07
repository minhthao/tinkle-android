package com.hitchlab.tinkle.objects;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.hitchlab.tinkle.dbrequest.Table;

public class MyNotification implements Parcelable{
	public static final int NUM_TYPE = 10;
	public static final int TYPE_SIMPLE_MESSAGE = 0;
	//public static final int TYPE_FOLLOW_ACTION = 1;
	//public static final int TYPE_RECEIVE_FOLLOW_ACTION = 2;
	public static final int TYPE_EVENT_RSVP = 3;
	public static final int TYPE_FRIEND_RSVP_CHANGE = 4;
	public static final int TYPE_FRIEND_JOIN_EVENT = 5;
	public static final int TYPE_FRIEND_TODAY_EVENTS = 6;
	//public static final int TYPE_FOLLOW_RSVP_CHANGE = 7;
	//public static final int TYPE_FOLLOW_JOIN_EVENT = 8;
	public static final int TYPE_INVITED_EVENT = 9;
	
	
	private String uid;
	private int type;
	private String message;
	private String messageExtra1;
	private String messageExtra2;
	private String extraInfo;
	private boolean viewed;
	private long time;
	
	public static final Parcelable.Creator<MyNotification> CREATOR = new Parcelable.Creator<MyNotification>() {
		@Override
		public MyNotification createFromParcel(Parcel source) {
			return new MyNotification(source);
		}

		@Override
		public MyNotification[] newArray(int size) {
			return new MyNotification[size];
		}	
	};
	
	
	@Override
	public int describeContents() {
		return hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(uid);
		dest.writeInt(type);
		dest.writeString(message);
		dest.writeString(messageExtra1);
		dest.writeString(messageExtra2);
		dest.writeString(extraInfo);
		dest.writeByte((byte) (viewed ? 1 : 0));
		dest.writeLong(time);
	}
	
	/**
	 * constructor using parcel
	 * @param parcel
	 */
	public MyNotification(Parcel in) {
		this.uid = in.readString();
		this.type = in.readInt();
		this.message = in.readString();
		this.messageExtra1 = in.readString();
		this.messageExtra2 = in.readString();
		this.extraInfo = in.readString();
		this.viewed = (in.readByte() != 0);
		this.time = in.readLong();
	}
	
	public MyNotification() {
		this.uid = "";
		this.type = 0;
		this.message = "";
		this.messageExtra1 = "";
		this.messageExtra2 = "";
		this.extraInfo = "";
		this.viewed = false;
		this.time = 0;
	}

	/**
	 * @param type
	 * @param message
	 * @param messageExtra1
	 * @param messageExtra2
	 * @param extraInfo
	 * @param viewed
	 * @param time
	 * @param clicked
	 */
	public MyNotification(String uid, int type, String message, String messageExtra1,
			String messageExtra2, String extraInfo, boolean viewed, long time) {
		super();
		this.uid = uid;
		this.type = type;
		this.message = message;
		this.messageExtra1 = messageExtra1;
		this.messageExtra2 = messageExtra2;
		this.extraInfo = extraInfo;
		this.viewed = viewed;
		this.time = time;
	}

	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the messageExtra1
	 */
	public String getMessageExtra1() {
		return messageExtra1;
	}

	/**
	 * @param messageExtra1 the messageExtra1 to set
	 */
	public void setMessageExtra1(String messageExtra1) {
		this.messageExtra1 = messageExtra1;
	}

	/**
	 * @return the messageExtra2
	 */
	public String getMessageExtra2() {
		return messageExtra2;
	}

	/**
	 * @param messageExtra2 the messageExtra2 to set
	 */
	public void setMessageExtra2(String messageExtra2) {
		this.messageExtra2 = messageExtra2;
	}

	/**
	 * @return the extraInfo
	 */
	public String getExtraInfo() {
		return extraInfo;
	}

	/**
	 * @param extraInfo the extraInfo to set
	 */
	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	/**
	 * @return the viewed
	 */
	public boolean isViewed() {
		return viewed;
	}

	/**
	 * @param viewed the viewed to set
	 */
	public void setViewed(boolean viewed) {
		this.viewed = viewed;
	}

	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}
	
	/**
	 * Write the obj to JSON
	 * @return JSONObject
	 */
	public JSONObject toJSON() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put(Table.NOTIFICATION_UID, uid);
		obj.put(Table.NOTIFICATION_TYPE, type);
		obj.put(Table.NOTIFICATION_MESSAGE, message);
		obj.put(Table.NOTIFICATION_MESSAGE_EXTRA1, messageExtra1);
		obj.put(Table.NOTIFICATION_MESSAGE_EXTRA2, messageExtra2);
		obj.put(Table.NOTIFICATION_EXTRA_INFO, extraInfo);
		obj.put(Table.NOTIFICATION_VIEWED, viewed);
		obj.put(Table.NOTIFICATION_TIME, time);
		return obj;
	}
	
	/**
	 * get the notification from JSON
	 * @param JSON
	 * @return Notification
	 */
	public static MyNotification fromJSON(JSONObject obj) throws JSONException {
		MyNotification notification = new MyNotification();
		notification.setUid(obj.getString(Table.NOTIFICATION_UID));
		notification.setType(obj.getInt(Table.NOTIFICATION_TYPE));
		notification.setMessage(obj.getString(Table.NOTIFICATION_MESSAGE));
		notification.setMessageExtra1(obj.getString(Table.NOTIFICATION_MESSAGE_EXTRA1));
		notification.setMessageExtra2(obj.getString(Table.NOTIFICATION_MESSAGE_EXTRA2));
		notification.setExtraInfo(obj.getString(Table.NOTIFICATION_EXTRA_INFO));
		notification.setViewed(obj.getBoolean(Table.NOTIFICATION_VIEWED));
		notification.setTime(obj.getLong(Table.NOTIFICATION_TIME));
		return notification;
	}
}
