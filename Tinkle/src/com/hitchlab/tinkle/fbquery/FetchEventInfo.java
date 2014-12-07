package com.hitchlab.tinkle.fbquery;

import java.util.Calendar;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

import com.hitchlab.tinkle.objects.FbEvent;
import com.hitchlab.tinkle.supports.TimeFrame;

public class FetchEventInfo {
	JSONObject eventData;
	JSONObject venue;
	FbEvent event;
	
	public FetchEventInfo(JSONObject eventData) {
		this.eventData = eventData;
		this.event = new FbEvent();
		
		fetchEventId();
		fetchEventName();
		fetchEventStartTime();
		fetchEventEndTime();
		fetchEventPicture();
		fetchEventLocation();
		fetchEventTimeZone();
		try {
			this.venue = eventData.getJSONObject("venue");
			if (venue != null) fetchEventVenue();
		} catch (JSONException e) {}
		fetchEventInterestsCount();
		fetchPrivacy();
		fetchHost();
		fetchDescription();
		fetchUpdateTime();
	}
	
	/**
	 * Get the event after fetched
	 * @return event
	 */
	public FbEvent getEvent() {
		return event;
	}
	
	/**
	 * fetch event interested count
	 */
	private void fetchEventInterestsCount() {
		try {
			int numAttending = eventData.getInt("attending_count");
			int numUnsure = eventData.getInt("unsure_count");
			event.setTotalInterested(numAttending + numUnsure);
		} catch (JSONException e) {
			Log.i("EVENT", "fail to get eid");
		}
	}
	
	/**
	 * fetch event id
	 */
	private void fetchEventId() {
		try {
			event.setId(eventData.getString("eid"));
		} catch (JSONException e) {
			Log.i("EVENT", "fail to get eid");
		}
	}
	
	/**
	 * fetch event name
	 */
	private void fetchEventName() {
		try {
			event.setName(eventData.getString("name"));
			if (event.getName().equals("null")) event.setName("");
		} catch (JSONException e) {
			Log.i("EVENT", "fail to get event name");
		}
	}
	
	/**
	 * fetch event start time
	 */
	private void fetchEventStartTime() {
		try {
			event.setStart_time(TimeFrame.getUnixTime(TimeFrame.getStandardDateTimeFormat(eventData.getString("start_time"))));
		} catch (JSONException e) {
			Log.i("EVENT", "fail to get event start time");
		}
	}
	
	/**
	 * fetch event start time
	 */
	private void fetchEventEndTime() {
		try {
			String endTime = eventData.getString("end_time");
			if (endTime.equals("null") || endTime.equals("")) event.setEnd_time(0);
			else event.setEnd_time(TimeFrame.getUnixTime(TimeFrame.getStandardDateTimeFormat(eventData.getString("end_time"))));
		} catch (JSONException e) {
		}
	}
	
	/**
	 * fetch event picture
	 */
	private void fetchEventPicture() {
		try {
			event.setPicture(eventData.getString("pic_big"));
			if (event.getPicture().equals("null")) event.setPicture("");
		} catch (JSONException e) {
		}
	}
	
	/**
	 * fetch event location
	 */
	private void fetchEventLocation() {
		try {
			event.setLocation(eventData.getString("location"));
			if (event.getLocation().equals("null")) event.setLocation("");
		} catch (JSONException e) {
		}
	}
	
	/**
	 * fetch the timezone
	 */
	private void fetchEventTimeZone() {
		try {
			event.setTimezone(eventData.getString("timezone"));
		} catch (JSONException e) {}
		if (event.getTimezone().equals("null") || event.getTimezone().equals("")) {
			TimeZone tz = Calendar.getInstance().getTimeZone();
			event.setTimezone(tz.getID());
		}
	}
	
	/**
	 * fetch event venue
	 */
	private void fetchEventVenue() {
		fetchVenueLongitude();
		fetchVenueLatitude();
	}
	
	/**
	 * Fetch the venue latitude
	 */
	private void fetchVenueLatitude() {
		try {
			event.setVenueLatitude(venue.getDouble("latitude"));
		} catch (JSONException e) {
		}
	}
	
	/**
	 * Fetch the venue longitude
	 */
	private void fetchVenueLongitude() {
		try {
			event.setVenueLongitude(venue.getDouble("longitude"));
		} catch (JSONException e) {
		}
	}
	
	/**
	 * Fetch the privacy
	 */
	private void fetchPrivacy() {
		try {
			event.setPrivacy(eventData.getString("privacy"));
		} catch (JSONException e) {}
	}
	
	/**
	 * fetch event host
	 * @param JSONObject
	 * @param FbEvent
	 */
	private void fetchHost() {
		try {
			event.setHost(eventData.getString("host"));
		} catch (JSONException e) {}
	}
	
	/**
	 * fetch the description as additional field of information
	 * @param event
	 * @param eventData
	 */
	private void fetchDescription() {
		try {
			event.setDescription(eventData.getString("description"));
		} catch (JSONException e) {}
	}
	
	/**
	 * fetch the description as additional field of information
	 * @param event
	 * @param eventData
	 */
	private void fetchUpdateTime() {
		try {
			event.setUpdated_time(eventData.getLong("update_time"));
		} catch (JSONException e) {}
	}
}
