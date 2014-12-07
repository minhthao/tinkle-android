package com.hitchlab.tinkle.objects;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class Attendee implements Parcelable{
	private String uid;
	private String name;
	
	public static final Parcelable.Creator<Attendee> CREATOR = new Parcelable.Creator<Attendee>() {
		public Attendee createFromParcel(Parcel in) {
			return new Attendee(in);
		}

		public Attendee[] newArray(int size) {
			return new Attendee[size];
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
	}

	/**
	 * Constructor using parcel
	 * @param in
	 */
	public Attendee(Parcel in) {
		this.uid = in.readString();
		this.name = in.readString();
	}
	
	/**
	 * @param uid
	 * @param name
	 */
	public Attendee(String uid, String name) {
		this.uid = uid;
		this.name = name;
	}
	
	/**
	 * @param string
	 */
	public Attendee(String concatStr) {
		String[] uidAndName = concatStr.split(",");
		if (uidAndName.length >= 2) {
			this.uid = uidAndName[0];
			this.name =  uidAndName[1];
		}
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

	@Override
	public String toString() {
		return uid + "," + name;
	}
	
	/**
	 * Get the attendees str to store into the local db
	 * @param attendees
	 * @return attendees str
	 */
	public static String getAttendeesStrFromList(ArrayList<Attendee> attendees) {
		if (attendees == null || attendees.size() == 0) return "";
		else {
			ArrayList<String> attendeesStr = new ArrayList<String>();
			for (Attendee attendee : attendees) attendeesStr.add(attendee.toString());
			String atet = TextUtils.join("|", attendeesStr);
			return atet;
		}
	}
	
	/**
	 * Get the attendees list from the str that retrieved from local db
	 * @param str
	 * @return
	 */
	public static ArrayList<Attendee> getAttendeesListFromStr(String str) {
		ArrayList<Attendee> attendees = new ArrayList<Attendee>();
		if (str.equals("")) return attendees;
		else {
			String[] attendeesStr = str.split("\\|");
			for (int i = 0; i < attendeesStr.length; i++) 
				attendees.add(new Attendee(attendeesStr[i]));
			return attendees;
		}
	}
}
