package com.hitchlab.tinkle.template;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.hitchlab.tinkle.R;
import com.hitchlab.tinkle.objects.Attendee;
import com.hitchlab.tinkle.objects.FbEvent;
import com.hitchlab.tinkle.supports.ImageLoading;
import com.hitchlab.tinkle.supports.ScreenStat;
import com.hitchlab.tinkle.supports.TimeFrame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchListAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	private ArrayList<FbEvent> events;
	private ImageLoading imageLoading;

	public SearchListAdapter(Context context, ImageLoading imageLoading) {
		this.context = context;
		this.imageLoading = imageLoading;
		this.events = new ArrayList<FbEvent>();
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);	
	}

	/**
	 * Set the events to be display on the list
	 * @param events
	 */
	public void setEvents(ArrayList<FbEvent> events) {
		this.events.clear();
		this.events.addAll(events);
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
	 * Get the FbEventListItem at given position
	 * @param position
	 * @return FbEventListItem
	 */
	public FbEvent getEventItem(int position) {
		return (FbEvent) getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	@SuppressLint("InflateParams") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FbEvent event = getEventItem(position);
		EventViewHolder holder = null;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.menu_history_item, null);
			convertView.setMinimumWidth(ScreenStat.getViewWidth(context, 5, 5));
			holder = new EventViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.history_item_title);
			holder.eventIcon = (ImageView) convertView.findViewById(R.id.history_item_icon);
			holder.time = (TextView) convertView.findViewById(R.id.history_item_time);
			holder.distance = (TextView) convertView.findViewById(R.id.history_item_location_distance);
			holder.msg = (TextView) convertView.findViewById(R.id.history_item_rsvp);
			holder.location = (TextView) convertView.findViewById(R.id.history_item_location);
			convertView.setTag(holder);
		} else holder = (EventViewHolder) convertView.getTag();

		holder.title.setText(event.getName());
		holder.time.setText(TimeFrame.getEventDisplayTime(event.getStart_time()));
		displayEventFriendsAttending(event, holder);
		displayEventDistance(event, holder);

		imageLoading.displayImage(event.getPicture(), holder.eventIcon);

		return convertView;

	}
	
	/**
	 * display the friends interested in the events
	 * @param event
	 * @param view holder
	 */
	private void displayEventFriendsAttending(FbEvent event, EventViewHolder holder) {
		ArrayList<Attendee> attendees = event.getFriendsAttending();
		if (attendees == null || attendees.size() == 0) holder.msg.setVisibility(View.GONE);
		else  {
			holder.msg.setVisibility(View.VISIBLE);
			if (attendees.size() == 1) 
				holder.msg.setText(Html.fromHtml("<i>" + attendees.get(0).getName() + "</i> is interested"));
			else if (attendees.size() == 2) 
				holder.msg.setText(Html.fromHtml("<i>" + attendees.get(0).getName() + "</i> and <i>" + attendees.get(1).getName() + "</i> are interested"));
			else holder.msg.setText(Html.fromHtml("<i>" + attendees.get(0).getName() + "</i>" + " and <i>" + (attendees.size() - 1) + "</i> others are interested"));
		}
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
	 * Private view holder class for event
	 */
	private class EventViewHolder {
		ImageView eventIcon;
		TextView title;
		TextView distance;
		TextView time;
		TextView msg;
		TextView location;
	}
}
