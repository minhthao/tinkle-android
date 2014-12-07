package com.hitchlab.tinkle.service.event;

import java.util.ArrayList;

import com.facebook.Session;
import com.hitchlab.tinkle.appevent.MemberActivity;
import com.hitchlab.tinkle.fbquery.event.QueryMember;
import com.hitchlab.tinkle.objects.RecommendUser;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;
import com.hitchlab.tinkle.supports.Internet;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class QueryEventMember extends IntentService {

	public static final String NOTIFICATION = "com.hitchlab.tinkle.service.event.QueryEventMember";
	
	public static final int RESULT_OK = 0;
	public static final int RESULT_INVALID = 2;
	public static final int RESULT_NO_INTERNET = 3;
	
	private Context context;
	private QueryMember query;
	
	public QueryEventMember() {
		super("QueryEventMember");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		this.context = this;
		String eid = intent.getStringExtra("eid");
		int type = intent.getIntExtra(MemberActivity.TYPE, 0);
		if (query == null) query = new QueryMember() {
			@Override
			public void onQueryCompleted(ArrayList<RecommendUser> members) {
				publishResult(members, RESULT_OK);
			}
		};
		
		boolean hasInternet = Internet.hasActiveInternetConnection(context);
		if (hasInternet) {
			Session session = Session.getActiveSession();
			if (session == null) session = Session.openActiveSessionFromCache(context);
			if (session != null && session.isOpened() && SharedPreference.containKey(context, Preference.UID)) {
				query.queryMembers(session, eid, type);
			} else publishResult(null, RESULT_INVALID);
		} else publishResult(null, RESULT_NO_INTERNET);
	}
	
	/**
	 * publish result back to the activity
	 * @param event
	 * @param result type
	 */
	private void publishResult(ArrayList<RecommendUser> members, int resultType) {
		Intent intent = new Intent(NOTIFICATION);
		intent.putExtra("result_type", resultType);
		if (resultType == RESULT_OK) intent.putParcelableArrayListExtra("data", members);
		sendBroadcast(intent);
	}
}
