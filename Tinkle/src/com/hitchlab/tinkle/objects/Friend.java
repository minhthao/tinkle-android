package com.hitchlab.tinkle.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Friend implements Parcelable{
	private String uid;
	private String name;
	private int numOngoingEvents;
	
	public static final Parcelable.Creator<Friend> CREATOR = new Parcelable.Creator<Friend>() {
		@Override
		public Friend createFromParcel(Parcel source) {
			return new Friend(source);
		}

		@Override
		public Friend[] newArray(int size) {
			return new Friend[size];
		}	
	};
	
	@Override
	public int describeContents() {
		return hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(uid);
		dest.writeString(name);
		dest.writeInt(numOngoingEvents);
	}
	
	/**
	 * Constructor using parcel
	 * @param parcel
	 */
	public Friend(Parcel in) {
		this.uid = in.readString();
		this.name = in.readString();
		this.numOngoingEvents = in.readInt();
	}
	
	/**
	 * Default empty constructor
	 */
	public Friend() {
		this.uid = "";
		this.name = "";
		this.numOngoingEvents = 0;
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
	 * @return the numOngoingEvents
	 */
	public int getNumOngoingEvents() {
		return numOngoingEvents;
	}
	/**
	 * @param numOngoingEvents the numOngoingEvents to set
	 */
	public void setNumOngoingEvents(int numOngoingEvents) {
		this.numOngoingEvents = numOngoingEvents;
	}
}
