package com.hitchlab.tinkle.objects;

public class MyNotificationCreator {
	
	/**
	 * Create a new notification for newly invited event
	 * @param uid
	 * @param event
	 * @return MyNotification
	 */
	public static MyNotification createInvitedEventNotification(String uid, FbEvent event) {
		MyNotification notification = new MyNotification();
		notification.setViewed(false);
		notification.setUid(uid);
		notification.setType(MyNotification.TYPE_INVITED_EVENT);
		notification.setTime(System.currentTimeMillis());
		notification.setMessage("you are invited to event");
		notification.setMessageExtra1(event.getName());
		notification.setMessageExtra2(event.getId() + "," + event.getStart_time() + "," + event.getPicture());
		return notification;
	}
	
	/**
	 * Create a new notification when your friend attend a new event
	 * @param uid
	 * @param friendUid
	 * @param event
	 * @return MyNotification
	 */
	public static MyNotification createEventFriendInterestedNotification(String uid, String friendUid, String friendName, FbEvent event) {
		MyNotification notification = new MyNotification();
		notification.setViewed(false);
		notification.setUid(uid);
		notification.setType(MyNotification.TYPE_FRIEND_JOIN_EVENT);
		notification.setTime(System.currentTimeMillis());
		notification.setMessage("<b>" + friendName + "</b> is interested in event");
		notification.setMessageExtra1(event.getName());
		notification.setMessageExtra2(event.getId() + "," + event.getStart_time() + "," + event.getPicture());
		notification.setExtraInfo(friendUid);
		return notification;
	}
}
