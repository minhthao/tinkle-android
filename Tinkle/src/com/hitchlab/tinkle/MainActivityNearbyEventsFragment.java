package com.hitchlab.tinkle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.hitchlab.tinkle.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.hitchlab.tinkle.appevent.EventFullActivity;
import com.hitchlab.tinkle.datasource.FriendEventDataSource;
import com.hitchlab.tinkle.dbrequest.Table;
import com.hitchlab.tinkle.dialog.NearbyEventsFilterDialog;
import com.hitchlab.tinkle.map.Coordinate;
import com.hitchlab.tinkle.map.InfoWindow;
import com.hitchlab.tinkle.map.MapLocationManager;
import com.hitchlab.tinkle.objects.FbEvent;
import com.hitchlab.tinkle.service.RefreshNearbyEvents;
import com.hitchlab.tinkle.supports.ImageLoading;
import com.hitchlab.tinkle.supports.TimeFrame;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivityNearbyEventsFragment extends Fragment {

	private Context context;
	private FriendEventDataSource friendEventDataSource;
	private NearbyEventsFilterDialog nearbyEventsFilterDialog;

	private InfoWindow infoWindow;
	private ImageLoading imageLoading;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private Bitmap iconBitmap;

	private TextView filterButton;
	private GoogleMap map;
	private TextView refreshButton;

	private ArrayList<FbEvent> originalEventsWithDistance;
	private ArrayList<FbEvent> events;
	private HashMap<String, Integer> markerToIndex;

	private MapLocationManager locationManager;
	private Location lastKnown;
	private String provider;

	private int interestFilter;
	private int timeFilter;

	/**
	 * receiver for my events service
	 */
	private BroadcastReceiver nearbyEventsReceiver = new BroadcastReceiver() {
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
		this.friendEventDataSource = new FriendEventDataSource(context);
		this.imageLoading = new ImageLoading(context);
		this.imageLoader = imageLoading.getImageLoader();
		this.options = imageLoading.getDisplayImagesOption();
		this.locationManager = new MapLocationManager(context);
		this.provider = locationManager.getProvider();
		if (provider != null) this.lastKnown = locationManager.getLastKnownLocation();

		this.events = new ArrayList<FbEvent>();

		this.nearbyEventsFilterDialog = new NearbyEventsFilterDialog(context) {
			@Override
			public void onNearbyEventsFilterDialogOkPressed(int interestType, int timeType) {
				interestFilter = interestType;
				timeFilter = timeType;
				if (originalEventsWithDistance != null) showEvents();
			}
		};

		this.timeFilter = NearbyEventsFilterDialog.TYPE_TIME_ALL;
		this.interestFilter = NearbyEventsFilterDialog.TYPE_INTEREST_NO;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_view_nearby_events, container, false);
		this.filterButton = (TextView) view.findViewById(R.id.main_view_nearby_event_filter_button);
		filterButton.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				nearbyEventsFilterDialog.showDialog(interestFilter, timeFilter);
			}
		});
		
		this.infoWindow = new InfoWindow(context);
		this.refreshButton = (TextView) view.findViewById(R.id.main_view_nearby_event_refresh_button);
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		FragmentManager fm = getChildFragmentManager();
		SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.nearby_events_map_fragment);
		if (fragment == null) {
			fragment = SupportMapFragment.newInstance();
			fm.beginTransaction().replace(R.id.nearby_events_map_fragment, fragment).commit();
		}
		this.map = fragment.getMap();
		map.setMyLocationEnabled(true);
		map.getUiSettings().setZoomControlsEnabled(false);
		if (lastKnown != null) moveCamera(lastKnown, 14);
		
		initMap();
	}

	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(nearbyEventsReceiver);
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(nearbyEventsReceiver, new IntentFilter(RefreshNearbyEvents.NOTIFICATION));
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
	 * Filter the events by interest
	 * @param events
	 */
	private ArrayList<FbEvent> filterEventsByInterest(ArrayList<FbEvent> events) {
		ArrayList<FbEvent> filteredByInterestEvents = new ArrayList<FbEvent>();
		for (FbEvent event : events) {
			if (interestFilter == NearbyEventsFilterDialog.TYPE_INTEREST_NO) {
				filteredByInterestEvents.add(event);
			} else if (interestFilter == NearbyEventsFilterDialog.TYPE_INTEREST_YES) {
				if (friendEventDataSource.isEventExist(event.getId())) filteredByInterestEvents.add(event);
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
			if (timeFilter == NearbyEventsFilterDialog.TYPE_TIME_ALL) {
				filteredByTimeEvents.add(event);
			} else if (timeFilter == NearbyEventsFilterDialog.TYPE_TIME_TODAY) {
				if (event.getStart_time() >= todayTimeFrame[0] && event.getStart_time() < todayTimeFrame[1])
					filteredByTimeEvents.add(event);
			} else if (timeFilter == NearbyEventsFilterDialog.TYPE_TIME_THIS_WEEKEND) {
				if (event.getStart_time() >= thisWeekendTimeFrame[0] && event.getStart_time() < thisWeekendTimeFrame[1])
					filteredByTimeEvents.add(event);
			} else if (timeFilter == NearbyEventsFilterDialog.TYPE_TIME_THIS_WEEK) {
				if (event.getStart_time() >= todayTimeFrame[0] && event.getStart_time() < thisWeekTimeFrame[1])
					filteredByTimeEvents.add(event);
			}
		}
		return filteredByTimeEvents;
	}

	/**
	 * Show events using filtered values
	 */
	public void showEvents() {
		ArrayList<FbEvent> filteredByInterestEvents = filterEventsByInterest(originalEventsWithDistance);
		ArrayList<FbEvent> filteredEvents = filterEventsByTime(filteredByInterestEvents);

		map.clear();
		if (filteredEvents.size() > 0) populateMap(filteredEvents);
		
		refreshButton.setVisibility(View.VISIBLE);
	}



	/**
	 * Init the map
	 */
	private void initMap() {
		map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				FbEvent fbEvent = events.get(markerToIndex.get(marker.getId()));
				final Marker myMarker = marker;
				if (!fbEvent.getPicture().equals("")) {
					imageLoader.loadImage(fbEvent.getPicture(), options, new SimpleImageLoadingListener() {
						final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
							if (loadedImage != null) {
								boolean firstDisplay = !displayedImages.contains(imageUri);
								if (firstDisplay) {
									displayedImages.add(imageUri);
								}
								iconBitmap = loadedImage;
								myMarker.showInfoWindow();
							}
						}
					});
				} else myMarker.showInfoWindow();
				return true;
			}
		});

		map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
			@Override
			public View getInfoWindow(Marker marker) {
				return null;
			}

			@Override
			public View getInfoContents(Marker marker) {
				FbEvent event = events.get(markerToIndex.get(marker.getId()));
				return infoWindow.getInfoContents(event, iconBitmap);
			}
		});

		map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {		
			@Override
			public void onInfoWindowClick(Marker marker) {
				Intent intent = new Intent(context, EventFullActivity.class);
				intent.putExtra("eid", events.get(markerToIndex.get(marker.getId())).getId());
				context.startActivity(intent);
			}
		});

		map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
			@Override
			public void onMapLoaded() {
				doRefresh();
			}
		});

		refreshButton.setOnClickListener(new ImageView.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				doRefresh();
			}
		});
	}

	/**
	 * move the camera to a specific coordinate
	 * @param the coordinate
	 */
	private void moveCamera(Location location, long zoom) {
		if (location != null) {
			LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(loc, zoom);
			if (zoom == 0) cameraUpdate = CameraUpdateFactory.newLatLng(loc);
			map.animateCamera(cameraUpdate);
		}
	}

	/**
	 * Populate the map with list of events
	 * @param list of events 
	 */
	private void populateMap(ArrayList<FbEvent> events) {
		markerToIndex = new HashMap<String, Integer>();

		for (int i = 0; i < events.size(); i++)  {
			FbEvent event = events.get(i);
			double longitude = event.getVenueLongitude();
			double latitude = event.getVenueLatitude();
			if (longitude != 0 || latitude != 0) {
				Marker marker = map.addMarker(new MarkerOptions()
				.position(new LatLng(latitude, longitude))
				.icon(BitmapDescriptorFactory.defaultMarker())   
				.draggable(false));
				markerToIndex.put(marker.getId(), i);
			}
		}
	}

	/**
	 * Refresh the nearby event list
	 */
	private void doRefresh() {
		refreshButton.setVisibility(View.GONE);
		
		double minLng = 0;
		double maxLng = 0;
		double minLat = 0;
		double maxLat = 0;
		long sinceTime = TimeFrame.getUnixTime(TimeFrame.getTodayDate());
		long toTime = TimeFrame.getUnixTime(TimeFrame.getIthDate(14));

		VisibleRegion visibleRegion = map.getProjection().getVisibleRegion();
		LatLng bottomLeft = visibleRegion.nearLeft;
		LatLng upperRight = visibleRegion.farRight;
		if (bottomLeft.latitude < upperRight.latitude) {
			minLat = bottomLeft.latitude;
			maxLat = upperRight.latitude;
		} else {
			minLat = upperRight.latitude;
			maxLat = bottomLeft.latitude;
		}

		if (bottomLeft.longitude < upperRight.longitude) {
			minLng = bottomLeft.longitude;
			maxLng = upperRight.longitude;
		} else  {
			minLng = upperRight.longitude;
			maxLng = bottomLeft.longitude;
		}

		if (minLng == 0 && provider != null) {
			double lat = lastKnown.getLatitude();
			double lng = lastKnown.getLongitude();
			minLat = Math.max(-90, lat - 0.5);
			maxLat = Math.min(90, lat + 0.5);
			minLng = Math.max(-180, lng - 0.1);
			maxLng = Math.min(180, lng + 0.1);
		}

		Intent intent = new Intent(context, RefreshNearbyEvents.class);
		intent.putExtra(Table.PUBLIC_EVENT_LOWER_LONGITUDE, minLng);
		intent.putExtra(Table.PUBLIC_EVENT_UPPER_LONGITUDE, maxLng);
		intent.putExtra(Table.PUBLIC_EVENT_LOWER_LATITUDE, minLat);
		intent.putExtra(Table.PUBLIC_EVENT_UPPER_LATITUDE, maxLat);
		intent.putExtra(Table.PUBLIC_EVENT_TIME_FRAME_BEGIN, sinceTime);
		intent.putExtra(Table.PUBLIC_EVENT_TIME_FRAME_END, toTime);
		context.startService(intent);
	}

	//attach to activity
	private OnNearbyEventShareClick onShareClick;
	public interface OnNearbyEventShareClick {
		public void onShareEvent(FbEvent event);
	}

	public void shareEvent(FbEvent event) {
		onShareClick.onShareEvent(event);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		onShareClick = (OnNearbyEventShareClick) activity;
	}
}
