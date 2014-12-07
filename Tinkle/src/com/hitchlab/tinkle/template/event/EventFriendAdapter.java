package com.hitchlab.tinkle.template.event;
import java.util.ArrayList;

import com.hitchlab.tinkle.R;
import com.hitchlab.tinkle.datasource.FriendDataSource;
import com.hitchlab.tinkle.objects.Friend;
import com.hitchlab.tinkle.supports.ImageLoading;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("InflateParams") 
public class EventFriendAdapter extends BaseAdapter {
	private static final int TYPE_HEADER = 0;
	private static final int TYPE_FRIEND = 1;
	
	private LayoutInflater layoutInflater;
	private ArrayList<Friend> friends;
	private ImageLoading imageLoading;
	
	private FriendDataSource friendDataSource;
	
	private int totalCount;
	private int joinedCount;
	private int maybeCount;
	private int unrepliedCount;
	
	private int joinedFriendHeaderIndex;
	private int maybeFriendHeaderIndex;
	private int unrepliedFriendHeaderIndex;

	public EventFriendAdapter(Context context) {
		this.imageLoading = new ImageLoading(context);
		this.friends = new ArrayList<Friend>();
		this.friendDataSource = new FriendDataSource(context);
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setDataSet(ArrayList<String> joined, ArrayList<String> maybe, ArrayList<String> unreplied) {
		friends.clear();
		totalCount = 0;
		joinedCount = 0;
		
		if (joined != null && joined.size() > 0) {
			joinedFriendHeaderIndex = totalCount;
			friends.add(null);
			totalCount += joined.size() + 1; // 1 for index
			for (int i = 0; i < joined.size(); i++) {
				Friend friend = friendDataSource.getFriendWithUid(joined.get(i));
				friends.add(friend);
			}
			joinedCount = joined.size();
		} else joinedFriendHeaderIndex = -1;
		
		if (maybe != null && maybe.size() > 0) {
			maybeFriendHeaderIndex = totalCount;
			friends.add(null);
			totalCount += maybe.size() + 1; // 1 for index
			for (int i = 0; i < maybe.size(); i++) {
				Friend friend = friendDataSource.getFriendWithUid(maybe.get(i));
				friends.add(friend);
			}
			maybeCount = maybe.size();
		} else maybeFriendHeaderIndex = -1;
		
		if (unreplied != null && unreplied.size() > 0) {
			unrepliedFriendHeaderIndex = totalCount;
			friends.add(null);
			totalCount += unreplied.size() + 1; // 1 for index
			for (int i = 0; i < unreplied.size(); i++) {
				Friend friend = friendDataSource.getFriendWithUid(unreplied.get(i));
				friends.add(friend);
			}
			unrepliedCount = unreplied.size();
		} else unrepliedFriendHeaderIndex = -1;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == joinedFriendHeaderIndex ||
				position == maybeFriendHeaderIndex ||
				position == unrepliedFriendHeaderIndex) return TYPE_HEADER;
		return TYPE_FRIEND;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public boolean isEnabled(int position) {
		return (getItemViewType(position) == TYPE_FRIEND);
	}

	@Override
	public int getCount() {
		return totalCount;
	}

	@Override
	public Object getItem(int position) {
		return friends.get(position);
	}
	
	public Friend getFriendItem(int position) {
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
		TextView username;
	}
	
	/**
	 * Private view header holder class
	 */
	private class HeaderHolder {
		TextView header;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (getItemViewType(position) == TYPE_FRIEND) {
			ViewHolder holder = null;
			Friend friend = (Friend) getItem(position);
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.event_friends_item, null);
				holder = new ViewHolder();
				holder.profilePic = (ImageView) convertView.findViewById(R.id.event_friend_item_icon);
				holder.username = (TextView) convertView.findViewById(R.id.event_friend_name);
				convertView.setTag(holder);
			} else holder = (ViewHolder) convertView.getTag();
			
			holder.username.setText(friend.getName());
			imageLoading.displayImage("http://graph.facebook.com/" + friend.getUid() + "/picture?width=100&height=100", holder.profilePic);
			
			return convertView;
		} else {
			HeaderHolder holder = null;
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.event_friends_header, null);
				holder = new HeaderHolder();
				holder.header = (TextView) convertView.findViewById(R.id.event_friends_header_name);
				convertView.setTag(holder);
			} else holder = (HeaderHolder) convertView.getTag();
			
			if (position == joinedFriendHeaderIndex) holder.header.setText(Html.fromHtml("<b>JOINED</b> (" + joinedCount + ")"));
			if (position == maybeFriendHeaderIndex) holder.header.setText(Html.fromHtml("<b>MAYBE</b> (" + maybeCount + ")"));
			if (position == unrepliedFriendHeaderIndex) holder.header.setText(Html.fromHtml("<b>UNREPLIED</b> (" + unrepliedCount + ")"));
			return convertView;
		}
	}
	
}