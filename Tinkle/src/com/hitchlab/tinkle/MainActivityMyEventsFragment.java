package com.hitchlab.tinkle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.hitchlab.tinkle.R;
import com.hitchlab.tinkle.appevent.EventFullActivity;
import com.hitchlab.tinkle.datasource.MyEventDataSource;
import com.hitchlab.tinkle.eventactions.RsvpEventHandling;
import com.hitchlab.tinkle.map.Coordinate;
import com.hitchlab.tinkle.map.MapLocationManager;
import com.hitchlab.tinkle.objects.FbEvent;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;
import com.hitchlab.tinkle.service.RefreshMyEvents;
import com.hitchlab.tinkle.service.ServiceStarter;
import com.hitchlab.tinkle.template.MyEventListAdapter;

import eu.erikw.PullToRefreshListView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

public class MainActivityMyEventsFragment extends Fragment {
	private Context context;
	private MyEventListAdapter adapter;
	private PullToRefreshListView list;

	private MapLocationManager locationManager;
	private Location lastKnown;
	private String provider;

	private MyEventDataSource myEventDataSource;
	private ServiceStarter serviceStarter;

	/**
	 * receiver for my events service
	 */
	private BroadcastReceiver myEventsReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			ArrayList<FbEvent> events = intent.getParcelableArrayListExtra("data");
			setEvents(events);
			list.onRefreshComplete();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = getActivity();
		this.myEventDataSource = new MyEventDataSource(context);
		this.adapter = new MyEventListAdapter(context) {
			@Override
			public void shareClick(FbEvent event) {
				shareEvent(event);
			}
		};

		this.locationManager = new MapLocationManager(context);
		this.lastKnown = locationManager.getLastKnownLocation();
		this.provider = locationManager.getProvider();

		this.serviceStarter = new ServiceStarter(context) {
			@Override
			public void noInternet() {
				Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
				if (list.isRefreshing()) list.onRefreshComplete();
			}

			@Override
			public void sessionClosed() {
				Toast.makeText(context, "Authentication Error. Please re-login.", Toast.LENGTH_LONG).show();
				if (list.isRefreshing()) list.onRefreshComplete();
			}
		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_view_my_events, container, false);
		this.list = (PullToRefreshListView) view.findViewById(R.id.pull_to_refresh_my_event_list);
		initList();
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(myEventsReceiver);
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(myEventsReceiver, new IntentFilter(RefreshMyEvents.NOTIFICATION));
		list.setRefreshing();

		if (SharedPreference.getPrefBooleanValue(context, Preference.DID_MY_EVENTS_INIT))
			setEvents(myEventDataSource.getOngoingEvents());
		else serviceStarter.updateMyEvents();
	}

	/**
	 * init the list 
	 */
	private void initList() {
		list.setAdapter(adapter);
		list.setDivider(new ColorDrawable(0x00c2c2c2));
		list.setDividerHeight(0);

		list.setOnItemClickListener(new PullToRefreshListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				Intent intent = new Intent(context, EventFullActivity.class);
				intent.putExtra("eid", adapter.getEventItem(position).getId());
				context.startActivity(intent);
			}	
		});

		list.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				serviceStarter.updateMyEvents();
			}
		}); 
	}

	/**
	 * Set the events to be display
	 * @param events
	 */
	public void setEvents(ArrayList<FbEvent> eventsToBeSet) {
		ArrayList<FbEvent> unrepliedEvents = new ArrayList<FbEvent>();
		ArrayList<FbEvent> repliedEvents = new ArrayList<FbEvent>();

		Location eventLocation = new Location(provider);
		for (FbEvent event : eventsToBeSet) {
			if (event.getVenueLatitude() == 0 && event.getVenueLongitude() == 0) {
				event.setDistance(-1);
			} else {
				eventLocation.setLatitude(event.getVenueLatitude());
				eventLocation.setLongitude(event.getVenueLongitude());
				double distance = lastKnown.distanceTo(eventLocation)/Coordinate.METERS_IN_MILE;
				event.setDistance(distance);
			}

			if (event.getRsvp_status().equals(RsvpEventHandling.NOT_REPLIED))
				unrepliedEvents.add(event);
			else repliedEvents.add(event);

		}

		sortEventsByTime(unrepliedEvents);
		sortEventsByTime(repliedEvents);
		adapter.setEvents(unrepliedEvents, repliedEvents);
		adapter.notifyDataSetChanged();

		if (list.isRefreshing()) list.onRefreshComplete();
	}

	/**
	 * sort the events by Time
	 * @param ArrayList<FbEvent>
	 */
	private void sortEventsByTime(ArrayList<FbEvent> eventsToSort) {
		if (eventsToSort != null && eventsToSort.size() > 0) {
			Collections.sort(eventsToSort, new Comparator<FbEvent>() {
				@Override
				public int compare(FbEvent lhs, FbEvent rhs) {
					if (lhs.getStart_time() > rhs.getStart_time()) return 1;
					else if (lhs.getStart_time() < rhs.getStart_time()) return -1;
					return 0;
				}
			});
		}
	}

	//attach to activity
	private OnMyEventShareClick onShareClick;
	public interface OnMyEventShareClick {
		public void onShareEvent(FbEvent event);
	}

	public void shareEvent(FbEvent event) {
		onShareClick.onShareEvent(event);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		onShareClick = (OnMyEventShareClick) activity;
	}
}
