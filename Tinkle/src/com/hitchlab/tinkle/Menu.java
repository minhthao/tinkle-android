package com.hitchlab.tinkle;

import com.hitchlab.tinkle.R;
import com.facebook.Session;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;
import com.hitchlab.tinkle.service.LogOutService;
import com.hitchlab.tinkle.supports.ImageLoading;
import com.hitchlab.tinkle.supports.Internet;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Menu {
	private Activity activity;
	private SlidingMenu menu;
	private ImageLoading imageLoading;
	
	//private View searchView;
	private TextView searchButton;
	private View pastEventButton;
	private View notificationButton;
	private View settingButton;
	private View logoutButton;
	
	private ImageView profilePic;
	private TextView username;
	
	
	public Menu(Activity activity) {
		this.activity = activity;
		this.imageLoading = new ImageLoading(activity);
		menu = new SlidingMenu(activity.getApplicationContext());
		menu.setMode(SlidingMenu.RIGHT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(activity, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.main_view_menu);
		
		initViewComponents();
	}
	
	/**
	 * Check if the menu is showing
	 * @return boolean
	 */
	public boolean isShowing() {
		return menu.isMenuShowing();
	}
	
	/**
	 * Show the menu
	 */
	public void showMenu() {
		menu.showMenu();
	}
	
	/**
	 * Show the content
	 */
	public void showContent() {
		menu.showContent();
	}
	
	/**
	 * Init the view components
	 */
	private void initViewComponents() {
		this.searchButton = (TextView) menu.findViewById(R.id.main_view_menu_fragment_search);
		searchButton.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, MenuSearchActivity.class);
				activity.startActivity(intent);
			}
		});
		
		this.pastEventButton = menu.findViewById(R.id.main_view_menu_fragment_past_events);
		pastEventButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, MenuHistoryActivity.class);
				activity.startActivity(intent);
			}
		});
		
		this.notificationButton = menu.findViewById(R.id.main_view_menu_fragment_notifications);
		notificationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, MenuNotificationActivity.class);
				activity.startActivity(intent);
			}
		});
		
		this.settingButton = menu.findViewById(R.id.main_view_menu_fragment_settings);
		settingButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, MenuSettingsActivity.class);
				activity.startActivity(intent);
			}
		});
		
		this.logoutButton = menu.findViewById(R.id.main_view_menu_fragment_logout);
		logoutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent logOutService = new Intent(activity, LogOutService.class);
				activity.startService(logOutService);

				Session session = Internet.getFacebookSession(activity);
				if (session != null && session.isOpened())
					session.closeAndClearTokenInformation();
				activity.finish();
				Intent loginActivity = new Intent(activity, LoginActivity.class);
				activity.startActivity(loginActivity);
			}
		});
		
		this.profilePic = (ImageView) menu.findViewById(R.id.main_view_menu_fragment_user_profile_pic);
		String uid = SharedPreference.getPrefStringValue(activity, Preference.UID);
		imageLoading.displayImage("http://graph.facebook.com/" + uid + "/picture?type=square", profilePic);
		
		this.username = (TextView) menu.findViewById(R.id.main_view_menu_fragment_username);
		String name = SharedPreference.getPrefStringValue(activity, Preference.USERNAME);
		username.setText(name);
	}
	
}
