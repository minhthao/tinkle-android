package com.hitchlab.tinkle.objects;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.hitchlab.tinkle.dbrequest.Table;

import android.os.Parcel;
import android.os.Parcelable;

public class FbEvent implements Parcelable{		

	private String id;
	private String name;
	private long start_time;
	private long end_time;
	private String location;
	private String picture;
	private String rsvp_status;
	private String description;
	private String timezone;
	private double venueLongitude;
	private double venueLatitude;
	private long updated_time;
	private ArrayList<Attendee> friendsAttending;
	private int totalInterested;
	private double distance;
	private String privacy;
	private String host;

	public static final Parcelable.Creator<FbEvent> CREATOR = new Parcelable.Creator<FbEvent>() {
		public FbEvent createFromParcel(Parcel in) {
			return new FbEvent(in);
		}

		public FbEvent[] newArray(int size) {
			return new FbEvent[size];
		}
	};
	
	@Override
	public int describeContents() {
		return hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
		dest.writeLong(start_time);
		dest.writeLong(end_time);
		dest.writeString(location);
		dest.writeString(picture);
		dest.writeString(rsvp_status);
		dest.writeString(description);
		dest.writeString(timezone);
		dest.writeDouble(venueLongitude);
		dest.writeDouble(venueLatitude);
		dest.writeLong(updated_time);
		dest.writeTypedList(friendsAttending);
		dest.writeInt(totalInterested);
		dest.writeDouble(distance);
		dest.writeString(privacy);
		dest.writeString(host);
	}

	/**
	 * Constructor using parcel
	 * @param in
	 */
	public FbEvent(Parcel in) {
		this.id = in.readString();
		this.name = in.readString();
		this.start_time = in.readLong();
		this.end_time = in.readLong();
		this.location = in.readString();
		this.picture = in.readString();
		this.rsvp_status = in.readString();
		this.description = in.readString();
		this.timezone = in.readString();
		this.venueLongitude = in.readDouble();
		this.venueLatitude = in.readDouble();
		this.updated_time = in.readLong();
		this.friendsAttending = new ArrayList<Attendee>();
		in.readTypedList(friendsAttending, Attendee.CREATOR);
		this.totalInterested = in.readInt();
		this.distance = in.readDouble();
		this.privacy = in.readString();
		this.host = in.readString();
	}
	
	/**
	 * Empty constructor
	 */
	public FbEvent() {
		super();
		this.id = "";
		this.name = "";
		this.start_time = 0;
		this.end_time = 0;
		this.location = "";
		this.picture = "";
		this.rsvp_status = "";
		this.description = "";
		this.timezone = "";
		this.venueLongitude = 0;
		this.venueLatitude = 0;
		this.updated_time = 0;
		this.friendsAttending = new ArrayList<Attendee>();
		this.totalInterested = 0;
		this.distance = -1;
		this.privacy = "";
		this.host = "";
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return distance to event
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * @param distance
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}

	/**
	 * @return the attending friend
	 */
	public ArrayList<Attendee> getFriendsAttending() {
		return friendsAttending;
	}

	/**
	 * @param friendsAttending
	 */
	public void setFriendsAttending(ArrayList<Attendee> friendsAttending) {
		this.friendsAttending = friendsAttending;
	}

	/**
	 * @return the totalInterested
	 */
	public int getTotalInterested() {
		return totalInterested;
	}

	/**
	 * @param totalInterested the totalInterested to set
	 */
	public void setTotalInterested(int totalInterested) {
		this.totalInterested = totalInterested;
	}

	/**
	 * @return the updateTime
	 */
	public long getUpdated_time() {
		return updated_time;
	}

	/**
	 * @param updateTime the updateTime to set
	 */
	public void setUpdated_time(long updated_time) {
		this.updated_time = updated_time;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
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
	 * @return the start_time
	 */
	public long getStart_time() {
		return start_time;
	}

	/**
	 * @param start_time the start_time to set
	 */
	public void setStart_time(long start_time) {
		this.start_time = start_time;
	}

	/**
	 * @return the end_time
	 */
	public long getEnd_time() {
		return end_time;
	}

	/**
	 * @param end_time the end_time to set
	 */
	public void setEnd_time(long end_time) {
		this.end_time = end_time;
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
	 * @return the rsvp_status
	 */
	public String getRsvp_status() {
		return rsvp_status;
	}

	/**
	 * @param rsvp_status the rsvp_status to set
	 */
	public void setRsvp_status(String rsvp_status) {
		this.rsvp_status = rsvp_status;
	}

	/**
	 * @return the timezone
	 */
	public String getTimezone() {
		return timezone;
	}

	/**
	 * @param timezone the timezone to set
	 */
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	/**
	 * @return the latitude
	 */
	public double getVenueLatitude() {
		return venueLatitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setVenueLatitude(double latitude) {
		this.venueLatitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getVenueLongitude() {
		return venueLongitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setVenueLongitude(double longitude) {
		this.venueLongitude = longitude;
	}

	/**
	 * @return the privacy
	 */
	public String getPrivacy() {
		return privacy;
	}

	/**
	 * @param privacy the privacy to set
	 */
	public void setPrivacy(String privacy) {
		this.privacy = privacy;
	}
	
	/**
	 * Write the object to JSON
	 */
	public JSONObject toJSON() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put(Table.EVENT_EID, id);
		obj.put(Table.EVENT_NAME, name);
		obj.put(Table.EVENT_PICTURE, picture);
		obj.put(Table.EVENT_START_TIME, start_time);
		obj.put(Table.EVENT_END_TIME, end_time);
		obj.put(Table.EVENT_PRIVACY, privacy);
		obj.put(Table.EVENT_LOCATION, location);
		obj.put(Table.EVENT_LONGITUDE, venueLongitude);
		obj.put(Table.EVENT_LATITUDE, venueLatitude);
		obj.put(Table.EVENT_NUM_INTERESTS, totalInterested);
		obj.put(Table.EVENT_HOST, host);
		return obj;
	}
	
	/**
	 * Get the event from JSON
	 * @param JSON
	 * @return FbEvent
	 */
	public static FbEvent fromJSON(JSONObject obj) throws JSONException {
		FbEvent event = new FbEvent();
		event.setId(obj.getString(Table.EVENT_EID));
		event.setName(obj.getString(Table.EVENT_NAME));
		event.setPicture(obj.getString(Table.EVENT_PICTURE));
		event.setStart_time(obj.getLong(Table.EVENT_START_TIME));
		event.setEnd_time(obj.getLong(Table.EVENT_END_TIME));
		event.setPrivacy(obj.getString(Table.EVENT_PRIVACY));
		event.setLocation(obj.getString(Table.EVENT_LOCATION));
		event.setVenueLongitude(obj.getDouble(Table.EVENT_LONGITUDE));
		event.setVenueLatitude(obj.getDouble(Table.EVENT_LATITUDE));
		event.setTotalInterested(obj.getInt(Table.EVENT_NUM_INTERESTS));
		event.setHost(obj.getString(Table.EVENT_HOST));
		return event;
	}
	
}