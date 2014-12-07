package com.hitchlab.tinkle.objects;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Feed implements Parcelable{
	ArrayList<Tag> tags;
	ArrayList<String> likeIds;
	
	String feedId;  //
  	String ownerId;  //
	String ownerName;  //
	String ownerProfile;  //
	String message; //
	String story; //
	String imageUrl; //get the small size image
	String videoUrl;
	String timePost;
	int numComments;
	int numLike;
	String updated_time;
	boolean liked;
	
	public static final Parcelable.Creator<Feed> CREATOR = new Parcelable.Creator<Feed>() {
		public Feed createFromParcel(Parcel in) {
			return new Feed(in);
		}

		public Feed[] newArray(int size) {
			return new Feed[size];
		}
	};
	
	@Override
	public int describeContents() {
		return hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(feedId);
		dest.writeString(ownerId);
		dest.writeString(ownerName);
		dest.writeString(ownerProfile);
		dest.writeString(message);
		dest.writeString(story);
		dest.writeString(imageUrl);
		dest.writeString(videoUrl);
		dest.writeString(timePost);
		dest.writeInt(numComments);
		dest.writeInt(numLike);
		dest.writeString(updated_time);
		dest.writeTypedList(tags);
		dest.writeStringList(likeIds);
		dest.writeByte((byte) (liked ? 1 : 0)); 
	}
	
	/**
	 * Constructor using parcel
	 * @param in
	 */
	public Feed(Parcel in) {
		this.feedId = in.readString();
		this.ownerId = in.readString();
		this.ownerName = in.readString();
		this.ownerProfile = in.readString();
		this.message = in.readString();
		this.story = in.readString();
		this.imageUrl = in.readString();
		this.videoUrl = in.readString();
		this.timePost = in.readString();
		this.numComments = in.readInt();
		this.numLike = in.readInt();
		this.updated_time = in.readString();
		this.tags = new ArrayList<Tag>();
		in.readTypedList(tags, Tag.CREATOR);
		this.likeIds = new ArrayList<String>();
		in.readStringList(likeIds);
		this.liked = (in.readByte() != 0);
	}
	
	
	/**
	 * Default empty constructor
	 */
	public Feed() {
		this.feedId = "";
		this.ownerId = "";
		this.ownerName = "";
		this.ownerProfile = "";
		this.message = "";
		this.story = "";
		this.imageUrl = "";
		this.videoUrl = "";
		this.timePost = "";
		this.numComments = 0;
		this.numLike = 0;
		this.updated_time = "";
		this.tags = new ArrayList<Tag>();
		this.likeIds = new ArrayList<String>();
		this.liked = false;
	}
	
	/**
	 * @return the liked
	 */
	public boolean isLiked() {
		return liked;
	}
	
	/**
	 * @param liked the liked to set
	 */
	public void setLiked(boolean liked) {
		this.liked = liked;
	}

	/**
	 * @return the likeIds
	 */
	public ArrayList<String> getLikeIds() {
		return likeIds;
	}

	/**
	 * @param likeIds the likeIds to set
	 */
	public void setLikeIds(ArrayList<String> likeIds) {
		this.likeIds = likeIds;
	}

	/**
	 * @return the tags
	 */
	public ArrayList<Tag> getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(ArrayList<Tag> tags) {
		this.tags = tags;
	}

	/**
	 * @return the story
	 */
	public String getStory() {
		return story;
	}

	/**
	 * @param story the story to set
	 */
	public void setStory(String story) {
		this.story = story;
	}

	/**
	 * @return the feedId
	 */
	public String getFeedId() {
		return feedId;
	}
	/**
	 * @param feedId the feedId to set
	 */
	public void setFeedId(String feedId) {
		this.feedId = feedId;
	}
	/**
	 * @return the ownerId
	 */
	public String getOwnerId() {
		return ownerId;
	}
	/**
	 * @param ownerId the ownerId to set
	 */
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	/**
	 * @return the ownerName
	 */
	public String getOwnerName() {
		return ownerName;
	}
	/**
	 * @param ownerName the ownerName to set
	 */
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	/**
	 * @return the ownerProfile
	 */
	public String getOwnerProfile() {
		return ownerProfile;
	}
	/**
	 * @param ownerProfile the ownerProfile to set
	 */
	public void setOwnerProfile(String ownerProfile) {
		this.ownerProfile = ownerProfile;
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
	 * @return the imageUrl
	 */
	public String getImageUrl() {
		return imageUrl;
	}
	/**
	 * @param imageUrl the imageUrl to set
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	/**
	 * @return the videoUrl
	 */
	public String getVideoUrl() {
		return videoUrl;
	}
	/**
	 * @param videoUrl the videoUrl to set
	 */
	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
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
	 * @return the numComments
	 */
	public int getNumComments() {
		return numComments;
	}
	/**
	 * @param numComments the numComments to set
	 */
	public void setNumComments(int numComments) {
		this.numComments = numComments;
	}
	/**
	 * @return the numLike
	 */
	public int getNumLike() {
		return numLike;
	}
	/**
	 * @param numLike the numLike to set
	 */
	public void setNumLike(int numLike) {
		this.numLike = numLike;
	}
	/**
	 * @return the updated_time
	 */
	public String getUpdated_time() {
		return updated_time;
	}
	/**
	 * @param updated_time the updated_time to set
	 */
	public void setUpdated_time(String updated_time) {
		this.updated_time = updated_time;
	}
	

}
