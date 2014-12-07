package com.hitchlab.tinkle.fbquery;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.hitchlab.tinkle.eventactions.RsvpEventHandling;
import com.hitchlab.tinkle.objects.Attendee;
import com.hitchlab.tinkle.objects.FbEvent;
import com.hitchlab.tinkle.objects.Friend;
import com.hitchlab.tinkle.supports.TimeFrame;

public abstract class QueryFriendEvents {

	public static final int QUERY_LIMIT = 5000;
	public int friendsSizePerRequest = 0;
	public Set<String> eids;
	public ArrayList<FbEvent> allEvents;

	public QueryFriendEvents() {
		//empty constructor
	}

	/**
	 * call back to notify the application that the query is completed
	 */
	public abstract void onMyQueryCompleted(ArrayList<FbEvent> events);
	
	/**
	 * Prepare the query bundle given the set of friends
	 * @param ArrayList<Friend>
	 * @return Bundle
	 */
	private Bundle prepareQueryBundleWithFriends(ArrayList<Friend> friends, int listIndex) {
		long unixStartTime = TimeFrame.getUnixTime(TimeFrame.getTodayDate()); 
		String friendsString = "(";
		int size = Math.min(friends.size(), listIndex + friendsSizePerRequest);
		for (int i = listIndex; i < size; i++) {
			if (i != size - 1) friendsString += "uid = \"" + friends.get(i).getUid() + "\" OR ";
			else friendsString += "uid = \"" + friends.get(i).getUid() + "\") ";
		}
		
		String friendEvents =  "'friendEvents':'SELECT eid, uid FROM event_member WHERE "
				+ "(rsvp_status = \"attending\" OR rsvp_status = \"unsure\") "  //rsvp_status
				+ "AND " + friendsString 
				+ "AND start_time >= " + unixStartTime //the start time
				+ " ORDER BY start_time ASC LIMIT " + QUERY_LIMIT + "'"; //query limit

		String friendNames = "'friendNames':'SELECT uid, name FROM user WHERE uid IN (SELECT uid from #friendEvents)'";
		
		String eventInfo = "'eventinfo':'SELECT eid, name, pic_big, start_time, end_time, location, venue, timezone, unsure_count, attending_count, privacy, host FROM event "
				+ "WHERE eid IN (SELECT eid from #friendEvents) ORDER BY start_time ASC LIMIT " + QUERY_LIMIT + "'";

		String eventBatchQuery = "{" + friendEvents + ", " + friendNames + ", " + eventInfo + "}";
		
		Bundle params = new Bundle();
		params.putString("q", eventBatchQuery);
		return params;
	}
	
	/**
	 * Query friends future events
	 * @param session
	 */
	public void queryEventsAndWait(Session session) {
		final Session mSession = session;
		eids = new HashSet<String>();
		allEvents = new ArrayList<FbEvent>();
		
		QueryFriends queryFriends =  new QueryFriends() {
			@Override
			public void onQueryCompleted(String name, String work, String coverUrl) {
				//Do nothing here
			}

			@Override
			public void onQueryCompleted(ArrayList<Friend> friends) {
				ArrayList<Request> requests = new ArrayList<Request>();
				friendsSizePerRequest = (friends.size() % 50 == 0 ? friends.size() / 50 : friends.size() / 50 + 1);
				for (int i = 0; i < friends.size(); i += friendsSizePerRequest) {
					Bundle params = prepareQueryBundleWithFriends(friends, i);
					
					Request request = new Request(mSession, "/fql", params, HttpMethod.GET, new Request.Callback() {
						@Override
						public void onCompleted(Response response) {
							ArrayList<FbEvent> events = new ArrayList<FbEvent>();
							try {
								GraphObject graphObject = response.getGraphObject();
								JSONObject jsonObject = graphObject.getInnerJSONObject();
								JSONArray data = jsonObject.getJSONArray("data");
								HashMap<String, String> friends = getFriendsMap(data);
								HashMap<String, Set<String>> attendeesMap = getAttendeesMap(data);
								events = getEvents(data);
								//update the people attending
								for (FbEvent event : events) {
									ArrayList<Attendee> attendeeList = new ArrayList<Attendee>();
									Set<String> attendees = attendeesMap.get(event.getId());
									if (attendees != null) {
										for (String attendeeUid : attendees) 
											attendeeList.add(new Attendee(attendeeUid, friends.get(attendeeUid)));
									}
									event.setFriendsAttending(attendeeList);
									if (!eids.contains(event.getId())) {
										eids.add(event.getId());
										allEvents.add(event);
									}
									
								}
								
							} catch (JSONException e) {}
						}
					});
					request.setVersion("v1.0");
					requests.add(request);
					
					Request.executeBatchAndWait(requests);

					onMyQueryCompleted(allEvents);
				}
			}
		};
		
		queryFriends.queryAllFriendsAndWait(session);
	}

	/**
	 * Get the attendee map of all events queried
	 * @param data
	 * @return the map between event and participants
	 * @throws JSONException
	 */
	private HashMap<String, Set<String>> getAttendeesMap(JSONArray data) throws JSONException{
		HashMap<String, Set<String>> attendeesMap = new HashMap<String, Set<String>>();
		JSONObject attendeesObj = data.getJSONObject(0);
		JSONArray attendees = attendeesObj.getJSONArray("fql_result_set");
		for (int i = 0; i < attendees.length(); i++) {
			JSONObject attendee = attendees.getJSONObject(i);
			String eid = attendee.getString("eid");
			String uid = attendee.getString("uid");
			Set<String> eventAttendees = attendeesMap.get(eid);
			if (eventAttendees == null) {
				eventAttendees = new HashSet<String>();
				attendeesMap.put(eid, eventAttendees);
			}
			eventAttendees.add(uid);
		}
		return attendeesMap;
	}
	
	/**
	 * get the friends map from the JSON response
	 * @param data
	 * @return the map between uid and name
	 * @throws JSONException
	 */
	private HashMap<String, String> getFriendsMap(JSONArray data) throws JSONException {
		HashMap<String, String> friends = new HashMap<String, String>();
		JSONObject friendsMapObj = data.getJSONObject(1);
		if (!friendsMapObj.getString("name").equals("friendNames")) friendsMapObj = data.getJSONObject(2);
		
		JSONArray friendArray = friendsMapObj.getJSONArray("fql_result_set");
		for (int i = 0; i < friendArray.length(); i++) {
			JSONObject friend = friendArray.getJSONObject(i);
			friends.put(friend.getString("uid"), friend.getString("name"));
		}
		return friends;
	}

	/**
	 * Get the events from the JSON response
	 * @param data
	 * @return the events
	 * @throws JSONException
	 */
	private ArrayList<FbEvent> getEvents(JSONArray data) throws JSONException {
		ArrayList<FbEvent> events = new ArrayList<FbEvent>();
		JSONObject eventsDataObj = data.getJSONObject(2);
		if (eventsDataObj.getString("name").equals("friendNames")) eventsDataObj = data.getJSONObject(1);
		JSONArray eventsData = eventsDataObj.getJSONArray("fql_result_set");

		for (int i = 0; i < eventsData.length(); i++) {
			JSONObject eventData = eventsData.getJSONObject(i);
			FetchEventInfo eventInfo = new FetchEventInfo(eventData);
			FbEvent event = eventInfo.getEvent();
			event.setRsvp_status(RsvpEventHandling.NOT_INVITED);
			events.add(event);
		}
		return events;
	}
}
