package com.hitchlab.tinkle.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.facebook.Session;
import com.hitchlab.tinkle.datasource.FriendDataSource;
import com.hitchlab.tinkle.datasource.NotificationDataSource;
import com.hitchlab.tinkle.dbrequest.NotificationRequest;
import com.hitchlab.tinkle.fbquery.QueryTodayEventGoers;
import com.hitchlab.tinkle.objects.Friend;
import com.hitchlab.tinkle.objects.MyNotification;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;
import com.hitchlab.tinkle.supports.FrenventNotification;
import com.hitchlab.tinkle.supports.Internet;
import com.hitchlab.tinkle.supports.TimeFrame;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CheckNotificationService extends IntentService {

	private Context context;
	private NotificationDataSource notificationDataSource;
	private FriendDataSource friendDataSource;
	private String uid;
	private boolean isNotificationEnable;

	private ArrayList<MyNotification> myNotifications;

	public CheckNotificationService() {
		super("CheckNotificationService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		this.context = this;
		boolean hasInternet = Internet.hasActiveInternetConnection(context);
		Session session = Session.getActiveSession();
		if (session == null) 
			try {
				session = Session.openActiveSessionFromCache(context);
			} catch (UnsupportedOperationException e) {
				Log.e("fbSession", "error open fb session");
			}

		if (hasInternet && session != null && session.isOpened()) {
			if (SharedPreference.containKey(this, Preference.UID)) {
				myNotifications = new ArrayList<MyNotification>();
				uid = SharedPreference.getPrefStringValue(this, Preference.UID);
				isNotificationEnable = SharedPreference.getPrefBooleanValue(this, Preference.NOTIFICATIONS);
				friendDataSource = new FriendDataSource(this);
				notificationDataSource = new NotificationDataSource(this);

				checkDailyEventNotification(session);

				
				try {
					NotificationRequest.addNotifications(myNotifications);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Check the daily event notification
	 * @param session
	 */
	private void checkDailyEventNotification(Session session) {
		if (TimeFrame.getCurrentHour() == 9) {
			QueryTodayEventGoers query = new QueryTodayEventGoers() {
				@Override
				public void onQueryCompleted(ArrayList<String> attendees) {
					if (attendees != null && attendees.size() > 0) {
						MyNotification notification = new MyNotification();
						notification.setViewed(false);
						notification.setUid(uid);
						notification.setType(MyNotification.TYPE_FRIEND_TODAY_EVENTS);
						notification.setTime(System.currentTimeMillis());
						notification.setMessageExtra1(attendees.toString());
						if (attendees.size() == 1) {
							Friend friend = friendDataSource.getFriendWithUid(attendees.get(0));
							if (friend != null) notification.setMessage("<b>" + friend.getName() + "</b> is going out today");
							else notification.setMessage("<b>1</b> friend is going to event today");
						} else {
							Friend friend = friendDataSource.getFriendWithUid(attendees.get(0));
							if (friend != null) notification.setMessage("<b>" + friend.getName() + "</b> and <b>" + String.valueOf(attendees.size() - 1) + " others</b> are going out today");
							else notification.setMessage("<b>" + attendees.size() + "</b> of your friends are going to event today");
						}
						notificationDataSource.addNotification(notification);
						myNotifications.add(notification);

						if (isNotificationEnable) FrenventNotification.pushNotification(context, notification);
					}
				}
			};
			query.queryTodayEventsAttendees(session);
		}
	}

	/**
	 * Chop list into sublist of length L
	 * @param List of object
	 * @param max length of the result list
	 * @return list containing all the sublist
	 */
	public static <T> List<List<T>> chopped(List<T> list, int L) {
		List<List<T>> parts = new ArrayList<List<T>>();
		final int N = list.size();
		for (int i = 0; i < N; i += L) 
			parts.add(new ArrayList<T>(list.subList(i, Math.min(N, i + L))));
		return parts;
	}
}
