package com.hitchlab.tinkle.service;

import com.facebook.Session;
import com.hitchlab.tinkle.supports.Internet;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public abstract class ServiceStarter {
	private Context context;
	
	public ServiceStarter(Context context) {
		this.context = context;
	}
	
	public abstract void noInternet();
	public abstract void sessionClosed();

	/**
	 * Update a list of user events
	 */
	public void updateUsersEvents(String uid) {
		final String mUid = uid;
		
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... arg0) {
				return Internet.hasActiveInternetConnection(context);
			}

			@Override
			protected void onPostExecute(Boolean hasInternet) {
				if (hasInternet) {
					Session session = Internet.getFacebookSession(context);
					if (session != null && session.isOpened()) {
						Intent intent = new Intent(context, QueryFbUserEvents.class);
						intent.putExtra(QueryFbUserEvents.UID, mUid);
						context.startService(intent);
					} else sessionClosed();
				} else noInternet(); 
			}
		}.execute();
	}
	
	/**
	 * Update a list friends
	 */
	public void updateFriends() {
		
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... arg0) {
				return Internet.hasActiveInternetConnection(context);
			}

			@Override
			protected void onPostExecute(Boolean hasInternet) {
				if (hasInternet) {
					Session session = Internet.getFacebookSession(context);
					if (session != null && session.isOpened()) {
						Intent intent = new Intent(context, RefreshFriends.class);
						context.startService(intent);
					} else sessionClosed();
				} else noInternet(); 
			}
		}.execute();
	}
	
	/**
	 * Update the list of Friends events
	 */
	public void updateFriendsEvents() {
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... arg0) {
				return Internet.hasActiveInternetConnection(context);
			}

			@Override
			protected void onPostExecute(Boolean hasInternet) {
				if (hasInternet) {
					Session session = Internet.getFacebookSession(context);
					if (session != null && session.isOpened()) {
						Intent intent = new Intent(context, RefreshFriendEvents.class);
						context.startService(intent);
					} else sessionClosed();
				} else noInternet();
			}
		}.execute();
	}


	/**
	 * Update the list of my events
	 */
	public void updateMyEvents() {
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... arg0) {
				return Internet.hasActiveInternetConnection(context);
			}

			@Override
			protected void onPostExecute(Boolean hasInternet) {
				if (hasInternet) {
					Session session = Internet.getFacebookSession(context);
					if (session != null && session.isOpened()) {
						Intent intent = new Intent(context, RefreshMyEvents.class);
						context.startService(intent);
					} else sessionClosed();
				} else noInternet();
			}
		}.execute();
	}
	
	/**
	 * Update the list of my past events
	 */
	public void updatePastEvents() {
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... arg0) {
				return Internet.hasActiveInternetConnection(context);
			}

			@Override
			protected void onPostExecute(Boolean hasInternet) {
				if (hasInternet) {
					Session session = Internet.getFacebookSession(context);
					if (session != null && session.isOpened()) {
						Intent intent = new Intent(context, RefreshMyPastEvents.class);
						context.startService(intent);
					} else sessionClosed();
				} else noInternet();
			}
		}.execute();
	}

	/**
	 * Update the list of public events
	 * @param Context
	 */
	public void updateNearbyEvents(Context mContext) {
		final Context context = mContext;
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... arg0) {
				return Internet.hasActiveInternetConnection(context);
			}

			@Override
			protected void onPostExecute(Boolean hasInternet) {
				if (hasInternet) {
					Intent intent = new Intent(context, RefreshNearbyEvents.class);
					context.startService(intent);
				} else noInternet();
			}
		}.execute();
	}
}
