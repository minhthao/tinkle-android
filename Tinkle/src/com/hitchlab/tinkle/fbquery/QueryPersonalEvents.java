package com.hitchlab.tinkle.fbquery;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.hitchlab.tinkle.objects.FbEvent;

public abstract class QueryPersonalEvents {
	public static final long LOWER_TIME_LIMIT = 1262304000;
	public static int QUERY_LIMIT = 5000;
	
	
	public QueryPersonalEvents() {
		//do nothing
	}
	
	/**
	 * Call back function when events request is called and loaded
	 * @param events
	 */
	protected abstract void onQueryCompleted(ArrayList<FbEvent> events);
	
	/**
	 * Prepare the request code
	 * @param unixStartTime
	 * @return bundle contains the request
	 */
	private Bundle prepareQueryBundle(String uid) {
		String eventRsvp = "'eventrsvp':'SELECT eid, rsvp_status FROM event_member WHERE uid = " + uid
				+ " AND (rsvp_status = \"attending\" OR rsvp_status = \"unsure\") "
				+ "AND start_time > " + LOWER_TIME_LIMIT + " ORDER BY start_time DESC LIMIT 300'";
		String eventInfo = "'eventinfo':'SELECT eid, name, pic_big, start_time, end_time, " +
				"location, venue, timezone, unsure_count, attending_count, privacy, host FROM event " +
				"WHERE eid IN (SELECT eid FROM #eventrsvp) ORDER BY start_time DESC'";
		String eventBatchQuery = "{" + eventRsvp + ", " + eventInfo + "}";
		Bundle params = new Bundle();
		params.putString("q", eventBatchQuery);
		return params;
	}
	
	//requery
	private Bundle prepareRequery(String uid) {
		String eventInfo = "SELECT eid, name, pic_big, start_time, end_time, " +
				"location, venue, timezone, unsure_count, attending_count, privacy, host FROM event " +
				"WHERE eid IN (SELECT eid FROM event_member where eid IN (SELECT eid FROM event_member " +
				"WHERE uid = me() and start_time > 1262304000) and uid = \"" + uid + "\") ORDER BY start_time DESC";
		Bundle params = new Bundle();
		params.putString("q", eventInfo);
		return params;
	}
	

	/**
	 * Query requery
	 * @param session
	 * @param uid
	 */
	public void queryRequeryEventsAndWait(Session session, String uid) {
		Bundle params = prepareRequery(uid);
		Request request = new Request(session, "/fql", params, HttpMethod.GET, new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				ArrayList<FbEvent> events = new ArrayList<FbEvent>();
				try {
					GraphObject graphObject = response.getGraphObject();
					JSONObject jsonObject = graphObject.getInnerJSONObject();
					JSONArray data = jsonObject.getJSONArray("data");
					events = getRequeryEvents(data);
				} catch (JSONException e) {}
				onQueryCompleted(events);
			}
		});
		request.executeAndWait();
	}
	
	/**
	 * Query all the events
	 * @param session
	 * @param uid
	 */
	public void queryEventsAndWait(Session session, String uid) {
		final Session mSession = session;
		final String mUid = uid;
		Bundle params = prepareQueryBundle(uid);
		Request request = new Request(session, "/fql", params, HttpMethod.GET, new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				ArrayList<FbEvent> events = new ArrayList<FbEvent>();
				try {
					GraphObject graphObject = response.getGraphObject();
					JSONObject jsonObject = graphObject.getInnerJSONObject();
					JSONArray data = jsonObject.getJSONArray("data");
					HashMap<String, String> rsvpMap = getRsvpMap(data);
					events = getEvents(data);
					for (FbEvent event : events) {
						String rsvp = rsvpMap.get(event.getId());
						if (rsvp != null) event.setRsvp_status(rsvp);
					}
				} catch (JSONException e) {}
				if (events.size() != 0) onQueryCompleted(events);
				else queryRequeryEventsAndWait(mSession, mUid);
			}
		});
		request.setVersion("v1.0");
		request.executeAndWait();
	}
	
	
	/**
	 * get the rsvp map of all the queried events
	 * @param data
	 * @return the rsvp map
	 * @throws JSONException
	 */
	private HashMap<String, String> getRsvpMap(JSONArray data) throws JSONException {
		HashMap<String, String> rsvpMap = new HashMap<String, String>();
		JSONObject eventsDataObj = data.getJSONObject(0);
		JSONArray rsvpList = eventsDataObj.getJSONArray("fql_result_set");
		
		for (int i = 0; i < rsvpList.length(); i++) {
			JSONObject rsvpEvent = rsvpList.getJSONObject(i);
			fetchRsvp(rsvpMap, rsvpEvent);
		}
		return rsvpMap;
	}
	
	/**
	 * fetch the rsvp status of each event
	 * @param rsvpMap
	 * @param rsvpEvent
	 */
	private void fetchRsvp(HashMap<String, String> rsvpMap, JSONObject rsvpEvent) {
		try {
			rsvpMap.put(rsvpEvent.getString("eid"), rsvpEvent.getString("rsvp_status"));
		} catch (JSONException e) {}
	}
	
	/**
	 * Get the events from the JSON response
	 * @param JSONArray
	 * @return list of FbEvent
	 * @throws JSONException
	 */
	private ArrayList<FbEvent> getEvents(JSONArray data) throws JSONException {
		ArrayList<FbEvent> events = new ArrayList<FbEvent>();
		JSONObject eventsDataObj = data.getJSONObject(1);
		JSONArray eventsData = eventsDataObj.getJSONArray("fql_result_set");
		
		for (int i = 0; i < eventsData.length(); i++) {
			JSONObject eventData = eventsData.getJSONObject(i);
			FetchEventInfo eventInfo = new FetchEventInfo(eventData);
			FbEvent event = eventInfo.getEvent(); 
			events.add(event);
		}
		return events;
	}
	
	/**
	 * Get the events from the JSON response
	 * @param JSONArray
	 * @return list of FbEvent
	 * @throws JSONException
	 */
	private ArrayList<FbEvent> getRequeryEvents(JSONArray data) throws JSONException {
		ArrayList<FbEvent> events = new ArrayList<FbEvent>();
		
		for (int i = 0; i < data.length(); i++) {
			JSONObject eventData = data.getJSONObject(i);
			FetchEventInfo eventInfo = new FetchEventInfo(eventData);
			FbEvent event = eventInfo.getEvent(); 
			events.add(event);
		}
		return events;
	}
}
