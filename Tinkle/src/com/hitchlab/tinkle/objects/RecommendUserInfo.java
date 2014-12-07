package com.hitchlab.tinkle.objects;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class RecommendUserInfo implements Parcelable{
	private String uid;
	private String name;
	private String coverUrl;
	private boolean canMessage;
	private boolean canPost;
	private ArrayList<Friend> mutualFriends;
	private ArrayList<FbEvent> commonEvents;
	
	public static final Parcelable.Creator<RecommendUserInfo> CREATOR = new Parcelable.Creator<RecommendUserInfo>() {
		@Override
		public RecommendUserInfo createFromParcel(Parcel in) {
			return new RecommendUserInfo(in);
		}

		@Override
		public RecommendUserInfo[] newArray(int size) {
			return new RecommendUserInfo[size];
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
		dest.writeString(coverUrl);
		dest.writeByte((byte) (canMessage ? 1 : 0));
		dest.writeByte((byte) (canPost ? 1 : 0));
		dest.writeTypedList(mutualFriends);
		dest.writeTypedList(commonEvents);
	}
	
	/**
	 * Constructor using parcel
	 * @param in
	 */
	public RecommendUserInfo(Parcel in) {
		this.uid = in.readString();
		this.name = in.readString();
		this.coverUrl = in.readString();
		this.canMessage = (in.readByte() != 0);
		this.canPost = (in.readByte() != 0);
		this.mutualFriends = new ArrayList<Friend>();
		this.commonEvents = new ArrayList<FbEvent>();
		in.readTypedList(mutualFriends, Friend.CREATOR);
		in.readTypedList(commonEvents, FbEvent.CREATOR);
	}
	
	/**
	 * Default empty constructor
	 */
	public RecommendUserInfo() {
		this.uid = "";
		this.name = "";
		this.coverUrl = "";
		this.canMessage = false;
		this.canPost = false;
		this.mutualFriends = new ArrayList<Friend>();
		this.commonEvents = new ArrayList<FbEvent>();
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
	 * @return the coverUrl
	 */
	public String getCoverUrl() {
		return coverUrl;
	}

	/**
	 * @param coverUrl the coverUrl to set
	 */
	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	/**
	 * @return the canMessage
	 */
	public boolean isCanMessage() {
		return canMessage;
	}

	/**
	 * @param canMessage the canMessage to set
	 */
	public void setCanMessage(boolean canMessage) {
		this.canMessage = canMessage;
	}

	/**
	 * @return the canPost
	 */
	public boolean isCanPost() {
		return canPost;
	}

	/**
	 * @param canPost the canPost to set
	 */
	public void setCanPost(boolean canPost) {
		this.canPost = canPost;
	}

	/**
	 * @return the mutualFriends
	 */
	public ArrayList<Friend> getMutualFriends() {
		return mutualFriends;
	}

	/**
	 * @param mutualFriends the mutualFriends to set
	 */
	public void setMutualFriends(ArrayList<Friend> mutualFriends) {
		this.mutualFriends = mutualFriends;
	}

	/**
	 * @return the commonEvents
	 */
	public ArrayList<FbEvent> getCommonEvents() {
		return commonEvents;
	}

	/**
	 * @param commonEvents the commonEvents to set
	 */
	public void setCommonEvents(ArrayList<FbEvent> commonEvents) {
		this.commonEvents = commonEvents;
	}
	
}
