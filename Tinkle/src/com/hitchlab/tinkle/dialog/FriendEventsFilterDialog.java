package com.hitchlab.tinkle.dialog;

import com.hitchlab.tinkle.R;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

@SuppressLint("InflateParams") 
public abstract class FriendEventsFilterDialog{
	public static final int TYPE_INTEREST_ALL = 0;
	public static final int TYPE_INTEREST_FIVE = 1;
	public static final int TYPE_INTEREST_TEN = 2;
	
	public static final int TYPE_DISTANCE_ALL = 0;
	public static final int TYPE_DISTANCE_ONE = 1;
	public static final int TYPE_DISTANCE_FIVE = 2;
	public static final int TYPE_DISTANCE_FIFTEEN = 3;
	public static final int TYPE_DISTANCE_FIFTY = 4;
	
	public static final int TYPE_TIME_ALL = 0;
	public static final int TYPE_TIME_TODAY = 1;
	public static final int TYPE_TIME_THIS_WEEK = 2;
	public static final int TYPE_TIME_THIS_WEEKEND = 3;
	
	private View dialogView;
	private TextView interestAll;
	private TextView interestFive;
	private TextView interestTen;
	private TextView[] interestViews = new TextView[3];
	
	private TextView distanceAll;
	private TextView distanceOne;
	private TextView distanceFive;
	private TextView distanceFifteen;
	private TextView distanceFifty;
	private TextView[] distanceViews = new TextView[5];
	
	private TextView timeAll;
	private TextView timeToday;
	private TextView timeWeek;
	private TextView timeWeekend;
	private TextView[] timeViews = new TextView[4];
	
	private TextView cancelButton;
	private TextView okButton;
	
	private int interestType;
	private int distanceType;
	private int timeType;
	
	private Dialog dialog;
	
	public abstract void onFriendEventsFilterDialogOkPressed(int interestType, int distanceType, int timeType);
	
	//constructor
	public FriendEventsFilterDialog(Context context) {
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.dialogView = layoutInflater.inflate(R.layout.main_view_friend_events_filter_view, null);
		
		this.interestAll = (TextView) dialogView.findViewById(R.id.main_view_friend_events_filter_interest_all);
		this.interestFive = (TextView) dialogView.findViewById(R.id.main_view_friend_events_filter_interest_five);
		this.interestTen = (TextView) dialogView.findViewById(R.id.main_view_friend_events_filter_interest_ten);
		this.interestViews[0] = this.interestAll;
		this.interestViews[1] = this.interestFive;
		this.interestViews[2] = this.interestTen;
		
		this.distanceAll = (TextView) dialogView.findViewById(R.id.main_view_friend_events_filter_distance_all);
		this.distanceOne = (TextView) dialogView.findViewById(R.id.main_view_friend_events_filter_distance_one);
		this.distanceFive = (TextView) dialogView.findViewById(R.id.main_view_friend_events_filter_distance_five);
		this.distanceFifteen = (TextView) dialogView.findViewById(R.id.main_view_friend_events_filter_distance_fifteen);
		this.distanceFifty = (TextView) dialogView.findViewById(R.id.main_view_friend_events_filter_distance_fifty);
		this.distanceViews[0] = this.distanceAll;
		this.distanceViews[1] = this.distanceOne;
		this.distanceViews[2] = this.distanceFive;
		this.distanceViews[3] = this.distanceFifteen;
		this.distanceViews[4] = this.distanceFifty;
		
		this.timeAll = (TextView) dialogView.findViewById(R.id.main_view_friend_events_filter_time_all);
		this.timeToday = (TextView) dialogView.findViewById(R.id.main_view_friend_events_filter_time_today);
		this.timeWeek = (TextView) dialogView.findViewById(R.id.main_view_friend_events_filter_time_this_week);
		this.timeWeekend = (TextView) dialogView.findViewById(R.id.main_view_friend_events_filter_time_weekend);
		this.timeViews[0] = this.timeAll;
		this.timeViews[1] = this.timeToday;
		this.timeViews[2] = this.timeWeek;
		this.timeViews[3] = this.timeWeekend;
		
		this.cancelButton = (TextView) dialogView.findViewById(R.id.main_view_friend_events_filter_cancel);
		this.okButton = (TextView) dialogView.findViewById(R.id.main_view_friend_events_filter_ok);
		
		for (int i = 0; i < timeViews.length; i++) {
			final int type = i;
			timeViews[type].setOnClickListener(new TextView.OnClickListener() {
				@Override
				public void onClick(View v) {
					selectTimeType(type);
				}
			});
		}
		
		for (int i = 0; i < interestViews.length; i++) {
			final int type = i;
			interestViews[type].setOnClickListener(new TextView.OnClickListener() {
				@Override
				public void onClick(View v) {
					selectInterestType(type);
				}
			});
		}
		
		for (int i = 0; i < distanceViews.length; i++) {
			final int type = i;
			distanceViews[type].setOnClickListener(new TextView.OnClickListener() {
				@Override
				public void onClick(View v) {
					selectDistanceType(type);
				}
			});
		}
		
		cancelButton.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}	
		});
		
		okButton.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				onFriendEventsFilterDialogOkPressed(interestType, distanceType, timeType);
				dialog.dismiss();
			}
		});
		
		dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(dialogView);
 	}
	
	/**
	 * showFilterDialog
	 * @param currentInterestType
	 * @param currentDistanceType
	 * @param currentTimeType
	 */
	public void showDialog(int currentInterestType, int currentDistanceType, int currentTimeType) {		
		selectInterestType(currentInterestType);
		selectDistanceType(currentDistanceType);
		selectTimeType(currentTimeType);
		
		dialog.show();
	}
	
	/**
	 * Show the filter (time) option selected.
	 * @param time select option
	 */
	private void selectTimeType(int type) {
		this.timeType = type;
		for (int i = 0; i < timeViews.length; i++) {
			if (i == type) timeViews[i].setSelected(true);
			else timeViews[i].setSelected(false);
		}
	}
	
	/**
	 * Show the filter (distance) option selected.
	 * @param distance select option
	 */
	private void selectDistanceType(int type) {
		this.distanceType = type;
		for (int i = 0; i < distanceViews.length; i++) {
			if (i == type) distanceViews[i].setSelected(true);
			else distanceViews[i].setSelected(false);
		}
	}
	
	/**
	 * Show the filter (interest) option selected.
	 * @param interest select option
	 */
	private void selectInterestType(int type) {
		this.interestType = type;
		for (int i = 0; i < interestViews.length; i++) {
			if (i == type) interestViews[i].setSelected(true);
			else interestViews[i].setSelected(false);
		}
	}
}
