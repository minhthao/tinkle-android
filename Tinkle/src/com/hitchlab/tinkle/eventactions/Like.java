package com.hitchlab.tinkle.eventactions;

import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.hitchlab.tinkle.objects.Feed;

public abstract class Like {
	Session session;
	Context context;
	
	/**
	 * @param session
	 * @param context
	 * @param feedId
	 */
	public Like(Session session, Context context) {
		super();
		this.session = session;
		this.context = context;
	}
	
	/**
	 * Call back in case that the like has been succeed
	 */
	protected abstract void onLikeSucceed(int position, Feed feed);
	
	/**
	 * Call back in case that unlike request succeed
	 */
	protected abstract void onUnlikeSucceed(int position, Feed feed);
	
	/**
	 * Like a post
	 */
	public void likePost(int position, Feed feed) {
		final int mPosition = position;
		final Feed mFeed = feed;
		String path = "/" + feed.getFeedId() + "/likes";
		Request request = new Request(session, path, null, HttpMethod.POST, new Request.Callback() {
			
			@Override
			public void onCompleted(Response response) {
				try {
					GraphObject graphObject = response.getGraphObject();
					JSONObject jsonObject = graphObject.getInnerJSONObject();

					boolean result = jsonObject.getBoolean("FACEBOOK_NON_JSON_RESULT");
					if (result == true) onLikeSucceed(mPosition, mFeed);
					else Toast.makeText(context, "Like action fail", Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					Toast.makeText(context, "Exception encountered", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		});
		request.setVersion("v1.0");
		request.executeAsync();
	}
	
	/**
	 * Like a post
	 */
	public void unlikePost(int position, Feed feed) {
		final int mPosition = position;
		final Feed mFeed = feed;
		String path = "/" + feed.getFeedId() + "/likes";
		Request request = new Request(session, path, null, HttpMethod.DELETE, new Request.Callback() {
			
			@Override
			public void onCompleted(Response response) {
				try {
					GraphObject graphObject = response.getGraphObject();
					JSONObject jsonObject = graphObject.getInnerJSONObject();

					boolean result = jsonObject.getBoolean("FACEBOOK_NON_JSON_RESULT");
					if (result == true) onUnlikeSucceed(mPosition, mFeed);
					else Toast.makeText(context, "unlike action fail", Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					Toast.makeText(context, "Exception encountered", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		});
		request.setVersion("v1.0");
		request.executeAsync();
	}
}
