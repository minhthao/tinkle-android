package com.hitchlab.tinkle.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class RecommendUser implements Parcelable{
	private String uid;
	private String username;
	private String gender;
	private String rsvpStatus;
	private int numMutualFriend;
	private boolean isFriend;

	public static final Parcelable.Creator<RecommendUser> CREATOR = new Parcelable.Creator<RecommendUser>() {
		public RecommendUser createFromParcel(Parcel in) {
			return new RecommendUser(in);
		}

		public RecommendUser[] newArray(int size) {
			return new RecommendUser[size];
		}
	};
	
	@Override
	public int describeContents() {
		return hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(uid);
		dest.writeString(username);
		dest.writeString(gender);
		dest.writeString(rsvpStatus);
		dest.writeInt(numMutualFriend);
		dest.writeByte((byte) (isFriend ? 1 : 0));
	}

	/**
	 * Constructor using parcel
	 * @param in
	 */
	public RecommendUser(Parcel in) {
		this.uid = in.readString();
		this.username = in.readString();
		this.gender = in.readString();
		this.rsvpStatus =  in.readString();
		this.numMutualFriend = in.readInt();
		this.isFriend = (in.readByte() != 0);
	}
	/**
	 * Default constructor
	 */
	public RecommendUser() {
		this.uid = "";
		this.username = "";
		this.gender = "";
		this.rsvpStatus = "";
		this.numMutualFriend = 0;
		this.isFriend = false;
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
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * @return the rsvpStatus
	 */
	public String getRsvpStatus() {
		return rsvpStatus;
	}

	/**
	 * @param rsvpStatus the rsvpStatus to set
	 */
	public void setRsvpStatus(String rsvpStatus) {
		this.rsvpStatus = rsvpStatus;
	}

	/**
	 * @return the numMutualFriend
	 */
	public int getNumMutualFriend() {
		return numMutualFriend;
	}

	/**
	 * @param numMutualFriend the numMutualFriend to set
	 */
	public void setNumMutualFriend(int numMutualFriend) {
		this.numMutualFriend = numMutualFriend;
	}

	/**
	 * @return the isFriend
	 */
	public boolean isFriend() {
		return isFriend;
	}

	/**
	 * @param isFriend the isFriend to set
	 */
	public void setFriend(boolean isFriend) {
		this.isFriend = isFriend;
	}
	
}
