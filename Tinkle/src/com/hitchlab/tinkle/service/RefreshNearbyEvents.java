package com.hitchlab.tinkle.service;

import java.util.ArrayList;

import org.json.JSONException;

import com.hitchlab.tinkle.datasource.PublicEventDataSource;
import com.hitchlab.tinkle.dbrequest.EventRequest;
import com.hitchlab.tinkle.dbrequest.Table;
import com.hitchlab.tinkle.objects.FbEvent;
import com.hitchlab.tinkle.supports.TimeFrame;

import android.app.IntentService;
import android.content.Intent;

public class RefreshNearbyEvents extends IntentService{

	public static final String NOTIFICATION = "com.hitchlab.tinkle.service.RefreshNearbyEvents";

	public RefreshNearbyEvents() {
		super("RefreshNearbyEvents");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		PublicEventDataSource dataSource = new PublicEventDataSource(this);
		
		double minLng = intent.getDoubleExtra(Table.PUBLIC_EVENT_LOWER_LONGITUDE, 0);
		double maxLng = intent.getDoubleExtra(Table.PUBLIC_EVENT_UPPER_LONGITUDE, 0);
		double minLat = intent.getDoubleExtra(Table.PUBLIC_EVENT_LOWER_LATITUDE, 0);
		double maxLat = intent.getDoubleExtra(Table.PUBLIC_EVENT_UPPER_LATITUDE, 0);
//		String sinceTime = intent.getStringExtra(Table.PUBLIC_EVENT_TIME_FRAME_BEGIN);
//		String toTime = intent.getStringExtra(Table.PUBLIC_EVENT_TIME_FRAME_END);
		long sinceTime = TimeFrame.getUnixTime(TimeFrame.getTodayDate());
		long toTime = TimeFrame.getUnixTime(TimeFrame.getIthDate(14));
		
		try {
			ArrayList<FbEvent> events = EventRequest.getBoundedEvents(minLng, maxLng, minLat, maxLat, sinceTime, toTime);
			for (FbEvent event : events) {
				if (!dataSource.isEventExist(event.getId())) dataSource.addEvent(event);
			}
			
			publishResults(events);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * publish the results
	 * @param events
	 */
	private void publishResults(ArrayList<FbEvent> events) {
		Intent intent = new Intent(NOTIFICATION);
		intent.putParcelableArrayListExtra("data", events);
		sendBroadcast(intent);
	}
	
}
