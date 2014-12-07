package com.hitchlab.tinkle.appevent;

import java.util.ArrayList;

import com.hitchlab.tinkle.R;
import com.facebook.Session;
import com.facebook.widget.FacebookDialog;
import com.hitchlab.tinkle.ActivityTransition;
import com.hitchlab.tinkle.LoginActivity;
import com.hitchlab.tinkle.eventactions.RsvpEventHandling;
import com.hitchlab.tinkle.objects.FbEventCompleteInfo;
import com.hitchlab.tinkle.supports.ImageLoading;
import com.hitchlab.tinkle.supports.TimeFrame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EventDetailFragment extends Fragment{
	public static final int RSVP_ATTEND = 0;
	public static final int RSVP_MAYBE = 1;
	public static final int RSVP_DECLINED = 2;

	private Context context;
	private ImageLoading imageLoading;

	private View emptyView; 
	private ImageView eventImage;
	private TextView eventTitle;

	private View rsvpDecline;
	private View rsvpMaybe;
	private View rsvpAttending;

	private View addressView;
	private TextView eventLocation;
	private TextView eventStreet;
	private TextView eventCity;
	private View getDirection;

	private TextView eventPrivacy;
	private TextView eventTime;
	private TextView eventHost;
	private TextView shareButton;

	private View friendsView;
	private LinearLayout friendsList;
	private View goingView;
	private View maybeView;
	private View invitedView;
	private TextView goingNumber;
	private TextView maybeNumber;
	private TextView invitedNumber;

	private TextView eventDescription;

	private FbEventCompleteInfo event;
	private RsvpEventHandling rsvpEventHandling;

	private String attemptRsvp;

	private ArrayList<String> friendsInterested;
	private ArrayList<FrameLayout> friendsInterestedView;

	private int currentRsvpChoice;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.context = getActivity();
		View view = inflater.inflate(R.layout.event_detail_fragment, container, false);
		this.imageLoading = new ImageLoading(context);
		initEventDetailContents(view);
		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	/**
	 * Init the view components
	 */
	private void initEventDetailContents(View view) {
		this.emptyView = view.findViewById(R.id.event_full_detail_empty_view);
		this.eventImage = (ImageView) view.findViewById(R.id.event_full_event_image);
		this.eventTitle = (TextView) view.findViewById(R.id.event_full_event_header_title);
		this.shareButton = (TextView) view.findViewById(R.id.event_full_event_share_button);
		this.shareButton.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				shareEvent();
			}
		});

		this.rsvpAttending = view.findViewById(R.id.event_full_detail_button_rsvp_attend);
		rsvpAttending.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!rsvpAttending.isSelected()) changeRsvpStatus(RSVP_ATTEND);
			}
		});

		this.rsvpMaybe = view.findViewById(R.id.event_full_detail_button_rsvp_maybe);
		rsvpMaybe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!rsvpMaybe.isSelected()) changeRsvpStatus(RSVP_MAYBE);
			}
		});

		this.rsvpDecline = view.findViewById(R.id.event_full_detail_button_rsvp_decline);
		rsvpDecline.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!rsvpDecline.isSelected()) changeRsvpStatus(RSVP_DECLINED);
			}
		});


		//locations
		this.addressView = view.findViewById(R.id.event_full_detail_address_container);
		this.eventLocation = (TextView) view.findViewById(R.id.event_full_detail_location);
		this.eventStreet = (TextView) view.findViewById(R.id.event_full_detail_location_street);
		this.eventCity = (TextView) view.findViewById(R.id.event_full_detail_location_city);
		this.getDirection = view.findViewById(R.id.event_full_detail_get_direction);
		getDirection.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + event.getEvent().getLocation()));
				startActivity(intent);
			}	
		});

		//details
		this.eventPrivacy = (TextView) view.findViewById(R.id.event_full_event_privacy);
		this.eventTime = (TextView) view.findViewById(R.id.event_full_event_time);
		this.eventHost = (TextView) view.findViewById(R.id.event_full_event_host);
		this.eventDescription = (TextView) view.findViewById(R.id.event_full_event_description);

		//friends
		this.friendsView = view.findViewById(R.id.event_full_friends_view);
		this.friendsList = (LinearLayout) view.findViewById(R.id.event_full_friends);
		this.goingView = view.findViewById(R.id.event_full_members_going);
		this.goingNumber = (TextView) view.findViewById(R.id.event_full_members_going_number);
		goingView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, MemberActivity.class);
				intent.putExtra("eid", event.getEvent().getId());
				intent.putExtra(MemberActivity.TYPE, MemberActivity.TYPE_ATTENDING);
				context.startActivity(intent);
			}
		});

		this.maybeView = view.findViewById(R.id.event_full_members_maybe);
		this.maybeNumber = (TextView) view.findViewById(R.id.event_full_members_maybe_number);
		maybeView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, MemberActivity.class);
				intent.putExtra("eid", event.getEvent().getId());
				intent.putExtra(MemberActivity.TYPE, MemberActivity.TYPE_MAYBE);
				context.startActivity(intent);
			}
		});

		this.invitedView = view.findViewById(R.id.event_full_members_invited);
		this.invitedNumber = (TextView) view.findViewById(R.id.event_full_members_invited_number);
		invitedView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, MemberActivity.class);
				intent.putExtra("eid", event.getEvent().getId());
				intent.putExtra(MemberActivity.TYPE, MemberActivity.TYPE_INVITED);
				context.startActivity(intent);
			}
		});
	}
	
	/**
	 * public method that will basically handle the rsvp change
	 * @param rsvp code
	 */
	public void changeRsvpStatus(int rsvpCode) {
		if (rsvpCode != -1) currentRsvpChoice = rsvpCode;
		Session session = Session.getActiveSession();
		if (!LoginActivity.hasPermission(session, LoginActivity.RSVP_EVENT)) madeRsvpPermissionRequest();
		else {
			switch (currentRsvpChoice) {
				case RSVP_ATTEND:
					attemptRsvp = RsvpEventHandling.ATTENDING;
					rsvpEventHandling.changeRsvp(session, attemptRsvp);
					break;
				case RSVP_MAYBE:
					attemptRsvp = RsvpEventHandling.UNSURE;
					rsvpEventHandling.changeRsvp(session, attemptRsvp);
					break;
				case RSVP_DECLINED:
					attemptRsvp = RsvpEventHandling.DECLINED;
					rsvpEventHandling.changeRsvp(session, attemptRsvp);
					break;
				default: break;
			}
		}
	}

	/**
	 * fill in the data that pass from the activity
	 */
	public void displayEventInfo(FbEventCompleteInfo fbEvent) {
		this.event = fbEvent;
		//display the view
		if (event.getCoverPicture().equals(""))	eventImage.setImageResource(R.drawable.no_cover);
		else imageLoading.displayImage(event.getCoverPicture(), eventImage);
		eventTitle.setText(event.getEvent().getName());

		showCurrentRsvp();
		eventHost.setText(event.getEvent().getHost());
		eventDescription.setText(event.getEvent().getDescription());
		displayEventTime();
		displayEventPrivacy();
		displayAddressView();
		if (rsvpEventHandling == null) rsvpEventHandling = new RsvpEventHandling(context, event.getEvent()) {
			@Override
			public void rsvpChange(boolean success) {
				if (success) {
					event.getEvent().setRsvp_status(attemptRsvp);
					notifyRsvpChanged(attemptRsvp);
					showCurrentRsvp();
				}
			}
		};

		goingNumber.setText(String.valueOf(event.getAttendingCount()));
		maybeNumber.setText(String.valueOf(event.getMaybeCount()));
		invitedNumber.setText(String.valueOf(event.getNotRepliedCount()));

		if (event.getFriendsAttending().size() == 0 && event.getFriendsMaybe().size() == 0) friendsView.setVisibility(View.GONE);
		else {
			friendsView.setVisibility(View.VISIBLE);
			populateFriendsInterestedList(event);
		}

		this.emptyView.setVisibility(View.GONE);
	}

	/**
	 * populate the list with people who interested in the event
	 * @param FbEventCompleteInfo
	 */
	private void populateFriendsInterestedList(FbEventCompleteInfo event) {
		friendsList.removeAllViews();
		if (friendsInterested == null) friendsInterested = new ArrayList<String>();
		else friendsInterested.clear();
		if (friendsInterestedView == null) friendsInterestedView = new ArrayList<FrameLayout>();
		else friendsInterestedView.clear();

		final float scale = context.getResources().getDisplayMetrics().density;
		int size = (int) (58 * scale + 0.5f);
		int margin = (int) (3 * scale + 0.5f);
		int rsvpInPixel = (int) (20 * scale + 0.5f);
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(size + 2*margin, size + 2*margin);
		LinearLayout.LayoutParams pictureParams = new LinearLayout.LayoutParams(size, size);
		FrameLayout.LayoutParams rsvpParams = new FrameLayout.LayoutParams(rsvpInPixel, rsvpInPixel);
		rsvpParams.gravity = Gravity.TOP | Gravity.LEFT;

		for (String friendUid : event.getFriendsAttending()) {
			FrameLayout frameLayout = new FrameLayout(context);
			frameLayout.setLayoutParams(layoutParams);
			frameLayout.setPadding(margin, margin, margin, margin);
			frameLayout.setBackgroundResource(R.drawable.recommend_people_view_bg);

			ImageView picture = new ImageView(context);
			picture.setLayoutParams(pictureParams);
			imageLoading.displayImage("http://graph.facebook.com/" + friendUid + "/picture?width=150&height=150", picture);
			frameLayout.addView(picture);

			ImageView rsvp = new ImageView(context);
			rsvp.setLayoutParams(rsvpParams);
			rsvp.setBackgroundResource(R.drawable.recommend_people_attending);
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) rsvp.setAlpha((float) 0.8);
			frameLayout.addView(rsvp);

			friendsList.addView(frameLayout);
			friendsInterestedView.add(frameLayout);
			friendsInterested.add(friendUid);
		}

		for (String friendUid : event.getFriendsMaybe()) {
			FrameLayout frameLayout = new FrameLayout(context);
			frameLayout.setLayoutParams(layoutParams);
			frameLayout.setPadding(margin, margin, margin, margin);
			frameLayout.setBackgroundResource(R.drawable.recommend_people_view_bg);

			ImageView picture = new ImageView(context);
			picture.setLayoutParams(pictureParams);
			imageLoading.displayImage("http://graph.facebook.com/" + friendUid + "/picture?width=150&height=150", picture);
			frameLayout.addView(picture);

			ImageView rsvp = new ImageView(context);
			rsvp.setLayoutParams(rsvpParams);
			rsvp.setBackgroundResource(R.drawable.recommend_people_maybe);
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) rsvp.setAlpha((float) 0.8);
			frameLayout.addView(rsvp);

			friendsList.addView(frameLayout);
			friendsInterestedView.add(frameLayout);
			friendsInterested.add(friendUid);
		}

		for (int i = 0; i < friendsInterestedView.size(); i++) {
			final int index = i;
			friendsInterestedView.get(index).setOnClickListener(new FrameLayout.OnClickListener() {
				@Override
				public void onClick(View view) {
					ActivityTransition.displayUserInfoPage(friendsInterested.get(index), context);
				}
			});
		}
	}

	/**
	 * Check if the given user has rsvp for event. if so, display his/her rsvp selection. 
	 * Otherwise, notify for further action
	 * @return true if prior selection has beeb made
	 */
	public boolean showCurrentRsvp() {
		String rsvp = event.getEvent().getRsvp_status();	
		if (rsvp.equals(RsvpEventHandling.ATTENDING)) {
			rsvpAttending.setSelected(true);
			rsvpMaybe.setSelected(false);
			rsvpDecline.setSelected(false);
		} else if (rsvp.equals(RsvpEventHandling.UNSURE)) {
			rsvpAttending.setSelected(false);
			rsvpMaybe.setSelected(true);
			rsvpDecline.setSelected(false);
		} else if (rsvp.equals(RsvpEventHandling.DECLINED)){
			rsvpAttending.setSelected(false);
			rsvpMaybe.setSelected(false);
			rsvpDecline.setSelected(true);
		} else return false;
		return true;
	}

	/**
	 * Display event time
	 * @param event basic info
	 */
	private void displayEventTime() {
		long start_time = event.getEvent().getStart_time();
		long end_time = event.getEvent().getEnd_time();
		if (end_time == 0) eventTime.setText(TimeFrame.getEventDisplayTime(start_time));
		else eventTime.setText(TimeFrame.getEventDisplayTime(start_time) + " - " +
				TimeFrame.getEventDisplayTime(end_time));
	}

	/**
	 * fetch the event's privacy
	 * @param eventAdditionalInfo
	 */
	private void displayEventPrivacy() {
		String privacy = event.getEvent().getPrivacy();
		boolean canInvite = event.isInviteOption();
		boolean canPresentOpenGraphMessageDialog = FacebookDialog.canPresentOpenGraphMessageDialog(context, FacebookDialog.OpenGraphMessageDialogFeature.OG_MESSAGE_DIALOG);

		if (privacy.equals("SECRET")) {
			this.shareButton.setVisibility(View.GONE);
			eventPrivacy.setCompoundDrawablesWithIntrinsicBounds(R.drawable.privacy_secret, 0, 0, 0);
			eventPrivacy.setText("Invited-Only");
		} else {
			if (privacy.equals("OPEN")) {
				eventPrivacy.setCompoundDrawablesWithIntrinsicBounds(R.drawable.privacy_public, 0, 0, 0);
				eventPrivacy.setText("Public");
				if (canPresentOpenGraphMessageDialog) this.shareButton.setVisibility(View.VISIBLE);
				else this.shareButton.setVisibility(View.GONE);
			} else {
				eventPrivacy.setCompoundDrawablesWithIntrinsicBounds(R.drawable.privacy_standard, 0, 0, 0);
				eventPrivacy.setText("Friends of Guests");
				if (canInvite && canPresentOpenGraphMessageDialog) this.shareButton.setVisibility(View.VISIBLE);
				else this.shareButton.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * Display the address view if the location exist
	 */
	private void displayAddressView() {
		final String location = event.getEvent().getLocation();
		final String venue_street = event.getVenueStreet();
		final String venue_city = event.getVenueCity();
		final String venue_state = event.getVenueState();
		final String venue_zip = event.getVenueZip();

		if (location.equals("")) {
			addressView.setVisibility(View.GONE);
		} else {
			addressView.setVisibility(View.VISIBLE);
			eventLocation.setText(location);
			if (!venue_street.equals("")) {
				eventStreet.setVisibility(View.VISIBLE);
				eventStreet.setText(venue_street);
			} else eventStreet.setVisibility(View.GONE);
			if (!venue_city.equals("")) {
				eventCity.setVisibility(View.VISIBLE);
				eventCity.setText(venue_city + ", " + venue_state + " " + venue_zip); 
			}
			else eventCity.setVisibility(View.GONE);
		}
	}

	//attach to activity
	/**
	 * Interface to tell activity that share event action is clicked
	 */
	private OnEventShareClick onShareClick;
	public interface OnEventShareClick {
		public void onShareEvent();
	}

	public void shareEvent() {
		onShareClick.onShareEvent();
	}

	/**
	 * Interface to ask activity to request the rsvp event permission
	 */
	private RequestRsvpPermission requestRsvpPermission;
	public interface RequestRsvpPermission {
		public void onRsvpPermissionRequest();
	}

	public void madeRsvpPermissionRequest() {
		requestRsvpPermission.onRsvpPermissionRequest();
	}

	/**
	 * Interface to tell the activity that the rsvp has been changed
	 */
	private RsvpChanged rsvpChanged;
	public interface RsvpChanged {
		public void onRsvpChanged(String rsvp);
	}

	public void notifyRsvpChanged(String rsvp) {
		rsvpChanged.onRsvpChanged(rsvp);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		onShareClick = (OnEventShareClick) activity;
		requestRsvpPermission = (RequestRsvpPermission) activity;
		rsvpChanged = (RsvpChanged) activity;
	}
}
