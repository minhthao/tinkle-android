package com.hitchlab.tinkle.fbquery;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.hitchlab.tinkle.objects.Friend;

public abstract class QueryFriends {

	public QueryFriends() {
		//empty constructor
	}

	/**
	 * abstract class to tell that the query is done
	 * @param friends
	 */
	public abstract void onQueryCompleted(String name, String work, String coverUrl);
	public abstract void onQueryCompleted(ArrayList<Friend> friends);

	public void queryFriend(Session session, String uid) {
		Bundle params = new Bundle();
		params.putString("fields", "name,cover,work");
		String path = "/" + uid;
		Request request = new Request(session, path, params, HttpMethod.GET, new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				JSONObject user = new JSONObject();
				String name = "";
				String job = "";
				String coverUri = "";
				try {
					GraphObject graphObject = response.getGraphObject();
					user = graphObject.getInnerJSONObject();
					name = user.getString("name");
				} catch (JSONException e) {}
				
				try {
					JSONObject data = user.getJSONObject("cover");
					coverUri = data.getString("source");
					if (coverUri == null || coverUri.equals("null")) coverUri = "";
				} catch (JSONException e) {}
				
				try {
					JSONArray works = user.getJSONArray("work");
					if (works.length() > 0) {
						JSONObject work = works.getJSONObject(0);
						JSONObject position = work.getJSONObject("position");
						JSONObject employer = work.getJSONObject("employer");
						String positionName = position.getString("name");
						String employerName = employer.getString("name");
						job = positionName + " at " + employerName;
					}
				} catch (JSONException e) {}
				
				onQueryCompleted(name, job, coverUri);
			}
		});
		request.setVersion("v1.0");
		request.executeAsync();
	}

	/**
	 * Query a list of all your friends. Useful for sorting
	 */
	public void queryAllFriends(Session session) {
		Bundle params = new Bundle();
		params.putString("fields", "id,name");
		String path = "/me/friends";
		Request request = new Request(session, path, params, HttpMethod.GET, new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				new AsyncTask<Response, Void, ArrayList<Friend>> () {

					@Override
					protected ArrayList<Friend> doInBackground(Response... res) {
						ArrayList<Friend> friends = new ArrayList<Friend>();
						try {
							Response resp = res[0];
							GraphObject graphObject = resp.getGraphObject();
							JSONObject jsonObject = graphObject.getInnerJSONObject();
							JSONArray data = jsonObject.getJSONArray("data");

							for (int i = 0; i < data.length(); i++) {
								Friend friend = new Friend();
								JSONObject user = data.getJSONObject(i);
								friend.setUid(user.getString("id"));
								friend.setName(user.getString("name"));
								friends.add(friend);	
							}
						} catch (JSONException e) {}
						return friends;
					}

					@Override
					protected void onPostExecute(ArrayList<Friend> result) {
						onQueryCompleted(result);
					}					
				}.execute(response);
			}
		});
		request.setVersion("v1.0");
		request.executeAsync();
	}

	/**
	 * Query a list of all your friends. Useful for sorting
	 * @param session
	 */
	public void queryAllFriendsAndWait(Session session) {
		Bundle params = new Bundle();
		params.putString("fields", "id,name");
		String path = "/me/friends";
		Request request = new Request(session, path, params, HttpMethod.GET, new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				ArrayList<Friend> friends = new ArrayList<Friend>();
				try {
					GraphObject graphObject = response.getGraphObject();
					JSONObject jsonObject = graphObject.getInnerJSONObject();
					JSONArray data = jsonObject.getJSONArray("data");

					for (int i = 0; i < data.length(); i++) {
						Friend friend = new Friend();
						JSONObject user = data.getJSONObject(i);
						friend.setUid(user.getString("id"));
						friend.setName(user.getString("name"));
						friends.add(friend);	
					}
				} catch (JSONException e) {}
				onQueryCompleted(friends);
			}
		});
		request.setVersion("v1.0");
		request.executeAndWait();
	}

}
