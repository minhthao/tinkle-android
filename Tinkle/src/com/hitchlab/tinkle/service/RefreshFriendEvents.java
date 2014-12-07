package com.hitchlab.tinkle.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;

import com.facebook.Session;
import com.hitchlab.tinkle.datasource.AttendeeDataSource;
import com.hitchlab.tinkle.datasource.FriendDataSource;
import com.hitchlab.tinkle.datasource.FriendEventDataSource;
import com.hitchlab.tinkle.datasource.NotificationDataSource;
import com.hitchlab.tinkle.dbrequest.EventRequest;
import com.hitchlab.tinkle.dbrequest.NotificationRequest;
import com.hitchlab.tinkle.dbrequest.UserRequest;
import com.hitchlab.tinkle.fbquery.QueryFriendEvents;
import com.hitchlab.tinkle.objects.Attendee;
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

public class RefreshFriendEvents extends IntentService{

	public static final String NOTIFICATION = "com.hitchlab.tinkle.service.RefreshFriendEvents";

	private Context context;
	private QueryFriendEvents queryFriendEvents;
	private FriendEventDataSource friendEventDataSource;
	private NotificationDataSource notificationDataSource;
	private AttendeeDataSource attendeeDataSource;
	private FriendDataSource friendDataSource;

	private boolean isInit;
	private boolean isNotificationActive;
	private String uid;

	public RefreshFriendEvents() {
		super("RefreshFriendEvents");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		this.context = this;
		this.friendEventDataSource = new FriendEventDataSource(context);
		this.notificationDataSource = new NotificationDataSource(context);
		this.attendeeDataSource = new AttendeeDataSource(context);
		this.friendDataSource = new FriendDataSource(context);
		this.isInit = SharedPreference.getPrefBooleanValue(context, Preference.DID_FRIEND_EVENTS_INIT);

		if (SharedPreference.getPrefBooleanValue(this, Preference.NOTIFICATIONS)) {
			isNotificationActive = SharedPreference.getPrefBooleanValue(context, Preference.NOTIFICATION_FRIENDS);
		} else isNotificationActive = false;

		boolean hasInternet = Internet.hasActiveInternetConnection(context);
		Session session = Session.getActiveSession();
		if (session == null) session = Session.openActiveSessionFromCache(context);
		if (hasInternet && session != null && session.isOpened() && SharedPreference.containKey(context, Preference.UID)) {
			this.uid = SharedPreference.getPrefStringValue(context, Preference.UID);
			setupQueryFriendEvents();
			queryFriendEvents.queryEventsAndWait(session);
		}
	}

	/**
	 * Setup the query friend events
	 */
	private void setupQueryFriendEvents() {
		queryFriendEvents = new QueryFriendEvents() {
			@Override
			public void onMyQueryCompleted(ArrayList<FbEvent> events) {
				HashMap<String, Integer> friendToNumEvents = new HashMap<String, Integer>();
				ArrayList<FbEvent> eventsToBeUpload = new ArrayList<FbEvent>();
				ArrayList<MyNotification> notificationsToBeUpload = new ArrayList<MyNotification>();

				for (FbEvent event : events) {
					ArrayList<Attendee> attendees = event.getFriendsAttending();

					if (!friendEventDataSource.isEventExist(event.getId())) {
						eventsToBeUpload.add(event);
						for (Attendee attendee : attendees) { 
							attendeeDataSource.addUserEventPair(uid, event.getId());
							notificationsToBeUpload.add(MyNotificationCreator.createEventFriendInterestedNotification(uid, attendee.getUid(), attendee.getName(), event));
						}
					} else {
						boolean needUpdate = false;
						for (Attendee attendee : attendees) {
							if (!attendeeDataSource.isUserEventPairExist(uid, event.getId())) {
								needUpdate = true;
								attendeeDataSource.addUserEventPair(uid, event.getId());
								notificationsToBeUpload.add(MyNotificationCreator.createEventFriendInterestedNotification(uid, attendee.getUid(), attendee.getName(), event));
							}
						}

						if (needUpdate) friendEventDataSource.updateAttendees(event.getId(), Attendee.getAttendeesStrFromList(attendees));
					}

					//here, we update the number of events in friend list
					for (Attendee attendee : attendees) { 
						if (friendToNumEvents.containsKey(attendee.getUid())) {
							int newNum = friendToNumEvents.get(attendee.getUid()) + 1;
							friendToNumEvents.put(attendee.getUid(), newNum);
						} else friendToNumEvents.put(attendee.getUid(), 1);
					}
				}

				friendDataSource.updateNumberOfEvents(friendToNumEvents);

				if (!isInit) {
					try {
						UserRequest.addUser(uid, SharedPreference.getPrefStringValue(context, Preference.USERNAME), 0, events.size());
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}
				
				if (eventsToBeUpload.size() > 0) {
					friendEventDataSource.addEvents(eventsToBeUpload);
					try { 
						UserRequest.updateUserFriendEventsSize(uid, events.size());
						EventRequest.addEvents(eventsToBeUpload); 
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
				SharedPreference.updateSharedPref(context, Preference.DID_FRIEND_EVENTS_INIT, true);
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
