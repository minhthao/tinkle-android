package com.hitchlab.tinkle.objects;

import org.json.JSONException;
import org.json.JSONObject;

import com.hitchlab.tinkle.dbrequest.Table;

public class Follow {
	private String fromId;
	private String toId;
	private String fromName;
	private String toName;
	private boolean visibility;
	
	public Follow() {
		//empty constructor
	}
	
	/**
	 * @param fromId
	 * @param toId
	 * @param visibility
	 */
	public Follow(String fromId, String fromName, String toId, String toName, boolean visibility) {
		super();
		this.fromId = fromId;
		this.fromName = fromName;
		this.toId = toId;
		this.toName = toName;
		this.visibility = visibility;
		
	}

	/**
	 * @return the fromId
	 */
	public String getFromId() {
		return fromId;
	}

	/**
	 * @param fromId the fromId to set
	 */
	public void setFromId(String fromId) {
		this.fromId = fromId;
	}

	/**
	 * @return the toId
	 */
	public String getToId() {
		return toId;
	}

	/**
	 * @param toId the toId to set
	 */
	public void setToId(String toId) {
		this.toId = toId;
	}

	/**
	 * @return the visibility
	 */
	public boolean isVisibility() {
		return visibility;
	}

	/**
	 * @param visibility the visibility to set
	 */
	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
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
	 * @return the toName
	 */
	public String getToName() {
		return toName;
	}

	/**
	 * @param toName the toName to set
	 */
	public void setToName(String toName) {
		this.toName = toName;
	}

	/**
	 * write the object to json
	 */
	public JSONObject toJSON() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put(Table.FOLLOW_FROM_ID, fromId);
		obj.put(Table.FOLLOW_FROM_NAME, fromName);
		obj.put(Table.FOLLOW_TO_ID, toId);
		obj.put(Table.FOLLOW_TO_NAME, toName);
		obj.put(Table.FOLLOW_VISIBILITY, visibility);
		return obj;
	}
	
	/**
	 * Get the follow object from json.
	 * @param JSONObject
	 * @return follow
	 */
	public static Follow fromJSON(JSONObject followObj) {
		Follow follow = new Follow();
		try {
			follow.setFromId(followObj.getString(Table.FOLLOW_FROM_ID));
			follow.setFromName(followObj.getString(Table.FOLLOW_FROM_NAME));
			follow.setToId(followObj.getString(Table.FOLLOW_TO_ID));
			follow.setToName(followObj.getString(Table.FOLLOW_TO_NAME));
			follow.setVisibility(followObj.getBoolean(Table.FOLLOW_VISIBILITY));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return follow;
	}
	
}
