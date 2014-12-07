package com.hitchlab.tinkle.service.event;

import org.json.JSONException;

import com.facebook.Session;
import com.hitchlab.tinkle.dbrequest.FbuserRequest;
import com.hitchlab.tinkle.fbquery.event.QueryRecommendPersonInfo;
import com.hitchlab.tinkle.objects.RecommendUserInfo;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;
import com.hitchlab.tinkle.supports.Internet;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class QueryEventRecommendUserInfo extends IntentService {

	public static final String NOTIFICATION = "com.hitchlab.tinkle.service.event.QueryEventRecommendUserInfo";
	
	public static final int RESULT_OK = 0;
	public static final int RESULT_ERROR = 1;
	public static final int RESULT_INVALID = 2;
	public static final int RESULT_NO_INTERNET = 3;
	
	private Context context;
	private String uid;
	private QueryRecommendPersonInfo query;
	
	public QueryEventRecommendUserInfo() {
		super("QueryEventRecommendUserInfo");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		this.context = this;
		this.uid = intent.getStringExtra("uid");
		if (query == null) query = new QueryRecommendPersonInfo() {
			@Override
			public void onQueryCompleted(RecommendUserInfo userInfo) {
				if (userInfo.getUid().equals("")) publishResult (null, RESULT_ERROR);
				else {
					try {
						FbuserRequest.addFbuser(uid, userInfo.getName(), "");
					} catch (JSONException e) { 
						e.printStackTrace(); 
					}
					publishResult(userInfo, RESULT_OK);
				}
			}
		};
		
		boolean hasInternet = Internet.hasActiveInternetConnection(context);
		if (hasInternet) {
			Session session = Session.getActiveSession();
			if (session == null) session = Session.openActiveSessionFromCache(context);
			if (session != null && session.isOpened() && SharedPreference.containKey(context, Preference.UID)) {
				query.queryRecommendPersonInfo(session, uid);
			} else publishResult(null, RESULT_INVALID);
		} else publishResult(null, RESULT_NO_INTERNET);
	}
	
	/**
	 * publish result back to the activity
	 * @param event
	 * @param result type
	 */
	private void publishResult(RecommendUserInfo info, int resultType) {
		Intent intent = new Intent(NOTIFICATION);
		intent.putExtra("result_type", resultType);
		if (resultType == RESULT_OK) intent.putExtra("data", info);
		sendBroadcast(intent);
	}
}
