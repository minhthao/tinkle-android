package com.hitchlab.tinkle.fbquery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.hitchlab.tinkle.supports.TimeFrame;

public abstract class QueryTodayEventGoers {
	
	private ArrayList<String> eventGoers;

	public QueryTodayEventGoers() {
		//empty constructor
	}
	
	/**
	 * call back to notify the application that the query is completed
	 */
	public abstract void onQueryCompleted(ArrayList<String> attendees);
	
	/**
	 * Prepare the query bundle
	 * @return the query
	 */
	private Bundle prepareQueryBundle() {
		long lowerBound = TimeFrame.getUnixTime(TimeFrame.getTodayDate());
		long upperBound = TimeFrame.getUnixTime(TimeFrame.getIthDate(1));
		
		String query = "SELECT uid FROM event_member WHERE " 
				+ "(rsvp_status = \"attending\" OR rsvp_status = \"unsure\") "
				+ "AND uid IN (SELECT uid2 FROM friend WHERE uid1 = me()) "
				+ "AND start_time >= " + lowerBound + " AND start_time < " + upperBound;
		
		Bundle params = new Bundle();
		params.putString("q", query);
		return params;
	}
	
	/**
	 * Query the list of today events attendees
	 * @param session
	 */
	public void queryTodayEventsAttendees(Session session) {
		Bundle params = prepareQueryBundle();
		Request request = new Request(session, "/fql", params, HttpMethod.GET, new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				try {
					Set<String> attendees = new HashSet<String>();
					GraphObject graphObject = response.getGraphObject();
					JSONObject jsonObject = graphObject.getInnerJSONObject();
					JSONArray data = jsonObject.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {
						JSONObject obj = data.getJSONObject(i);
						attendees.add(obj.getString("uid"));
					}
	
					eventGoers = new ArrayList<String>(attendees);
				} catch (JSONException e) {}
				
				if (eventGoers == null) eventGoers = new ArrayList<String>();
				onQueryCompleted(eventGoers);
			}
		});
		request.setVersion("v1.0");
		request.executeAndWait();
	}
	
}
