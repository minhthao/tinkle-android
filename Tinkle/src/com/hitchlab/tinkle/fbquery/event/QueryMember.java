package com.hitchlab.tinkle.fbquery.event;

import java.util.ArrayList;
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
import com.hitchlab.tinkle.appevent.MemberActivity;
import com.hitchlab.tinkle.objects.RecommendUser;

import android.os.Bundle;

public abstract class QueryMember {

	public QueryMember() {
		//empty
	}

	public abstract void onQueryCompleted(ArrayList<RecommendUser> members);

	/**
	 * Prepare the query
	 * @param eid
	 * @return Bundle
	 */
	private Bundle prepareQuery(String eid, int type) {
		String rsvpType = "\"attending\"";
		if (type == MemberActivity.TYPE_MAYBE) rsvpType = "\"unsure\"";
		else if (type == MemberActivity.TYPE_INVITED) rsvpType = "\"not_replied\"";
		
		String members = "'members':'SELECT uid, name FROM user WHERE uid IN " +
				"(SELECT uid FROM event_member WHERE eid = " + eid + 
				" AND rsvp_status = " + rsvpType + " LIMIT 500)'";
		
		String friendShip = "'friendship':'SELECT uid2 FROM friend WHERE uid1 = me() AND " +
				"uid2 IN (SELECT uid FROM #members)'";

		String query = "{" +  members + ", " + friendShip + "}";

		Bundle params = new Bundle();
		params.putString("q", query);
		return params;
	}

	/**
	 * Query all the event members of a given type
	 * @param session
	 * @param eid
	 */
	public void queryMembers(Session session, String eid, int type) {
		Bundle params = prepareQuery(eid, type);
		Request request = new Request(session, "/fql", params, HttpMethod.GET, new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				ArrayList<RecommendUser> members = new ArrayList<RecommendUser>();
				try {
					GraphObject graphObject = response.getGraphObject();
					JSONObject jsonObject = graphObject.getInnerJSONObject();
					JSONArray data = jsonObject.getJSONArray("data");
					members = getMembers(data);
					Set<String> friends = getFriends(data);

					for (RecommendUser member : members) {
						member.setFriend(friends.contains(member.getUid()));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				onQueryCompleted(members);
			}
		});
		request.setVersion("v1.0");
		request.executeAndWait();
	}
	
	/**
	 * Get the members
	 * @param JSONArray
	 * @return ArrayList<RecommendUser>
	 * @throws JSONException
	 */
	private ArrayList<RecommendUser> getMembers(JSONArray data) throws JSONException {
		ArrayList<RecommendUser> members = new ArrayList<RecommendUser>();
		JSONObject membersObj = data.getJSONObject(0);
		JSONArray membersArr = membersObj.getJSONArray("fql_result_set");
		for (int i = 0; i < membersArr.length(); i++) {
			RecommendUser member = new RecommendUser();
			JSONObject memberObj = membersArr.getJSONObject(i);
			member.setUid(memberObj.getString("uid"));
			member.setUsername(memberObj.getString("name"));
			members.add(member);
		}
		return members;
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
		JSONArray friendsArr = friendsObj.getJSONArray("fql_result_set");
		for (int i = 0; i < friendsArr.length(); i++) {
			JSONObject friend = friendsArr.getJSONObject(i);
			friends.add(friend.getString("uid2"));
		}
		return friends;
	}

}
