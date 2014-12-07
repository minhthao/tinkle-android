package com.hitchlab.tinkle.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hitchlab.tinkle.objects.FbEvent;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

public class Coordinate {
	public static final double METERS_IN_MILE = 1609.344;
	 
	/**
	 * Update the coordinate of a event if it can be found, but not yet determined
	 * @param event
	 * @param context
	 */
	public static void updateCoordinate(FbEvent event, Context context) {
		if (!event.getLocation().equals("") && event.getVenueLatitude() == 0) {
			Geocoder geocoder = new Geocoder(context);  
			try {
				List<Address> addresses = geocoder.getFromLocationName(event.getLocation(), 1);
				if(addresses.size() > 0) {
				    event.setVenueLatitude(addresses.get(0).getLatitude());
				    event.setVenueLongitude(addresses.get(0).getLongitude());
				}
			} catch (IOException e) {
				//Log.e("address", "Cannot get the address of event " + event.getId());
			}
		}
	}
	
	/**
	 * Get the list of all the events within a certain radius
	 * @param events
	 * @param radius
	 * @param manager
	 * @return list of events
	 */
	public static ArrayList<FbEvent> getEventsWithinRadius(ArrayList<FbEvent> events, double radius, MapLocationManager manager) {
		Location lastKnown = manager.getLastKnownLocation();
		ArrayList<FbEvent> results = new ArrayList<FbEvent>();
		for (int i = 0; i < events.size(); i++) {
			FbEvent event = events.get(i);
			if (event.getVenueLatitude() != 0) {
				Location eventLocation = new Location(manager.getProvider());
				eventLocation.setLatitude(event.getVenueLatitude());
				eventLocation.setLongitude(event.getVenueLongitude());
				double distance = lastKnown.distanceTo(eventLocation)/METERS_IN_MILE;
				if (radius == -1 || radius >= distance) {
					event.setDistance(distance);
					results.add(event);
				}
			}
		}
		return results;
	}
}
