package com.hitchlab.tinkle.template;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.hitchlab.tinkle.R;
import com.facebook.widget.FacebookDialog;
import com.hitchlab.tinkle.appevent.EventFullActivity;
import com.hitchlab.tinkle.datasource.FriendEventDataSource;
import com.hitchlab.tinkle.datasource.ShareDataSource;
import com.hitchlab.tinkle.datasource.ViewDataSource;
import com.hitchlab.tinkle.objects.Attendee;
import com.hitchlab.tinkle.objects.FbEvent;
import com.hitchlab.tinkle.supports.ImageLoading;
import com.hitchlab.tinkle.supports.TimeFrame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class NearbyEventListAdapter extends BaseAdapter {	
	private Context context;
	private LayoutInflater layoutInflater;
	private ArrayList<FbEvent> events;
	private ImageLoading imageLoading;
	private FriendEventDataSource friendEventDataSource;
	private ViewDataSource viewDataSource;
	private ShareDataSource shareDataSource;
	
	private int todayEventStartIndex;
	private int thisWeekendEventStartIndex;
	private int thisWeekEventStartIndex;
	private int nextWeekEventStartIndex;
	private int otherEventStartIndex;
	
	private boolean canMessage;
	
	public abstract void shareClick(FbEvent event);

	public NearbyEventListAdapter(Context context, ImageLoading imageLoading) {
		this.context = context;
		this.imageLoading = imageLoading;
		this.friendEventDataSource = new FriendEventDataSource(context);
		this.viewDataSource = new ViewDataSource(context);
		this.shareDataSource = new ShareDataSource(context);
		this.events = new ArrayList<FbEvent>();
		this.canMessage = FacebookDialog.canPresentOpenGraphMessageDialog(context, FacebookDialog.OpenGraphMessageDialogFeature.OG_MESSAGE_DIALOG);
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);	
	}

	/**
	 * Set the events to be display on the list
	 * @param events
	 */
	public void setEvents(ArrayList<FbEvent> todayEvents,
			ArrayList<FbEvent> thisWeekEvents,
			ArrayList<FbEvent> thisWeekendEvents, 
			ArrayList<FbEvent> nextWeekEvents,
			ArrayList<FbEvent> otherEvents) {
		
		events.clear();
		
		if (todayEvents != null && todayEvents.size() > 0) {
			todayEventStartIndex = events.size();
			events.addAll(todayEvents);
		} else todayEventStartIndex = -1;
		
		if (thisWeekEvents != null && thisWeekEvents.size() > 0) {
			thisWeekEventStartIndex = events.size();
			events.addAll(thisWeekEvents);
		} else thisWeekEventStartIndex = -1;
		
		if (thisWeekendEvents != null && thisWeekendEvents.size() > 0) {
			thisWeekendEventStartIndex = events.size();
			events.addAll(thisWeekendEvents);
		} else thisWeekendEventStartIndex = -1;
		
		if (nextWeekEvents != null && nextWeekEvents.size() > 0) {
			nextWeekEventStartIndex = events.size();
			events.addAll(nextWeekEvents);
		} else nextWeekEventStartIndex = -1;
		
		if (otherEvents != null && otherEvents.size() > 0) {
			otherEventStartIndex = events.size();
			events.addAll(otherEvents);
		} else otherEventStartIndex = -1;
	}

	@Override
	public int getCount() {
		return events.size();
	}
	
	@Override
	public Object getItem(int position) {
		return events.get(position);
	}

	/**
	 * Get the FbEvent at given position
	 * @param position
	 * @return FbEvent
	 */
	public FbEvent getEventItem(int position) {
		return (FbEvent) getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@SuppressLint("InflateParams") 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final FbEvent event = getEventItem(position);
		EventViewHolder holder = null;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.main_view_nearby_events_item, null);
			//convertView.setMinimumWidth(ScreenStat.getViewWidth(context, 5, 5));
			holder = new EventViewHolder();
			holder.header = (TextView) convertView.findViewById(R.id.nearby_event_item_header);
			holder.title = (TextView) convertView.findViewById(R.id.nearby_event_item_title);
			holder.eventIcon = (ImageView) convertView.findViewById(R.id.nearby_event_item_icon);
			holder.distance = (TextView) convertView.findViewById(R.id.nearby_event_item_location_distance);
			holder.time = (TextView) convertView.findViewById(R.id.nearby_event_item_time);
			holder.mgs = (TextView) convertView.findViewById(R.id.nearby_event_item_msg);
			holder.location = (TextView) convertView.findViewById(R.id.nearby_event_item_location);
			holder.shareButton = convertView.findViewById(R.id.nearby_event_item_share_button);
			holder.separator = convertView.findViewById(R.id.nearby_event_item_separator);
			holder.moreButton = convertView.findViewById(R.id.nearby_event_item_more_button);
			convertView.setTag(holder);
		} else holder = (EventViewHolder) convertView.getTag();

		holder.header.setVisibility(View.VISIBLE);
		if (position == todayEventStartIndex) {
			holder.header.setText("TODAY");
		} else if (position == thisWeekEventStartIndex) {
			holder.header.setText("THIS WEEK");
		} else if (position == thisWeekendEventStartIndex) {
			holder.header.setText("THIS WEEKEND");
		} else if (position == nextWeekEventStartIndex) {
			holder.header.setText("NEXT WEEK");
		} else if (position == otherEventStartIndex) {
			holder.header.setText("OTHERS");	
		} else holder.header.setVisibility(View.GONE);
		
		holder.title.setText(Html.fromHtml("<b>" + (position + 1) + ".</b> " + event.getName()));
		holder.time.setText(TimeFrame.getEventDisplayTime(event.getStart_time()));
		displayEventDistance(event, holder);
		displayEventFriendsAttending(event, holder);
		imageLoading.displayImage(event.getPicture(), holder.eventIcon);
		
		if (canMessage) {
			holder.shareButton.setVisibility(View.VISIBLE);
			holder.separator.setVisibility(View.VISIBLE);
			holder.moreButton.setBackgroundResource(R.drawable.rounded_list_item_bottom_right);
		} else {
			holder.shareButton.setVisibility(View.GONE);
			holder.separator.setVisibility(View.GONE);
			holder.moreButton.setBackgroundResource(R.drawable.rounded_list_item_bottom_view);
		}
		
		holder.shareButton.setSelected(shareDataSource.checkEventShared(event.getId()));
		holder.shareButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				shareClick(event);
			}
		});
		
		holder.moreButton.setSelected(viewDataSource.checkEventViewed(event.getId()));
		holder.moreButton.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				viewDataSource.addEventViewed(event.getId());
				Intent intent = new Intent(context, EventFullActivity.class);
				intent.putExtra("eid", event.getId());
				context.startActivity(intent);
			}
		});

		return convertView;

	}

	/**
	 * Display event distance
	 * @param event
	 * @param view holder
	 */
	private void displayEventDistance(FbEvent event, EventViewHolder holder) {
		if (event.getLocation().equals("")) {
			holder.location.setVisibility(View.GONE);
			holder.distance.setVisibility(View.GONE);
		} else {
			holder.location.setVisibility(View.VISIBLE);
			holder.distance.setVisibility(View.VISIBLE);
			holder.location.setText(event.getLocation());
			if (event.getDistance() == -1 || event.getDistance() == -2) holder.distance.setVisibility(View.GONE);
			else {
				holder.distance.setVisibility(View.VISIBLE);
				if (event.getDistance() < 10) {
					DecimalFormat df = new DecimalFormat("#.#");
					holder.distance.setText(df.format(event.getDistance()) + " mi");
				} else if (event.getDistance() < 1000) holder.distance.setText(((int)event.getDistance()) + " mi");
				else holder.distance.setText("999+ mi");
			}
		}
	}

	/**
	 * display the friends interested in the events
	 * @param event
	 * @param view holder
	 */
	private void displayEventFriendsAttending(FbEvent event, EventViewHolder holder) {
		ArrayList<Attendee> attendees = new ArrayList<Attendee>();
		FbEvent mevent = friendEventDataSource.getEvent(event.getId());
		if (mevent != null) attendees = mevent.getFriendsAttending();
		if (attendees == null || attendees.size() == 0) 
			holder.mgs.setVisibility(View.GONE);
		else {
			holder.mgs.setVisibility(View.VISIBLE);
			if (attendees.size() == 1) holder.mgs.setText(Html.fromHtml("<i>" + attendees.get(0).getName() + "</i> is interested"));
			else if (attendees.size() == 2) 
				holder.mgs.setText(Html.fromHtml("<i>" + attendees.get(0).getName() + "</i> and <i>" + attendees.get(1).getName() + "</i> are interested"));
			else holder.mgs.setText(Html.fromHtml("<i>" + attendees.get(0).getName() + "</i>" + " and <i>" + (attendees.size() - 1) + "</i> others are interested"));
		}
	}


	/**
	 * Private view holder class for event
	 */
	private class EventViewHolder {
		TextView header;
		ImageView eventIcon;
		TextView title;
		TextView distance;
		TextView time;
		TextView mgs;
		TextView location;
		View shareButton;
		View separator;
		View moreButton;
	}
}
