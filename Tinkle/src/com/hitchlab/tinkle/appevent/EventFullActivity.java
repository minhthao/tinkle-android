package com.hitchlab.tinkle.appevent;

import java.io.File;
import java.util.ArrayList;

import com.hitchlab.tinkle.R;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.google.analytics.tracking.android.EasyTracker;
import com.hitchlab.tinkle.LoginActivity;
import com.hitchlab.tinkle.datasource.ShareDataSource;
import com.hitchlab.tinkle.eventactions.RsvpEventHandling;
import com.hitchlab.tinkle.images.Path;
import com.hitchlab.tinkle.images.PostEdit;
import com.hitchlab.tinkle.images.PostImageResultConstant;
import com.hitchlab.tinkle.objects.*;
import com.hitchlab.tinkle.service.event.QueryEventDetail;
import com.hitchlab.tinkle.supports.AppLinking;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EventFullActivity extends FragmentActivity implements EventRecommendPeopleListFragment.UserItem, EventDetailFragment.OnEventShareClick, EventDetailFragment.RequestRsvpPermission, EventDetailFragment.RsvpChanged, EventRecommendPersonInfoFragment.UserItem{

	private static final int SHARE_EVENT_THROUGH_MESSAGE_CODE = 9000;
	private static final int SHARE_EVENT_TIMELINE_CODE = 9001;
	private static final int SHARE_USER_CODE = 9002;

	private Context context;
	private Activity activity;
	private ShareDataSource shareDataSource;

	private View progressView;
	private TextView backButton;
	private ImageView attendeesButton;
	private ImageView publicShareButton;

	private EventSlidingMenu menu;
	private static final int INFO_VIEW = 0;
	private static final int ALBUM_VIEW = 1;
	private static final int POST_VIEW = 2;
	private ImageView[] fragmentButtons = new ImageView[3];
	private Fragment[] fragments = new Fragment[3];
	private int currentFragmentOption;
	private EventDetailFragment detailFragment;
	private EventPhotoFragment albumFragment;
	private EventPostFragment postFragment;

	private SlidingUpPanelLayout slidingPanelLayout;
	private EventRecommendPersonInfoFragment eventRecommendPersonInfoFragment;
	private EventRecommendPeopleListFragment eventRecommendPeopleListFragment;

	private String selectedUid;
	private boolean hasSharedEventObjectMessageRetried;
	private boolean hasSharedEventObjectPublicRetried;
	private boolean hasSharedUserRetried;
	private int currentPostOptionCode;

	private boolean pendingPublishActionReauthorization = false;
	private boolean pendingRsvpEventReauthorization = false;
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			if (state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
				if (pendingPublishActionReauthorization) {
					switch(currentPostOptionCode) {
					case SHARE_EVENT_THROUGH_MESSAGE_CODE:
						onShareEvent();
						break;
					case SHARE_EVENT_TIMELINE_CODE:
						shareEventPublicly();
						break;
					case SHARE_USER_CODE:
						onShare(selectedUid);
						break;
					default: break;
					}
				} else if (pendingRsvpEventReauthorization) {
					detailFragment.changeRsvpStatus(-1);
				}
			}
		}

	};

	private FbEventCompleteInfo eventInfo;
	private BroadcastReceiver eventFullDetailReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int resultType = intent.getIntExtra("result_type", -1);
			if (resultType == QueryEventDetail.RESULT_ERROR) {
				Toast.makeText(context, "Event may no longer exist", Toast.LENGTH_SHORT).show();
				finish();
			} else if (resultType == QueryEventDetail.RESULT_INVALID) {
				Toast.makeText(context, "Invalid session. Please re-login.", Toast.LENGTH_SHORT).show();
				finish();
			} else if (resultType == QueryEventDetail.RESULT_NO_INTERNET) {
				Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show();
				finish();
			} else if (resultType == QueryEventDetail.RESULT_OK) {
				eventInfo = (FbEventCompleteInfo) intent.getParcelableExtra("data");
				detailFragment.displayEventInfo(eventInfo);
				menu.setFriends(eventInfo.getFriendsAttending(), eventInfo.getFriendsMaybe(), eventInfo.getFriendsUnreplied());
				if (!eventInfo.getEvent().getPrivacy().equals("SECRET") && eventInfo.isInviteOption()) {
					publicShareButton.setVisibility(View.VISIBLE);
				}
				setCanPost();
			}
		}
	};

	private RecommendUser recUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_full_view);
		this.context = this;
		this.activity = this;
		this.shareDataSource = new ShareDataSource(context);
		this.uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		this.menu = new EventSlidingMenu(this);
		initViewComponents();

		final String eid = getIntent().getStringExtra("eid");
		albumFragment.setEid(eid);
		postFragment.setEid(eid);
		eventRecommendPeopleListFragment.setEid(eid);
		Intent intent = new Intent(context, QueryEventDetail.class);
		intent.putExtra("eid", eid);
		context.startService(intent);

		this.publicShareButton.setVisibility(View.GONE);
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		registerReceiver(eventFullDetailReceiver, new IntentFilter(QueryEventDetail.NOTIFICATION));

		ArrayList<RecommendUser> recUsers = eventRecommendPeopleListFragment.getRecommendUsers();
		if (eventInfo != null && !eventInfo.getEvent().getId().isEmpty() && 
				(recUsers == null || recUsers.isEmpty())) eventRecommendPeopleListFragment.setEid(eventInfo.getEvent().getId());
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onResume();
		unregisterReceiver(eventFullDetailReceiver);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
			@Override
			public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
				if (pendingCall.getRequestCode() == SHARE_EVENT_TIMELINE_CODE) {
					progressView.setVisibility(View.VISIBLE);
					if (hasSharedEventObjectPublicRetried) {
						if (eventInfo != null && LoginActivity.hasPermission(Session.getActiveSession(), LoginActivity.PUBLISH_ACTION)) 
							AppLinking.shareEventPubliclyFallback(activity, eventInfo.getEvent(), uiHelper, -1);
						else {
							Log.e("EventFullActivity", String.format("Error: %s", error.toString()));
							Toast.makeText(context, "Fail to share.", Toast.LENGTH_SHORT).show();
							progressView.setVisibility(View.GONE);
						}
					} else {
						new AsyncTask<Void, Void, Void>() {
							@Override
							protected Void doInBackground(Void... arg0) {
								hasSharedEventObjectPublicRetried = true;
								// wait 1 second
								long currentTime = System.currentTimeMillis();
								while(System.currentTimeMillis() - currentTime < 1000) {}
								return null;
							}

							@Override
							protected void onPostExecute(Void result) {
								onShareEvent();
							}		
						}.execute();
					}
				} else if (pendingCall.getRequestCode() == SHARE_EVENT_THROUGH_MESSAGE_CODE) {
					progressView.setVisibility(View.VISIBLE);
					if (hasSharedEventObjectMessageRetried) {
						if (eventInfo != null && LoginActivity.hasPermission(Session.getActiveSession(), LoginActivity.PUBLISH_ACTION)) 
							AppLinking.shareEventPrivateFallback(activity, eventInfo.getEvent(), uiHelper, -1);
						else {
							Log.e("EventFullActivity", String.format("Error: %s", error.toString()));
							Toast.makeText(context, "Fail to share.", Toast.LENGTH_SHORT).show();
							progressView.setVisibility(View.GONE);
						}
					} else {
						new AsyncTask<Void, Void, Void>() {
							@Override
							protected Void doInBackground(Void... arg0) {
								hasSharedEventObjectMessageRetried = true;
								// wait 1 second
								long currentTime = System.currentTimeMillis();
								while(System.currentTimeMillis() - currentTime < 1000) {}
								return null;
							}

							@Override
							protected void onPostExecute(Void result) {
								onShareEvent();
							}		
						}.execute();
					}
				} else if (pendingCall.getRequestCode() == SHARE_USER_CODE) {
					progressView.setVisibility(View.VISIBLE);
					if (hasSharedUserRetried) {
						if (recUser != null && LoginActivity.hasPermission(Session.getActiveSession(), LoginActivity.PUBLISH_ACTION)) 
							AppLinking.recommendUserFallback(activity, recUser, uiHelper, -1);
						else {
							Log.e("EventFullActivity", String.format("Error: %s", error.toString()));
							Toast.makeText(context, "Fail to share.", Toast.LENGTH_SHORT).show();
							progressView.setVisibility(View.GONE);
						}
					} else {
						new AsyncTask<Void, Void, Void>() {
							@Override
							protected Void doInBackground(Void... arg0) {
								hasSharedUserRetried = true;
								// wait 1 second
								long currentTime = System.currentTimeMillis();
								while(System.currentTimeMillis() - currentTime < 1000) {}
								return null;
							}

							@Override
							protected void onPostExecute(Void result) {
								onShare(selectedUid);
							}		
						}.execute();
					}
				} else {
					Log.e("EventFullActivity", String.format("Error: %s", error.toString()));
					Toast.makeText(context, "Fail to share.", Toast.LENGTH_SHORT).show();
					progressView.setVisibility(View.GONE);
				}
			}

			@Override
			public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
				if (pendingCall.getRequestCode() == SHARE_EVENT_TIMELINE_CODE) {
					String postId = FacebookDialog.getNativeDialogPostId(data);
					if (postId != null && !postId.isEmpty()) 
						shareDataSource.addEventShared(eventInfo.getEvent().getId());
				} 

				hasSharedEventObjectPublicRetried = false;
				hasSharedEventObjectMessageRetried = false;
				hasSharedUserRetried = false;
				progressView.setVisibility(View.GONE);
			}
		});

		if (resultCode == RESULT_OK) {

			Bitmap bitmap   = null;
			String path     = "";

			switch (requestCode) {

			case PostImageResultConstant.PICK_FROM_CAMERA:
				if (currentFragmentOption == ALBUM_VIEW) {
					path = albumFragment.getUneditedPostImageUri().getPath();
					bitmap  = BitmapFactory.decodeFile(path);
					if (bitmap != null && !path.equals("")) 
						albumFragment.uploadImage(PostEdit.getPostPhotoBitmap(bitmap, path));
				} else if (currentFragmentOption == POST_VIEW){
					path = postFragment.getUneditedPostImageUri().getPath();
					bitmap = BitmapFactory.decodeFile(path);
					if (bitmap != null && !path.equals("")) 
						postFragment.uploadImage(PostEdit.getPostPhotoBitmap(bitmap, path));
				}
				File file = new File(path);
				file.delete();
				break;

			case PostImageResultConstant.PICK_FROM_FILE: 
				Uri imageUri = data.getData();
				path = Path.getRealPathFromURI(this, imageUri); //from Gallery

				if (path == null)
					path = imageUri.getPath(); //from File Manager

				if (path != null)
					bitmap  = BitmapFactory.decodeFile(path);

				if (bitmap != null && !path.equals("")) {
					final Bitmap mBitmap = bitmap;
					final String mPath = path;
					if (currentFragmentOption == ALBUM_VIEW) {
						//this could over run the memory, so best to converted in in a task service
						new AsyncTask<Void, Void, Bitmap>() {
							@Override
							protected Bitmap doInBackground(Void... arg0) {
								Bitmap bitmap = PostEdit.getPostPhotoBitmap(mBitmap, mPath);
								return bitmap;
							}

							@Override
							protected void onPostExecute(Bitmap result) {
								albumFragment.uploadImage(result);
							}
						}.execute();
					} else if (currentFragmentOption == POST_VIEW) {
						//this could over run the memory, so best to converted in in a task service
						new AsyncTask<Void, Void, Bitmap>() {
							@Override
							protected Bitmap doInBackground(Void... arg0) {
								Bitmap bitmap = PostEdit.getPostPhotoBitmap(mBitmap, mPath);
								return bitmap;
							}

							@Override
							protected void onPostExecute(Bitmap result) {
								postFragment.uploadImage(result);
							}
						}.execute();
					}
				}
				break;	    
			}
		}
	}

	/**
	 * Init the view components
	 */
	private void initViewComponents() {
		this.progressView = findViewById(R.id.event_full_activity_progress_view);
		progressView.setVisibility(View.GONE);

		this.backButton = (TextView) findViewById(R.id.event_full_info_back_button);
		backButton.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		this.attendeesButton = (ImageView) findViewById(R.id.event_full_attendees);
		attendeesButton.setOnClickListener(new ImageView.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (menu.isShowing()) menu.showContent();
				else menu.showMenu();
			}
		});

		this.publicShareButton = (ImageView) findViewById(R.id.event_full_share_message);
		publicShareButton.setOnClickListener(new ImageView.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				shareEventPublicly();
			}
		});

		FragmentManager  fm  = getSupportFragmentManager();
		fragments[INFO_VIEW] = fm.findFragmentById(R.id.event_full_detail_fragment);
		fragmentButtons[INFO_VIEW] = (ImageView) findViewById(R.id.event_full_detail);
		fragments[ALBUM_VIEW] = fm.findFragmentById(R.id.event_album_fragment);
		fragmentButtons[ALBUM_VIEW] = (ImageView) findViewById(R.id.event_full_photos);
		fragments[POST_VIEW] = fm.findFragmentById(R.id.event_post_fragment);
		fragmentButtons[POST_VIEW] = (ImageView) findViewById(R.id.event_full_posts);

		this.detailFragment = (EventDetailFragment) fragments[INFO_VIEW];
		this.albumFragment = (EventPhotoFragment) fragments[ALBUM_VIEW];
		this.postFragment = (EventPostFragment) fragments[POST_VIEW];
		this.eventRecommendPersonInfoFragment = (EventRecommendPersonInfoFragment) fm.findFragmentById(R.id.event_recommend_user_info_fragment);
		this.eventRecommendPeopleListFragment = (EventRecommendPeopleListFragment) fm.findFragmentById(R.id.event_recommendation_people_list_fragment);

		showFragment(INFO_VIEW);

		for (int i = 0; i < fragments.length; i++) {
			final int index = i;
			fragmentButtons[index].setOnClickListener(new ImageView.OnClickListener() {
				@Override
				public void onClick(View v) {
					showFragment(index);
					slidingPanelLayout.collapsePane();
					if (index == ALBUM_VIEW) albumFragment.queryEventPhotos();
				}	
			});
		}

		this.slidingPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.event_full_sliding_panel);
		slidingPanelLayout.setDragView(findViewById(R.id.event_full_drag_view));
	}

	/**
	 * Show a given fragment and hide all other
	 * @param fragmentIndex
	 * @param addToBackStack
	 */
	private void showFragment(int fragmentIndex) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++) {
			if (i == fragmentIndex) {
				fragmentButtons[i].setSelected(true);
				transaction.show(fragments[i]);
			} else {
				fragmentButtons[i].setSelected(false);
				transaction.hide(fragments[i]);
			}
		}
		currentFragmentOption = fragmentIndex;
		transaction.commit();
	}

	/**
	 * Set whether the event can make any post
	 */
	private void setCanPost() {
		String rsvp = eventInfo.getEvent().getRsvp_status();
		if (rsvp.equals(RsvpEventHandling.ATTENDING) || rsvp.equals(RsvpEventHandling.UNSURE)) {
			albumFragment.setCanPost(true);
			postFragment.setCanPost(true);
		} else {
			albumFragment.setCanPost(false);
			postFragment.setCanPost(false);
		}
	}

	@Override
	public void onBackPressed() {
		if (menu.isShowing()) menu.showContent();
		else super.onBackPressed();
	}

	/**
	 * send the message dialog to list of friends telling about this event
	 */
	private void shareEventPublicly() {
		if (eventInfo != null) {
			currentPostOptionCode = SHARE_EVENT_TIMELINE_CODE;
			Session session = Session.getActiveSession();
			if (!LoginActivity.hasPermission(session, LoginActivity.PUBLISH_ACTION)) {
				pendingPublishActionReauthorization = true;
				LoginActivity.requestPublishPermissions(this, session, LoginActivity.PERMISSION_PUBLISH_ACTION);
			} else {
				pendingPublishActionReauthorization = false;
				AppLinking.shareEventPublicly(this, eventInfo.getEvent(), uiHelper, SHARE_EVENT_TIMELINE_CODE);
			}
		}
	}

	@Override
	public void onShareEvent() {
		if (eventInfo != null) {
			currentPostOptionCode = SHARE_EVENT_THROUGH_MESSAGE_CODE;
			Session session = Session.getActiveSession();
			if (!LoginActivity.hasPermission(session, LoginActivity.PUBLISH_ACTION)) {
				pendingPublishActionReauthorization = true;
				LoginActivity.requestPublishPermissions(this, session, LoginActivity.PERMISSION_PUBLISH_ACTION);
			} else {
				pendingPublishActionReauthorization = false;
				AppLinking.shareEventPrivate(this, eventInfo.getEvent(), uiHelper, SHARE_EVENT_THROUGH_MESSAGE_CODE);
			}
		}
	}

	@Override
	public void onClick(RecommendUser user, boolean needExpandView) {
		if (user != null) {
			this.recUser = user;
			this.eventRecommendPersonInfoFragment.showUserInfo(user);
			if (needExpandView) slidingPanelLayout.expandPane();
		} else {
			if (slidingPanelLayout.isExpanded()) slidingPanelLayout.collapsePane();
			else slidingPanelLayout.expandPane();
		}
	}

	@Override
	public void onShare(String uid) {
		this.selectedUid = uid;
		currentPostOptionCode = SHARE_USER_CODE;
		Session session = Session.getActiveSession();
		if (!LoginActivity.hasPermission(session, LoginActivity.PUBLISH_ACTION)) {
			pendingPublishActionReauthorization = true;
			LoginActivity.requestPublishPermissions(this, session, LoginActivity.PERMISSION_PUBLISH_ACTION);
		} else {
			pendingPublishActionReauthorization = false;
			AppLinking.recommendUser(this, uid, uiHelper, SHARE_USER_CODE);
		}
	}

	@Override
	public void onRsvpChanged(String rsvp) {
		pendingRsvpEventReauthorization = false;
		eventInfo.getEvent().setRsvp_status(rsvp);
		setCanPost();
	}

	@Override
	public void onRsvpPermissionRequest() {
		Session session = Session.getActiveSession();
		pendingRsvpEventReauthorization = true;
		LoginActivity.requestPublishPermissions(this, session, LoginActivity.PERMISSION_RSVP_EVENT);
	}
}
