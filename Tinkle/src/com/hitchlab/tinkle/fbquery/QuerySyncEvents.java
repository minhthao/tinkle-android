package com.hitchlab.tinkle.fbquery;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.hitchlab.tinkle.objects.FbEvent;

import android.os.Bundle;

public abstract class QuerySyncEvents {
	public static final int QUERY_LIMIT = 5000;
	
	public QuerySyncEvents() {
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
	private Bundle prepareQueryBundle() {
		long unixStartTime = System.currentTimeMillis() / 1000L;
		String myEvents = "'myevents':'SELECT eid FROM event_member where uid = me() "
				+ "AND rsvp_status = \"attending\" "
				+ "AND start_time >= " + unixStartTime + " LIMIT " + QUERY_LIMIT + "'";
	
		String eventInfo = "'eventInfo':'SELECT eid, name, start_time, end_time, update_time, location, timezone, description FROM event "
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
	public void queryEvents(Session session) {
		Bundle params = prepareQueryBundle();
		Request request = new Request(session, "/fql", params, HttpMethod.GET, new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				ArrayList<FbEvent> events = new ArrayList<FbEvent>();
				try {
					GraphObject graphObject = response.getGraphObject();
					JSONObject jsonObject = graphObject.getInnerJSONObject();
					JSONArray data = jsonObject.getJSONArray("data");
					events = getEvents(data);
				} catch (JSONException e) {}
			
				onQueryCompleted(events);
			}
		});
		request.setVersion("v1.0");
		request.executeAndWait();
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
