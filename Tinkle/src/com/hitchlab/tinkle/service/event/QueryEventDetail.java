package com.hitchlab.tinkle.service.event;

import com.facebook.Session;
import com.hitchlab.tinkle.fbquery.event.QueryEventCompleteInfo;
import com.hitchlab.tinkle.objects.FbEventCompleteInfo;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;
import com.hitchlab.tinkle.supports.Internet;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class QueryEventDetail extends IntentService {

	public static final String NOTIFICATION = "com.hitchlab.tinkle.service.event.QueryEventDetail";
	
	public static final int RESULT_OK = 0;
	public static final int RESULT_ERROR = 1;
	public static final int RESULT_INVALID = 2;
	public static final int RESULT_NO_INTERNET = 3;
	
	private Context context;
	private String eid;
	private QueryEventCompleteInfo query;
	
	public QueryEventDetail() {
		super("QueryEventDetail");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		this.context = this;
		this.eid = intent.getStringExtra("eid");
		if (query == null) query = new QueryEventCompleteInfo() {
			@Override
			public void onQueryCompleted(FbEventCompleteInfo event) {
				event.getEvent().setId(eid);
				if (event.getEvent().getStart_time() == 0) publishResult(null, RESULT_ERROR);
				else publishResult(event, RESULT_OK);
			}
		};
		
		boolean hasInternet = Internet.hasActiveInternetConnection(context);
		if (hasInternet) {
			Session session = Session.getActiveSession();
			if (session == null) session = Session.openActiveSessionFromCache(context);
			if (session != null && session.isOpened() && SharedPreference.containKey(context, Preference.UID)) {
				query.queryEventInfo(session, eid);
			} else publishResult(null, RESULT_INVALID);
		} else publishResult(null, RESULT_NO_INTERNET);
	}
	
	/**
	 * publish result back to the activity
	 * @param event
	 * @param result type
	 */
	private void publishResult(FbEventCompleteInfo event, int resultType) {
		Intent intent = new Intent(NOTIFICATION);
		intent.putExtra("result_type", resultType);
		if (resultType == RESULT_OK) intent.putExtra("data", event);
		sendBroadcast(intent);
	}
}
