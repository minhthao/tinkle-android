package com.hitchlab.tinkle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.hitchlab.tinkle.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.hitchlab.tinkle.appevent.EventFullActivity;
import com.hitchlab.tinkle.datasource.NotificationDataSource;
import com.hitchlab.tinkle.objects.MyNotification;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;
import com.hitchlab.tinkle.service.NotificationViewChangeService;
import com.hitchlab.tinkle.service.RefreshNotification;
import com.hitchlab.tinkle.supports.FrenventNotification;
import com.hitchlab.tinkle.template.NotificationListAdapter;

import eu.erikw.PullToRefreshListView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

public class MenuNotificationActivity extends Activity {

	private Context context;
	private NotificationListAdapter adapter;

	private PullToRefreshListView list;
	private TextView backButton;
	
	private boolean isLaunchFromNotif;

	/**
	 * receiver for my events service
	 */
	private BroadcastReceiver notificationsReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			ArrayList<MyNotification> notifications = intent.getParcelableArrayListExtra("data");
			Collections.sort(notifications, new Comparator<MyNotification>() {
				@Override
				public int compare(MyNotification lhs, MyNotification rhs) {
					if (lhs.getTime() > rhs.getTime()) return -1;
					if (lhs.getTime() < rhs.getTime()) return 1;
					return 0;
				}
			});
			
			adapter.setNotifications(notifications);
			adapter.notifyDataSetChanged();
			if (list.isRefreshing()) list.onRefreshComplete();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_notifications_activity);
		this.context = this;
		this.isLaunchFromNotif = getIntent().getBooleanExtra(FrenventNotification.LAUNCH_FROM_NOTIF, false);
		this.adapter = new NotificationListAdapter(context);
		this.backButton = (TextView) findViewById(R.id.main_view_notifications_back_button);
		backButton.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isLaunchFromNotif) {
					Intent mainClass = new Intent(context, MainActivity.class);
					startActivity(mainClass);
					finish();
				}
				onBackPressed();
			}
		});
		
		initList();
		
		NotificationDataSource notificationDataSource = new NotificationDataSource(context);
		ArrayList<MyNotification> notifications = notificationDataSource.getNotifications();
		if (notifications == null || notifications.size() == 0) {
			getNotifications();
		} else {
			Collections.sort(notifications, new Comparator<MyNotification>() {
				@Override
				public int compare(MyNotification lhs, MyNotification rhs) {
					if (lhs.getTime() > rhs.getTime()) return -1;
					if (lhs.getTime() < rhs.getTime()) return 1;
					return 0;
				}
			});
			
			adapter.setNotifications(notifications);
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	}
	
	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(notificationsReceiver);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(notificationsReceiver, new IntentFilter(RefreshNotification.NOTIFICATION));
	}

	/**
	 * Init the list as well as its item click listener
	 */
	private void initList() {
		list = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_notification_list);
		list.setAdapter(adapter);
		list.setDivider(new ColorDrawable(0xffc2c2c2));
		list.setDividerHeight(2);

		list.setOnItemClickListener(new PullToRefreshListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				if (adapter.isEnabled(position)) {
					MyNotification notification = adapter.getNotificationItem(position);
					if (!notification.isViewed()) {
						Intent intent = new Intent(context, NotificationViewChangeService.class);
						intent.putExtra("mNotification", notification);
						context.startService(intent);
					}

					if (notification.getType() == MyNotification.TYPE_FRIEND_JOIN_EVENT ||
							notification.getType() == MyNotification.TYPE_INVITED_EVENT) {
						String[] eventInfos = notification.getMessageExtra2().split(",");
						Intent intent = new Intent(context, EventFullActivity.class);
						intent.putExtra("eid", eventInfos[0]);
						context.startActivity(intent);
					}
				}
			}	
		});

		list.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				getNotifications();
			}
		}); 
	}
	
	/**
	 * get the notification
	 */
	private void getNotifications() {
		SharedPreference.updateSharedPref(context, Preference.NOTIFICATION_LAST_VIEWED_TIME, System.currentTimeMillis());
		list.setRefreshing();
		Intent intent = new Intent(context, RefreshNotification.class);
		context.startService(intent);
	}
}
