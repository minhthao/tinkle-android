package com.hitchlab.tinkle.appevent;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hitchlab.tinkle.R;
import com.hitchlab.tinkle.ActivityTransition;
import com.hitchlab.tinkle.template.event.EventFriendAdapter;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class EventSlidingMenu {
	private Activity activity;
	private SlidingMenu menu;
	private EventFriendAdapter adapter;
	
	private ListView list;
	
	public EventSlidingMenu(Activity activity) {
		this.activity = activity;
		menu = new SlidingMenu(activity.getApplicationContext());
		menu.setMode(SlidingMenu.RIGHT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(activity, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.event_friends);
		
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
	 * Set the friends to be display
	 * @param joined friends
	 * @param unsure friends
	 * @param unreplied friends
	 */
	public void setFriends(ArrayList<String> joined, ArrayList<String> maybe, ArrayList<String> unreplied) {
		adapter.setDataSet(joined, maybe, unreplied);
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * Init the view components
	 */
	private void initViewComponents() {
		this.adapter = new EventFriendAdapter(activity);
		this.list = (ListView) menu.findViewById(R.id.event_friends_list);
		list.setAdapter(adapter);
		list.setDivider(new ColorDrawable(0xffc2c2c2));
		list.setDividerHeight(1);
		list.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				ActivityTransition.displayUserInfoPage(adapter.getFriendItem(position).getUid(), activity);
			}	
		});
	}
}