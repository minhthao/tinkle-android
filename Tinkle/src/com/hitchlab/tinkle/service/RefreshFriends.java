package com.hitchlab.tinkle.service;

import java.util.ArrayList;

import com.facebook.Session;
import com.hitchlab.tinkle.datasource.FriendDataSource;
import com.hitchlab.tinkle.fbquery.QueryFriends;
import com.hitchlab.tinkle.objects.Friend;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;
import com.hitchlab.tinkle.supports.Internet;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class RefreshFriends extends IntentService{

	public static final String NOTIFICATION = "com.hitchlab.tinkle.service.RefreshFriends";

	private Context context;
	private QueryFriends queryFriends;

	public RefreshFriends() {
		super("RefreshFriends");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		this.context = this;
		if (queryFriends == null) setupQueryFriends();
		
		boolean hasInternet = Internet.hasActiveInternetConnection(context);
		Session session = Session.getActiveSession();
		if (session == null) session = Session.openActiveSessionFromCache(context);
		if (hasInternet && session != null && session.isOpened() && SharedPreference.containKey(context, Preference.UID)) 
			queryFriends.queryAllFriendsAndWait(session);
	}

	/**
	 * Setup the query friends
	 */
	private void setupQueryFriends() {
		queryFriends = new QueryFriends() {
			@Override
			public void onQueryCompleted(ArrayList<Friend> friends) {
				FriendDataSource friendDataSource = new FriendDataSource(context);
				friendDataSource.updateFriendsList(friends);
				
				publishResults(friends);
				SharedPreference.updateSharedPref(context, Preference.DID_FRIENDS_INIT, true);
			}

			@Override
			public void onQueryCompleted(String name, String work,
					String coverUrl) {
				// TODO Auto-generated method stub
			}
		};
	}
	
	/**
	 * publish the results
	 * @param friends
	 */
	private void publishResults(ArrayList<Friend> friends) {
		Intent intent = new Intent(NOTIFICATION);
		intent.putParcelableArrayListExtra("data", friends);
		sendBroadcast(intent);
	}
}
