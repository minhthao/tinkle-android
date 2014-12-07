package com.hitchlab.tinkle.service.event;

import java.util.ArrayList;

import com.facebook.Session;
import com.hitchlab.tinkle.fbquery.event.QueryRecommendPeople;
import com.hitchlab.tinkle.objects.RecommendUser;
import com.hitchlab.tinkle.supports.Internet;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class QueryEventPeopleRecommendation extends IntentService {

	public static final String NOTIFICATION = "com.hitchlab.tinkle.service.event.QueryEventPeopleRecommendation";
	
	public static final int RESULT_OK = 0;
	public static final int RESULT_INVALID = 2;
	public static final int RESULT_NO_INTERNET = 3;
	
	private Context context;
	private String eid;
	private QueryRecommendPeople query;
	
	public QueryEventPeopleRecommendation() {
		super("QueryEventPeopleRecommendation");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		this.context = this;
		this.eid = intent.getStringExtra("eid");
		if (query == null) query = new QueryRecommendPeople() {
			@Override
			public void onQueryCompleted(ArrayList<RecommendUser> recommendations) {
				publishResult(recommendations, RESULT_OK);
			}
		};
		
		boolean hasInternet = Internet.hasActiveInternetConnection(context);
		if (hasInternet) {
			Session session = Session.getActiveSession();
			if (session == null) session = Session.openActiveSessionFromCache(context);
			if (session != null && session.isOpened()) {
				query.queryListOfRecommendPeople(session, eid);
			} else publishResult(null, RESULT_INVALID);
		} else publishResult(null, RESULT_NO_INTERNET);
	}
	
	/**
	 * publish result back to the activity
	 * @param event
	 * @param result type
	 */
	private void publishResult(ArrayList<RecommendUser> recommendations, int resultType) {
		Intent intent = new Intent(NOTIFICATION);
		intent.putExtra("result_type", resultType);
		if (resultType == RESULT_OK) {
			intent.putExtra("eid", eid);
			intent.putExtra("data", recommendations);
		}
		sendBroadcast(intent);
	}
}
