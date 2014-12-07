package com.hitchlab.tinkle.dbrequest;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;


public class UserRequest {
	
	/**
	 * add the user to the db. Do this when log in
	 * @param uid
	 * @param username
	 * @param myEventsSize
	 * @param friendsEventsSize
	 * @throws JSONException
	 */
	public static void addUser(String uid, String username, int myEventsSize, int friendsEventsSize) throws JSONException {
		ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_TYPE, PublicDbConstants.REQUEST_TYPE_POST));
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA_TYPE, PublicDbConstants.REQUEST_DATA_TYPE_USER));
		JSONObject obj = new JSONObject();
		obj.put(Table.USER_UID, uid);
		obj.put(Table.USER_USERNAME, username);
		obj.put(Table.USER_NUM_USER_EVENTS, myEventsSize);
		obj.put(Table.USER_NUM_FRIENDS_EVENTS, friendsEventsSize);
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA, obj.toString()));
		try {
			AppHttpClient.executeHttpPost(AppHttpClient.url, postParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Check if user is register in our app
	 * @param uid
	 * @return Boolean
	 * @throws JSONException
	 */
	public static boolean getUser(String uid) throws JSONException {
		ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_TYPE, PublicDbConstants.REQUEST_TYPE_GET));
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA_TYPE, PublicDbConstants.REQUEST_DATA_TYPE_USER));
		JSONObject obj = new JSONObject();
		obj.put(Table.USER_UID, uid);
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA, obj.toString()));
		try {
			String result = AppHttpClient.executeHttpPostWithReturnValue(AppHttpClient.url, postParams);
			JSONObject responseObj = new JSONObject(result);
			return responseObj.getBoolean(PublicDbConstants.RESPONSE_DATA);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Update user my events number
	 * @param uid
	 * @param myEventsSize
	 * @throws JSONException
	 */
	public static void updateUserMyEventsSize(String uid, int myEventsSize) throws JSONException {
		ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_TYPE, PublicDbConstants.REQUEST_TYPE_UPDATE));
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA_TYPE, PublicDbConstants.REQUEST_DATA_TYPE_USER));
		JSONObject obj = new JSONObject();
		obj.put(PublicDbConstants.REQUEST_UPDATE_TYPE, PublicDbConstants.REQUEST_UPDATE_TYPE_USER_MY_EVENT);
		obj.put(Table.USER_UID, uid);
		obj.put(Table.USER_NUM_USER_EVENTS, myEventsSize);
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA, obj.toString()));
		try {
			AppHttpClient.executeHttpPost(AppHttpClient.url, postParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update user friend events number
	 * @param uid
	 * @param friendsEventsSize
	 * @throws JSONException
	 */
	public static void updateUserFriendEventsSize(String uid, int friendsEventsSize) throws JSONException {
		ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_TYPE, PublicDbConstants.REQUEST_TYPE_UPDATE));
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA_TYPE, PublicDbConstants.REQUEST_DATA_TYPE_USER));
		JSONObject obj = new JSONObject();
		obj.put(PublicDbConstants.REQUEST_UPDATE_TYPE, PublicDbConstants.REQUEST_UPDATE_TYPE_USER_FRIEND_EVENT);
		obj.put(Table.USER_UID, uid);
		obj.put(Table.USER_NUM_FRIENDS_EVENTS, friendsEventsSize);
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA, obj.toString()));
		try {
			AppHttpClient.executeHttpPost(AppHttpClient.url, postParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
