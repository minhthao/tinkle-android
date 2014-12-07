package com.hitchlab.tinkle.map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

public class MapLocationManager {
	
	Context context;
	LocationManager locationManager;
	String provider;
	
	public MapLocationManager(Context context) {
		this.context = context;
		this.locationManager= (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		provider = locationManager.getBestProvider(new Criteria(), true);
		if (provider == null) onProviderDisabled(provider); 
	}
	
	/**
	 * get last known location
	 * @return location
	 */
	public Location getLastKnownLocation() {
		Location loc = locationManager.getLastKnownLocation(provider);
		if (loc == null) {
			loc = new Location(provider);
			//Set it to stanford. Which is the current headquarter
			loc.setLatitude(37.4225);
			loc.setLongitude(122.1653);
		}
		return loc;
	}
	
	/**
	 * Get the provider
	 * @return provider
	 */
	public String getProvider() {
		return provider;
	}
	
	/**
	 * Handling when the provider became disable
	 * @param provider
	 */
	public void onProviderDisabled(String provider) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("GPS is disable");
		builder.setCancelable(false);

		//take user to turn on the GPS setting
		builder.setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent startGps = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				context.startActivity(startGps);
			} 
		});

		//close
		builder.setNegativeButton("Leave GPS off", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		//display the alert
		AlertDialog alert = builder.create();
		alert.show();
	}
}
