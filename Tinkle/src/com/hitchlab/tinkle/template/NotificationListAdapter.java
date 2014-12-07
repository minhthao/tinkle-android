package com.hitchlab.tinkle.template;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.hitchlab.tinkle.R;
import com.hitchlab.tinkle.MainActivity;
import com.hitchlab.tinkle.objects.MyNotification;
import com.hitchlab.tinkle.service.NotificationViewChangeService;
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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("InflateParams") 
public class NotificationListAdapter extends BaseAdapter{
	
	private Context context;
	private LayoutInflater layoutInflater;
	private ImageLoading imageLoading;
	private ArrayList<MyNotification> notifications;
	
	public NotificationListAdapter(Context context) {
		this.context = context;
		this.imageLoading = new ImageLoading(context);
		this.notifications = new ArrayList<MyNotification>();
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);	
	}
	
	/**
	 * Set the notifications
	 * @param ArrayList<MyNotification>
	 */
	public void setNotifications(ArrayList<MyNotification> notifications) {
		if (notifications != null) this.notifications = notifications;
	}
	
	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return (getItemViewType(position) != MyNotification.TYPE_SIMPLE_MESSAGE);
	}

	@Override
	public int getItemViewType(int position) {
		return notifications.get(position).getType();
	}

	@Override
	public int getViewTypeCount() {
		return MyNotification.NUM_TYPE;
	}

	@Override
	public int getCount() {
		return notifications.size();
	}
	
	@Override
	public Object getItem(int position) {
		return notifications.get(position);
	}
	
	public MyNotification getNotificationItem(int position) {
		return notifications.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyNotification myNotif = getNotificationItem(position);
		if (getItemViewType(position) == MyNotification.TYPE_SIMPLE_MESSAGE) 
			return getSimpleMessageView(convertView, myNotif);
		else if (getItemViewType(position) == MyNotification.TYPE_INVITED_EVENT)
			return getInvitedEventView(convertView, myNotif);
		else if (getItemViewType(position) == MyNotification.TYPE_FRIEND_JOIN_EVENT)
			return getFriendJoinEventView(convertView, myNotif);
		else if (getItemViewType(position) == MyNotification.TYPE_FRIEND_TODAY_EVENTS) 
			return getTodayEventsView(convertView, myNotif);
		return getSimpleMessageView(convertView, myNotif);
	}
	
	/**
	 * Display the view of those that will go out today
	 * @param ConvertView
	 * @param Notification
	 * @return View
	 */
	private View getTodayEventsView(View convertView, MyNotification notification) {
		NotificationTodayEvents holder = null;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.notification_type_today_events, null);
			holder = new NotificationTodayEvents();
			holder.container = convertView.findViewById(R.id.notification_type_today_events_container);
			holder.message = (TextView) convertView.findViewById(R.id.notification_type_today_events_main_message);
			holder.time = (TextView) convertView.findViewById(R.id.notification_type_today_events_time_stamp);
			holder.attendees = (LinearLayout) convertView.findViewById(R.id.notification_type_today_events_scroll_view);
			convertView.setTag(holder);
		} else holder = (NotificationTodayEvents) convertView.getTag();
		
		holder.container.setSelected(notification.isViewed() == true);
		holder.attendees.removeAllViews();
		holder.message.setText(Html.fromHtml(notification.getMessage()));
		holder.time.setText(getDisplayTime(notification.getTime()));
		
		String[] uids = notification.getMessageExtra1().substring(1, notification.getMessageExtra1().length() - 1).split(", ");
		final float scale = context.getResources().getDisplayMetrics().density;
	
		for (String uid : uids) {
			ImageView view = new ImageView(context);
			int iconSizeInPixel = (int) (36 * scale + 0.5f);
			int paddingInPixel = (int) (2 * scale + 0.5f);
			view.setLayoutParams(new HorizontalScrollView.LayoutParams(iconSizeInPixel, iconSizeInPixel));
			view.setPadding(paddingInPixel, paddingInPixel, paddingInPixel, paddingInPixel);
			imageLoading.displayImage("http://graph.facebook.com/" + uid + "/picture?type=square", view);
			holder.attendees.addView(view);
		}
		
		final MyNotification mNotif = notification;
		holder.container.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				if (!mNotif.isViewed()) {
					Intent intent = new Intent(context, NotificationViewChangeService.class);
					intent.putExtra("mNotification", mNotif);
					context.startService(intent);
				}
				Intent intent = new Intent(context, MainActivity.class);
				context.startActivity(intent);
			}
		});
		
		return convertView;
	}
	
	/**
	 * private view holder to display which of your friends are going out today
	 */
	private class NotificationTodayEvents {
		View container;
		LinearLayout attendees;
		TextView message;
		TextView time;
	}
	
	/**
	 * Display the view when one of your friend/person you follow replied to an event
	 * @param ConvertView
	 * @param Notification
	 * @return View
	 */
	private View getFriendJoinEventView(View convertView, MyNotification notification) {
		NotificationFriendOrFollowJoinEvent holder = null;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.notification_type_friend_joint_event, null);
			holder = new NotificationFriendOrFollowJoinEvent();
			holder.container = convertView.findViewById(R.id.notification_type_friend_join_event_container);
			holder.icon = (ImageView) convertView.findViewById(R.id.notification_type_friend_join_event_main_icon);
			holder.message = (TextView) convertView.findViewById(R.id.notification_type_friend_join_event_main_message);
			holder.time = (TextView) convertView.findViewById(R.id.notification_type_friend_join_event_time_stamp);
			holder.eventIcon = (ImageView) convertView.findViewById(R.id.notification_type_friend_join_event_sub_icon);
			holder.eventTitle = (TextView) convertView.findViewById(R.id.notification_type_friend_join_event_event_name);
			holder.eventTime = (TextView) convertView.findViewById(R.id.notification_type_friend_join_event_event_time);
			convertView.setTag(holder);
		} else holder = (NotificationFriendOrFollowJoinEvent) convertView.getTag();
		
		holder.container.setSelected(notification.isViewed() == true);
		imageLoading.displayImage("http://graph.facebook.com/" + notification.getExtraInfo() + "/picture?width=100&height=100", holder.icon);
		holder.message.setText(Html.fromHtml(notification.getMessage()));
		holder.time.setText(getDisplayTime(notification.getTime()));
		holder.eventTitle.setText(notification.getMessageExtra1());
		String[] eventInfos = notification.getMessageExtra2().split(",");
		imageLoading.displayImage(eventInfos[2], holder.eventIcon);
		holder.eventTime.setText(TimeFrame.getEventDisplayTime(Long.valueOf(eventInfos[1])));
		return convertView;
	}
	
	
	/**
	 * Private view holder to display notification when either your friend or person you follow join
	 * an event
	 */
	private class NotificationFriendOrFollowJoinEvent {
		View container;
		ImageView icon;
		TextView message;
		TextView time;
		ImageView eventIcon;
		TextView eventTitle;
		TextView eventTime;
	}
	
	/**
	 * Display the invited event notification
	 * @param ConvertView
	 * @param Notification
	 * @return View
	 */
	private View getInvitedEventView(View convertView, MyNotification notification) {
		NotificationInvitedEventViewHolder holder = null;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.notification_type_invited_event, null);
			holder = new NotificationInvitedEventViewHolder();
			holder.container = convertView.findViewById(R.id.notification_type_invited_event_container);
			holder.message = (TextView) convertView.findViewById(R.id.notification_type_invited_event_main_message);
			holder.time = (TextView) convertView.findViewById(R.id.notification_type_invited_event_time_stamp);
			holder.eventIcon = (ImageView) convertView.findViewById(R.id.notification_type_invited_event_sub_icon);
			holder.eventTitle = (TextView) convertView.findViewById(R.id.notification_type_invited_event_event_name);
			holder.eventTime = (TextView) convertView.findViewById(R.id.notification_type_invited_event_event_time);
			convertView.setTag(holder);
		} else holder = (NotificationInvitedEventViewHolder) convertView.getTag();
				
		holder.container.setSelected(notification.isViewed() == true);
		holder.message.setText(Html.fromHtml(notification.getMessage()));
		holder.time.setText(getDisplayTime(notification.getTime()));
		holder.eventTitle.setText(notification.getMessageExtra1());
		String[] eventInfos = notification.getMessageExtra2().split(",");
		imageLoading.displayImage(eventInfos[2], holder.eventIcon);
		holder.eventTime.setText(TimeFrame.getEventDisplayTime(Long.parseLong(eventInfos[1])));
		return convertView;
	}
	
	/**
	 * Private view holder to display when someone invited you to an event
	 */
	private class NotificationInvitedEventViewHolder {
		View container;
		TextView message;
		TextView time;
		ImageView eventIcon;
		TextView eventTitle;
		TextView eventTime;
	}
	
	/**
	 * Display the simple message
	 * @param convertView
	 * @param Notification
	 * @return View
	 */
	private View getSimpleMessageView(View convertView, MyNotification notification) {
		NotificationSimpleMessageViewHolder holder = null;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.notification_type_simple_message, null);
			holder = new NotificationSimpleMessageViewHolder();
			holder.message = (TextView) convertView.findViewById(R.id.notification_type_simple_message_main_message);
			holder.time = (TextView) convertView.findViewById(R.id.notification_type_simple_message_time_stamp);
			convertView.setTag(holder);
		} else holder = (NotificationSimpleMessageViewHolder) convertView.getTag();
		
		holder.message.setText(Html.fromHtml(notification.getMessage()));
		holder.time.setText(getDisplayTime(notification.getTime()));
		return convertView;
	}
	
	/**
	 * Private view holder for simple message type of notification
	 */
	private class NotificationSimpleMessageViewHolder {
		TextView message;
		TextView time;
	}
	
	/**
	 * private view holder for the events today type of notification
	 */
	
	/**
	 * get the time in long and then display it in the format "EEE, MMM d, yyyy' at 'h:mm a"
	 * @param time in millis
	 * @return String rep of the time
	 */
	private String getDisplayTime(long timeInMillis) {
		Date date = new Date(timeInMillis);
		return new SimpleDateFormat("EEE, MMM d, yyyy' at 'h:mm a").format(date);
	}
}
