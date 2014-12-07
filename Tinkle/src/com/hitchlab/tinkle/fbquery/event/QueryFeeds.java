package com.hitchlab.tinkle.fbquery.event;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.hitchlab.tinkle.objects.Feed;

public abstract class QueryFeeds {
	public static final String FEED_REQUEST_PARAMETERS = "id,created_time,message,story,with_tags,likes.fields(id),comments.fields(id),message_tags,from.fields(id,name,picture),updated_time,picture";

	private String uid; 

	public QueryFeeds() {
	}

	/**
	 * Call back function when feeds request is called and loaded
	 * @param events
	 */
	protected abstract void onFeedsLoaded(ArrayList<Feed> feeds);

	/**
	 * Query all the people reply with maybe to the events
	 */
	public void queryAllFeeds(Session session, String eid, String uid) {
		this.uid = uid;
		String path = "/" + eid + "/feed";
		Bundle params = new Bundle();
		params.putString("fields", FEED_REQUEST_PARAMETERS);
		Request request = new Request(session, path, params, HttpMethod.GET, new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				ArrayList<Feed> feeds = new ArrayList<Feed>();
				try { 
					GraphObject graphObject = response.getGraphObject();

					JSONObject jsonObject = graphObject.getInnerJSONObject();
					JSONArray data = jsonObject.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {
						Feed feed = new Feed();
						JSONObject feedObj = data.getJSONObject(i);
						fetchFeedId(feedObj, feed);
						fetchUploader(feedObj, feed);
						fetchStory(feedObj, feed);
						fetchMessage(feedObj, feed);
						fetchImage(feedObj, feed);
						fetchTimePost(feedObj, feed);
						fetchUpdated_time(feedObj, feed);
						fetchNumComments(feedObj, feed);
						fetchLikes(feedObj, feed);
						feeds.add(feed);
					}
				} catch (JSONException e) {
					Log.e("Feed", "Error getting feed in com.ebeam.eventbook.query.queryfeeds");
				}
				onFeedsLoaded(feeds);
			}
		});
		request.setVersion("v1.0");
		request.executeAndWait();
	}

	/**
	 * Fetch the feedId
	 * @param feedObj
	 * @param target feed
	 */
	private void fetchFeedId(JSONObject feedObj, Feed feed) {
		try {
			feed.setFeedId(feedObj.getString("id"));
		} catch (JSONException e) {
			Log.i("FEED", "fail to obtain feed id");
		}
	}

	/**
	 * Fetch the uploader
	 * @param feedObj
	 * @param target feed
	 */
	private void fetchUploader(JSONObject feedObj, Feed feed) {
		try {
			JSONObject from = feedObj.getJSONObject("from");
			feed.setOwnerId(from.getString("id"));
			feed.setOwnerName(from.getString("name"));
			JSONObject picture = from.getJSONObject("picture");
			JSONObject data = picture.getJSONObject("data");
			feed.setOwnerProfile(data.getString("url"));
		} catch (JSONException e) {
			Log.i("FEED", "fail to obtain feed creator");
		}
	}

	/**
	 * Fetch the story. This is event modifier only
	 * @param feedObj
	 * @param target feed
	 */
	private void fetchStory(JSONObject feedObj, Feed feed) {
		try {
			feed.setStory(feedObj.getString("story"));
		} catch (JSONException e) {
			//Log.i("FEED", "fail to obtain feed story");
		}
	}

	/**
	 * Fetch message. either message or story should appear only
	 * @param feedObj
	 * @param target feed
	 */
	private void fetchMessage(JSONObject feedObj, Feed feed) {
		try {
			feed.setMessage(feedObj.getString("message"));
		} catch (JSONException e) {
		}
	}

	/**
	 * Fetch the image
	 * @param feedObj
	 * @param target feed
	 */
	private void fetchImage(JSONObject feedObj, Feed feed) {
		try {
			String smallPhotoUrl = feedObj.getString("picture");
			String originalPhotoUrl = smallPhotoUrl.replace("s.jpg", "o.jpg");
			feed.setImageUrl(originalPhotoUrl);
		} catch (JSONException e) {
		}
	}

	/**
	 * Fetch the time post
	 * @param feedObj
	 * @param target feed
	 */
	private void fetchTimePost(JSONObject feedObj, Feed feed) {
		try {
			feed.setTimePost(feedObj.getString("created_time"));
		} catch (JSONException e) {
			Log.i("FEED", "fail to catch the created time");
		}
	}

	/**
	 * Fetch the updated time
	 * @param feedObj
	 * @param target feed
	 */
	private void fetchUpdated_time(JSONObject feedObj, Feed feed) {
		try {
			feed.setUpdated_time(feedObj.getString("updated_time"));
		} catch (JSONException e) {
			Log.i("FEED", "fail to catch the updated time");
		}
	}

	/**
	 * Fetch the comments
	 * @param feedObj
	 * @param target feed
	 */
	private void fetchNumComments(JSONObject feedObj, Feed feed) {
		try {
			JSONObject cms =  feedObj.getJSONObject("comments");
			JSONArray comments = cms.getJSONArray("data");
			feed.setNumComments(comments.length());
		} catch (JSONException e) {
		}
	}

	/**
	 * Fetch likes
	 * @param feedObj
	 * @param target feed
	 */
	private void fetchLikes(JSONObject feedObj, Feed feed) {
		try {
			ArrayList<String> likeIds = new ArrayList<String>();
			JSONObject likes = feedObj.getJSONObject("likes");
			JSONArray likesData = likes.getJSONArray("data");
			feed.setNumLike(likesData.length());
			for (int i =0; i< likesData.length(); i++) {
				JSONObject like = likesData.getJSONObject(i);
				String likeId = like.getString("id");
				likeIds.add(likeId);
				if (likeId.equals(uid)) feed.setLiked(true);
			}
		} catch (JSONException e) {

		}
	}
}
