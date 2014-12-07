package com.hitchlab.tinkle.supports;

import com.hitchlab.tinkle.R;
import com.hitchlab.tinkle.MenuNotificationActivity;
import com.hitchlab.tinkle.datasource.NotificationDataSource;
import com.hitchlab.tinkle.objects.MyNotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.Html;

public class FrenventNotification {
	public static final String LAUNCH_FROM_NOTIF = "notif_launch";

	public static void removeNotification(Context context) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
		NotificationDataSource notificationDataSource = new NotificationDataSource(context);
		notificationDataSource.updateClicked();
	}

	public static void pushNotification(Context context, MyNotification notif) {
		if (notif.getType() == MyNotification.TYPE_FRIEND_TODAY_EVENTS)
			pushTypeTodayEventsNotifications(context, notif);
		
		if (notif.getType() == MyNotification.TYPE_FRIEND_JOIN_EVENT)
			pushTypeFriendJoinEvent(context, notif);
		
		if (notif.getType() == MyNotification.TYPE_INVITED_EVENT)
			pushNewInvitedEvent(context, notif);
	}
	
	/**
	 * make notification when you get new event invited
	 * @param context
	 * @param notif
	 */
	private static void pushNewInvitedEvent(Context context, MyNotification notif) {
		PendingIntent pending = prepareEventbookIntent(context);
		NotificationDataSource notificationDataSource = new NotificationDataSource(context);
		int numNotif = notificationDataSource.getNumNotViewed(MyNotification.TYPE_INVITED_EVENT);
		if (numNotif == 0) createNotification(context, pending, notif.getMessage(), MyNotification.TYPE_INVITED_EVENT);
		else createNotification(context, pending, "you got invited to " + numNotif + " new events", MyNotification.TYPE_INVITED_EVENT);
	}
	
	/**
	 * Make notification when your friend expressed interest in the event
	 * @param context
	 * @param notif
	 */
	private static void pushTypeFriendJoinEvent(Context context, MyNotification notif) {
		PendingIntent pending = prepareEventbookIntent(context);
		NotificationDataSource notificationDataSource = new NotificationDataSource(context);
		int numNotif = notificationDataSource.getNumNotViewed(MyNotification.TYPE_FRIEND_JOIN_EVENT);
		if (numNotif == 0) createNotification(context, pending, notif.getMessage(), MyNotification.TYPE_FRIEND_JOIN_EVENT);
		else createNotification(context, pending, numNotif + " of your friends replied to events", MyNotification.TYPE_FRIEND_JOIN_EVENT);
	}

	/**
	 * make notification for the type today events
	 * @param context
	 * @param notif
	 */
	private static void pushTypeTodayEventsNotifications(Context context, MyNotification notif) {
		PendingIntent pending = prepareEventbookIntent(context);
		createNotification(context, pending, notif.getMessage(), MyNotification.TYPE_FRIEND_TODAY_EVENTS);
	}
	
	/**
	 * prepare the action for when the user click on the notification
	 * @param context
	 * @return PendingIntent
	 */
	private static PendingIntent prepareEventbookIntent(Context context) {
		Intent intent = new Intent(context, MenuNotificationActivity.class);
		intent.putExtra(LAUNCH_FROM_NOTIF, true);
		PendingIntent pending = PendingIntent.getActivity(context, 0, intent, 0);
		return pending;
	}
	
	/**
	 * Create the actual notification
	 * @param context
	 * @param intent
	 * @param message
	 * @param id
	 */
	private static void createNotification(Context context, PendingIntent intent, String message, int id) {
		Notification notification = new Notification.Builder(context)
		.setContentTitle("Frenvent")
		.setContentText(Html.fromHtml(message))
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentIntent(intent).build();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(id, notification);
	}
}
