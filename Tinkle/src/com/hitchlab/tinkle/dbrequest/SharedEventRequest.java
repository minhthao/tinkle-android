package com.hitchlab.tinkle.dbrequest;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hitchlab.tinkle.objects.SharedEvent;

public class SharedEventRequest {
	
	/**
	 * Add a list of event to the db
	 * @param events
	 * @throws JSONException
	 */
	public static void addEvent(SharedEvent event, ArrayList<String> targetUids) throws JSONException {
		ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_TYPE, PublicDbConstants.REQUEST_TYPE_POST));
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA_TYPE, PublicDbConstants.REQUEST_DATA_TYPE_SHARED_EVENT));
		JSONObject obj = event.toJSON(targetUids);
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA, obj.toString()));

		try {
			AppHttpClient.executeHttpPost(AppHttpClient.url, postParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * get all the shared events
	 * @param uid
	 * @param sinceTime
	 * @return list of FbEvent
	 * @throws JSONException
	 */
	public static ArrayList<SharedEvent> getSharedEvents(String uid, long sinceTime) throws JSONException {
		ArrayList<SharedEvent> events = new ArrayList<SharedEvent>();
		ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_TYPE, PublicDbConstants.REQUEST_TYPE_GET));
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA_TYPE, PublicDbConstants.REQUEST_DATA_TYPE_SHARED_EVENT));
		JSONObject obj = new JSONObject();
		obj.put(Table.SHARED_EVENT_TO_UID, uid);
		obj.put(Table.SHARED_EVENT_TIME_POST, sinceTime);
		
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA, obj.toString()));
		try {
			String result = AppHttpClient.executeHttpPostWithReturnValue(AppHttpClient.url, postParams);
			JSONObject responseObj = new JSONObject(result);
			JSONArray eventsArray = responseObj.getJSONArray(PublicDbConstants.RESPONSE_DATA);
			for (int i = 0; i < eventsArray.length(); i++) {
				JSONObject eventObj = eventsArray.getJSONObject(i);
				events.add(SharedEvent.fromJSON(eventObj));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return events;
	}
}
