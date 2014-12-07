package com.hitchlab.tinkle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.hitchlab.tinkle.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.hitchlab.tinkle.appevent.EventFullActivity;
import com.hitchlab.tinkle.datasource.MyEventDataSource;
import com.hitchlab.tinkle.map.Coordinate;
import com.hitchlab.tinkle.map.MapLocationManager;
import com.hitchlab.tinkle.objects.FbEvent;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;
import com.hitchlab.tinkle.service.RefreshMyPastEvents;
import com.hitchlab.tinkle.service.ServiceStarter;
import com.hitchlab.tinkle.supports.ImageLoading;
import com.hitchlab.tinkle.template.HistoryListAdapter;

import eu.erikw.PullToRefreshListView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MenuHistoryActivity extends Activity {

	private Context context;
	private ImageLoading imageLoading;
	private HistoryListAdapter adapter;
	private ListView list;
	private View loadingView;
	private TextView backButton;
	
	private MapLocationManager locationManager;
	private Location lastKnown;
	private String provider;
	
	private MyEventDataSource myEventDataSource;
	private ServiceStarter serviceStarter;
	
	/**
	 * receiver for my events service
	 */
	private BroadcastReceiver pastEventsReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			ArrayList<FbEvent> events = intent.getParcelableArrayListExtra("data");
			setEvents(events);
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_history_activity);
		
		this.context = this;
		this.imageLoading = new ImageLoading(context);
		
		this.loadingView = findViewById(R.id.main_view_history_loading_view);
		loadingView.setVisibility(View.VISIBLE);
		
		this.backButton = (TextView) findViewById(R.id.main_view_history_back_button);
		backButton.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		this.myEventDataSource = new MyEventDataSource(context);
		this.adapter = new HistoryListAdapter(context, imageLoading);
		initList();
		
		this.locationManager = new MapLocationManager(context);
		this.lastKnown = locationManager.getLastKnownLocation();
		this.provider = locationManager.getProvider();
		
		this.serviceStarter = new ServiceStarter(context) {
			@Override
			public void noInternet() {
				Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
			}

			@Override
			public void sessionClosed() {
				Toast.makeText(context, "Authentication Error. Please re-login.", Toast.LENGTH_LONG).show();
			}
		};
		
		if (SharedPreference.getPrefBooleanValue(context, Preference.DID_PAST_EVENTS_INIT))
			setEvents(myEventDataSource.getPastEvents());
	}
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	}
	
	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(pastEventsReceiver);
	}

	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(pastEventsReceiver, new IntentFilter(RefreshMyPastEvents.NOTIFICATION));
	
		if (!SharedPreference.getPrefBooleanValue(context, Preference.DID_PAST_EVENTS_INIT)) {
			serviceStarter.updatePastEvents();
		}
	}

	/**
	 * init the list 
	 */
	private void initList() {
		this.list = (ListView) findViewById(R.id.main_view_history_list);
		list.setAdapter(adapter);
		list.setDivider(new ColorDrawable(0x00c2c2c2));
		list.setDividerHeight(0);

		list.setOnItemClickListener(new PullToRefreshListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				if (adapter.isEnabled(position)) {
					Intent intent = new Intent(context, EventFullActivity.class);
					intent.putExtra("eid", adapter.getEventItem(position).getId());
					context.startActivity(intent);
				}
			}	
		});
	}
	
	/**
	 * Set the events to be display
	 * @param events
	 */
	public void setEvents(ArrayList<FbEvent> events) {
		for (FbEvent event : events) {
			if (event.getVenueLatitude() == 0 && event.getVenueLongitude() == 0) {
				event.setDistance(-1);
			} else {
				Location eventLocation = new Location(provider);
				eventLocation.setLatitude(event.getVenueLatitude());
				eventLocation.setLongitude(event.getVenueLongitude());
				double distance = lastKnown.distanceTo(eventLocation)/Coordinate.METERS_IN_MILE;
				event.setDistance(distance);
			}
		}
		
		if (events != null) displayEvents(events);
	}
	
	/**
	 * Show the events
	 * @param ArrayList<FbEvent>
	 */
	private void displayEvents(ArrayList<FbEvent> events) {
		Collections.sort(events, new Comparator<FbEvent>() {
			@Override
			public int compare(FbEvent lhs, FbEvent rhs) {
				if (lhs.getStart_time() > rhs.getStart_time()) return -1;
				else if (lhs.getStart_time() < rhs.getStart_time()) return 1;
				return 0;
			}
		});
		adapter.setEvents(events);
		adapter.notifyDataSetChanged();
		loadingView.setVisibility(View.GONE);
	}
}
