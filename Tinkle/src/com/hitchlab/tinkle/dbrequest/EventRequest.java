package com.hitchlab.tinkle.dbrequest;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hitchlab.tinkle.objects.FbEvent;

public class EventRequest {
	
	/**
	 * Add a list of event to the db
	 * @param events
	 * @throws JSONException
	 */
	public static void addEvents(ArrayList<FbEvent> events) throws JSONException {
		ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_TYPE, PublicDbConstants.REQUEST_TYPE_POST));
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA_TYPE, PublicDbConstants.REQUEST_DATA_TYPE_EVENT));
		JSONArray array = new JSONArray();
		for (FbEvent event : events) array.put(event.toJSON());
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA, array.toString()));

		try {
			AppHttpClient.executeHttpPost(AppHttpClient.url, postParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * get all the bounded events
	 * @param lower lng
	 * @param upper lng
	 * @param lower lat
	 * @param upper lat
	 * @param sinceTime
	 * @param toTime
	 * @return list of FbEvent
	 * @throws JSONException
	 */
	public static ArrayList<FbEvent> getBoundedEvents(double minLng, double maxLng, double minLat, double maxLat, long sinceTime, long toTime) throws JSONException {
		ArrayList<FbEvent> events = new ArrayList<FbEvent>();
		ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_TYPE, PublicDbConstants.REQUEST_TYPE_GET));
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA_TYPE, PublicDbConstants.REQUEST_DATA_TYPE_EVENT));
		JSONObject obj = new JSONObject();
		obj.put(Table.PUBLIC_EVENT_LOWER_LONGITUDE, minLng);
		obj.put(Table.PUBLIC_EVENT_UPPER_LONGITUDE, maxLng);
		obj.put(Table.PUBLIC_EVENT_LOWER_LATITUDE, minLat);
		obj.put(Table.PUBLIC_EVENT_UPPER_LATITUDE, maxLat);
		obj.put(Table.PUBLIC_EVENT_TIME_FRAME_BEGIN, sinceTime);
		obj.put(Table.PUBLIC_EVENT_TIME_FRAME_END, toTime);
		
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA, obj.toString()));
		try {
			String result = AppHttpClient.executeHttpPostWithReturnValue(AppHttpClient.url, postParams);
			JSONObject responseObj = new JSONObject(result);
			JSONArray eventsArray = responseObj.getJSONArray(PublicDbConstants.RESPONSE_DATA);
			for (int i = 0; i < eventsArray.length(); i++) {
				JSONObject eventObj = eventsArray.getJSONObject(i);
				events.add(FbEvent.fromJSON(eventObj));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return events;
	}
	
	/**
	 * get all the public events within a certain radius
	 * @param longitude
	 * @param latitude
	 * @param distance
	 * @param sinceTime
	 * @param toTime
	 * @return list of FbEvent
	 * @throws JSONException
	 */
	public static ArrayList<FbEvent> getPublicEvents(double longitude, double latitude, double distance, long sinceTime, long toTime) throws JSONException {
		ArrayList<FbEvent> events = new ArrayList<FbEvent>();
		ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_TYPE, PublicDbConstants.REQUEST_TYPE_GET));
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA_TYPE, PublicDbConstants.REQUEST_DATA_TYPE_EVENT));
		JSONObject obj = new JSONObject();
		obj.put(Table.EVENT_LONGITUDE, longitude);
		obj.put(Table.EVENT_LATITUDE, latitude);
		obj.put(Table.EVENT_DISTANCE, distance);
		obj.put(Table.EVENT_START_TIME, sinceTime);
		obj.put(Table.EVENT_END_TIME, toTime);
		obj.put(Table.EVENT_NAME, "");
		
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA, obj.toString()));
		try {
			String result = AppHttpClient.executeHttpPostWithReturnValue(AppHttpClient.url, postParams);
			JSONObject responseObj = new JSONObject(result);
			JSONArray eventsArray = responseObj.getJSONArray(PublicDbConstants.RESPONSE_DATA);
			for (int i = 0; i < eventsArray.length(); i++) {
				JSONObject eventObj = eventsArray.getJSONObject(i);
				events.add(FbEvent.fromJSON(eventObj));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return events;
	}
	
	/**
	 * get all the public events within a certain radius
	 * @param longitude
	 * @param latitude
	 * @param distance
	 * @param sinceTime
	 * @param toTime
	 * @param searchPhrase
	 * @return list of FbEvent
	 * @throws JSONException
	 */
	public static ArrayList<FbEvent> getSearchedEvents(double longitude, double latitude, double distance, long sinceTime, long toTime, String searchPhrase) throws JSONException {
		ArrayList<FbEvent> events = new ArrayList<FbEvent>();
		ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_TYPE, PublicDbConstants.REQUEST_TYPE_GET));
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA_TYPE, PublicDbConstants.REQUEST_DATA_TYPE_EVENT));
		JSONObject obj = new JSONObject();
		obj.put(Table.EVENT_LONGITUDE, longitude);
		obj.put(Table.EVENT_LATITUDE, latitude);
		obj.put(Table.EVENT_DISTANCE, distance);
		obj.put(Table.EVENT_START_TIME, sinceTime);
		obj.put(Table.EVENT_END_TIME, toTime);
		obj.put(Table.EVENT_NAME, searchPhrase);
		
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA, obj.toString()));
		try {
			String result = AppHttpClient.executeHttpPostWithReturnValue(AppHttpClient.url, postParams);
			JSONObject responseObj = new JSONObject(result);
			JSONArray eventsArray = responseObj.getJSONArray(PublicDbConstants.RESPONSE_DATA);
			for (int i = 0; i < eventsArray.length(); i++) {
				JSONObject eventObj = eventsArray.getJSONObject(i);
				events.add(FbEvent.fromJSON(eventObj));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return events;
	}
	
	/**
	 * Delete an event from the Db
	 * @param eid
	 */
	public static void deleteEvent(String eid) throws JSONException {
		ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_TYPE, PublicDbConstants.REQUEST_TYPE_DELETE));
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA_TYPE, PublicDbConstants.REQUEST_DATA_TYPE_EVENT));
		JSONObject obj = new JSONObject();
		obj.put(Table.EVENT_EID, eid);
		
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA, obj.toString()));
		try {
			AppHttpClient.executeHttpPost(AppHttpClient.url, postParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
