package com.hitchlab.tinkle.appevent;

import java.util.ArrayList;
import java.util.HashMap;

import com.hitchlab.tinkle.R;
import com.facebook.widget.FacebookDialog;
import com.hitchlab.tinkle.ActivityTransition;
import com.hitchlab.tinkle.dialog.RedirectDialog;
import com.hitchlab.tinkle.objects.FbEvent;
import com.hitchlab.tinkle.objects.Friend;
import com.hitchlab.tinkle.objects.RecommendUser;
import com.hitchlab.tinkle.objects.RecommendUserInfo;
import com.hitchlab.tinkle.service.event.QueryEventRecommendUserInfo;
import com.hitchlab.tinkle.supports.ImageLoading;
import com.hitchlab.tinkle.template.UserEventAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

@SuppressLint("InflateParams") 
public class EventRecommendPersonInfoFragment extends Fragment{

	private Context context;
	private ImageLoading imageLoading;

	//here is the recommended session
	private View userInfoLoadingView;
	private ListView eventsList;

	private View headerView;
	private ImageView userCoverPic;
	private ImageView userProfilePic;
	private TextView userProfileName;
	private TextView userProfileInfo;
	private View redirectToProfile;
	private View redirectToMessage;
	private View shareButton;
	private View friendsListView;
	private LinearLayout friendsList;

	private UserEventAdapter eventAdapter;
	private RecommendUser selectedUser;
	private ArrayList<Friend> friends;
	private ArrayList<LinearLayout> friendsLayout;
	
	private HashMap<String, RecommendUserInfo> recommendUserMap;

	BroadcastReceiver recommendUserInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int resultType = intent.getIntExtra("result_type", -1);
			if (resultType == QueryEventRecommendUserInfo.RESULT_INVALID) {
				Toast.makeText(context, "Invalid session. Please re-login.", Toast.LENGTH_SHORT).show();
			} else if (resultType == QueryEventRecommendUserInfo.RESULT_NO_INTERNET) {
				Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show();
			} else if (resultType == QueryEventRecommendUserInfo.RESULT_ERROR) {
				Toast.makeText(context, "Error retreaving the user info", Toast.LENGTH_SHORT).show();
			} else if (resultType == QueryEventRecommendUserInfo.RESULT_OK) {
				RecommendUserInfo userInfo = intent.getParcelableExtra("data");
				if (userInfo.getUid().equals(selectedUser.getUid())) {
					displayRecommendUserInfo(userInfo);
					userInfoLoadingView.setVisibility(View.GONE);
				}
				recommendUserMap.put(userInfo.getUid(), userInfo);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = getActivity();
		this.recommendUserMap = new HashMap<String, RecommendUserInfo>();
		this.imageLoading = new ImageLoading(context);
		this.eventAdapter = new UserEventAdapter(context, imageLoading);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.headerView = inflater.inflate(R.layout.event_recommend_person_header, null);
		this.userCoverPic = (ImageView) headerView.findViewById(R.id.recommend_user_cover_pic);
		this.userProfilePic = (ImageView) headerView.findViewById(R.id.recommend_user_profile_pic);
		this.userProfileName = (TextView) headerView.findViewById(R.id.recommend_user_profile_name);
		this.userProfileInfo = (TextView) headerView.findViewById(R.id.recommend_user_profile_info);
		this.redirectToProfile = headerView.findViewById(R.id.recommend_user_redirect_profile);
		redirectToProfile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RedirectDialog.showRedirectDialog(getActivity(), selectedUser.getUid(), RedirectDialog.REDIRECT_TO_PROFILE);
			}
		});
		this.redirectToMessage = headerView.findViewById(R.id.recommend_user_redirect_message);
		redirectToMessage.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				RedirectDialog.showRedirectDialog(getActivity(), selectedUser.getUid(), RedirectDialog.REDIRECT_TO_MESSAGE);
			}
		});
		this.shareButton = headerView.findViewById(R.id.recommend_user_share_user);
		shareButton.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				if (selectedUser != null) userItemShare(selectedUser.getUid());
			}
		});
		if (FacebookDialog.canPresentOpenGraphMessageDialog(context, FacebookDialog.OpenGraphMessageDialogFeature.OG_MESSAGE_DIALOG)) {
			shareButton.setVisibility(View.VISIBLE);
			redirectToMessage.setBackgroundResource(R.drawable.rounded_list_item_bottom_right);
		} else {
			shareButton.setVisibility(View.GONE);
			redirectToMessage.setBackgroundResource(R.drawable.rounded_list_item_bottom_mid);
		}

		this.friendsListView = headerView.findViewById(R.id.recommend_user_mutual_friends_view);
		this.friendsList = (LinearLayout) headerView.findViewById(R.id.recommend_user_mutual_friends);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.event_recommend_person_info, container, false);

		this.userInfoLoadingView = view.findViewById(R.id.event_recommend_person_info_loading_view);
		this.eventsList = (ListView) view.findViewById(R.id.recommend_user_common_events_list);
		eventsList.addHeaderView(headerView, null, false);
		eventsList.setAdapter(eventAdapter);
		eventsList.setDivider(new ColorDrawable(0x00c2c2c2));
		eventsList.setDividerHeight(0);
		eventsList.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				FbEvent event = eventAdapter.getEventItem(position - 1); //minus 1 for the header
				Intent intent = new Intent(context, EventFullActivity.class);
				intent.putExtra("eid", event.getId());
				context.startActivity(intent);
			}	
		});

		return view;
	}

	@Override
	public void onPause() {
		getActivity().unregisterReceiver(recommendUserInfoReceiver);
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(recommendUserInfoReceiver, new IntentFilter(QueryEventRecommendUserInfo.NOTIFICATION));
	} 

	/**
	 * Populate the friends list
	 */
	private void populateFriendsList() {
		if (friendsLayout == null) friendsLayout = new ArrayList<LinearLayout>();
		else friendsLayout.clear();
		friendsList.removeAllViews();

		final float scale = context.getResources().getDisplayMetrics().density;
		int imageSize = (int) (60 * scale + 0.5f);
		int margin = (int) (2 * scale + 0.5f);
		LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(imageSize + 2 * margin, imageSize + 2 * margin);
		LinearLayout.LayoutParams pictureParams = new LinearLayout.LayoutParams(imageSize, imageSize);

		for (Friend friend : friends) {
			LinearLayout viewLayout = new LinearLayout(context);
			viewLayout.setLayoutParams(viewParams);
			viewLayout.setPadding(margin, margin, margin, margin);
			viewLayout.setBackgroundResource(R.drawable.recommend_people_view_bg);

			ImageView picture = new ImageView(context);
			picture.setLayoutParams(pictureParams);
			imageLoading.displayImage("http://graph.facebook.com/" + friend.getUid() + "/picture?width=150&height=150", picture);
			viewLayout.addView(picture);

			friendsList.addView(viewLayout);
			friendsLayout.add(viewLayout);
		}

		for (int i = 0; i < friendsLayout.size(); i++) {
			final int index = i;
			friendsLayout.get(index).setOnClickListener(new FrameLayout.OnClickListener() {
				@Override
				public void onClick(View view) {
					ActivityTransition.displayUserInfoPage(friends.get(index).getUid(), context);
				}
			});
		}


	}

	/**
	 * Show the user info
	 * @param selectedUser
	 */
	public void showUserInfo(RecommendUser selectedUser) {
		this.selectedUser = selectedUser;
		if (recommendUserMap.containsKey(selectedUser.getUid())) {
			displayRecommendUserInfo(recommendUserMap.get(selectedUser.getUid()));
		} else {
			userInfoLoadingView.setVisibility(View.VISIBLE);
			Intent intent = new Intent(context, QueryEventRecommendUserInfo.class);
			intent.putExtra("uid", selectedUser.getUid());
			context.startService(intent);
		}

		//now we do a basic preparation
		if (selectedUser.isFriend()) this.userProfileInfo.setText("FRIEND");
		else if (selectedUser.getNumMutualFriend() == 0) this.userProfileInfo.setText("");
		else this.userProfileInfo.setText(selectedUser.getNumMutualFriend() + " mutual friends");

		this.userProfileName.setText(selectedUser.getUsername());
		imageLoading.displayImage("http://graph.facebook.com/" + selectedUser.getUid() + "/picture?width=100&height=100", this.userProfilePic);
	}
	
	/**
	 * Display the recommend user info
	 * @param recommendUserInfo
	 */
	private void displayRecommendUserInfo(RecommendUserInfo userInfo) {
		if (!userInfo.getCoverUrl().equals("")) 
			imageLoading.displayImage(userInfo.getCoverUrl(), userCoverPic);
		else userCoverPic.setBackgroundResource(R.drawable.no_cover);

		friends = userInfo.getMutualFriends();
		if (friends.size() == 0) friendsListView.setVisibility(View.GONE);
		else friendsListView.setVisibility(View.VISIBLE);

		populateFriendsList();

		eventAdapter.setEvents(userInfo.getCommonEvents());
		eventAdapter.notifyDataSetChanged();
		eventsList.smoothScrollToPosition(0);
	}

	//attach to activity
	private UserItem userItem;
	public interface UserItem {
		public void onShare(String uid);
	}

	public void userItemShare(String uid) {
		userItem.onShare(uid);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		userItem = (UserItem) activity;
	}
}
