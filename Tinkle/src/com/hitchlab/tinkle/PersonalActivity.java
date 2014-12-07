package com.hitchlab.tinkle;

import java.util.ArrayList;

import com.hitchlab.tinkle.R;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.google.analytics.tracking.android.EasyTracker;
import com.hitchlab.tinkle.appevent.EventFullActivity;
import com.hitchlab.tinkle.dialog.RedirectDialog;
import com.hitchlab.tinkle.fbquery.QueryFriends;
import com.hitchlab.tinkle.objects.FbEvent;
import com.hitchlab.tinkle.objects.Friend;
import com.hitchlab.tinkle.objects.RecommendUser;
import com.hitchlab.tinkle.service.QueryFbUserEvents;
import com.hitchlab.tinkle.service.ServiceStarter;
import com.hitchlab.tinkle.service.UploadFbUserInfo;
import com.hitchlab.tinkle.supports.AppLinking;
import com.hitchlab.tinkle.supports.ImageLoading;
import com.hitchlab.tinkle.template.UserEventAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams") 
public class PersonalActivity extends Activity {
	public static final int REQUEST_CONST = 9994; //any number will do

	private Context context;
	private Activity activity;
	private ImageLoading imageLoading;

	private View progressView;
	private TextView backButton;
	private View loadingView;
	private ListView eventsList;

	private ImageView cover;
	private ImageView profilePic;
	private TextView profileName;
	private TextView profileInfo;
	private View redirectToProfile;
	private View redirectToMessage;
	private View shareButton;

	private LinearLayout noResultView;
	private ImageView nscover;
	private ImageView nsprofilePic;
	private TextView nsprofileName;
	private TextView nsprofileInfo;
	private View nsredirectToProfile;
	private View nsredirectToMessage;
	private View nsshareButton;

	private QueryFriends queryFriends;

	private UserEventAdapter adapter;

	private String uid;
	private ServiceStarter serviceStarter;

	private boolean hasRetried;

	private boolean pendingPublishActionReauthorization = false;
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			if (state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
				if (pendingPublishActionReauthorization) {
					sharePersonUsingMessageDialog();
				}
			}
		}
	};

	/**
	 * receiver for friend events services
	 */
	private BroadcastReceiver userEventsReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			ArrayList<FbEvent> events = intent.getParcelableArrayListExtra(QueryFbUserEvents.DATA);
			if (events != null && events.size() != 0) {
				adapter.setEvents(events);
				adapter.notifyDataSetChanged();
			} 
			if (FacebookDialog.canPresentOpenGraphMessageDialog(context, FacebookDialog.OpenGraphMessageDialogFeature.OG_MESSAGE_DIALOG)) {
				shareButton.setVisibility(View.VISIBLE);
				nsshareButton.setVisibility(View.VISIBLE);
				redirectToMessage.setBackgroundResource(R.drawable.rounded_list_item_bottom_mid);
				nsredirectToMessage.setBackgroundResource(R.drawable.rounded_list_item_bottom_mid);
			} else {
				redirectToMessage.setBackgroundResource(R.drawable.rounded_list_item_bottom_right);
				nsredirectToMessage.setBackgroundResource(R.drawable.rounded_list_item_bottom_right);
			}
			loadingView.setVisibility(View.GONE);
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_activity);
		this.context = this;
		this.activity = this;
		this.uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		this.imageLoading = new ImageLoading(context);

		this.serviceStarter = new ServiceStarter(context) {
			@Override
			public void noInternet() {
				Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
				activity.finish();
			}

			@Override
			public void sessionClosed() {
				Toast.makeText(context, "Authentication Error. Please re-login.", Toast.LENGTH_LONG).show();
				activity.finish();
			}
		};

		initViews();
		displayUserInfo();

		serviceStarter.updateUsersEvents(uid);
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
	protected void onPause() {
		super.onPause();
		uiHelper.onResume();
		unregisterReceiver(userEventsReceiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		uiHelper.onResume();
		registerReceiver(userEventsReceiver, new IntentFilter(QueryFbUserEvents.NOTIFICATION));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
			@Override
			public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
				if (pendingCall.getRequestCode() == REQUEST_CONST) {
					if (hasRetried) {
						RecommendUser recUser = new RecommendUser();
						recUser.setUid(uid);
						recUser.setUsername(profileName.getText().toString());
						if (LoginActivity.hasPermission(Session.getActiveSession(), LoginActivity.PUBLISH_ACTION))
							AppLinking.recommendUserFallback(activity, recUser, uiHelper, -1);
						else {
							Log.e("PersonalActivity", String.format("Error: %s", error.toString()));
							Toast.makeText(context, "Fail to share user. This may due to user's privacy setting.", Toast.LENGTH_SHORT).show();
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
								sharePersonUsingMessageDialog();
							}		
						}.execute();
					}
				} else {
					Log.e("PersonalActivity", String.format("Error: %s", error.toString()));
					Toast.makeText(context, "Fail to share user. This may due to user's privacy setting.", Toast.LENGTH_SHORT).show();
					progressView.setVisibility(View.GONE);
				}
			}

			@Override
			public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
				progressView.setVisibility(View.GONE);
				hasRetried = false;
			}
		});
	}

	/**
	 * Init the views
	 */
	private void initViews() {
		this.loadingView = findViewById(R.id.personal_activity_loading_view);
		this.backButton = (TextView) findViewById(R.id.personal_activity_back_button);
		backButton.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		this.progressView = findViewById(R.id.personal_event_progress_view);
		this.progressView.setVisibility(View.GONE);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View headerView = inflater.inflate(R.layout.personal_activity_header, null);
		this.cover = (ImageView) headerView.findViewById(R.id.personal_cover_pic);
		this.profilePic = (ImageView) headerView.findViewById(R.id.personal_profile_pic);
		this.profileName = (TextView) headerView.findViewById(R.id.personal_profile_name);
		this.profileInfo = (TextView) headerView.findViewById(R.id.personal_profile_info);
		this.redirectToProfile = headerView.findViewById(R.id.personal_profile_redirect_profile);
		redirectToProfile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RedirectDialog.showRedirectDialog(activity, uid, RedirectDialog.REDIRECT_TO_PROFILE);
			}
		});
		this.redirectToMessage = headerView.findViewById(R.id.personal_profile_message);
		redirectToMessage.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				RedirectDialog.showRedirectDialog(activity, uid, RedirectDialog.REDIRECT_TO_MESSAGE);
			}
		});
		this.shareButton = headerView.findViewById(R.id.personal_profile_share_user);
		shareButton.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				sharePersonUsingMessageDialog();
			}
		});
		shareButton.setVisibility(View.GONE);

		View noResultHeaderView = inflater.inflate(R.layout.personal_activity_header, null);
		this.nscover = (ImageView) noResultHeaderView.findViewById(R.id.personal_cover_pic);
		this.nsprofilePic = (ImageView) noResultHeaderView.findViewById(R.id.personal_profile_pic);
		this.nsprofileName = (TextView) noResultHeaderView.findViewById(R.id.personal_profile_name);
		this.nsprofileInfo = (TextView) noResultHeaderView.findViewById(R.id.personal_profile_info);
		this.nsredirectToProfile = noResultHeaderView.findViewById(R.id.personal_profile_redirect_profile);
		nsredirectToProfile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RedirectDialog.showRedirectDialog(activity, uid, RedirectDialog.REDIRECT_TO_PROFILE);
			}
		});
		this.nsredirectToMessage = noResultHeaderView.findViewById(R.id.personal_profile_message);
		nsredirectToMessage.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				RedirectDialog.showRedirectDialog(activity, uid, RedirectDialog.REDIRECT_TO_MESSAGE);
			}
		});

		this.nsshareButton = noResultHeaderView.findViewById(R.id.personal_profile_share_user);
		nsshareButton.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				sharePersonUsingMessageDialog();
			}
		});
		nsshareButton.setVisibility(View.GONE);

		this.noResultView = (LinearLayout) findViewById(R.id.personal_event_no_result);
		noResultView.addView(noResultHeaderView);

		TextView emptyText = new TextView(context);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		param.gravity = Gravity.CENTER;
		emptyText.setLayoutParams(param);
		emptyText.setBackgroundColor(Color.parseColor("#22000000"));
		emptyText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		emptyText.setTextColor(Color.parseColor("#444444"));
		emptyText.setTypeface(null, Typeface.BOLD);
		emptyText.setGravity(Gravity.CENTER);
		emptyText.setText("No results found.");
		noResultView.addView(emptyText);

		this.adapter = new UserEventAdapter(context, imageLoading);

		this.eventsList = (ListView) findViewById(R.id.personal_event_list);
		eventsList.addHeaderView(headerView, null, false);
		eventsList.setEmptyView(noResultView);
		eventsList.setAdapter(adapter);
		eventsList.setDivider(new ColorDrawable(0x00c2c2c2));
		eventsList.setDividerHeight(0);
		eventsList.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				FbEvent event = adapter.getEventItem(position - 1); //minus 1 for the header
				Intent intent = new Intent(context, EventFullActivity.class);
				intent.putExtra("eid", event.getId());
				context.startActivity(intent);
			}	
		});
	}

	/**
	 * Display the user basic info
	 */
	private void displayUserInfo() {
		Intent intent = getIntent();
		uid = intent.getStringExtra("uid");
		imageLoading.displayImage("http://graph.facebook.com/" + uid + "/picture?width=100&height=100", profilePic);
		imageLoading.displayImage("http://graph.facebook.com/" + uid + "/picture?width=100&height=100", nsprofilePic);

		if (queryFriends == null) queryFriends = new QueryFriends() {
			@Override
			public void onQueryCompleted(String name, String work,
					String coverUrl) {
				if (!coverUrl.equals("")) {
					imageLoading.displayImage(coverUrl, cover);
					imageLoading.displayImage(coverUrl, nscover);
				}
				profileName.setText(name);
				backButton.setText(name);
				profileInfo.setText(work);

				nsprofileName.setText(name);
				nsprofileInfo.setText(work);

				Intent intent = new Intent(context, UploadFbUserInfo.class);
				intent.putExtra("uid", uid);
				intent.putExtra("name", name);
				intent.putExtra("info", work);
				startService(intent);
			}

			@Override
			public void onQueryCompleted(ArrayList<Friend> friends) {	
			}	
		};
		queryFriends.queryFriend(Session.getActiveSession(), uid);
	}

	/**
	 * send the message dialog to list of friends telling about this person
	 */
	private void sharePersonUsingMessageDialog() {
		Session session = Session.getActiveSession();
		if (!LoginActivity.hasPermission(session, LoginActivity.PUBLISH_ACTION)) {
			pendingPublishActionReauthorization = true;
			LoginActivity.requestPublishPermissions(this, session, LoginActivity.PERMISSION_PUBLISH_ACTION);
		} else {
			pendingPublishActionReauthorization = false;
			AppLinking.recommendUser(this, uid, uiHelper, REQUEST_CONST);
		}
	}
}
