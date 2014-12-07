package com.hitchlab.tinkle.fbquery.event;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.hitchlab.tinkle.objects.InviteFriend;

import android.os.Bundle;

public abstract class QueryInviteFriend {

	public QueryInviteFriend() {
		//empty
	}

	public abstract void onQueryCompleted(ArrayList<InviteFriend> friends);

	/**
	 * Prepare the query
	 * @param eid
	 * @return Bundle
	 */
	private Bundle prepareQuery(String eid) {
		String query = "SELECT uid, name FROM user WHERE uid IN " +
				"(SELECT uid2 FROM friend WHERE uid1 = me()) AND " +
				"NOT (uid IN (SELECT uid FROM event_member WHERE eid = " + eid + 
				")) ORDER BY name ASC LIMIT 5000";
		
		Bundle params = new Bundle();
		params.putString("q", query);
		return params;
	}

	/**
	 * Query all the event members of a given type
	 * @param session
	 * @param eid
	 */
	public void queryInviteFriends(Session session, String eid) {
		Bundle params = prepareQuery(eid);
		Request request = new Request(session, "/fql", params, HttpMethod.GET, new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				ArrayList<InviteFriend> friends = new ArrayList<InviteFriend>();
				try {
					GraphObject graphObject = response.getGraphObject();
					JSONObject jsonObject = graphObject.getInnerJSONObject();
					JSONArray data = jsonObject.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {
						JSONObject friendObj = data.getJSONObject(i);
						friends.add(getFriend(friendObj, i));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				onQueryCompleted(friends);
			}
		});
		request.setVersion("v1.0");
		request.executeAndWait();
	}
	
	/**
	 * Get the friend Object
	 * @param JSONObject
	 * @param index
	 */
	private InviteFriend getFriend(JSONObject friendObj, int index) {
		InviteFriend friend = new InviteFriend();
		friend.setCheck(false);
		friend.setPosition(index);
		try {
			friend.setUid(friendObj.getString("uid"));
			friend.setName(friendObj.getString("name"));
		} catch (JSONException e) {}
		return friend;
	}
}
