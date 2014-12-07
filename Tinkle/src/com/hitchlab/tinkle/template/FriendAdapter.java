package com.hitchlab.tinkle.template;
import java.util.ArrayList;
import java.util.Locale;

import com.hitchlab.tinkle.R;
import com.hitchlab.tinkle.objects.Friend;
import com.hitchlab.tinkle.supports.ImageLoading;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("InflateParams") 
public class FriendAdapter extends BaseAdapter {
	private LayoutInflater layoutInflater;
	private ArrayList<Friend> friends;
	private ArrayList<Friend> keeper;
	private ImageLoading imageLoading;

	public FriendAdapter(Context context) {
		this.imageLoading = new ImageLoading(context);
		this.friends = new ArrayList<Friend>();
		this.keeper = new ArrayList<Friend>();
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public FriendAdapter(Context context, ImageLoading imageLoading) {
		this.friends = new ArrayList<Friend>();
		this.keeper = new ArrayList<Friend>();
		this.imageLoading = imageLoading;
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	/**
	 * Set the data
	 * @param friends
	 */
	public void setDataSet(ArrayList<Friend> friends) {
		if (friends != null) {
			this.friends = friends;
			this.keeper.clear();
			this.keeper.addAll(friends);
		}
	}

	@Override
	public int getCount() {
		return friends.size();
	}

	@Override
	public Object getItem(int position) {
		return friends.get(position);
	}
	
	/**
	 * Get the graph user item
	 */
	public Friend getUserItem(int position) {
		return (Friend) getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	/**
	 * Private view holder class
	 */
	private class ViewHolder {
		ImageView profilePic;
		ImageView eventIcon;
		TextView username;
		TextView numEvents;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Friend friend = getUserItem(position);
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.main_view_friends_item, null);
			holder = new ViewHolder();
			holder.profilePic = (ImageView) convertView.findViewById(R.id.friend_item_icon);
			holder.username = (TextView) convertView.findViewById(R.id.friend_name);
			holder.numEvents = (TextView) convertView.findViewById(R.id.friend_number_of_events);
			holder.eventIcon = (ImageView) convertView.findViewById(R.id.friend_item_events_icon);
			convertView.setTag(holder);
		} else holder = (ViewHolder) convertView.getTag();
		
		holder.username.setText(friend.getName());
		imageLoading.displayImage("http://graph.facebook.com/" + friend.getUid() + "/picture?width=100&height=100", holder.profilePic);
		
		if (friend.getNumOngoingEvents() != 0) {
			holder.numEvents.setVisibility(View.VISIBLE);
			holder.eventIcon.setVisibility(View.VISIBLE);
			holder.numEvents.setText(String.valueOf(friend.getNumOngoingEvents()));
		} else {
			holder.numEvents.setVisibility(View.GONE);
			holder.eventIcon.setVisibility(View.GONE);
		}
		
		return convertView;
	}
	
	/**
	 * Implement the filtering method
	 * @param Text
	 */
	public void filter(String charText) {
		String text = charText.toLowerCase(Locale.getDefault());
		friends.clear();
		if (text.length() == 0) 
			friends.addAll(keeper);
		else for (Friend friend : keeper) {
			if (friend.getName().toLowerCase(Locale.getDefault()).contains(text))
				friends.add(friend);
		}
		notifyDataSetChanged();
	}
	
}