package com.hitchlab.tinkle.template;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.hitchlab.tinkle.R;
import com.hitchlab.tinkle.objects.FbEvent;
import com.hitchlab.tinkle.supports.ImageLoading;
import com.hitchlab.tinkle.supports.ScreenStat;
import com.hitchlab.tinkle.supports.TimeFrame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UserEventAdapter extends BaseAdapter{
	public static final int TYPE_FUTURE_HEADER = 0;
	public static final int TYPE_PAST_HEADER = 1;
	public static final int TYPE_EVENT = 2;

	private Context context;
	private LayoutInflater layoutInflater;
	private ImageLoading imageLoading;
	private ArrayList<FbEvent> futureEvents;
	private ArrayList<FbEvent> pastEvents;


	public UserEventAdapter(Context context, ImageLoading imageLoading) {
		this.context = context;
		this.imageLoading = imageLoading;
		this.pastEvents = new ArrayList<FbEvent>();
		this.futureEvents = new ArrayList<FbEvent>();
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);	
	}

	public void setEvents(ArrayList<FbEvent> events) {
		final long currentTime = System.currentTimeMillis() / 1000L;
		this.pastEvents.clear();
		this.futureEvents.clear();
		for (FbEvent event : events) {
			if (event.getStart_time() >= currentTime) futureEvents.add(event);
			else pastEvents.add(event);
		}

		Collections.sort(futureEvents, new Comparator<FbEvent>() {
			@Override
			public int compare(FbEvent lhs, FbEvent rhs) {
				if (lhs.getStart_time() > rhs.getStart_time()) return 1;
				return -1;
			}
		});

		Collections.sort(pastEvents, new Comparator<FbEvent>() {
			@Override
			public int compare(FbEvent lhs, FbEvent rhs) {
				if (lhs.getStart_time() > rhs.getStart_time()) return -1;
				return 1;
			}
		});
	}

	@Override 
	public int getItemViewType(int position) {
		if (futureEvents.size() != 0 && pastEvents.size() != 0) {
			if (position == 0) return TYPE_FUTURE_HEADER;
			if (position == futureEvents.size() + 1) return TYPE_PAST_HEADER;
			else return TYPE_EVENT;
		} else if (futureEvents.size() != 0) {
			if (position == 0) return TYPE_FUTURE_HEADER;
			else return TYPE_EVENT;
		} else {
			if (position == 0) return TYPE_PAST_HEADER;
			else return TYPE_EVENT;
		} 
	}

	@Override
	public int getViewTypeCount() {
		return 3; //2 for header and 1 more the actual events
	}

	@Override
	public boolean isEnabled(int position) {
		if (getItemViewType(position) == TYPE_EVENT) return true;
		return false;
	}

	@Override
	public int getCount() {
		if (futureEvents.size() != 0 && pastEvents.size() != 0)
			return futureEvents.size() + pastEvents.size() + 2;
		else if (futureEvents.size() != 0) return futureEvents.size() + 1;
		else if (pastEvents.size() != 0) return pastEvents.size() + 1;
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (futureEvents.size() != 0 && pastEvents.size() != 0) {
			if (position > 0 && position <= futureEvents.size()) return futureEvents.get(position - 1);
			else if (position > (futureEvents.size() + 1)) return pastEvents.get(position - futureEvents.size() - 2);
		} else if (futureEvents.size() != 0) {
			if (position > 0) return futureEvents.get(position - 1);
		} else if (pastEvents.size() != 0){
			if (position > 0) return pastEvents.get(position - 1);
		}
		return null;
	}

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
		if (getItemViewType(position) == TYPE_FUTURE_HEADER) {
			return getHeaderView(convertView, "Future Events", position);
		} else if (getItemViewType(position) == TYPE_PAST_HEADER) {
			return getHeaderView(convertView, "Past Events", position);
		} else {
			FbEvent event = getEventItem(position);
			EventViewHolder holder = null;
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.menu_history_item, null);
				convertView.setMinimumWidth(ScreenStat.getViewWidth(context, 5, 5));
				holder = new EventViewHolder();
				holder.title = (TextView) convertView.findViewById(R.id.history_item_title);
				holder.eventIcon = (ImageView) convertView.findViewById(R.id.history_item_icon);
				holder.distance = (TextView) convertView.findViewById(R.id.history_item_location_distance);
				holder.time = (TextView) convertView.findViewById(R.id.history_item_time);
				holder.location = (TextView) convertView.findViewById(R.id.history_item_location);
				holder.mgs = (TextView) convertView.findViewById(R.id.history_item_rsvp);
				convertView.setTag(holder);
			} else holder = (EventViewHolder) convertView.getTag();

			holder.title.setText(event.getName());
			holder.time.setText(TimeFrame.getEventDisplayTime(event.getStart_time()));

			displayEventDistance(event, holder);
			displayEventFriendsAttending(event, holder);

			imageLoading.displayImage(event.getPicture(), holder.eventIcon);
			return convertView;
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
		holder.mgs.setText(Html.fromHtml("Host: <i>" + event.getHost() + "</i>"));
	}

	/**
	 * Get the header view 
	 * @param convertView
	 * @param header
	 * @return view 
	 */
	@SuppressLint("InflateParams") 
	public View getHeaderView(View convertView, String header, int position) {
		HeaderViewHolder holder = null;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.list_header, null);
			convertView.setMinimumWidth(ScreenStat.getViewWidth(context, 5, 5));
			convertView.setBackgroundColor(Color.TRANSPARENT);
			holder = new HeaderViewHolder();
			holder.header = (TextView) convertView.findViewById(R.id.list_header_title);
			convertView.setTag(holder);
		} else holder = (HeaderViewHolder) convertView.getTag();
		if (position == 0) holder.header.setPadding(20, 20, 20, 20);
		else holder.header.setPadding(20, 50, 20, 20);
		holder.header.setText(header);
		return convertView;
	}

	/**
	 * Private view holder class for event
	 */
	private class EventViewHolder {
		ImageView eventIcon;
		TextView title;
		TextView location;
		TextView distance;
		TextView time;
		TextView mgs;
	}

	/**
	 * Private view holder class for header
	 */
	private class HeaderViewHolder {
		TextView header;
	}
}
