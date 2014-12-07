package com.hitchlab.tinkle.eventactions;

import org.json.JSONObject;

import android.content.Context;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

public abstract class RsvpEvent {
	Context context;
	String eventId;
	
	public static final int RSVP_OK = 0;
	public static final int RSVP_DENIED = 1;
	public static final int RSVP_ERROR = 2;
	
	/**
	 * Constructor
	 * @param session
	 * @param context
	 * @param eventId
	 */
	public RsvpEvent(Context context, String eventId) {
		this.context = context;
		this.eventId = eventId;
	}
	
	/**
	 * call back function when the reservation status has been made
	 */
	protected abstract void onEventInvitationRsvpReplied(int rsvpRequestStatus);
	
	/**
	 * Response to event. Attending
	 */
	public void responseAttending(Session session) {
		String graphPath = "/" + eventId + "/attending";
		responseToEvent(session, graphPath);
	}
	
	/**
	 * Response to event. Maybe
	 */
	public void responseMaybe(Session session) {
		String graphPath = "/" + eventId + "/maybe";
		responseToEvent(session, graphPath);
	}
	
	/**
	 * Response to event. Maybe
	 */
	public void responseDeclined(Session session) {
		String graphPath = "/" + eventId + "/declined";
		responseToEvent(session, graphPath);
	}	
	
	/**
	 * Response to event with the code
	 * @param response
	 */
	private void responseToEvent(Session session, String graphPath) {
		Request request = Request.newPostRequest(session, graphPath, null, new Request.Callback() {	
			@Override
			public void onCompleted(Response response) {
				try {
					GraphObject graphObject = response.getGraphObject();
					JSONObject jsonObject = graphObject.getInnerJSONObject();

					boolean result = jsonObject.getBoolean("FACEBOOK_NON_JSON_RESULT");
					if (result == true) {
						onEventInvitationRsvpReplied(RSVP_OK);
					} else {
						onEventInvitationRsvpReplied(RSVP_DENIED);
					}
				} catch (Exception e) {
					onEventInvitationRsvpReplied(RSVP_ERROR);
					e.printStackTrace();
				}
			}
		});
		request.setVersion("v1.0");
		request.executeAsync();
	}
}
