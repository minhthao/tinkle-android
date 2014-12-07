package com.hitchlab.tinkle.service;

import java.util.ArrayList;

import com.facebook.Session;
import com.hitchlab.tinkle.fbquery.QueryPersonalEvents;
import com.hitchlab.tinkle.map.Coordinate;
import com.hitchlab.tinkle.map.MapLocationManager;
import com.hitchlab.tinkle.objects.FbEvent;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

public class QueryFbUserEvents extends IntentService{

	public static final String NOTIFICATION = "com.hitchlab.tinkle.service.QueryFbUserEvents";
	
	public static final String UID = "uid";
	
	public static final String DATA = "data";
	
	private Context context;
	private MapLocationManager locationManager;
	private Location lastKnown;
	private String provider;
	private String uid;
	private QueryPersonalEvents queryPersonalEvents;
	
	public QueryFbUserEvents() {
		super("QueryFbUserEvents");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		this.context = this;
		this.locationManager = new MapLocationManager(context);
		this.lastKnown = locationManager.getLastKnownLocation();
		this.provider = locationManager.getProvider();
		this.uid = intent.getStringExtra("uid");
		
		if (queryPersonalEvents == null) setupQueryPersonalEvents();
		
		Session session = Session.getActiveSession();
		if (session == null) session = Session.openActiveSessionFromCache(context);
		if (session != null && session.isOpened())
			queryPersonalEvents.queryEventsAndWait(session, uid);
	}
	
	/**
	 * Setup the query personal events
	 */
	private void setupQueryPersonalEvents() {
		queryPersonalEvents = new QueryPersonalEvents() {
			@Override
			protected void onQueryCompleted(ArrayList<FbEvent> events) {
				Location eventLocation = new Location(provider);
				for (FbEvent event : events) {
					if (event.getVenueLatitude() == 0 && event.getVenueLongitude() == 0) {
						event.setDistance(-1);
					} else {
						eventLocation.setLatitude(event.getVenueLatitude());
						eventLocation.setLongitude(event.getVenueLongitude());
						double distance = lastKnown.distanceTo(eventLocation)/Coordinate.METERS_IN_MILE;
						event.setDistance(distance);
					}
				}
				
				publishResults(events);
			}
		};
	}
	
	/**
	 * publish the results
	 * @param type
	 */
	private void publishResults(ArrayList<FbEvent> events) {
		Intent intent = new Intent(NOTIFICATION);
		intent.putExtra(UID, uid);
		intent.putParcelableArrayListExtra(DATA, events);
		sendBroadcast(intent);
	}
}
