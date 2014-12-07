package com.hitchlab.tinkle.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class InviteFriend implements Parcelable{
	private int position;
	private boolean isCheck;
	private String uid;
	private String name;
	
	public static final Parcelable.Creator<InviteFriend> CREATOR = new Parcelable.Creator<InviteFriend>() {
		@Override
		public InviteFriend createFromParcel(Parcel source) {
			return new InviteFriend(source);
		}

		@Override
		public InviteFriend[] newArray(int size) {
			return new InviteFriend[size];
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
		dest.writeInt(position);
		dest.writeByte((byte) (isCheck ? 1 : 0));
	}

	/**
	 * Constructor from Parcel
	 * @param in
	 */
	public InviteFriend(Parcel in) {
		this.uid = in.readString();
		this.name = in.readString();
		this.position = in.readInt();
		this.isCheck = (in.readByte() != 0);
	}

	public InviteFriend() {
		this.uid = "";
		this.name = "";
		this.position = -1;
		this.isCheck = false;
	}

	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * @return the isCheck
	 */
	public boolean isCheck() {
		return isCheck;
	}

	/**
	 * @param isCheck the isCheck to set
	 */
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
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
}