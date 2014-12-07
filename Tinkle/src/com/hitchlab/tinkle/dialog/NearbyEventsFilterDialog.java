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
public abstract class NearbyEventsFilterDialog{
	public static final int TYPE_INTEREST_NO = 0;
	public static final int TYPE_INTEREST_YES = 1;
	
	public static final int TYPE_TIME_ALL = 0;
	public static final int TYPE_TIME_TODAY = 1;
	public static final int TYPE_TIME_THIS_WEEK = 2;
	public static final int TYPE_TIME_THIS_WEEKEND = 3;
	
	private View dialogView;
	private TextView interestNo;
	private TextView interestYes;
	private TextView[] interestViews = new TextView[2];
	
	private TextView timeAll;
	private TextView timeToday;
	private TextView timeWeek;
	private TextView timeWeekend;
	private TextView[] timeViews = new TextView[4];
	
	private TextView cancelButton;
	private TextView okButton;
	
	private int interestType;
	private int timeType;
	
	private Dialog dialog;
	
	public abstract void onNearbyEventsFilterDialogOkPressed(int interestType, int timeType);
	
	//constructor
	public NearbyEventsFilterDialog(Context context) {
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.dialogView = layoutInflater.inflate(R.layout.main_view_nearby_events_filter_view, null);
		
		this.interestYes = (TextView) dialogView.findViewById(R.id.main_view_nearby_events_filter_interest_yes);
		this.interestNo = (TextView) dialogView.findViewById(R.id.main_view_nearby_events_filter_interest_no);
		this.interestViews[0] = this.interestNo;
		this.interestViews[1] = this.interestYes;
		
		this.timeAll = (TextView) dialogView.findViewById(R.id.main_view_nearby_events_filter_time_all);
		this.timeToday = (TextView) dialogView.findViewById(R.id.main_view_nearby_events_filter_time_today);
		this.timeWeek = (TextView) dialogView.findViewById(R.id.main_view_nearby_events_filter_time_this_week);
		this.timeWeekend = (TextView) dialogView.findViewById(R.id.main_view_nearby_events_filter_time_weekend);
		this.timeViews[0] = this.timeAll;
		this.timeViews[1] = this.timeToday;
		this.timeViews[2] = this.timeWeek;
		this.timeViews[3] = this.timeWeekend;
		
		this.cancelButton = (TextView) dialogView.findViewById(R.id.main_view_nearby_events_filter_cancel);
		this.okButton = (TextView) dialogView.findViewById(R.id.main_view_nearby_events_filter_ok);
		
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
		
		cancelButton.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}	
		});
		
		okButton.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				onNearbyEventsFilterDialogOkPressed(interestType, timeType);
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
	public void showDialog(int currentInterestType, int currentTimeType) {		
		selectInterestType(currentInterestType);
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
