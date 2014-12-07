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
import com.hitchlab.tinkle.supports.TimeFrame;

public abstract class QueryMyEvents {
	public static final long LOWER_TIME_LIMIT = 1262304000;
	public static final int QUERY_LIMIT = 5000;

	public QueryMyEvents() {
		//do nothing
	}

	/**
	 * Call back to notify the application that the query is completed
	 */
	public abstract void onQueryCompleted(ArrayList<FbEvent> events);
	
	/**
	 * Prepare the query
	 * @return query Bundle
	 */
	private Bundle preparePastQueryBundle() {
		long unixStartTime = TimeFrame.getUnixTime(TimeFrame.getTodayDate());
		String myEvents = "'myevents':'SELECT eid, rsvp_status FROM event_member where uid = me() "
				+ "AND (rsvp_status = \"attending\" OR rsvp_status = \"unsure\") "
				+ "AND start_time < " + unixStartTime 
				+ " AND start_time > " + LOWER_TIME_LIMIT 
				+ " LIMIT " + QUERY_LIMIT + "'";
		
		String eventInfo = "'eventInfo':'SELECT eid, name, pic_big, start_time, end_time, location, venue, timezone, unsure_count, attending_count, privacy, host FROM event "
				+ "WHERE eid IN (SELECT eid from #myevents) LIMIT " + QUERY_LIMIT + "'";
		
		String eventBatchQuery = "{" + myEvents + ", " + eventInfo + "}";
		
		Bundle params = new Bundle();
		params.putString("q", eventBatchQuery);
		return params;
	}


	/**
	 * Prepare the query
	 * @return query Bundle
	 */
	private Bundle prepareQueryBundle() {
		long unixStartTime = TimeFrame.getUnixTime(TimeFrame.getTodayDate());
		String myEvents = "'myevents':'SELECT eid, rsvp_status FROM event_member where uid = me() "
				+ "AND start_time >= " + unixStartTime + " LIMIT " + QUERY_LIMIT + "'";
		String eventInfo = "'eventInfo':'SELECT eid, name, pic_big, start_time, end_time, location, venue, timezone, unsure_count, attending_count, privacy, host FROM event "
				+ "WHERE eid IN (SELECT eid from #myevents) LIMIT " + QUERY_LIMIT + "'";

		String eventBatchQuery = "{" + myEvents + ", " + eventInfo + "}";
		
		Bundle params = new Bundle();
		params.putString("q", eventBatchQuery);
		return params;
		
	}
	
	/**
	 * query all the sync events
	 * @param session
	 */
	public void queryPastEventsAndWait(Session session) {
		Bundle params = preparePastQueryBundle();
		doQuery(session, params);
	}

	/**
	 * query all the sync events
	 * @param session
	 */
	public void queryEventsAndWait(Session session) {
		Bundle params = prepareQueryBundle();
		doQuery(session, params);
	}
	
	/**
	 * Do the query with the bundle params
	 * @param session
	 * @param bundle
	 */
	private void doQuery(Session session, Bundle bundle) {
		Request request = new Request(session, "/fql", bundle, HttpMethod.GET, new Request.Callback() {
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
				} catch (JSONException e) {
					e.printStackTrace();
				}
				onQueryCompleted(events);
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
			try {
				rsvpMap.put(rsvpEvent.getString("eid"), rsvpEvent.getString("rsvp_status"));
			} catch (JSONException e) {}
		}
		return rsvpMap;
	}

	/**
	 * Get the events from the JSON response
	 * @param data
	 * @return the events
	 * @throws JSONException
	 */
	private ArrayList<FbEvent> getEvents(JSONArray data) throws JSONException {
		ArrayList<FbEvent> events = new ArrayList<FbEvent>();
		JSONObject eventsDataObj = data.getJSONObject(1);
		if (!eventsDataObj.getString("name").equals("eventInfo")) eventsDataObj = data.getJSONObject(3);
		JSONArray eventsData = eventsDataObj.getJSONArray("fql_result_set");
		
		for (int i = 0; i < eventsData.length(); i++) {
			JSONObject eventData = eventsData.getJSONObject(i);
			FetchEventInfo eventInfo = new FetchEventInfo(eventData);
			FbEvent event = eventInfo.getEvent();
			events.add(event);
		}
		return events;
	}
}
