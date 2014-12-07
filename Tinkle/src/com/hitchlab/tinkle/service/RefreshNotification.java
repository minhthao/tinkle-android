package com.hitchlab.tinkle.service;

import java.util.ArrayList;

import org.json.JSONException;

import com.hitchlab.tinkle.datasource.NotificationDataSource;
import com.hitchlab.tinkle.dbrequest.NotificationRequest;
import com.hitchlab.tinkle.objects.MyNotification;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class RefreshNotification extends IntentService{
	public static final String NOTIFICATION = "com.hitchlab.tinkle.service.RefreshNotification";

	private Context context;

	public RefreshNotification() {
		super("RefreshNotification");
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		this.context = this;
		NotificationDataSource notificationDataSource = new NotificationDataSource(context);
		ArrayList<MyNotification> notifications = new ArrayList<MyNotification>();
		try { 
			notifications = NotificationRequest.getNotifications(this); 
			if (notifications != null && notifications.size() > 0) {
				notificationDataSource.addNotifications(notifications);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		publishResults(notifications);
	}

	/**
	 * publish the result
	 * @param ArrayList<MyNotification>
	 */
	private void publishResults(ArrayList<MyNotification> notifications) {
		Intent intent = new Intent(NOTIFICATION);
		intent.putParcelableArrayListExtra("data", notifications);
		sendBroadcast(intent);
	}

}
