package com.hitchlab.tinkle.appevent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.hitchlab.tinkle.R;
import com.hitchlab.tinkle.eventactions.RsvpEventHandling;
import com.hitchlab.tinkle.objects.RecommendUser;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;
import com.hitchlab.tinkle.service.event.QueryEventPeopleRecommendation;
import com.hitchlab.tinkle.service.event.QueryEventPhoto;
import com.hitchlab.tinkle.supports.ImageLoading;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class EventRecommendPeopleListFragment extends Fragment{
	private Context context;
	private ImageLoading imageLoading;

	private LinearLayout list;
	private String eid;

	private int selectedIndex;

	private ArrayList<RecommendUser> listUsers;
	private ArrayList<FrameLayout> listUsersLayout;
	
	private View loadingView;
	private View emptyView;

	private BroadcastReceiver eventPeopleRecommendationReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int resultType = intent.getIntExtra("result_type", -1);
			if (resultType == QueryEventPeopleRecommendation.RESULT_INVALID) {
				Toast.makeText(context, "Invalid session. Please re-login.", Toast.LENGTH_SHORT).show();
			} else if (resultType == QueryEventPeopleRecommendation.RESULT_NO_INTERNET) {
				Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show();
			} else if (resultType == QueryEventPhoto.RESULT_OK) {
				if (eid.equals(intent.getStringExtra("eid"))) {
					ArrayList<RecommendUser> recommendations = intent.getParcelableArrayListExtra("data");
					final Context mContext = context;
					Collections.sort(recommendations, new Comparator<RecommendUser>() {
						@Override
						public int compare(RecommendUser lhs, RecommendUser rhs) {
							if (SharedPreference.containKey(mContext, Preference.GENDER)) {
								String userGender = SharedPreference.getPrefStringValue(mContext, Preference.GENDER);
								if (lhs.getGender().equals(userGender) && !rhs.getGender().equals(userGender)) {
									if (lhs.getNumMutualFriend() - rhs.getNumMutualFriend() < 8) return 1;
								}
								else if (!lhs.getGender().equals(userGender) && rhs.getGender().equals(userGender)) {
									if (rhs.getNumMutualFriend() - lhs.getNumMutualFriend() < 8) return -1;
								}
							}
							if (lhs.isFriend() && !rhs.isFriend()) return 1;
							else if (!lhs.isFriend() && rhs.isFriend()) return -1;
							else return rhs.getNumMutualFriend() - lhs.getNumMutualFriend();
						}
					});

					//now after we filtered it out, we get the top 15 results, put it in random order
					ArrayList<RecommendUser> filteredResults = new ArrayList<RecommendUser>();
					for (int i = 0; i < Math.min(recommendations.size(), 15); i++) 
						filteredResults.add(recommendations.get(i));

					//					long seed = System.nanoTime();
					//					Collections.shuffle(filteredResults, new Random(seed));

					setListPeople(filteredResults);
				}
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = getActivity();
		this.imageLoading = new ImageLoading(context);
		this.selectedIndex = 0;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.event_recommendation_people_list, container, false);
		this.list = (LinearLayout) view.findViewById(R.id.event_recommendation_people_list);
		this.loadingView = view.findViewById(R.id.event_recommend_people_loading);
		this.emptyView = view.findViewById(R.id.event_recommend_people_empty);

		return view;
	}
	
	@Override
	public void onPause() {
		getActivity().unregisterReceiver(eventPeopleRecommendationReceiver);
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(eventPeopleRecommendationReceiver, new IntentFilter(QueryEventPeopleRecommendation.NOTIFICATION));
	} 
	
	/**
	 * set the eid of the event
	 * @param eid
	 */
	public void setEid(String eid) {
		this.eid = eid;
		Intent intent = new Intent(context, QueryEventPeopleRecommendation.class);
		intent.putExtra("eid", eid);
		context.startService(intent);
	}
	
	/**
	 * get the set of recommend users
	 */
	public ArrayList<RecommendUser> getRecommendUsers() {
		return listUsers;
	}

	/**
	 * Set the list of people to display
	 * This will be deprecate
	 */
	public void setListPeople(ArrayList<RecommendUser> people) {
		this.listUsers = people;
		this.listUsersLayout = new ArrayList<FrameLayout>();

		if (people.size() > 0) emptyView.setVisibility(View.GONE);

		list.removeAllViews();

		final float scale = context.getResources().getDisplayMetrics().density;
		int iconWidthInPixel = (int) (85 * scale + 0.5f);
		int iconHeightInPixel = (int) (116 * scale + 0.5f);
		int marginInPixel = (int) (2 * scale + 0.5f);

		int rsvpInPixel = (int) (20 * scale + 0.5f);

		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(iconWidthInPixel + 2*marginInPixel, iconHeightInPixel + 2*marginInPixel);
		LinearLayout.LayoutParams pictureParams = new LinearLayout.LayoutParams(iconWidthInPixel, iconHeightInPixel);
		LinearLayout.LayoutParams separatorParams = new LinearLayout.LayoutParams(2, iconHeightInPixel + 2 * marginInPixel);

		int additionalInfoColor = Color.parseColor("#22000000");
		FrameLayout.LayoutParams additionalInfosParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		additionalInfosParams.gravity = Gravity.BOTTOM;

		LinearLayout.LayoutParams info = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		FrameLayout.LayoutParams rsvpParams = new FrameLayout.LayoutParams(rsvpInPixel, rsvpInPixel);
		rsvpParams.gravity = Gravity.TOP | Gravity.LEFT;

		for (RecommendUser person : people) {

			FrameLayout frameLayout = new FrameLayout(context);
			frameLayout.setLayoutParams(layoutParams);
			frameLayout.setPadding(marginInPixel, marginInPixel, marginInPixel, marginInPixel);
			frameLayout.setBackgroundResource(R.drawable.recommend_people_view_bg);

			ImageView picture = new ImageView(context);
			picture.setLayoutParams(pictureParams);
			imageLoading.displayImage("http://graph.facebook.com/" + person.getUid() + "/picture?type=large", picture);
			picture.setScaleType(ScaleType.CENTER_CROP);
			frameLayout.addView(picture);

			LinearLayout additionalInfos = new LinearLayout(context);
			additionalInfos.setOrientation(LinearLayout.VERTICAL);
			additionalInfos.setBackgroundColor(additionalInfoColor);
			additionalInfos.setPadding(marginInPixel, 4, marginInPixel, 4);
			additionalInfos.setLayoutParams(additionalInfosParams);

			TextView name = new TextView(context);
			name.setLayoutParams(info);
			name.setTextColor(Color.WHITE);
			name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
			name.setSingleLine();
			name.setTypeface(null, Typeface.BOLD);
			name.setGravity(Gravity.RIGHT);
			name.setText(person.getUsername());
			additionalInfos.addView(name);

			TextView numMutualFriends = new TextView(context);
			numMutualFriends.setLayoutParams(info);
			numMutualFriends.setTextColor(Color.WHITE);
			numMutualFriends.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
			numMutualFriends.setSingleLine();
			numMutualFriends.setGravity(Gravity.RIGHT);
			if (person.isFriend()) numMutualFriends.setText("FRIEND");
			else if (person.getNumMutualFriend() == 0) numMutualFriends.setText("");
			else numMutualFriends.setText(person.getNumMutualFriend() + " mutuals");
			additionalInfos.addView(numMutualFriends);

			frameLayout.addView(additionalInfos);

			ImageView rsvp = new ImageView(context);
			rsvp.setLayoutParams(rsvpParams);
			if (person.getRsvpStatus().equals(RsvpEventHandling.ATTENDING)) rsvp.setBackgroundResource(R.drawable.recommend_people_attending);
			else rsvp.setBackgroundResource(R.drawable.recommend_people_maybe);
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) rsvp.setAlpha((float) 0.8);
			frameLayout.addView(rsvp);

			View separator = new View(context);
			separator.setLayoutParams(separatorParams);
			separator.setBackgroundColor(Color.BLACK);

			list.addView(frameLayout);
			list.addView(separator);

			listUsersLayout.add(frameLayout);
		}

		loadingView.setVisibility(View.GONE);

		for (int i = 0; i < people.size(); i++) {
			final int index = i;
			listUsersLayout.get(index).setOnClickListener(new FrameLayout.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (!listUsersLayout.get(index).isSelected()) 
						selectRecommendPersonIndex(index, true);
					else userItemClick(null, true);
				}
			});
		}

		if (people.size() != 0) selectRecommendPersonIndex(0, false);
	}

	/**
	 * Select a view with the index from the recommend people list
	 * @param index
	 */
	private void selectRecommendPersonIndex(int index, boolean needExpandView) {
		listUsersLayout.get(selectedIndex).setSelected(false);
		selectedIndex = index;
		listUsersLayout.get(selectedIndex).setSelected(true);

		RecommendUser selectedUser = listUsers.get(selectedIndex);
		userItemClick(selectedUser, needExpandView);
	}

	//attach to activity
	private UserItem userItem;
	public interface UserItem {
		public void onClick(RecommendUser user, boolean needExpandView);
	}

	public void userItemClick(RecommendUser user, boolean needExpandView) {
		userItem.onClick(user, needExpandView);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		userItem = (UserItem) activity;
	}

}
