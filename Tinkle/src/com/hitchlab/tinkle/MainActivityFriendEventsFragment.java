package com.hitchlab.tinkle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
import android.widget.TextView;
import android.widget.Toast;

import com.hitchlab.tinkle.R;
import com.hitchlab.tinkle.appevent.EventFullActivity;
import com.hitchlab.tinkle.datasource.FriendEventDataSource;
import com.hitchlab.tinkle.dialog.FriendEventsFilterDialog;
import com.hitchlab.tinkle.map.Coordinate;
import com.hitchlab.tinkle.map.MapLocationManager;
import com.hitchlab.tinkle.objects.FbEvent;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;
import com.hitchlab.tinkle.service.RefreshFriendEvents;
import com.hitchlab.tinkle.service.ServiceStarter;
import com.hitchlab.tinkle.supports.ImageLoading;
import com.hitchlab.tinkle.supports.TimeFrame;
import com.hitchlab.tinkle.template.FriendEventListAdapter;

import eu.erikw.PullToRefreshListView;

public class MainActivityFriendEventsFragment extends Fragment{
	
	private Context context; 
	private FriendEventListAdapter adapter;
	private FriendEventDataSource friendEventDataSource;
	private ServiceStarter serviceStarter;
	private FriendEventsFilterDialog friendEventsFilterDialog;

	private ImageLoading imageLoading;

	private TextView filterButton;
	private PullToRefreshListView list;

	private ArrayList<FbEvent> originalEventsWithDistance;
	private ArrayList<FbEvent> events;
	private ArrayList<FbEvent> todayEvents;
	private ArrayList<FbEvent> thisWeekendEvents;
	private ArrayList<FbEvent> thisWeekEvents;
	private ArrayList<FbEvent> nextWeekEvents;
	private ArrayList<FbEvent> thisMonthEvents;
	private ArrayList<FbEvent> otherEvents;

	private MapLocationManager locationManager;
	private Location lastKnown;
	private String provider;

	private int interestFilter;
	private int distanceFilter;
	private int timeFilter;
	
	/**
	 * receiver for my events service
	 */
	private BroadcastReceiver friendEventsReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			ArrayList<FbEvent> tempEvents = intent.getParcelableArrayListExtra("data");
			if (tempEvents != null) {
				 findEventsDistance(tempEvents);
				 showEvents();
			} 
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = getActivity();
		this.imageLoading = new ImageLoading(context);
		this.locationManager = new MapLocationManager(context);
		this.provider = locationManager.getProvider();
		if (provider != null) this.lastKnown = locationManager.getLastKnownLocation();

		this.events = new ArrayList<FbEvent>();
		this.friendEventDataSource = new FriendEventDataSource(context);
		this.adapter = new FriendEventListAdapter(context, imageLoading) {
			@Override
			public void shareClick(FbEvent event) {
				shareEvent(event);
			}
		};

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
		
		this.friendEventsFilterDialog = new FriendEventsFilterDialog(context) {
			@Override
			public void onFriendEventsFilterDialogOkPressed(int interestType, int distanceType, int timeType) {
				interestFilter = interestType;
				distanceFilter = distanceType;
				timeFilter = timeType;
				if (originalEventsWithDistance != null) showEvents();
			}
		};
		
		this.timeFilter = FriendEventsFilterDialog.TYPE_TIME_ALL;
		this.distanceFilter = FriendEventsFilterDialog.TYPE_DISTANCE_ALL;
		this.interestFilter = FriendEventsFilterDialog.TYPE_INTEREST_ALL;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_view_friend_events, container, false);
		this.filterButton = (TextView) view.findViewById(R.id.main_view_friend_event_filter_button);
		filterButton.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				friendEventsFilterDialog.showDialog(interestFilter, distanceFilter, timeFilter);
			}
		});
		
		this.list = (PullToRefreshListView) view.findViewById(R.id.main_view_friend_event_list);
		initList();

		if (SharedPreference.getPrefBooleanValue(context, Preference.DID_FRIEND_EVENTS_INIT)) {
			if (!list.isRefreshing()) list.setRefreshing();
			ArrayList<FbEvent> tempEvents = friendEventDataSource.getOngoingEvents();
			findEventsDistance(tempEvents);
			showEvents();
		} else doRefresh();
		
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(friendEventsReceiver);
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(friendEventsReceiver, new IntentFilter(RefreshFriendEvents.NOTIFICATION));
	}

	/**
	 * we first try to find the distance of the vents
	 * @param events
	 */
	private void findEventsDistance(ArrayList<FbEvent> undistanceEvents) {
		if (provider != null) {
			Location eventLocation = new Location(provider);
			for (FbEvent event : undistanceEvents) {
				if (event.getVenueLatitude() == 0 && event.getVenueLongitude() == 0) {
					event.setDistance(-1);
				} else {
					eventLocation.setLatitude(event.getVenueLatitude());
					eventLocation.setLongitude(event.getVenueLongitude());
					double distance = lastKnown.distanceTo(eventLocation)/Coordinate.METERS_IN_MILE;
					event.setDistance(distance);
				}
			}
		}
		this.originalEventsWithDistance = undistanceEvents;
	}

	/**
	 * Filter the events by distance
	 * @params events
	 */
	private ArrayList<FbEvent> filterEventsByDistance(ArrayList<FbEvent> events) {
		ArrayList<FbEvent> filteredByDistanceEvents = new ArrayList<FbEvent>();
		for (FbEvent event : events) {
			if (distanceFilter == FriendEventsFilterDialog.TYPE_DISTANCE_ALL) {
				filteredByDistanceEvents.add(event);
			} else if (distanceFilter == FriendEventsFilterDialog.TYPE_DISTANCE_ONE) {
				if (event.getDistance() != -1 && event.getDistance() <= 1) filteredByDistanceEvents.add(event);
			} else if (distanceFilter == FriendEventsFilterDialog.TYPE_DISTANCE_FIVE) {
				if (event.getDistance() != -1 && event.getDistance() <= 5) filteredByDistanceEvents.add(event);
			} else if (distanceFilter == FriendEventsFilterDialog.TYPE_DISTANCE_FIFTEEN) {
				if (event.getDistance() != -1 && event.getDistance() <= 15) filteredByDistanceEvents.add(event);
			} else if (distanceFilter == FriendEventsFilterDialog.TYPE_DISTANCE_FIFTY) {
				if (event.getDistance() != -1 && event.getDistance() <= 50) filteredByDistanceEvents.add(event);
			}
		}
		return filteredByDistanceEvents;
	}
	
	/**
	 * Filter the events by interest
	 * @param events
	 */
	private ArrayList<FbEvent> filterEventsByInterest(ArrayList<FbEvent> events) {
		ArrayList<FbEvent> filteredByInterestEvents = new ArrayList<FbEvent>();
		for (FbEvent event : events) {
			if (interestFilter == FriendEventsFilterDialog.TYPE_INTEREST_ALL) {
				filteredByInterestEvents.add(event);
			} else if (interestFilter == FriendEventsFilterDialog.TYPE_INTEREST_FIVE) {
				if (event.getFriendsAttending().size() >= 5) filteredByInterestEvents.add(event);
			} else if (interestFilter == FriendEventsFilterDialog.TYPE_INTEREST_TEN) {
				if (event.getFriendsAttending().size() >= 10) filteredByInterestEvents.add(event);
			}
		}
		return filteredByInterestEvents;
	}
	
	/**
	 * Filtered the events by time
	 * @param events
	 */
	private ArrayList<FbEvent> filterEventsByTime(ArrayList<FbEvent> events) {
		long[] todayTimeFrame = TimeFrame.getTodayTimeFrame();
		long[] thisWeekendTimeFrame = TimeFrame.getThisWeekendTimeFrame();
		long[] thisWeekTimeFrame = TimeFrame.getThisWeekTimeFrame();
		
		ArrayList<FbEvent> filteredByTimeEvents = new ArrayList<FbEvent>();
		for (FbEvent event : events) {
			if (timeFilter == FriendEventsFilterDialog.TYPE_TIME_ALL) {
				filteredByTimeEvents.add(event);
			} else if (timeFilter == FriendEventsFilterDialog.TYPE_TIME_TODAY) {
				if (event.getStart_time() >= todayTimeFrame[0] && event.getStart_time() < todayTimeFrame[1])
					filteredByTimeEvents.add(event);
			} else if (timeFilter == FriendEventsFilterDialog.TYPE_TIME_THIS_WEEKEND) {
				if (event.getStart_time() >= thisWeekendTimeFrame[0] && event.getStart_time() < thisWeekendTimeFrame[1])
					filteredByTimeEvents.add(event);
			} else if (timeFilter == FriendEventsFilterDialog.TYPE_TIME_THIS_WEEK) {
				if (event.getStart_time() >= todayTimeFrame[0] && event.getStart_time() < thisWeekTimeFrame[1])
					filteredByTimeEvents.add(event);
			}
		}
		return filteredByTimeEvents;
	}
	
	/**
	 * Show the events using filtered values
	 */
	private void showEvents() {
		ArrayList<FbEvent> filteredByDistanceEvents = filterEventsByDistance(originalEventsWithDistance);
		ArrayList<FbEvent> filteredByDistanceAndInterestEvents = filterEventsByInterest(filteredByDistanceEvents);
		ArrayList<FbEvent> filteredEvents = filterEventsByTime(filteredByDistanceAndInterestEvents);
		
		//we first separate the events into 5 categories 
		todayEvents = new ArrayList<FbEvent>();
		thisWeekendEvents = new ArrayList<FbEvent>();
		thisWeekEvents = new ArrayList<FbEvent>();
		nextWeekEvents = new ArrayList<FbEvent>();
		thisMonthEvents = new ArrayList<FbEvent>();
		otherEvents = new ArrayList<FbEvent>();

		long[] todayTimeFrame = TimeFrame.getTodayTimeFrame();
		long[] thisWeekendTimeFrame = TimeFrame.getThisWeekendTimeFrame();
		long[] thisWeekTimeFrame = TimeFrame.getThisWeekTimeFrame();
		long[] nextWeekTimeFrame = TimeFrame.getNextWeekTimeFrame();
		long[] thisMonthTimeFrame = TimeFrame.getThisMonthTimeFrame();

		for (FbEvent event : filteredEvents) {
			long startTime = event.getStart_time();
			if (startTime >= thisWeekendTimeFrame[0] && startTime < thisWeekendTimeFrame[1])
				thisWeekendEvents.add(event);
			else if (startTime >= todayTimeFrame[0] && startTime < todayTimeFrame[1])
				todayEvents.add(event);
			else if (startTime >= thisWeekTimeFrame[0] && startTime < thisWeekTimeFrame[1])
				thisWeekEvents.add(event);
			else if (startTime >= nextWeekTimeFrame[0] && startTime < nextWeekTimeFrame[1])
				nextWeekEvents.add(event);
			else if (startTime >= thisMonthTimeFrame[0] && startTime < thisMonthTimeFrame[1])
				thisMonthEvents.add(event);
			else otherEvents.add(event);
		}
		
		sortEventsByDistance(thisWeekendEvents);
		sortEventsByDistance(todayEvents);
		sortEventsByDistance(thisWeekEvents);
		sortEventsByDistance(nextWeekEvents);
		sortEventsByDistance(thisMonthEvents);
		sortEventsByDistance(otherEvents);

		events.clear();
		events.addAll(todayEvents);
		events.addAll(thisWeekEvents);
		events.addAll(thisWeekendEvents);
		events.addAll(nextWeekEvents);
		events.addAll(thisMonthEvents);
		events.addAll(otherEvents);

		adapter.setEvents(todayEvents, thisWeekEvents, thisWeekendEvents, nextWeekEvents, thisMonthEvents, otherEvents);
		adapter.notifyDataSetChanged();

		if (list.isRefreshing()) list.onRefreshComplete();
	}

	/**
	 * init the list 
	 */
	private void initList() {
		list.setAdapter(adapter);
		list.setDivider(new ColorDrawable(0x00c2c2c2));
		list.setDividerHeight(5);

		list.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				doRefresh();
			}
		}); 

		list.setOnItemClickListener(new PullToRefreshListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				FbEvent event = events.get(position);
				Intent intent = new Intent(context, EventFullActivity.class);
				intent.putExtra("eid", event.getId());
				context.startActivity(intent);
			}
		});

	}

	/**
	 * sort the events by distance
	 * @param ArrayList<FbEvent>
	 */
	private void sortEventsByDistance(ArrayList<FbEvent> eventsToSort) {
		if (eventsToSort != null && eventsToSort.size() > 0) {
			Collections.sort(eventsToSort, new Comparator<FbEvent>() {
				@Override
				public int compare(FbEvent lhs, FbEvent rhs) {
					if (lhs.getDistance() == rhs.getDistance()) return 0;
					else if (lhs.getDistance() == -1) return 1;
					else if (rhs.getDistance() == -1) return -1;
					else if (lhs.getDistance() > rhs.getDistance()) return 1;
					else return -1;
				}
			});
		}
	}


	/**
	 * Refresh the friend event list
	 */
	private void doRefresh() {
		if (!list.isRefreshing()) list.setRefreshing();
		serviceStarter.updateFriendsEvents();
	}

	//attach to activity
	private OnFriendEventShareClick onShareClick;
	public interface OnFriendEventShareClick {
		public void onShareEvent(FbEvent event);
	}

	public void shareEvent(FbEvent event) {
		onShareClick.onShareEvent(event);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		onShareClick = (OnFriendEventShareClick) activity;
	}
}
