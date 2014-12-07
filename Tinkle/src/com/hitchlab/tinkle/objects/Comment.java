package com.hitchlab.tinkle.objects;

import java.util.ArrayList;

public class Comment {
	ArrayList<Tag> tags;
	
	String commentId;
	String ownerId;
	String ownerName;
	String ownerProfile;
	String created_time;
	int numLikes;
	String photoUrl;
	String message;

	
	/**
	 * default constructor
	 */
	public Comment() {
		this.commentId = "";
		this.ownerId = "";
		this.ownerName = "";
		this.ownerProfile = "";
		this.created_time = "";
		this.numLikes = 0;
		this.photoUrl = "";
		this.message = "";
		this.tags = new ArrayList<Tag>();
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
	 * @return the commentId
	 */
	public String getCommentId() {
		return commentId;
	}


	/**
	 * @param commentId the commentId to set
	 */
	public void setCommentId(String commentId) {
		this.commentId = commentId;
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
	 * @return the created_time
	 */
	public String getCreated_time() {
		return created_time;
	}


	/**
	 * @param created_time the created_time to set
	 */
	public void setCreated_time(String created_time) {
		this.created_time = created_time;
	}


	/**
	 * @return the numLikes
	 */
	public int getNumLikes() {
		return numLikes;
	}


	/**
	 * @param numLikes the numLikes to set
	 */
	public void setNumLikes(int numLikes) {
		this.numLikes = numLikes;
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
	
	
	
	
}
