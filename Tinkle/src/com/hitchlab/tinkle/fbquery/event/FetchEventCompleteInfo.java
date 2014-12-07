package com.hitchlab.tinkle.fbquery.event;

import java.util.Calendar;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

import com.hitchlab.tinkle.objects.FbEventCompleteInfo;
import com.hitchlab.tinkle.supports.TimeFrame;

public class FetchEventCompleteInfo {
	JSONObject eventData;
	JSONObject venue;
	FbEventCompleteInfo event;
	
	public FetchEventCompleteInfo(JSONObject eventData) {
		this.eventData = eventData;
		this.event = new FbEventCompleteInfo();
		
		fetchEventId();
		fetchEventName();
		fetchEventStartTime();
		fetchEventEndTime();
		fetchEventPicture();
		fetchEventLocation();
		fetchEventTimeZone();
		fetchEventAttendingCount();
		fetchEventMaybeCount();
		fetchEventNotRepliedCount();
		fetchInviteOption();
		fetchHost();
		fetchDescription();
		fetchEventPrivacy();
		fetchCoverPicture();
		fetchUpdateTime();
		try {
			this.venue = eventData.getJSONObject("venue");
			if (venue != null) fetchEventVenue();
		} catch (JSONException e) {}
	}
	
	/**
	 * fetch the description as additional field of information
	 */
	private void fetchUpdateTime() {
		try {
			event.getEvent().setUpdated_time(eventData.getLong("update_time"));
		} catch (JSONException e) {}
	}
	
	/**
	 * fetch the number people attending the event 
	 * @param event
	 */
	private void fetchEventAttendingCount() {
		try {
			event.setAttendingCount(eventData.getInt("attending_count"));
		} catch (JSONException e) {}
	}

	/**
	 * fetch the number people answer maybe to the event 
	 */
	private void fetchEventMaybeCount() {
		try {
			event.setMaybeCount(eventData.getInt("unsure_count"));
		} catch (JSONException e) {}
	}

	/**
	 * fetch the number people who has not answer to the invitation to the event
	 */
	private void fetchEventNotRepliedCount(){
		try {
			event.setNotRepliedCount(eventData.getInt("not_replied_count"));
		} catch (JSONException e) {}
	}

	/**
	 * fetch the invite option to the event
	 */
	private void fetchInviteOption(){
		try {
			event.setInviteOption(eventData.getBoolean("can_invite_friends"));
		} catch (JSONException e) {}
	}

	/**
	 * fetch the host of the event
	 */
	private void fetchHost(){
		try {
			event.getEvent().setHost(eventData.getString("host"));
		} catch (JSONException e) {}
	}

	/**
	 * fetch the event's description
	 */
	private void fetchDescription(){
		try {
			event.getEvent().setDescription(eventData.getString("description"));
		} catch (JSONException e) {}
	}
	
	/**
	 * fetch the event's privacy
	 */
	private void fetchEventPrivacy(){
		try {
			event.getEvent().setPrivacy(eventData.getString("privacy"));
		} catch (JSONException e) {}
	}
	
	/**
	 * fetch the event's cover picture
	 */
	private void fetchCoverPicture(){
		try {
			JSONObject cover = eventData.getJSONObject("pic_cover");
			String url = cover.getString("source");
			if (url.equals("null")) url = "";
			event.setCoverPicture(url);
		} catch (JSONException e) {}
	}
	
	/**
	 * Get the event after fetched
	 * @return event
	 */
	public FbEventCompleteInfo getEvent() {
		return event;
	}
	
	/**
	 * fetch event id
	 */
	private void fetchEventId() {
		try {
			event.getEvent().setId(eventData.getString("eid"));
		} catch (JSONException e) {
			Log.i("EVENT", "fail to get eid");
		}
	}
	
	/**
	 * fetch event name
	 */
	private void fetchEventName() {
		try {
			event.getEvent().setName(eventData.getString("name"));
			if (event.getEvent().getName().equals("null")) event.getEvent().setName("");
		} catch (JSONException e) {
			Log.i("EVENT", "fail to get event name");
		}
	}
	
	/**
	 * fetch event start time
	 */
	private void fetchEventStartTime() {
		try {
			event.getEvent().setStart_time(TimeFrame.getUnixTime(TimeFrame.getStandardDateTimeFormat(eventData.getString("start_time"))));
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
			if (endTime.equals("null") || endTime.equals("")) event.getEvent().setEnd_time(0);
			else event.getEvent().setEnd_time(TimeFrame.getUnixTime(TimeFrame.getStandardDateTimeFormat(eventData.getString("end_time"))));
		} catch (JSONException e) {
		}
	}
	
	/**
	 * fetch event picture
	 */
	private void fetchEventPicture() {
		try {
			event.getEvent().setPicture(eventData.getString("pic_big"));
			if (event.getEvent().getPicture().equals("null")) event.getEvent().setPicture("");
		} catch (JSONException e) {
		}
	}
	
	/**
	 * fetch event location
	 */
	private void fetchEventLocation() {
		try {
			event.getEvent().setLocation(eventData.getString("location"));
			if (event.getEvent().getLocation().equals("null")) event.getEvent().setLocation("");
		} catch (JSONException e) {
		}
	}
	
	/**
	 * fetch the timezone
	 */
	private void fetchEventTimeZone() {
		try {
			event.getEvent().setTimezone(eventData.getString("timezone"));
		} catch (JSONException e) {}
		if (event.getEvent().getTimezone().equals("null") || event.getEvent().getTimezone().equals("")) {
			TimeZone tz = Calendar.getInstance().getTimeZone();
			event.getEvent().setTimezone(tz.getID());
		}
	}
	
	/**
	 * fetch event venue
	 */
	private void fetchEventVenue() {
		fetchVenueId();
		fetchVenueLongitude();
		fetchVenueLatitude();
		fetchVenueStreet();
		fetchVenueCity();
		fetchVenueState();
		fetchVenueZip();
		fetchVenueCountry();
	}
	
	/**
	 * Fetch the venue id
	 */
	private void fetchVenueId() {
		try {
			event.setVenueId(venue.getString("id"));
			if (event.getVenueId().equals("null")) event.setVenueId("");
		} catch (JSONException e) {
		}
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
	 * Fetch the venue street
	 */
	private void fetchVenueStreet() {
		try {
			event.setVenueStreet(venue.getString("street"));
			if (event.getVenueStreet().equals("null")) event.setVenueStreet("");
		} catch (JSONException e) {
		}
	}
	
	/**
	 * Fetch the venue city
	 */
	private void fetchVenueCity() {
		try {
			event.setVenueCity(venue.getString("city"));
			if (event.getVenueCity().equals("null")) event.setVenueCity("");
		} catch (JSONException e) {
		}
	}
	
	/**
	 * Fetch the venue state
	 */
	private void fetchVenueState() {
		try {
			event.setVenueState(venue.getString("state"));
			if (event.getVenueState().equals("null")) event.setVenueState("");
		} catch (JSONException e) {
		}
	}
	
	/**
	 * Fetch the venue zip
	 */
	private void fetchVenueZip() {
		try {
			event.setVenueZip(venue.getString("zip"));
			if (event.getVenueZip().equals("null")) event.setVenueZip("");
		} catch (JSONException e) {
		}
	}
	
	/**
	 * Fetch the venue country
	 */
	private void fetchVenueCountry() {
		try {
			event.setVenueCountry(venue.getString("country"));
			if (event.getVenueCountry().equals("null")) event.setVenueCountry("");
		} catch (JSONException e) {
		}
	}
}
