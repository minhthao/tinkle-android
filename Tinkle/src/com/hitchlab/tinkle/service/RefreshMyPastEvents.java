package com.hitchlab.tinkle.service;

import java.util.ArrayList;

import org.json.JSONException;

import com.facebook.Session;
import com.hitchlab.tinkle.datasource.MyEventDataSource;
import com.hitchlab.tinkle.dbrequest.EventRequest;
import com.hitchlab.tinkle.fbquery.QueryMyEvents;
import com.hitchlab.tinkle.objects.FbEvent;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;
import com.hitchlab.tinkle.supports.Internet;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class RefreshMyPastEvents extends IntentService{

	public static final String NOTIFICATION = "com.hitchlab.tinkle.service.RefreshMyPastEvents";

	private Context context;
	private QueryMyEvents queryMyEvents;

	private MyEventDataSource myEventDataSource;
	
	public RefreshMyPastEvents() {
		super("RefreshMyPastEvents");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		this.context = this;
		this.myEventDataSource = new MyEventDataSource(context);
		if (queryMyEvents == null) setupQueryMyEvents();
		
		boolean hasInternet = Internet.hasActiveInternetConnection(context);
		Session session = Session.getActiveSession();
		if (session == null) session = Session.openActiveSessionFromCache(context);
		if (hasInternet && session != null && session.isOpened() 
				&& SharedPreference.containKey(context, Preference.UID)
				&& !SharedPreference.getPrefBooleanValue(context, Preference.DID_PAST_EVENTS_INIT)) {
			SharedPreference.updateSharedPref(context, Preference.DID_PAST_EVENTS_INIT, true);
			queryMyEvents.queryPastEventsAndWait(session);
		}
	}

	/**
	 * Setup the query my events
	 */
	private void setupQueryMyEvents() {
		queryMyEvents = new QueryMyEvents() {
			@Override
			public void onQueryCompleted(ArrayList<FbEvent> events) {
				ArrayList<FbEvent> pastEvents = new ArrayList<FbEvent>();
				for (FbEvent event : events) {
					if (!myEventDataSource.isEventExist(event.getId()))
						pastEvents.add(event);
				}
				
				myEventDataSource.addEvents(pastEvents);
				publishResults(events);
				
				try { EventRequest.addEvents(events); } 
				catch (JSONException e) { e.printStackTrace(); }
			}
		};
	}

	/**
	 * publish the results
	 * @param events
	 */
	private void publishResults(ArrayList<FbEvent> events) {
		Intent intent = new Intent(NOTIFICATION);
		intent.putParcelableArrayListExtra("data", events);
		sendBroadcast(intent);
	}
	
}
