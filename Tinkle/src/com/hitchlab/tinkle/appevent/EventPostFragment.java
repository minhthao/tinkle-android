package com.hitchlab.tinkle.appevent;

import java.util.ArrayList;

import com.hitchlab.tinkle.R;
import com.facebook.Session;
import com.hitchlab.tinkle.LoginActivity;
import com.hitchlab.tinkle.dialog.StatusDialog;
import com.hitchlab.tinkle.images.PhotoOptionDialog;
import com.hitchlab.tinkle.images.UploadPhoto;
import com.hitchlab.tinkle.objects.Feed;
import com.hitchlab.tinkle.service.event.QueryEventFeed;
import com.hitchlab.tinkle.template.event.FeedListViewAdapter;

import eu.erikw.PullToRefreshListView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class EventPostFragment extends Fragment {

	private Context context;
	private PhotoOptionDialog photoOptionDialog;
	private UploadPhoto uploadPhoto;
	private StatusDialog statusDialog;
	
	private View newStatus;
	private View newPhoto;
	private PullToRefreshListView feedList;
	private FeedListViewAdapter adapter;
	
	private String eid;
	private boolean canPost;
	
	private BroadcastReceiver eventFeedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int resultType = intent.getIntExtra("result_type", -1);
			if (resultType == QueryEventFeed.RESULT_INVALID) {
				Toast.makeText(context, "Invalid session. Please re-login.", Toast.LENGTH_SHORT).show();
			} else if (resultType == QueryEventFeed.RESULT_NO_INTERNET) {
				Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show();
			} else if (resultType == QueryEventFeed.RESULT_OK) {
				ArrayList<Feed> feeds = intent.getParcelableArrayListExtra("data");
				adapter.setFeeds(feeds);
				adapter.notifyDataSetChanged();
			}
			if (feedList.isRefreshing()) feedList.onRefreshComplete();
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = getActivity();
		this.adapter = new FeedListViewAdapter(context);
		this.photoOptionDialog = new PhotoOptionDialog(getActivity(), context);
		this.uploadPhoto = new UploadPhoto(context) {
			@Override
			public void notifyImageUploaded() {
				Toast.makeText(context, "Image uploaded", Toast.LENGTH_SHORT).show();
				doUpdateList();
			}
		};
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.event_post_fragment, container, false);
		this.feedList = (PullToRefreshListView) view.findViewById(R.id.event_feed_list);
		feedList.setDivider(new ColorDrawable(0x00c2c2c2));
		feedList.setDividerHeight(0);
		feedList.setAdapter(adapter);
		feedList.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				doUpdateList();
			}
		});
		
		this.newStatus = view.findViewById(R.id.event_feed_new_status);
		this.newPhoto = view.findViewById(R.id.event_feed_new_photo);
		initUploadOptions();
		return view;
	}
	
	@Override
	public void onPause() {
		getActivity().unregisterReceiver(eventFeedReceiver);
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(eventFeedReceiver, new IntentFilter(QueryEventFeed.NOTIFICATION));
	}

	/**
	 * Init the action for the uploading
	 */
	private void initUploadOptions() {
		newPhoto.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				if (canPost) {
					Session session = Session.getActiveSession();
					if (!LoginActivity.hasPermission(session, LoginActivity.PUBLISH_ACTION)) 
						LoginActivity.requestPublishPermissions(getActivity(), session, LoginActivity.PERMISSION_PUBLISH_ACTION);
					else photoOptionDialog.showDialog();
				} else Toast.makeText(context, "Have to make rsvp before posting", Toast.LENGTH_SHORT).show(); 
			}
		});


		statusDialog = new StatusDialog(context) {
			@Override
			public void onPostStatusSucceed() {
				Toast.makeText(context, "Status posted.", Toast.LENGTH_SHORT).show();
				doUpdateList();
			}
		};

		newStatus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (canPost) {
					Session session = Session.getActiveSession();
					if (!LoginActivity.hasPermission(session, LoginActivity.PUBLISH_ACTION)) 
						LoginActivity.requestPublishPermissions(getActivity(), session, LoginActivity.PERMISSION_PUBLISH_ACTION);
					else statusDialog.show(eid); 
				} else Toast.makeText(context, "Have to make rsvp before posting", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	/**
	 * Set can post
	 * @param canPost
	 */
	public void setCanPost(boolean canPost) {
		this.canPost = canPost;
	}
	
	
	/**
	 * set the eid of the event
	 * @param eid
	 */
	public void setEid(String eid) {
		if (this.eid == null) {
			this.eid = eid;
			doUpdateList();
		} else this.eid = eid;
	}
	
	/**
	 * do update
	 */
	private void doUpdateList() {
		if (!feedList.isRefreshing()) feedList.setRefreshing();
		Intent intent = new Intent(context, QueryEventFeed.class);
		intent.putExtra("eid", eid);
		context.startService(intent);
	}

	/**
	 * Get the unedited post image url capture from the camera
	 */
	public Uri getUneditedPostImageUri() {
		return photoOptionDialog.getImageUri();
	}

	public void uploadImage(Bitmap photo) {
		uploadPhoto.uploadImage(photo, eid);
	}
}
