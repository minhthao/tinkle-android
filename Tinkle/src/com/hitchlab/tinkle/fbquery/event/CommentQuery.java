package com.hitchlab.tinkle.fbquery.event;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Session;
import com.facebook.Request.Callback;
import com.facebook.model.GraphObject;
import com.facebook.Response;
import com.hitchlab.tinkle.dialog.CommentDialog;
import com.hitchlab.tinkle.objects.Feed;
import com.hitchlab.tinkle.supports.ImageLoading;
import com.hitchlab.tinkle.supports.LoadingSpinner;

public class CommentQuery {
	Context context;
	CommentDialog dialog;
	LoadingSpinner loadingSpinner; 
	String feedId;

	public CommentQuery(Context context, ImageLoading imageLoading) {
		this.context = context;
		this.loadingSpinner = new LoadingSpinner(context);
		this.dialog = new CommentDialog(context, imageLoading) {
			@Override
			public void commentAdded(String id) {
				querySingleComment(id);
			}
		};
	}

	/**
	 * Call back for single comment query
	 * @return callback
	 */
	private Callback getSingleCommentCallback() {
		return new Request.Callback() {	
			@Override
			public void onCompleted(Response response) {
				try {
					processSingleCommentResponse(response);
				} catch (JSONException e) {
					Log.e("Comment", "Error getting comment in com.ebeam.eventbook.query.commentquery");
				}
				if (loadingSpinner.isShowing()) loadingSpinner.dismiss();
			}
		};
	}

	/**
	 * Call back for all comments query
	 * @return call back
	 */
	private Callback getQueryCallback() {
		return new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				try {
					processCommentsResponse(response);
				} catch (JSONException e) {
					Log.e("Comment", "Error getting comment in com.ebeam.eventbook.query.commentquery");
				}
				if (loadingSpinner.isShowing()) loadingSpinner.dismiss();
			}
		};
	}

	/**
	 * Query single comment
	 * @param cid
	 */
	private void querySingleComment(String cid) {
		Session session = Session.getActiveSession();
		if (session == null) session = Session.openActiveSessionFromCache(context);
		if (session != null && session.isOpened()) {
			loadingSpinner.show();
			String path = "/" + cid;
			Bundle params = new Bundle();
			params.putString("fields", "id, from, message, created_time");
			Request request = new Request(session, path, params, HttpMethod.GET, getSingleCommentCallback());
			request.executeAsync();
		} else Toast.makeText(context, "unable to query comment", Toast.LENGTH_SHORT).show();
	}

	public void queryAllComments(String fid) {
		this.feedId = fid;
		Session session = Session.getActiveSession();
		if (session == null) session = Session.openActiveSessionFromCache(context);
		if (session != null && session.isOpened()) {
			loadingSpinner.show();
			String path = "/" + fid + "/comments";
			Bundle params = new Bundle();
			params.putString("fields", "id, from, message, created_time, attachment");
			Request request = new Request(session, path, params, HttpMethod.GET, getQueryCallback());
			request.setVersion("v1.0");
			request.executeAsync();
		} else Toast.makeText(context, "unable to query post's comments", Toast.LENGTH_SHORT).show();
	}

	/**
	 * process the response for all comments
	 * @param response
	 * @throws JSONException
	 */
	private void processCommentsResponse(Response response) throws JSONException{
		ArrayList<Feed> feeds = new ArrayList<Feed>();
		GraphObject graphObject = response.getGraphObject();

		JSONObject jsonObject = graphObject.getInnerJSONObject();
		JSONArray data = jsonObject.getJSONArray("data");
		for (int i = 0; i < data.length(); i++) {
			Feed feed = new Feed();
			JSONObject feedObj = data.getJSONObject(i);
			fetchFeedId(feedObj, feed);
			fetchUploader(feedObj, feed);
			fetchMessage(feedObj, feed);
			fetchImage(feedObj, feed);
			fetchTimePost(feedObj, feed);
			feeds.add(feed);
		}
		dialog.displayComments(feeds, feedId);
	}

	/**
	 * process the response for single comments
	 * @param response
	 * @throws JSONException
	 */
	private void processSingleCommentResponse(Response response) throws JSONException{

		GraphObject graphObject = response.getGraphObject();

		JSONObject jsonObject = graphObject.getInnerJSONObject();
		JSONObject feedObj = jsonObject.getJSONObject("data");

		Feed feed = new Feed();
		fetchFeedId(feedObj, feed);
		fetchUploader(feedObj, feed);
		fetchMessage(feedObj, feed);
		fetchTimePost(feedObj, feed);
		dialog.addComment(feed);
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
		} catch (JSONException e) {
			Log.i("FEED", "fail to obtain feed creator");
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
			JSONObject attachmentObj = feedObj.getJSONObject("attachment");
			if (attachmentObj.getString("type").equals("photo")) {
				JSONObject media = attachmentObj.getJSONObject("media");
				JSONObject image = media.getJSONObject("image");
				String smallPhotoUrl = image.getString("src");
				String originalPhotoUrl = smallPhotoUrl.replace("s.jpg", "o.jpg");
				feed.setImageUrl(originalPhotoUrl);
			}
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
}
