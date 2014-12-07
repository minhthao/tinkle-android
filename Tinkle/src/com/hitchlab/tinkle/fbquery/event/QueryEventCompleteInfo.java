package com.hitchlab.tinkle.fbquery.event;

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
import com.hitchlab.tinkle.eventactions.RsvpEventHandling;
import com.hitchlab.tinkle.objects.FbEventCompleteInfo;

public abstract class QueryEventCompleteInfo {

	public QueryEventCompleteInfo() {
		//empty
	}

	/**
	 * call back to notify the application that the query is completed
	 */
	public abstract void onQueryCompleted(FbEventCompleteInfo event);

	/**
	 * prepare the query
	 * @param eid
	 * @return query bundle
	 */
	private Bundle prepareQueryBundle(String eid) {
		String myrsvp = "'myrsvp':'SELECT uid, rsvp_status FROM event_member where eid = " + eid +
				" AND uid = me()'";
		String eventParticipants = "'eventparticipants':'SELECT uid, rsvp_status FROM event_member where eid = " + eid +
				" AND uid IN (SELECT uid2 FROM friend WHERE uid1 = me()) LIMIT 5000'";
		String eventInfo = "'eventInfo':'SELECT eid, name, pic_big, start_time, end_time, update_time, location, description, venue, timezone, " +
				"attending_count, unsure_count, not_replied_count, pic_cover, host, privacy, can_invite_friends " +
				"FROM event WHERE eid = " + eid + "'";

		String eventBatchQuery = "{" + myrsvp + ", " + eventParticipants + ", " + eventInfo + "}";

		Bundle params = new Bundle();
		params.putString("q", eventBatchQuery);
		return params;
	}

	/**
	 * query all the information about a particular event
	 * @param session
	 * @param the id of event
	 */
	public void queryEventInfo(Session session, String eid) {
		Bundle params = prepareQueryBundle(eid);
		Request request = new Request(session, "/fql", params, HttpMethod.GET, new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				FbEventCompleteInfo eventInfo = new FbEventCompleteInfo();
				try {
					GraphObject graphObject = response.getGraphObject();
					JSONObject jsonObject = graphObject.getInnerJSONObject();
					JSONArray data = jsonObject.getJSONArray("data");
					eventInfo = getEventInfo(data);
					getEventParticipants(data, eventInfo);
					String myRsvp = getMyRsvp(data);
					eventInfo.getEvent().setRsvp_status(myRsvp);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				onQueryCompleted(eventInfo);
			}
		});
		request.setVersion("v1.0");
		request.executeAndWait();
	}

	/**
	 * Get the event info
	 * @param JSON array data
	 */
	private FbEventCompleteInfo getEventInfo(JSONArray data) throws JSONException{
		for (int i = 0; i < data.length(); i++) {
			JSONObject infoObj = data.getJSONObject(i);
			if (infoObj.getString("name").equals("eventInfo")) {
				JSONArray infoData = infoObj.getJSONArray("fql_result_set");
				JSONObject info = infoData.getJSONObject(0);
				FetchEventCompleteInfo eventInfo = new FetchEventCompleteInfo(info);
				return eventInfo.getEvent();
			}
		}
		return new FbEventCompleteInfo();
	}

	/**
	 * get the event participants
	 * @param Json array data of the response
	 * @param array of attendee
	 * @param array of maybe people
	 * @param array of unreplied people
	 */
	private void getEventParticipants(JSONArray data, FbEventCompleteInfo eventInfo) throws JSONException{
		ArrayList<String> attendingFriends = new ArrayList<String>();
		ArrayList<String> maybeFriends = new ArrayList<String>();
		ArrayList<String> unrepliedFriends = new ArrayList<String>();

		for (int i = 0; i < data.length(); i++) {
			JSONObject participantObj = data.getJSONObject(i);
			if (participantObj.getString("name").equals("eventparticipants")) {
				JSONArray participantsData = participantObj.getJSONArray("fql_result_set");

				for (int j = 0; j < participantsData.length(); j++) {
					JSONObject participant = participantsData.getJSONObject(j);
					String rsvp_status = participant.getString("rsvp_status");
					String uid = participant.getString("uid");
					if (rsvp_status.equals("attending")) attendingFriends.add(uid);
					else if (rsvp_status.equals("not_replied")) unrepliedFriends.add(uid);
					else if (rsvp_status.equals("unsure")) maybeFriends.add(uid);
				}
				break;
			}
		}
		eventInfo.setFriendsAttending(attendingFriends);
		eventInfo.setFriendsMaybe(maybeFriends);
		eventInfo.setFriendsUnreplied(unrepliedFriends);
	}

	/**
	 * get the string of the rsvp status
	 * @param JSON array of data
	 * @return the return string
	 */
	private String getMyRsvp(JSONArray data) {
		String rsvpstatus = "";
		for (int i = 0; i < data.length(); i++) {
			try {
				JSONObject rsvpObj = data.getJSONObject(i);
				if (rsvpObj.getString("name").equals("myrsvp")) {
					JSONArray rsvpData = rsvpObj.getJSONArray("fql_result_set");
					JSONObject rsvp = rsvpData.getJSONObject(0);
					rsvpstatus = rsvp.getString("rsvp_status");
				}
			} catch (JSONException e) {}
		}
		if (rsvpstatus == null || rsvpstatus.equals("") || rsvpstatus.equals("null")) 
			rsvpstatus = RsvpEventHandling.NOT_INVITED;
		return rsvpstatus;
	}
}
