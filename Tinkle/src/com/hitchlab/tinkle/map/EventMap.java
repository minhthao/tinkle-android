package com.hitchlab.tinkle.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hitchlab.tinkle.objects.FbEvent;
import com.hitchlab.tinkle.supports.ImageLoading;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public abstract class EventMap {
	HashMap<String, FbEvent> markerToEvent;
	Context context;
	ImageLoading imageLoading;
	ImageLoader imageLoader;
	DisplayImageOptions options;

	Bitmap bitmap;

	InfoWindow infoWindow;
	GoogleMap googleMap;

	public EventMap(SupportMapFragment mf, Context context) {
		this.googleMap = mf.getMap();
		this.infoWindow = new InfoWindow(context);
		this.imageLoading = new ImageLoading(context);
		this.imageLoader = imageLoading.getImageLoader();
		this.options = imageLoading.getDisplayImagesOption();

		googleMap.setMyLocationEnabled(true);
		//googleMap.getUiSettings().setMyLocationButtonEnabled(false);

		setOnMarkerClickListener();
		setInfoWindowAdapter();
		setOnInfoWindowClickListener();
	}

	/**
	 * Abstract class for when event is selected
	 */
	public abstract void onEventSelected(FbEvent event);

	/**
	 * On marker click listener
	 */
	private void setOnMarkerClickListener() {
		googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				FbEvent fbEvent = markerToEvent.get(marker.getId());
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
								bitmap = loadedImage;
								myMarker.showInfoWindow();
							}
						}
					});
				} else myMarker.showInfoWindow();
				return false;
			}
		});
	}

	/**
	 * Set on window info adapter
	 */
	private void setInfoWindowAdapter() {
		googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

			@Override
			public View getInfoWindow(Marker marker) {
				return null;
			}

			@Override
			public View getInfoContents(Marker marker) {
				FbEvent event = markerToEvent.get(marker.getId());
				return infoWindow.getInfoContents(event, bitmap);
			}
		});
	}

	/**
	 * Set info window click listener
	 */
	private void setOnInfoWindowClickListener() {
		googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {		
			@Override
			public void onInfoWindowClick(Marker marker) {
				onEventSelected(markerToEvent.get(marker.getId()));
			}
		});
	}

	/**
	 * move the camera to a specific coordinate
	 * @param the coordinate
	 */
	public void moveCamera(Location location, long zoom) {
		if (location != null) {
			LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(loc, zoom);
			if (zoom == 0) cameraUpdate = CameraUpdateFactory.newLatLng(loc);
			googleMap.animateCamera(cameraUpdate);
		}
	}

	/**
	 * Clear the everything on the map
	 */
	public void clearMap() {
		googleMap.clear();
	}

	/**
	 * Populate the map with list of events
	 * @param list of events 
	 */
	public void populateMap(ArrayList<FbEvent> events) {
		markerToEvent = new HashMap<String, FbEvent>();
		for (FbEvent event : events)  {
			double longitude = event.getVenueLongitude();
			double latitude = event.getVenueLatitude();
			if (longitude != 0 || latitude != 0) {
				Marker marker = googleMap.addMarker(new MarkerOptions()
				.position(new LatLng(latitude, longitude))
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))   
				.draggable(false));
				//TODO remove marker mechanism
				markerToEvent.put(marker.getId(), event);
			}
		}
	}
}
