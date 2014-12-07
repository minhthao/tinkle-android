package com.hitchlab.tinkle.dbrequest;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.hitchlab.tinkle.objects.MyNotification;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;

public class NotificationRequest {

	/**
	 * Get the list of all notifications from the db
	 * @param context
	 * @return ArrayList<Notification>
	 * @throws JSONException
	 */
	public static ArrayList<MyNotification> getNotifications(Context context) throws JSONException {
		String uid = SharedPreference.getPrefStringValue(context, Preference.UID);
		return getNotifications(uid);
	}
	
	/**
	 * get the list of all notification from the db
	 * @param uid
	 * @return ArrayList<Notification>
	 * @throws JSONException
	 */
	public static ArrayList<MyNotification> getNotifications(String uid) throws JSONException {
		ArrayList<MyNotification> notifications = new ArrayList<MyNotification>();
		ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_TYPE, PublicDbConstants.REQUEST_TYPE_GET));
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA_TYPE, PublicDbConstants.REQUEST_DATA_TYPE_NOTIFICATION));
		JSONObject obj = new JSONObject();
		obj.put(Table.NOTIFICATION_UID, uid);
		
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA, obj.toString()));
		try {
			String result = AppHttpClient.executeHttpPostWithReturnValue(AppHttpClient.url, postParams);
			JSONObject responseObj = new JSONObject(result);
			JSONArray notificationsArray = responseObj.getJSONArray(PublicDbConstants.RESPONSE_DATA);
			for (int i = 0; i < notificationsArray.length(); i++) {
				JSONObject notificationObj = notificationsArray.getJSONObject(i);
				notifications.add(MyNotification.fromJSON(notificationObj));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return notifications; 
	}
	
	/**
	 * Add notifications to the db
	 * @param notifications
	 * @throws JSONException
	 */
	public static void addNotifications(ArrayList<MyNotification> notifications) throws JSONException {
		ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_TYPE, PublicDbConstants.REQUEST_TYPE_POST));
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA_TYPE, PublicDbConstants.REQUEST_DATA_TYPE_NOTIFICATION));
		JSONArray array = new JSONArray();
		for (MyNotification notification : notifications) array.put(notification.toJSON());
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA, array.toString()));
		
		try {
			AppHttpClient.executeHttpPost(AppHttpClient.url, postParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update all notification from the db
	 * @param MyNotification
	 * @throws JSONException
	 */
	public static void updateNotification(MyNotification notification) throws JSONException {
		ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_TYPE, PublicDbConstants.REQUEST_TYPE_UPDATE));
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA_TYPE, PublicDbConstants.REQUEST_DATA_TYPE_NOTIFICATION));
		JSONObject obj = notification.toJSON();
		
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA, obj.toString()));

		try {
			AppHttpClient.executeHttpPost(AppHttpClient.url, postParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
