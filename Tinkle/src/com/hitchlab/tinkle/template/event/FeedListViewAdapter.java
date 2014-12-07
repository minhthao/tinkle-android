package com.hitchlab.tinkle.template.event;

import java.util.ArrayList;

import com.hitchlab.tinkle.R;
import com.facebook.Session;
import com.hitchlab.tinkle.eventactions.Like;
import com.hitchlab.tinkle.fbquery.event.CommentQuery;
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
public class FeedListViewAdapter extends BaseAdapter{

	private Context context;
	private ImageLoading imageLoading;
	private CommentQuery commentQuery;
	private Like like;
	private ArrayList<Feed> feeds;

	public FeedListViewAdapter(Context context) {
		this.context = context;
		this.feeds = new ArrayList<Feed>();
		this.imageLoading = new ImageLoading(context);
		this.commentQuery = new CommentQuery(context, imageLoading);
	}
	
	public void setFeeds(ArrayList<Feed> feeds) {
		this.feeds = feeds;
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
	 * Set the feed at given position
	 * @param position
	 * @param feed
	 */
	public void setFeed(int position, Feed feed) {
		feeds.set(position, feed);
	}

	/**
	 * Private view holder class
	 */
	private class ViewHolder {
		ImageView profilePic;
		TextView username;
		TextView created_time;
		TextView message;
		ImageView picture;
		TextView popularity;
		View likeButton;
		View unlikeButton;
		View commentButton;
	}

	/**
	 * Get the history item for the given position
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Feed feed = getFeed(position);
		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.feed_list_item, null);
			holder = new ViewHolder();
			holder.profilePic = (ImageView) convertView.findViewById(R.id.feed_item_user_profile_img);
			holder.username = (TextView) convertView.findViewById(R.id.feed_item_username);
			holder.created_time =  (TextView) convertView.findViewById(R.id.feed_item_created_time);
			holder.message = (TextView) convertView.findViewById(R.id.feed_item_message);
			holder.picture = (ImageView) convertView.findViewById(R.id.feed_item_picture);
			holder.popularity = (TextView) convertView.findViewById(R.id.feed_item_popularity);
			holder.likeButton = convertView.findViewById(R.id.feed_item_like_button_view);
			holder.unlikeButton = convertView.findViewById(R.id.feed_item_unlike_button_view);
			holder.commentButton = convertView.findViewById(R.id.feed_item_comment_button_view);
			convertView.setTag(holder);
		} else holder = (ViewHolder) convertView.getTag();

		holder.username.setText(feed.getOwnerName());

		setTime(feed, holder);
		setMessage(feed, holder);
		setPopularity(feed, holder);
		setImage(feed, holder);
		prepareLikeUnlikeButtonAppearance(feed, holder);

		holder.popularity.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO
			}
		});

		final Feed mFeed = feed;
		final int mPosition = position;
		holder.likeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (like == null) initLikeOption();
				like.likePost(mPosition, mFeed);
			}
		});

		holder.unlikeButton.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				if (like == null) initLikeOption();
				like.unlikePost(mPosition, mFeed);
			}
		});

		holder.commentButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				commentQuery.queryAllComments(feeds.get(mPosition).getFeedId());
			}
		});

		return convertView;
	}

	/**
	 * Init the like function
	 */
	private void initLikeOption() {
		Session session = Session.getActiveSession();
		if (session == null) session = Session.openActiveSessionFromCache(context);
		if (session != null && session.isOpened()) {
			this.like = new Like(session, context) {

				@Override
				protected void onLikeSucceed(int position, Feed feed) {
					Feed newFeed = feed;
					int numLike = feed.getNumLike();
					newFeed.setNumLike(numLike + 1);
					newFeed.setLiked(true);
					setFeed(position, newFeed);
					notifyDataSetChanged();
				}

				@Override
				protected void onUnlikeSucceed(int position, Feed feed) {
					Feed newFeed = feed;
					int numLike = feed.getNumLike();
					newFeed.setNumLike(numLike - 1);
					newFeed.setLiked(false);
					setFeed(position, newFeed);
					notifyDataSetChanged();
				}

			};
		}
	}

	/**
	 * prepare the like/unlike button
	 * @param feed
	 * @param view holder
	 */
	private void prepareLikeUnlikeButtonAppearance(Feed feed, ViewHolder holder) {
		if (feed.isLiked()) {
			holder.likeButton.setVisibility(View.GONE);
			holder.unlikeButton.setVisibility(View.VISIBLE); 
		} else {
			holder.unlikeButton.setVisibility(View.GONE); 
			holder.likeButton.setVisibility(View.VISIBLE); 
		}
	}

	/**
	 * Set feed image
	 * @param feed
	 * @param view holder
	 */
	private void setImage(Feed feed, ViewHolder holder) {
		imageLoading.displayImage(feed.getOwnerProfile(), holder.profilePic);
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
		String time = TimeFrame.getStandardDateTimeFormat(feed.getTimePost());
		holder.created_time.setText(TimeFrame.getEventDisplayTime(TimeFrame.getUnixTime(time)));
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

	/**
	 * Set the popularity of the post
	 * @param feed
	 * @param view holder
	 */
	private void setPopularity(Feed feed, ViewHolder holder) {
		if (feed.getNumLike() == 0 && feed.getNumComments() == 0)
			holder.popularity.setVisibility(View.GONE);
		else {
			holder.popularity.setVisibility(View.VISIBLE);
			if (feed.getNumLike() != 0 && feed.getNumComments() == 0)
				holder.popularity.setText(feed.getNumLike() + " likes");
			else if (feed.getNumLike() == 0 && feed.getNumComments() != 0)
				holder.popularity.setText(feed.getNumComments() + " comments");
			else holder.popularity.setText(feed.getNumLike() + " likes and " + feed.getNumComments() + " comments");
		}
	}

}
