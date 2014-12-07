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
import com.hitchlab.tinkle.fbquery.FetchEventInfo;
import com.hitchlab.tinkle.objects.FbEvent;
import com.hitchlab.tinkle.objects.Friend;
import com.hitchlab.tinkle.objects.RecommendUserInfo;

public abstract class QueryRecommendPersonInfo {

	public QueryRecommendPersonInfo() {
		//empty
	}

	public abstract void onQueryCompleted(RecommendUserInfo userInfo);

	/**
	 * Prepare query
	 * @param uid
	 * @return Bundle
	 */
	private Bundle prepareQuery(String uid) {
		String basicInfo = "'info':'SELECT uid, name, pic_cover, can_message, can_post " +
				"FROM user WHERE uid = \"" + uid + "\"'";
		String mutualFriends = "'mutual_friends':'SELECT uid, name FROM user WHERE uid IN " +
				"(SELECT uid2 FROM friend WHERE uid2 IN (SELECT uid2 FROM friend WHERE uid1 = me()) " +
				"AND uid1 = \"" + uid + "\")'";
		String commonEvents = "'common_events':'SELECT eid, name, pic_big, start_time, end_time, " +
				"location, venue, timezone, unsure_count, attending_count, privacy, host FROM event " +
				"WHERE eid IN (SELECT eid FROM event_member where eid IN (SELECT eid FROM event_member " +
				"WHERE uid = me() and start_time > 1262304000) and uid = \"" + uid + "\") ORDER BY start_time DESC'";

		String query = "{" + basicInfo + ", " + mutualFriends + ", " + commonEvents + "}";

		Bundle params = new Bundle();
		params.putString("q", query);
		return params;
	}

	/**
	 * get the recommendation person info
	 * @param Session
	 * @param uid
	 */
	public void queryRecommendPersonInfo(Session session, String uid) {
		Bundle params = prepareQuery(uid);
		Request request = new Request(session, "/fql", params, HttpMethod.GET, new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				RecommendUserInfo userInfo = new RecommendUserInfo();
				try {
					GraphObject graphObject = response.getGraphObject();
					JSONObject jsonObject = graphObject.getInnerJSONObject();
					JSONArray data = jsonObject.getJSONArray("data");
					getBasicInfo(userInfo, data);
					userInfo.setCommonEvents(getCommonEvents(data));
					userInfo.setMutualFriends(getMutualFriends(data));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				onQueryCompleted(userInfo);
			}
		});
		request.setVersion("v1.0");
		request.executeAndWait();
	}

	/**
	 * Get the basic info of the recommended person
	 * @param RecommendUserInfo
	 * @param JSONArray
	 * @throws JSONException
	 */
	private void getBasicInfo(RecommendUserInfo user, JSONArray data) throws JSONException {
		for (int i = 0; i < data.length(); i++) {
			JSONObject basicInfoObj = data.getJSONObject(i);
			if (basicInfoObj.getString("name").equals("info")) {
				JSONObject basicInfo = basicInfoObj.getJSONArray("fql_result_set").getJSONObject(0);
				user.setUid(basicInfo.getString("uid"));
				user.setName(basicInfo.getString("name"));
				user.setCanMessage(basicInfo.getBoolean("can_message"));
				user.setCanPost(basicInfo.getBoolean("can_post"));
				user.setCoverUrl(getCoverUri(basicInfo));
				break;
			}
		}
	}
	
	/**
	 * get the cover uri
	 * @param JSONObj
	 * @return coverUri
	 */
	private String getCoverUri(JSONObject basicInfoObj) {
		String uri = "";
		try {
			JSONObject coverObj = basicInfoObj.getJSONObject("pic_cover");
			uri = coverObj.getString("source");
		} catch (JSONException e) {}
		if (uri == null || uri.equals("null")) return "";
		return uri;
	}
	
	/**
	 * Get the list of mutual friends
	 * @param JSONArray
	 * @return list of Friend
	 * @throws JSONException
	 */
	private ArrayList<Friend> getMutualFriends(JSONArray data) throws JSONException {
		ArrayList<Friend> friends = new ArrayList<Friend>();
		for (int i = 0; i < data.length(); i++) {
			JSONObject mutualFriendsObj = data.getJSONObject(i);
			if (mutualFriendsObj.getString("name").equals("mutual_friends")) {
				JSONArray mutualFriendsArr = mutualFriendsObj.getJSONArray("fql_result_set");
				for (int j = 0; j < mutualFriendsArr.length(); j++) {
					JSONObject mutualFriend = mutualFriendsArr.getJSONObject(j);
					Friend friend = new Friend();
					friend.setUid(mutualFriend.getString("uid"));
					friend.setName(mutualFriend.getString("name"));
					friends.add(friend);
				}
				break;
			}
		}
		return friends;
	}
	

	/**
	 * Get the events from the JSON response
	 * @param JSONArray
	 * @return list of FbEvent
	 * @throws JSONException
	 */
	private ArrayList<FbEvent> getCommonEvents(JSONArray data) throws JSONException {
		ArrayList<FbEvent> events = new ArrayList<FbEvent>();

		for (int i = 0; i < data.length(); i++) {
			JSONObject commonEventsObj = data.getJSONObject(i);
			if (commonEventsObj.getString("name").equals("common_events")) {
				JSONArray commonEventsArr = commonEventsObj.getJSONArray("fql_result_set");
				for (int j = 0; j < commonEventsArr.length(); j++) {
					JSONObject eventData = commonEventsArr.getJSONObject(j);
					FetchEventInfo eventInfo = new FetchEventInfo(eventData);
					FbEvent event = eventInfo.getEvent(); 
					events.add(event);
				}
				break;
			}
		}
		return events;
	}
}
