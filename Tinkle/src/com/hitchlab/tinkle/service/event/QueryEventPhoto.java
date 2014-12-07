package com.hitchlab.tinkle.service.event;

import java.util.ArrayList;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.facebook.Session;
import com.hitchlab.tinkle.fbquery.event.QueryPhotos;
import com.hitchlab.tinkle.objects.Photo;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;
import com.hitchlab.tinkle.supports.Internet;

public class QueryEventPhoto extends IntentService{
	
	public static final String NOTIFICATION = "com.hitchlab.tinkle.service.event.QueryEventPhoto";
	
	public static final int RESULT_OK = 0;
	public static final int RESULT_INVALID = 2;
	public static final int RESULT_NO_INTERNET = 3;
	
	private Context context;
	private String eid;
	private QueryPhotos query;
	
	public QueryEventPhoto() {
		super("QueryEventPhoto");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		this.context = this;
		this.eid = intent.getStringExtra("eid");
		if (query == null) query = new QueryPhotos() {
			@Override
			protected void onPhotosLoaded(ArrayList<Photo> photos) {
				publishResult(photos, RESULT_OK);
			}
		};
		
		boolean hasInternet = Internet.hasActiveInternetConnection(context);
		if (hasInternet) {
			Session session = Session.getActiveSession();
			if (session != null && session.isOpened() && SharedPreference.containKey(context, Preference.UID)) {
				query.queryAllPhotos(session, eid);
			} else publishResult(null, RESULT_INVALID);
		} else publishResult(null, RESULT_NO_INTERNET);
	}
	
	/**
	 * publish result back to the activity
	 * @param event
	 * @param result type
	 */
	private void publishResult(ArrayList<Photo> photos, int resultType) {
		Intent intent = new Intent(NOTIFICATION);
		intent.putExtra("result_type", resultType);
		if (resultType == RESULT_OK) intent.putParcelableArrayListExtra("data", photos);
		sendBroadcast(intent);
	}
}
