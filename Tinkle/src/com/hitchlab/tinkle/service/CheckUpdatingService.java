package com.hitchlab.tinkle.service;

import com.facebook.Session;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;
import com.hitchlab.tinkle.supports.Internet;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class CheckUpdatingService extends IntentService {

	public CheckUpdatingService() {
		super("CheckUpdatingService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		boolean hasInternet = Internet.hasActiveInternetConnection(this);
		Session session = Session.getActiveSession();
		if (session == null) 
			try {
				session = Session.openActiveSessionFromCache(this);
			} catch (UnsupportedOperationException e) {
				Log.e("fbSession", "error open fb session");
			}
		if (hasInternet && session != null && session.isOpened()) {
			if (SharedPreference.containKey(this, Preference.UID)) {
				//update friend list
				Intent friendsIntent = new Intent(this, RefreshFriends.class);
				startService(friendsIntent);
				
				//update friends' events
				Intent friendEventsIntent = new Intent(this, RefreshFriendEvents.class);
				startService(friendEventsIntent);
				
				//update my events
				Intent myEventsIntent = new Intent(this, RefreshMyEvents.class);
				startService(myEventsIntent);
				
				//update the nearby events
				Intent nearbyEventsIntent = new Intent(this, RefreshNearbyEvents.class);
				startService(nearbyEventsIntent);
			}
		}
	}
}
