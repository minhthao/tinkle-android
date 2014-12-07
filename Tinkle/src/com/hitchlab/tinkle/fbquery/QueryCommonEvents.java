package com.hitchlab.tinkle.fbquery;

import java.util.ArrayList;

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

public abstract class QueryCommonEvents {
	public static final long LOWER_TIME_LIMIT = 1262304000;
	
	public QueryCommonEvents() {
	}
	
	//abstract callback
	public abstract void onQueryCompleted(ArrayList<FbEvent> events); 
	
	/**
	 * prepare the bundle for query
	 * @param uid
	 * @return bundle
	 */
	private Bundle prepareQueryBundle(String uid) {
		String query = "SELECT eid, name, pic_big, start_time, end_time, location, venue, timezone, unsure_count, attending_count, privacy, host FROM event "
				+ "WHERE eid IN (SELECT eid FROM event_member WHERE eid IN (SELECT eid FROM event_member WHERE "
				+ "uid = me() AND start_time > " + LOWER_TIME_LIMIT + " AND (rsvp_status = \"attending\" OR rsvp_status = \"unsure\") "
				+ "ORDER BT start_time DESC LIMIT 300) AND uid = \"" + uid + "\")";
		Bundle param = new Bundle();
		param.putString("q", query);
		return param;
	}
	
	/**
	 * Do query
	 * @param session
	 * @param eid
	 */
	public void doQuery(Session session, String uid) {
		Bundle param = prepareQueryBundle(uid);
		Request request = new Request(session, "/fql", param, HttpMethod.GET, new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				ArrayList<FbEvent> events = new ArrayList<FbEvent>();
				try {
					GraphObject graphObject = response.getGraphObject();
					JSONObject jsonObject = graphObject.getInnerJSONObject();
					JSONArray data = jsonObject.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {
						JSONObject eventData = data.getJSONObject(i);
						FetchEventInfo eventInfo = new FetchEventInfo(eventData);
						FbEvent event = eventInfo.getEvent();
						events.add(event);
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
}
