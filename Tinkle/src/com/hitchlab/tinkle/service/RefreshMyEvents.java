package com.hitchlab.tinkle.service;

import java.util.ArrayList;

import org.json.JSONException;

import com.facebook.Session;
import com.hitchlab.tinkle.datasource.MyEventDataSource;
import com.hitchlab.tinkle.datasource.NotificationDataSource;
import com.hitchlab.tinkle.dbrequest.EventRequest;
import com.hitchlab.tinkle.dbrequest.NotificationRequest;
import com.hitchlab.tinkle.dbrequest.UserRequest;
import com.hitchlab.tinkle.fbquery.QueryMyEvents;
import com.hitchlab.tinkle.objects.FbEvent;
import com.hitchlab.tinkle.objects.MyNotification;
import com.hitchlab.tinkle.objects.MyNotificationCreator;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;
import com.hitchlab.tinkle.supports.FrenventNotification;
import com.hitchlab.tinkle.supports.Internet;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class RefreshMyEvents extends IntentService{

	public static final String NOTIFICATION = "com.hitchlab.tinkle.service.RefreshMyEvents";

	private Context context;
	private QueryMyEvents queryMyEvents;

	private MyEventDataSource myEventDataSource;
	private NotificationDataSource notificationDataSource;

	private boolean isInit;
	private boolean isNotificationActive;
	private String uid;

	public RefreshMyEvents() {
		super("RefreshMyEvents");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		this.context = this;
		this.myEventDataSource = new MyEventDataSource(context);
		this.notificationDataSource = new NotificationDataSource(context);
		this.isInit = SharedPreference.getPrefBooleanValue(context, Preference.DID_MY_EVENTS_INIT);
		
		if (SharedPreference.getPrefBooleanValue(this, Preference.NOTIFICATIONS)) {
			isNotificationActive = SharedPreference.getPrefBooleanValue(context, Preference.NOTIFICATION_EVENT_INVITES);
		} else isNotificationActive = false;
		
		boolean hasInternet = Internet.hasActiveInternetConnection(context);
		Session session = Session.getActiveSession();
		if (session == null) session = Session.openActiveSessionFromCache(context);
		if (hasInternet && session != null && session.isOpened() && SharedPreference.containKey(context, Preference.UID)) {
			this.uid = SharedPreference.getPrefStringValue(context, Preference.UID);
			setupQueryMyEvents();
			queryMyEvents.queryEventsAndWait(session);
		}
	}

	/**
	 * Setup the query my events
	 */
	private void setupQueryMyEvents() {
		queryMyEvents = new QueryMyEvents() {
			@Override
			public void onQueryCompleted(ArrayList<FbEvent> events) {
				ArrayList<FbEvent> eventsToBeUpload = new ArrayList<FbEvent>();
				ArrayList<MyNotification> notificationsToBeUpload = new ArrayList<MyNotification>();
				
				for (FbEvent event : events) {
					if (!myEventDataSource.isEventExist(event.getId())) {
						eventsToBeUpload.add(event);
						notificationsToBeUpload.add(MyNotificationCreator.createInvitedEventNotification(uid, event));
					}
				}
				
				if (eventsToBeUpload.size() > 0) {
					myEventDataSource.addEvents(eventsToBeUpload);
					try { 
						UserRequest.updateUserMyEventsSize(uid, events.size());
						EventRequest.addEvents(events); 
					} catch (JSONException e) { 
						e.printStackTrace(); 
					}
				}

				if (isInit && notificationsToBeUpload.size() > 0) {
					notificationDataSource.addNotifications(notificationsToBeUpload);
					try { NotificationRequest.addNotifications(notificationsToBeUpload); }
					catch (JSONException e) { e.printStackTrace(); }
					
					if (isNotificationActive) {
						for (MyNotification notification : notificationsToBeUpload)
							FrenventNotification.pushNotification(context, notification);
					}
				}
				
				publishResults(events);
				SharedPreference.updateSharedPref(context, Preference.DID_MY_EVENTS_INIT, true);
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
