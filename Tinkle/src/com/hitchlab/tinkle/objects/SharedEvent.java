package com.hitchlab.tinkle.objects;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hitchlab.tinkle.dbrequest.Table;

import android.os.Parcel;
import android.os.Parcelable;

public class SharedEvent implements Parcelable{
	private String fromUid;
	private String fromName;
	private String timePost;
	
	private String eid;
	private String name;
	private String picture;
	private long startTime;
	private long endTime;
	private String location;
	private double longitude;
	private double latitude;
	private String host;
	
	public static final Parcelable.Creator<SharedEvent> CREATOR = new Parcelable.Creator<SharedEvent>() {
		@Override
		public SharedEvent createFromParcel(Parcel in) {
			return new SharedEvent(in);
		}

		@Override
		public SharedEvent[] newArray(int size) {
			return new SharedEvent[size];
		}
	};
	
	@Override
	public int describeContents() {
		return hashCode();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(fromUid);
		dest.writeString(fromName);
		dest.writeString(timePost);
		dest.writeString(eid);
		dest.writeString(name);
		dest.writeString(picture);
		dest.writeLong(startTime);
		dest.writeLong(endTime);
		dest.writeString(location);
		dest.writeDouble(longitude);
		dest.writeDouble(latitude);
		dest.writeString(host);
	}
	
	/**
	 * Construct from parcel
	 * @param Parcel
	 */
	public SharedEvent(Parcel in) {
		this.fromUid = in.readString();
		this.fromName = in.readString();
		this.timePost = in.readString();
		this.eid = in.readString();
		this.name = in.readString();
		this.picture = in.readString();
		this.startTime = in.readLong();
		this.endTime = in.readLong();
		this.location = in.readString();
		this.longitude = in.readDouble();
		this.latitude = in.readDouble();
		this.host = in.readString();
	}
	
	/**
	 * Empty constructor
	 */
	public SharedEvent() {
		this.fromUid = "";
		this.fromName = "";
		this.timePost = "";
		this.eid = "";
		this.name = "";
		this.picture = "";
		this.startTime = 0;
		this.endTime = 0;
		this.location = "";
		this.longitude = 0;
		this.latitude = 0;
		this.host = "";
	}

	/**
	 * @return the fromUid
	 */
	public String getFromUid() {
		return fromUid;
	}

	/**
	 * @param fromUid the fromUid to set
	 */
	public void setFromUid(String fromUid) {
		this.fromUid = fromUid;
	}

	/**
	 * @return the fromName
	 */
	public String getFromName() {
		return fromName;
	}

	/**
	 * @param fromName the fromName to set
	 */
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	/**
	 * @return the timePost
	 */
	public String getTimePost() {
		return timePost;
	}

	/**
	 * @param timePost the timePost to set
	 */
	public void setTimePost(String timePost) {
		this.timePost = timePost;
	}

	/**
	 * @return the eid
	 */
	public String getEid() {
		return eid;
	}

	/**
	 * @param eid the eid to set
	 */
	public void setEid(String eid) {
		this.eid = eid;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the picture
	 */
	public String getPicture() {
		return picture;
	}

	/**
	 * @param picture the picture to set
	 */
	public void setPicture(String picture) {
		this.picture = picture;
	}

	/**
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public long getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Write the object to JSON file
	 * @return JSONObject
	 * @throws JSONException 
	 */
	public JSONObject toJSON(ArrayList<String> targetUids) throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put(Table.SHARED_EVENT_FROM_UID, fromUid);
		obj.put(Table.SHARED_EVENT_FROM_NAME, fromName);
		obj.put(Table.EVENT_EID, eid);
		obj.put(Table.EVENT_NAME, name);
		obj.put(Table.EVENT_PICTURE, picture);
		obj.put(Table.EVENT_START_TIME, startTime);
		obj.put(Table.EVENT_END_TIME, endTime);
		obj.put(Table.EVENT_LOCATION, location);
		obj.put(Table.EVENT_LONGITUDE, longitude);
		obj.put(Table.EVENT_LATITUDE, latitude);
		obj.put(Table.EVENT_HOST, host);
		JSONArray targetArray = new JSONArray();
		for (String target : targetUids) {
			JSONObject targetObj = new JSONObject();
			targetObj.put(Table.SHARED_EVENT_TO_UID, target);
			targetArray.put(targetObj);
		}
		obj.put(Table.SHARED_EVENT_TO_UID, targetArray);
		return obj;
	}
	
	/**
	 * Get the event from JSON
	 * @param JSON
	 * @return SharedEvent
	 */
	public static SharedEvent fromJSON(JSONObject obj) throws JSONException {
		SharedEvent event = new SharedEvent();
		event.setFromUid(obj.getString(Table.SHARED_EVENT_FROM_UID));
		event.setFromName(obj.getString(Table.SHARED_EVENT_FROM_NAME));
		event.setTimePost(obj.getString(Table.SHARED_EVENT_TIME_POST));
		event.setEid(obj.getString(Table.EVENT_EID));
		event.setName(obj.getString(Table.EVENT_NAME));
		event.setPicture(obj.getString(Table.EVENT_PICTURE));
		event.setStartTime(obj.getLong(Table.EVENT_START_TIME));
		event.setEndTime(obj.getLong(Table.EVENT_END_TIME));
		event.setLocation(obj.getString(Table.EVENT_LOCATION));
		event.setLongitude(obj.getDouble(Table.EVENT_LONGITUDE));
		event.setLatitude(obj.getDouble(Table.EVENT_LATITUDE));
		event.setHost(obj.getString(Table.EVENT_HOST));
		return event;
	}
}
