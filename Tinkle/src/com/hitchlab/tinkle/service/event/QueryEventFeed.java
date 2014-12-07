package com.hitchlab.tinkle.service.event;

import java.util.ArrayList;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.facebook.Session;
import com.hitchlab.tinkle.fbquery.event.QueryFeeds;
import com.hitchlab.tinkle.objects.Feed;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;
import com.hitchlab.tinkle.supports.Internet;

public class QueryEventFeed extends IntentService{
	
	public static final String NOTIFICATION = "com.hitchlab.tinkle.service.event.QueryEventFeed";
	
	public static final int RESULT_OK = 0;
	public static final int RESULT_INVALID = 2;
	public static final int RESULT_NO_INTERNET = 3;
	
	private Context context;
	private String eid;
	private String uid;
	private QueryFeeds query;
	
	public QueryEventFeed() {
		super("QueryEventFeed");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		this.context = this;
		this.eid = intent.getStringExtra("eid");
		this.uid = SharedPreference.getPrefStringValue(context, Preference.UID);
		if (query == null) query = new QueryFeeds() {
			
			@Override
			protected void onFeedsLoaded(ArrayList<Feed> feeds) {
				publishResult(feeds, RESULT_OK);
			}
		};
		
		boolean hasInternet = Internet.hasActiveInternetConnection(context);
		if (hasInternet) {
			Session session = Session.getActiveSession();
			if (session == null) session = Session.openActiveSessionFromCache(context);
			if (session != null && session.isOpened() && SharedPreference.containKey(context, Preference.UID)) {
				query.queryAllFeeds(session, eid, uid);
			} else publishResult(null, RESULT_INVALID);
		} else publishResult(null, RESULT_NO_INTERNET);
	}
	
	/**
	 * publish result back to the activity
	 * @param event
	 * @param result type
	 */
	private void publishResult(ArrayList<Feed> feeds, int resultType) {
		Intent intent = new Intent(NOTIFICATION);
		intent.putExtra("result_type", resultType);
		if (resultType == RESULT_OK) intent.putParcelableArrayListExtra("data", feeds);
		sendBroadcast(intent);
	}
}
