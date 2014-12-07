package com.hitchlab.tinkle.eventactions;

import com.facebook.Session;
import com.hitchlab.tinkle.datasource.MyEventDataSource;
import com.hitchlab.tinkle.objects.FbEvent;
import com.hitchlab.tinkle.service.CheckSyncCalendarService;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public abstract class RsvpEventHandling {
	
	public static final String ATTENDING = "attending";
	public static final String UNSURE = "unsure";
	public static final String DECLINED = "declined";
	public static final String NOT_REPLIED = "not_replied";
	public static final String NOT_INVITED = "not_invited";
	
	private Context context;
	private FbEvent event;
	private String currentRsvpStatus;
	private String newRsvpStatus;
	
	private RsvpEvent rsvpEvent;
	private MyEventDataSource myEvents;
	
	
	public abstract void rsvpChange(boolean success);
	
	public RsvpEventHandling(Context context, FbEvent event) {
		this.context = context;
		this.event = event;
		this.currentRsvpStatus = event.getRsvp_status();	
		this.myEvents = new MyEventDataSource(context);
	}
	
	/**
	 * Change the rsvp status of event
	 * @param new rsvp status of an event
	 */
	public void changeRsvp(Session session, String myRsvp) {
		if (rsvpEvent == null) {
			rsvpEvent = new RsvpEvent(context, event.getId()) {

				@Override
				protected void onEventInvitationRsvpReplied(int rsvpRequestStatus) {
					if (rsvpRequestStatus == RsvpEvent.RSVP_OK) {
						event.setRsvp_status(newRsvpStatus);
						if (currentRsvpStatus.equals(ATTENDING)) {
							removeEventFromCalendar();
							myEvents.updateEventRsvp(event.getId(), newRsvpStatus);
						} else if (currentRsvpStatus.equals(UNSURE) || currentRsvpStatus.equals(DECLINED) || currentRsvpStatus.equals(NOT_REPLIED)) {
							if (newRsvpStatus.equals(ATTENDING)) addEventToCalendar();
							myEvents.updateEventRsvp(event.getId(), newRsvpStatus);
						} else {
							myEvents.addEvent(event);
							if (newRsvpStatus.equals(ATTENDING)) addEventToCalendar();
						}
						currentRsvpStatus = newRsvpStatus;
						rsvpChange(true);
						Toast.makeText(context, "Event rsvp status changed", Toast.LENGTH_LONG).show();
					} else if (rsvpRequestStatus == RsvpEvent.RSVP_DENIED) {
						rsvpChange(false);
						Toast.makeText(context, "Unable to change your rsvp status", Toast.LENGTH_LONG).show();
					} else if (rsvpRequestStatus == RsvpEvent.RSVP_ERROR) {
						rsvpChange(false);
						Toast.makeText(context, "You don't have permission to change the rsvp status of this event", Toast.LENGTH_LONG).show();
					}
				}
				
			};
		}
		newRsvpStatus = myRsvp;
		if (myRsvp.equals(ATTENDING)) rsvpEvent.responseAttending(session);
		else if (myRsvp.equals(UNSURE)) rsvpEvent.responseMaybe(session);
		else rsvpEvent.responseDeclined(session);
	}

	/**
	 * Add event to calendar
	 */
	public void addEventToCalendar() {
		Intent intent = new Intent(context, CheckSyncCalendarService.class);
		intent.putExtra(CheckSyncCalendarService.TYPE, CheckSyncCalendarService.TYPE_ADD_EVENT);
		intent.putExtra(CheckSyncCalendarService.EVENT_DATA, event);
		context.startService(intent);
	}
	
	/**
	 * remove an event from calendar
	 */
	public void removeEventFromCalendar() {
		Intent intent = new Intent(context, CheckSyncCalendarService.class);
		intent.putExtra(CheckSyncCalendarService.TYPE, CheckSyncCalendarService.TYPE_REMOVE_EVENT);
		intent.putExtra(CheckSyncCalendarService.EVENT_DATA, event);
		context.startService(intent);
	}
}
