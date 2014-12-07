package com.hitchlab.tinkle;

import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.hitchlab.tinkle.R;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.widget.LoginButton;
import com.google.analytics.tracking.android.EasyTracker;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;
import com.hitchlab.tinkle.service.ScheduleServiceReceiver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class LoginActivity extends Activity{
	private static final List<String> PERMISSIONS = Arrays.asList("user_events", "friends_events", "read_stream", "friends_work_history");
	public static final List<String> PUBLISH_PERMISSIONS = Arrays.asList("publish_actions", "rsvp_event");
	
	public static final String PUBLISH_ACTION = "publish_actions";
	public static final String RSVP_EVENT = "rsvp_event";
	public static final List<String> PERMISSION_PUBLISH_ACTION = Arrays.asList("publish_actions");
	public static final List<String> PERMISSION_RSVP_EVENT = Arrays.asList("rsvp_event");
	
	private Context context;

	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private View loadingView;
	private LoginButton authButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = this; 
		this.uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		this.authButton = (LoginButton) findViewById(R.id.login_button);
		authButton.setReadPermissions(PERMISSIONS);

		this.loadingView = findViewById(R.id.login_activity_loading_view);
		this.loadingView.setVisibility(View.GONE);
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onBackPressed() {
		//do nothing
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
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (state.isOpened() || state.equals(SessionState.OPENED)) {
			loadingView.setVisibility(View.VISIBLE);
			authButton.setClickable(false);
			Bundle params = new Bundle();
			params.putString("fields", "id,name,gender");
			Request request = new Request(session, "/me", params, HttpMethod.GET, new Request.Callback() {
				@Override
				public void onCompleted(Response response) {
					try {
						GraphObject graphObject = response.getGraphObject();
						JSONObject me = graphObject.getInnerJSONObject();

						SharedPreference.updateSharedPref(context, Preference.UID, me.getString("id"));
						SharedPreference.updateSharedPref(context, Preference.USERNAME, me.getString("name"));
						SharedPreference.updateSharedPref(context, Preference.GENDER, me.getString("gender"));

						SharedPreference.updateSharedPref(context, Preference.SYNC_EVENT_REMINDERS, true);
						SharedPreference.updateSharedPref(context, Preference.SYNC_EVENT_REMINDERS_TIME, 15);
						SharedPreference.updateSharedPref(context, Preference.NOTIFICATIONS, true);
						SharedPreference.updateSharedPref(context, Preference.NOTIFICATION_EVENT_INVITES, true);
						SharedPreference.updateSharedPref(context, Preference.NOTIFICATION_EVENT_ACTIVITIES, true);
						SharedPreference.updateSharedPref(context, Preference.NOTIFICATION_FRIENDS, true);

						if (!SharedPreference.getPrefBooleanValue(context, Preference.UPDATE, Preference.UPDATE)) {
							SharedPreference.updateSharedPref(context, Preference.UPDATE, Preference.UPDATE, true);
							ScheduleServiceReceiver.startRepeatingCalendarEventService(context);
							ScheduleServiceReceiver.startRepeatingNotificationService(context);
							ScheduleServiceReceiver.startUpdatingService(context);
						}

						startEventbook();
					} catch (JSONException e) {
						Log.e("Me", "Unable to get Me facebook info");
					}
				}
			});
			request.setVersion("v1.0");
			request.executeAsync();
		}
	}

	/**
	 * Go to the eventbook main activity
	 * But before we go the the main activity, we need to check if the 
	 */
	private void startEventbook() {
		finish();
		Intent eventbookActivity = new Intent(context, MainActivity.class);
		startActivity(eventbookActivity);
	}

	/**
	 * method of requesting new publish permissions
	 * @param Context
	 * @param Session
	 * @param List<String> permissions
	 */
	public static void requestPublishPermissions(Activity activity, Session session, List<String> permissions) {
		if (session != null) {
			Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(activity, permissions);
			session.requestNewPublishPermissions(newPermissionsRequest);
		}
	}

	/**
	 * method that checking if the current session contain a given permission
	 * @param Session
	 * @param permission
	 * @return boolean
	 */
	public static boolean hasPermission(Session session, String permission) {
		if (session == null) return false;
		return session.isPermissionGranted(permission);
	}
}
