package com.hitchlab.tinkle.template.event;

import java.util.List;
import com.hitchlab.tinkle.R;
import com.facebook.widget.ProfilePictureView;
import com.hitchlab.tinkle.objects.Feed;
import com.hitchlab.tinkle.supports.ImageLoading;
import com.hitchlab.tinkle.supports.TimeFrame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


@SuppressLint("InflateParams") 
public class CommentListViewAdapter extends BaseAdapter{

	Context context;
	ImageLoading imageLoading;
	List<Feed> feeds;

	public CommentListViewAdapter(Context context, List<Feed> feeds, ImageLoading imageLoading) {
		this.context = context;
		this.imageLoading = imageLoading;
		this.feeds = feeds;
	}
	
	public void setFeeds(List<Feed> feeds) {
		if (feeds != null)
			this.feeds = feeds;
	}
	
	public void addFeeds(Feed feed) {
		feeds.add(feed);
	}

	@Override
	public int getCount() {
		return feeds.size();
	}

	@Override
	public Object getItem(int position) {
		return feeds.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Get the feed at the given position
	 * @param position
	 * @return the feed at that position
	 */
	public Feed getFeed(int position) {
		return ((Feed) getItem(position));
	}

	/**
	 * Private view holder class
	 */
	private class ViewHolder {
		ProfilePictureView profilePic;
		TextView username;
		TextView created_time;
		TextView message;
		ImageView picture;
	}

	/**
	 * Get the history item for the given position
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Feed feed = getFeed(position);
		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.comment_item, null);
			holder = new ViewHolder();
			holder.profilePic = (ProfilePictureView) convertView.findViewById(R.id.comment_user_pic);
			holder.username = (TextView) convertView.findViewById(R.id.comment_user_name);
			holder.created_time =  (TextView) convertView.findViewById(R.id.comment_time);
			holder.message = (TextView) convertView.findViewById(R.id.comment_text);
			holder.picture = (ImageView) convertView.findViewById(R.id.comment_picture);
			convertView.setTag(holder);
		} else holder = (ViewHolder) convertView.getTag();

		holder.username.setText(feed.getOwnerName());
		holder.profilePic.setProfileId(feed.getOwnerId());
		setTime(feed, holder);
		setMessage(feed, holder);
		setImage(feed, holder);
		
		return convertView;
	}

	/**
	 * Set feed image
	 * @param feed
	 * @param view holder
	 */
	private void setImage(Feed feed, ViewHolder holder) {
		if (!feed.getImageUrl().equals("")) {
			holder.picture.setVisibility(View.VISIBLE);
			imageLoading.displayImage(feed.getImageUrl(), holder.picture);
		} else holder.picture.setVisibility(View.GONE);
	}

	/**
	 * Set feed updated time
	 * @param feed
	 * @param view holder
	 */
	private void setTime(Feed feed, ViewHolder holder) {
		if (feed.getTimePost().equals("")) holder.created_time.setText("just now");
		else {
			String time = TimeFrame.getStandardDateTimeFormat(feed.getTimePost());
			holder.created_time.setText(TimeFrame.getEventDisplayTime(TimeFrame.getUnixTime(time)));
		}
	}

	/**
	 * Set the feed message
	 * @param feed
	 * @param view holder
	 */
	private void setMessage(Feed feed, ViewHolder holder) {
		if (feed.getMessage().equals("") && feed.getStory().equals("")) {
			holder.message.setVisibility(View.GONE);
		} else {
			holder.message.setVisibility(View.VISIBLE);
			if (!feed.getMessage().equals("")) {
				holder.message.setTextColor(0xff444466);
				holder.message.setText(feed.getMessage());
			} else {
				holder.message.setTextColor(0xffb2b2b2);
				holder.message.setText(feed.getStory());
			}
		}
	}

}
