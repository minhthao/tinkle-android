package com.hitchlab.tinkle;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.hitchlab.tinkle.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;
import com.hitchlab.tinkle.service.CheckSyncCalendarService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class MenuSettingsActivity extends Activity{

	public static final boolean syncMinApi = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH);

	private Context context;

	private View notificationView;
	private View notificationSwitch;
	private View notificationEventsInvite;
	private View notificationEventsActivity;
	private View notificationFriends;

	private View syncView;
	private View syncSwitch;
	private TextView syncRecap;
	private View syncEventsReminder;
	private View syncEventsReminderTime;
	private EditText syncEventsReminderTimeSet;
	private TextView backButton;
	
	private boolean originalNotif;
	private boolean originalNotifInvite;
	private boolean originalNotifEventAct;
	private boolean originalNotifFriendEvent;
	private boolean	originalSync;
	private boolean originalSyncReminder;
	private int originalSyncReminderTime;
	
	private boolean currentNotif;
	private boolean currentNotifInvite;
	private boolean currentNotifEventAct;
	private boolean currentNotifFriendEvent;
	private boolean	currentSync;
	private boolean currentSyncReminder;
	private int currentSyncReminderTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_settings_activity);
		this.context = this; 
		this.backButton = (TextView) findViewById(R.id.main_view_settings_back_button);
		backButton.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		getValuesFromPref();
		initViewComponents();
		showSettings();
		setSettingComponentsClickListener();
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
	protected void onDestroy() {
		super.onDestroy();
		//On destroy, we check whether if anything is change and update accordingly
		if (currentNotif != originalNotif) SharedPreference.updateSharedPref(context, Preference.NOTIFICATIONS, currentNotif);
		if (currentNotifInvite != originalNotifInvite) SharedPreference.updateSharedPref(context, Preference.NOTIFICATION_EVENT_INVITES, currentNotifInvite);
		if (currentNotifFriendEvent != originalNotifFriendEvent) SharedPreference.updateSharedPref(context, Preference.NOTIFICATION_FRIENDS, currentNotifFriendEvent);
		if (currentNotifEventAct != originalNotifEventAct) SharedPreference.updateSharedPref(context, Preference.NOTIFICATION_EVENT_ACTIVITIES, currentNotifEventAct);
		
		if (currentSyncReminderTime != originalSyncReminderTime) SharedPreference.updateSharedPref(context, Preference.SYNC_EVENT_REMINDERS_TIME, currentSyncReminderTime);
		if (currentSyncReminder != originalSyncReminder) {
			SharedPreference.updateSharedPref(context, Preference.SYNC_EVENT_REMINDERS, currentSyncReminder);
			Intent service = new Intent(context, CheckSyncCalendarService.class);
			service.putExtra(CheckSyncCalendarService.TYPE, CheckSyncCalendarService.TYPE_UPDATE_ALARM);
			service.putExtra(CheckSyncCalendarService.ALARM_ON_OFF, currentSyncReminder);
			startService(service);
		}
		if (currentSync != originalSync) {
			SharedPreference.updateSharedPref(context, Preference.SYNC, currentSync);
			if (currentSync) {
				Intent service = new Intent(context, CheckSyncCalendarService.class);
				service.putExtra(CheckSyncCalendarService.TYPE, CheckSyncCalendarService.TYPE_REGULAR_SERVICE);
				startService(service);
			}
		}
	}

	/**
	 * get the values from the preferences
	 */
	private void getValuesFromPref() {
		this.originalNotif = SharedPreference.getPrefBooleanValue(context, Preference.NOTIFICATIONS);
		this.originalNotifInvite = SharedPreference.getPrefBooleanValue(context, Preference.NOTIFICATION_EVENT_INVITES);
		this.originalNotifFriendEvent = SharedPreference.getPrefBooleanValue(context, Preference.NOTIFICATION_FRIENDS);
		this.originalNotifEventAct = SharedPreference.getPrefBooleanValue(context, Preference.NOTIFICATION_EVENT_ACTIVITIES);
		this.originalSync = SharedPreference.getPrefBooleanValue(context, Preference.SYNC);
		this.originalSyncReminder = SharedPreference.getPrefBooleanValue(context, Preference.SYNC_EVENT_REMINDERS);
		this.originalSyncReminderTime = SharedPreference.getPrefIntValue(context, Preference.SYNC_EVENT_REMINDERS_TIME);
		
		this.currentNotif = originalNotif;
		this.currentNotifInvite = originalNotifInvite;
		this.currentNotifFriendEvent = originalNotifFriendEvent;
		this.currentNotifEventAct = originalNotifEventAct;
		this.currentSync = originalSync;
		this.currentSyncReminder = originalSyncReminder;
		this.currentSyncReminderTime = originalSyncReminderTime;
	}
	
	/**
	 * Init the view components
	 * @param View rootview
	 */
	private void initViewComponents() {
		this.notificationSwitch = findViewById(R.id.setting_notification_switch_view);	
		this.notificationView = findViewById(R.id.setting_notification_view);
		this.notificationEventsInvite = findViewById(R.id.setting_notification_events_invite_view);
		this.notificationEventsActivity = findViewById(R.id.setting_notification_events_activity_view);
		this.notificationFriends = findViewById(R.id.setting_notification_friends_view);
		this.syncView = findViewById(R.id.setting_sync);
		this.syncSwitch = findViewById(R.id.setting_sync_switch_view);
		this.syncRecap = (TextView) findViewById(R.id.setting_sync_recap);
		this.syncEventsReminder = findViewById(R.id.setting_notification_events_reminder_view);
		this.syncEventsReminderTime = findViewById(R.id.setting_sync_events_reminder_time_view);
		this.syncEventsReminderTimeSet = (EditText) findViewById(R.id.setting_sync_events_reminder_time);
	}

	/**
	 * do the initializing part for the view from current setting
	 */
	public void showSettings() {
		notificationSwitch.setSelected(currentNotif);
		if (currentNotif) notificationView.setVisibility(View.VISIBLE);
		else notificationView.setVisibility(View.GONE);

		notificationEventsInvite.setSelected(currentNotifInvite);
		notificationEventsActivity.setSelected(currentNotifEventAct);
		notificationFriends.setSelected(currentNotifFriendEvent);

		if (syncMinApi) {
			syncView.setVisibility(View.VISIBLE);
			syncSwitch.setSelected(currentSync);

			if (!currentSync) {
				syncEventsReminder.setVisibility(View.GONE);
				syncEventsReminderTime.setVisibility(View.GONE);
			} else {
				syncEventsReminder.setVisibility(View.VISIBLE);
				syncEventsReminderTime.setVisibility(View.VISIBLE);
			}
			
			syncEventsReminder.setSelected(currentSyncReminder);
			if (currentSyncReminder) syncEventsReminderTime.setVisibility(View.VISIBLE);
			else syncEventsReminderTime.setVisibility(View.GONE);

			syncEventsReminderTimeSet.setText(String.valueOf(SharedPreference.getPrefIntValue(context, Preference.SYNC_EVENT_REMINDERS_TIME)));

			if (SharedPreference.containKey(context, Preference.SYNC_UPDATE_TIME)) {
				syncRecap.setVisibility(View.VISIBLE);
				long syncUpdatedTime = SharedPreference.getPrefLongValue(context, Preference.SYNC_UPDATE_TIME);
				syncRecap.setText("Last sync at " + getSyncUpdatedTime(syncUpdatedTime));
			} else syncRecap.setVisibility(View.GONE);
		} else syncView.setVisibility(View.GONE);
	}


	/**
	 * Set the click function to the view components
	 */
	private void setSettingComponentsClickListener() {
		notificationSwitch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				currentNotif = (!currentNotif);
				showSettings();
			}
		});

		notificationEventsInvite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				currentNotifInvite = (!currentNotifInvite);
				showSettings();
			}
		});

		notificationEventsActivity.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				currentNotifEventAct = (!currentNotifEventAct);
				showSettings();
			}
		});

		notificationFriends.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				currentNotifFriendEvent = (!currentNotifFriendEvent);
				showSettings();
			}
		});

		syncSwitch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				currentSync = (!currentSync);
				showSettings();
			}
		});

		syncEventsReminder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e("presses", "pressed");
				currentSyncReminder = (!currentSyncReminder);
				showSettings();
			}
		});

		//sync event reminder time
		syncEventsReminderTimeSet.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean isFocus) {
				if (!isFocus) {
					if (syncEventsReminderTimeSet.getText().toString().length() == 0 ||
							Integer.valueOf(syncEventsReminderTimeSet.getText().toString()) < 0)
						syncEventsReminderTimeSet.setText(String.valueOf(currentSyncReminderTime));
					else {
						currentSyncReminderTime = Integer.valueOf(syncEventsReminderTimeSet.getText().toString());
						showSettings();
					}
					InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(syncEventsReminderTimeSet.getWindowToken(), 0);
				}
			}
		});
	}

	/**
	 * get the display in sync time
	 * @param time
	 * @return sync time display form
	 */
	private String getSyncUpdatedTime(long time) {
		Date date = new Date(time);
		DateFormat format = new SimpleDateFormat("h:mm a' on 'MMM d");
		return format.format(date);   
	}
}
