package com.hitchlab.tinkle.service;

import com.hitchlab.tinkle.calendar.CalendarHelper;
import com.hitchlab.tinkle.datasource.AttendeeDataSource;
import com.hitchlab.tinkle.datasource.CalendarEventDataSource;
import com.hitchlab.tinkle.datasource.FriendDataSource;
import com.hitchlab.tinkle.datasource.FriendEventDataSource;
import com.hitchlab.tinkle.datasource.MyEventDataSource;
import com.hitchlab.tinkle.datasource.NotificationDataSource;
import com.hitchlab.tinkle.datasource.PublicEventDataSource;
import com.hitchlab.tinkle.preference.SharedPreference;

import android.app.IntentService;
import android.content.Intent;

public class LogOutService extends IntentService {


	public LogOutService() {
		super("LogOutService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		long calId = CalendarHelper.getCalendar(this);
		if (calId != -1) CalendarHelper.deleteCalendar(this, calId);
		CalendarEventDataSource calendarDataSource = new CalendarEventDataSource(this);
		calendarDataSource.removeEvents();

		SharedPreference.clear(this);

		//remove all rows in friends table
		FriendDataSource friendDataSource = new FriendDataSource(this);
		friendDataSource.removeFriends();

		//remove all rows in my events table
		MyEventDataSource myEventDataSource = new MyEventDataSource(this);
		myEventDataSource.removeEvents();

		//remove all rows in friends events table
		FriendEventDataSource friendEventDataSource = new FriendEventDataSource(this);
		friendEventDataSource.removeEvents();

		//remove all rows in public events table
		PublicEventDataSource publicEventDataSource = new PublicEventDataSource(this);
		publicEventDataSource.removeEvents();

		NotificationDataSource notificationDataSource = new NotificationDataSource(this);
		notificationDataSource.removeNotifications();
		
		AttendeeDataSource attendeeDataSource = new AttendeeDataSource(this);
		attendeeDataSource.clear();
	}
}
