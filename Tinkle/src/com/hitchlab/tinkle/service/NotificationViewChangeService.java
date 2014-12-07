package com.hitchlab.tinkle.service;

import org.json.JSONException;

import com.hitchlab.tinkle.datasource.NotificationDataSource;
import com.hitchlab.tinkle.dbrequest.NotificationRequest;
import com.hitchlab.tinkle.objects.MyNotification;

import android.app.IntentService;
import android.content.Intent;

public class NotificationViewChangeService extends IntentService{
	
	public NotificationViewChangeService() {
		super("NotificationViewChangeService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		MyNotification notification = intent.getParcelableExtra("mNotification");
		if (notification != null) {
			NotificationDataSource notificationDataSource = new NotificationDataSource(this);
			notificationDataSource.updateViewed(notification);
			try {
				NotificationRequest.updateNotification(notification);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
