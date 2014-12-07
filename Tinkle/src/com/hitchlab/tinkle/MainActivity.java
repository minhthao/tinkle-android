package com.hitchlab.tinkle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;
import android.widget.Toast;

import com.hitchlab.tinkle.R;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.google.analytics.tracking.android.EasyTracker;
import com.hitchlab.tinkle.datasource.ShareDataSource;
import com.hitchlab.tinkle.dialog.SyncDialog;
import com.hitchlab.tinkle.objects.FbEvent;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;
import com.hitchlab.tinkle.service.CheckSyncCalendarService;
import com.hitchlab.tinkle.supports.AppLinking;

public class MainActivity extends FragmentActivity implements MainActivityMyEventsFragment.OnMyEventShareClick, MainActivityNearbyEventsFragment.OnNearbyEventShareClick, MainActivityFriendEventsFragment.OnFriendEventShareClick{
	public static final int REQUEST_CODE = 996; //any number will do

	private Activity activity;
	private Context context;
	private ShareDataSource shareDataSource;
	private UiLifecycleHelper uiHelper; 
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(final Session session, final SessionState state, final Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private View mainView;
	private View menuBar;
	private Menu submenu;
	private TextView submenuButton;
	private View progressView;

	private static final int FRIEND_EVENT_FRAGMENT = 0;
	private static final int MY_EVENT_FRAGMENT = 1;
	private static final int NEARBY_EVENT_FRAGMENT = 2;
	private static final int FRIEND_FRAGMENT = 3;
	private TextView[] menuButtons = new TextView[4];
	private Fragment[] fragments = new Fragment[4];

	private boolean pendingPublishActionReauthorization = false;
	private FbEvent pendingSharedEvent;

	private boolean hasRetried;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		this.activity = this;
		this.context = this;
		this.shareDataSource = new ShareDataSource(context);
		this.uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		initFragments();
		setInitialView();
		initSubmenu();
		checkEventSync();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
			@Override
			public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
				if (pendingCall.getRequestCode() == REQUEST_CODE) {
					if (hasRetried) {
						if (pendingSharedEvent != null && LoginActivity.hasPermission(Session.getActiveSession(), LoginActivity.PUBLISH_ACTION))
							AppLinking.shareEventPrivateFallback(activity, pendingSharedEvent, uiHelper, -1);
						else {
							Log.e("MainActivity", String.format("Error: %s", error.toString()));
							Toast.makeText(context, "Fail to share event.", Toast.LENGTH_SHORT).show();
							progressView.setVisibility(View.GONE);
						}
					} else {
						progressView.setVisibility(View.VISIBLE);
						new AsyncTask<Void, Void, Void>() {
							@Override
							protected Void doInBackground(Void... arg0) {
								hasRetried = true;
								// wait 1 second
								long currentTime = System.currentTimeMillis();
								while(System.currentTimeMillis() - currentTime < 1000) {}
								return null;
							}

							@Override
							protected void onPostExecute(Void result) {
								if (pendingSharedEvent != null) onShareEvent(pendingSharedEvent);
							}		
						}.execute();
					}
				} else {
					Log.e("MainActivity", String.format("Error: %s", error.toString()));
					Toast.makeText(context, "Fail to share event.", Toast.LENGTH_SHORT).show();
					progressView.setVisibility(View.GONE);
				}
			}

			@Override
			public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
				String postId = FacebookDialog.getNativeDialogPostId(data);
				if (postId != null) {
					shareDataSource.addEventShared(pendingSharedEvent.getId());
					Toast.makeText(context,"Event shared successfully.", Toast.LENGTH_SHORT).show();
				}
				progressView.setVisibility(View.GONE);
				hasRetried = false;
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed()) )
			onSessionStateChange(session, session.getState(), null);
		uiHelper.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
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

	/**
	 * Init the Fragments and their OnClickListener
	 */
	private void initFragments() {
		FragmentManager fm = getSupportFragmentManager();
		fragments[FRIEND_EVENT_FRAGMENT] = fm.findFragmentById(R.id.main_view_friend_events_fragment);
		fragments[MY_EVENT_FRAGMENT] = fm.findFragmentById(R.id.main_view_my_events_fragment);
		fragments[NEARBY_EVENT_FRAGMENT] = fm.findFragmentById(R.id.main_view_nearby_events_fragment);
		fragments[FRIEND_FRAGMENT] = fm.findFragmentById(R.id.main_view_friends_fragment);

		menuButtons[FRIEND_EVENT_FRAGMENT] = (TextView) findViewById(R.id.main_view_menu_friends_events_icon);
		menuButtons[NEARBY_EVENT_FRAGMENT] = (TextView) findViewById(R.id.main_view_menu_nearby_events_icon);
		menuButtons[MY_EVENT_FRAGMENT] = (TextView) findViewById(R.id.main_view_menu_my_events_icon);
		menuButtons[FRIEND_FRAGMENT] = (TextView) findViewById(R.id.main_view_menu_friends_icon);

		for (int i = 0; i < menuButtons.length; i++) {
			final int index = i;
			menuButtons[index].setOnClickListener(new TextView.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!menuButtons[index].isSelected()) selectMenuButton(index);
				}
			});
		}
	}

	/**
	 * high light the view to indicated that the view is currently the selected view
	 * @param fragment index (which also the button index
	 */
	private void selectMenuButton(int fragmentIndex) {
		for (int i = 0; i < menuButtons.length; i++) {
			if (i != fragmentIndex) menuButtons[i].setSelected(false);
			else menuButtons[i].setSelected(true);
		}

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++) transaction.hide(fragments[i]);
		transaction.show(fragments[fragmentIndex]);
		transaction.commit();
	}

	/**
	 * Initiate and setup the icons on the header bar
	 */
	private void setInitialView() {
		this.mainView = findViewById(R.id.main_view);
		this.menuBar = findViewById(R.id.main_view_menu_bar);
		mainView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				int heightDiff = mainView.getRootView().getHeight() - mainView.getHeight();
				if (heightDiff > 100) menuBar.setVisibility(View.GONE);
				else menuBar.setVisibility(View.VISIBLE);
			}
		});

		this.progressView = findViewById(R.id.main_activity_progress_view);
		this.progressView.setVisibility(View.GONE);

		selectMenuButton(FRIEND_EVENT_FRAGMENT);
	} 

	/**
	 * Check if the we need to do the sync
	 */
	private void checkEventSync() {
		if (!SharedPreference.containKey(context, Preference.SYNC)) {
			if (!MenuSettingsActivity.syncMinApi) {
				SharedPreference.updateSharedPref(context, Preference.SYNC, false);
			} else {
				new SyncDialog(context) {
					@Override
					public void onSyncDialogButtonSelect(boolean positive) {
						if (positive) {
							SharedPreference.updateSharedPref(context, Preference.SYNC, true);
							Intent intent = new Intent(context, CheckSyncCalendarService.class);
							intent.putExtra(CheckSyncCalendarService.TYPE, CheckSyncCalendarService.TYPE_REGULAR_SERVICE);
							context.startService(intent);
						} else SharedPreference.updateSharedPref(context, Preference.SYNC, false);
					}
				};
			}
		}
	}

	/**
	 * init submenu
	 */
	private void initSubmenu() {
		this.submenu = new Menu(this);
		this.submenuButton = (TextView) findViewById(R.id.main_view_menu_more_icon);
		submenuButton.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (submenu.isShowing()) submenu.showContent();
				else submenu.showMenu();
			}
		});
	}

	@Override
	public void onBackPressed() {
		if (submenu.isShowing()) submenu.showContent();
		else super.onBackPressed();
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
			if (pendingPublishActionReauthorization) {
				onShareEvent(pendingSharedEvent);
			}
		}
	}

	@Override
	public void onShareEvent(FbEvent event) {
		this.pendingSharedEvent = event; 

		Session session = Session.getActiveSession();
		if (!LoginActivity.hasPermission(session, LoginActivity.PUBLISH_ACTION)) {
			pendingPublishActionReauthorization = true;
			LoginActivity.requestPublishPermissions(this, session, LoginActivity.PERMISSION_PUBLISH_ACTION);
		} else {
			pendingPublishActionReauthorization = false;
			AppLinking.shareEventPrivate(this, event, uiHelper, REQUEST_CODE);
		}
	}

}
