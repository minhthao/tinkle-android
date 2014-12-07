package com.hitchlab.tinkle.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable{
	
	private String photoUrl;
	private String comment;
	
	public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
		public Photo createFromParcel(Parcel in) {
			return new Photo(in);
		}

		public Photo[] newArray(int size) {
			return new Photo[size];
		}
	};
	
	@Override
	public int describeContents() {
		return hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(photoUrl);
		dest.writeString(comment);
	}
	
	/**
	 * Constructor using parcel
	 * @param in
	 */
	public Photo(Parcel in) {
		this.photoUrl = in.readString();
		this.comment = in.readString();
	}
	
	/**
	 * Empty constructor
	 */
	public Photo() {
		super();
		this.photoUrl = "";
		this.comment = "";
	}
	
	/**
	 * @param photoUrl
	 * @param comment
	 */
	public Photo(String photoUrl, String comment) {
		super();
		this.photoUrl = photoUrl;
		this.comment = comment;
	}
	/**
	 * @return the photoUrl
	 */
	public String getPhotoUrl() {
		return photoUrl;
	}
	/**
	 * @param photoUrl the photoUrl to set
	 */
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}	
}
