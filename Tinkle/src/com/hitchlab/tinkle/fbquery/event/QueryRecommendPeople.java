package com.hitchlab.tinkle.fbquery.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.hitchlab.tinkle.objects.RecommendUser;

import android.os.Bundle;

public abstract class QueryRecommendPeople {
	
	public QueryRecommendPeople() {
		//empty
	}
	
	public abstract void onQueryCompleted(ArrayList<RecommendUser> recommendations);
	
	/**
	 * Prepare the query
	 * @param eid
	 * @return Bundle
	 */
	private Bundle prepareQuery(String eid) {
		String rsvp = "'rsvp':'SELECT uid, rsvp_status FROM event_member WHERE eid = " + eid + 
				" AND (rsvp_status = \"attending\" OR rsvp_status = \"unsure\") " +
				"AND uid != me() LIMIT 500'";
		String friendShip = "'friendship':'SELECT uid2 FROM friend WHERE uid1 = me() AND " +
				"uid2 IN (SELECT uid FROM #rsvp)'";
		String mutualFriends = "'mutuals':'SELECT name, uid, mutual_friend_count, sex FROM user WHERE " +
				"uid IN (SELECT uid from #rsvp)'";
		
		String query = "{" +  rsvp + ", " + friendShip + ", " + mutualFriends + "}";
		
		Bundle params = new Bundle();
		params.putString("q", query);
		return params;
	}
	
	/**
	 * Query all the people you wish to recommend
	 * @param session
	 * @param eid
	 */
	public void queryListOfRecommendPeople(Session session, String eid) {
		Bundle params = prepareQuery(eid);
		Request request = new Request(session, "/fql", params, HttpMethod.GET, new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				ArrayList<RecommendUser> recommendations = new ArrayList<RecommendUser>();
				try {
					GraphObject graphObject = response.getGraphObject();
					JSONObject jsonObject = graphObject.getInnerJSONObject();
					JSONArray data = jsonObject.getJSONArray("data");
					HashMap<String, String> uidToRsvp = getEventRsvp(data);
					Set<String> friends = getFriends(data);
					getOtherInfos(recommendations, data); //here, we get the name and num of mutual friends
					
					for (RecommendUser recommendation : recommendations) {
						recommendation.setFriend(friends.contains(recommendation.getUid()));
						recommendation.setRsvpStatus(uidToRsvp.get(recommendation.getUid()));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				onQueryCompleted(recommendations);
			}
		});
		request.setVersion("v1.0");
		request.executeAndWait();
	}
	
	/**
	 * get the event rsvp and stored stored in a hashmap
	 * @param JSONArray
	 * @return HashMap<String, String>
	 * @throws JSONException
	 */
	private HashMap<String, String> getEventRsvp(JSONArray data) throws JSONException {
		HashMap<String, String> uidToRsvp = new HashMap<String, String>();
		JSONObject rsvpObj = data.getJSONObject(0);
		JSONArray rsvpArr = rsvpObj.getJSONArray("fql_result_set");
		for (int i = 0; i < rsvpArr.length(); i++) {
			JSONObject rsvp = rsvpArr.getJSONObject(i);
			uidToRsvp.put(rsvp.getString("uid"), rsvp.getString("rsvp_status"));
		}
		return uidToRsvp;
	}
	
	/**
	 * get the friendship status from the query
	 * @param JSONArray
	 * @return HashMap<String, Boolean>
	 * @throws JSONException
	 */
	private Set<String> getFriends(JSONArray data) throws JSONException {
		Set<String> friends = new HashSet<String>();
		JSONObject friendsObj = data.getJSONObject(1);
		if (!friendsObj.getString("name").equals("friendship")) friendsObj = data.getJSONObject(2);
		JSONArray friendsArr = friendsObj.getJSONArray("fql_result_set");
		for (int i = 0; i < friendsArr.length(); i++) {
			JSONObject friend = friendsArr.getJSONObject(i);
			friends.add(friend.getString("uid2"));
		}
		return friends;
	}
	
	/**
	 * Get the other info. Right now, these are limited to name and num mutual friends
	 * @param ArrayList<RecommendUser>
	 * @param JSONArray
	 * @throws JSONException
	 */
	private void getOtherInfos(ArrayList<RecommendUser> recommendations, JSONArray data) throws JSONException {
		JSONObject othersInfoObj = data.getJSONObject(2);
		if (!othersInfoObj.getString("name").equals("mutuals")) othersInfoObj = data.getJSONObject(1);
		JSONArray othersInfoArr = othersInfoObj.getJSONArray("fql_result_set");
		for (int i = 0; i < othersInfoArr.length(); i++) {
			RecommendUser recommend = new RecommendUser();
			JSONObject infoObj = othersInfoArr.getJSONObject(i);
			recommend.setUid(infoObj.getString("uid"));
			recommend.setUsername(infoObj.getString("name"));
			recommend.setNumMutualFriend(infoObj.getInt("mutual_friend_count"));
			recommend.setGender(infoObj.getString("sex"));
			
			recommendations.add(recommend);
		}
	}

}
