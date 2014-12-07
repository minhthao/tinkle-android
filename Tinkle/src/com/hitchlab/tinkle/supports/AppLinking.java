package com.hitchlab.tinkle.supports;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.OpenGraphAction;
import com.facebook.model.OpenGraphObject;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.FeedDialogBuilder;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.hitchlab.tinkle.datasource.ShareDataSource;
import com.hitchlab.tinkle.dbrequest.Table;
import com.hitchlab.tinkle.objects.FbEvent;
import com.hitchlab.tinkle.objects.RecommendUser;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public abstract class AppLinking {

	public static final String APP_LINK_HOST = "http://FreventServer-6kvbkxqtmm.elasticbeanstalk.com/AppLinkHost";

	/**
	 * Recommend user using message dialog
	 * @param activity
	 * @param uid
	 * @param uiHelper
	 * @param request code
	 */
	public static void recommendUser(Activity activity, String uid, UiLifecycleHelper uiHelper, int code) {				
		OpenGraphAction action = GraphObject.Factory.create(OpenGraphAction.class);
		action.setType("aneventbook:recommend");
		action.setProperty("scrape", true);
		action.setProperty("person", APP_LINK_HOST + "?" + Table.FBUSER_UID + "=" + uid);

		FacebookDialog.OpenGraphMessageDialogBuilder messageDialogBuilder = new FacebookDialog.OpenGraphMessageDialogBuilder(activity, action, "person");
		if (code != -1) messageDialogBuilder.setRequestCode(code);
		FacebookDialog messageDialog = messageDialogBuilder.build();
		uiHelper.trackPendingDialogCall(messageDialog.present());
	}

	/**
	 * Recommend user using message dialog
	 * @param activity
	 * @param RecommendUser
	 * @param uiHelper
	 * @param request code
	 */
	public static void recommendUserFallback(Activity activity, RecommendUser recUser, UiLifecycleHelper uiHelper, int code) {				
		OpenGraphObject personObj = OpenGraphObject.Factory.createForPost("aneventbook:person");
		personObj.setTitle(recUser.getUsername());
		personObj.setProperty("image", "http://graph.facebook.com/" + recUser.getUid() + "/picture?width=150&height=150");
		personObj.setUrl("https://facebook.com/" + recUser.getUid());

		OpenGraphAction action = GraphObject.Factory.create(OpenGraphAction.class);
		action.setType("aneventbook:recommend");
		action.setProperty("person", personObj);

		FacebookDialog.OpenGraphMessageDialogBuilder messageDialogBuilder = new FacebookDialog.OpenGraphMessageDialogBuilder(activity, action, "person");
		if (code != -1) messageDialogBuilder.setRequestCode(code);
		FacebookDialog messageDialog = messageDialogBuilder.build();
		uiHelper.trackPendingDialogCall(messageDialog.present());
	}

	/**
	 * share event through message
	 * @param activity
	 * @param event
	 * @param uiHelper
	 * @param code
	 */
	public static void shareEventPrivate(Activity activity, FbEvent event, UiLifecycleHelper uiHelper, int code) {
		OpenGraphAction action = GraphObject.Factory.create(OpenGraphAction.class);
		action.setType("aneventbook:share");
		action.setProperty("scrape", true);
		action.setProperty("event", APP_LINK_HOST + "?meid=" + event.getId());

		FacebookDialog.OpenGraphMessageDialogBuilder messageDialogBuilder = new FacebookDialog.OpenGraphMessageDialogBuilder(activity, action, "event");
		if (code != -1) messageDialogBuilder.setRequestCode(code);
		FacebookDialog messageDialog = messageDialogBuilder.build();
		uiHelper.trackPendingDialogCall(messageDialog.present());
	}

	/**
	 * share event through message
	 * @param activity
	 * @param event
	 * @param uiHelper
	 * @param code
	 */
	public static void shareEventPrivateFallback(Activity activity, FbEvent event, UiLifecycleHelper uiHelper, int code) {
		String description = event.getLocation();
		if (description == null || description.isEmpty() || description.toLowerCase().equals("null"))
			description = event.getDescription();
		if (description != null && description.toLowerCase().equals("null")) description = "";

		OpenGraphObject eventObj = OpenGraphObject.Factory.createForPost("aneventbook:event");
		eventObj.setTitle(event.getName());
		eventObj.setProperty("image", event.getPicture());
		eventObj.setUrl("https://facebook.com/events/" + event.getId());
		eventObj.setDescription(TimeFrame.getEventDisplayTime(event.getStart_time()));

		OpenGraphAction action = GraphObject.Factory.create(OpenGraphAction.class);
		action.setType("aneventbook:share");
		action.setProperty("event", eventObj);

		FacebookDialog.OpenGraphMessageDialogBuilder messageDialogBuilder = new FacebookDialog.OpenGraphMessageDialogBuilder(activity, action, "event");
		if (code != -1) messageDialogBuilder.setRequestCode(code);
		FacebookDialog messageDialog = messageDialogBuilder.build();
		uiHelper.trackPendingDialogCall(messageDialog.present());

	}


	/**
	 * Share the event on public
	 * @param activity
	 * @param event
	 * @param uiHelper
	 */
	public static void shareEventPublicly(Activity activity, FbEvent event, UiLifecycleHelper uiHelper, int code) {
		final Activity mActivity = activity;
		final String eid = event.getId();
		String description = event.getLocation();
		if (description == null || description.isEmpty() || description.toLowerCase().equals("null"))
			description = event.getDescription();
		if (description != null && description.toLowerCase().equals("null")) description = "";

		if (FacebookDialog.canPresentOpenGraphActionDialog(activity, FacebookDialog.OpenGraphActionDialogFeature.OG_ACTION_DIALOG)) {
			OpenGraphAction action = GraphObject.Factory.create(OpenGraphAction.class);
			action.setType("aneventbook:share");
			action.setProperty("scrape", true);
			action.setExplicitlyShared(true);
			action.setProperty("event", APP_LINK_HOST + "?" + Table.EVENT_EID + "=" + event.getId());

			FacebookDialog.OpenGraphActionDialogBuilder shareDialogBuilder = new FacebookDialog.OpenGraphActionDialogBuilder(activity, action, "event");
			if (code != -1) shareDialogBuilder.setRequestCode(code);
			FacebookDialog shareDialog = shareDialogBuilder.build();

			uiHelper.trackPendingDialogCall(shareDialog.present());
		} else {
			Bundle params = new Bundle();
			params.putString("name", event.getName());
			params.putString("caption", TimeFrame.getEventDisplayTime(event.getStart_time()));
			params.putString("description", event.getLocation());
			params.putString("link", "https://facebook.com/event" + event.getId());
			params.putString("picture", event.getPicture());

			// Invoke the dialog
			FeedDialogBuilder feedDialogBuilder = new WebDialog.FeedDialogBuilder(activity, Session.getActiveSession(), params);
			feedDialogBuilder.setOnCompleteListener(new OnCompleteListener() {

				@Override
				public void onComplete(Bundle values, FacebookException error) {
					if (error == null) {
						final String postId = values.getString("post_id");
						if (postId != null) {
							ShareDataSource shareDataSource = new ShareDataSource(mActivity);
							shareDataSource.addEventShared(eid);
							Toast.makeText(mActivity,"Event shared successfully.", Toast.LENGTH_SHORT).show();
						}
					} else if (!(error instanceof FacebookOperationCanceledException)) 
						Toast.makeText(mActivity, "Error sharing event.", Toast.LENGTH_SHORT).show();
				}

			});

			WebDialog feedDialog = feedDialogBuilder.build();
			feedDialog.show();
		}
	}

	/**
	 * Share the event on public
	 * @param activity
	 * @param event
	 * @param uiHelper
	 */
	public static void shareEventPubliclyFallback(Activity activity, FbEvent event, UiLifecycleHelper uiHelper, int code) {
		final Activity mActivity = activity;
		final String eid = event.getId();
		String description = event.getLocation();
		if (description == null || description.isEmpty() || description.toLowerCase().equals("null"))
			description = event.getDescription();
		if (description != null && description.toLowerCase().equals("null")) description = "";

		if (FacebookDialog.canPresentOpenGraphActionDialog(activity, FacebookDialog.OpenGraphActionDialogFeature.OG_ACTION_DIALOG)) {
			OpenGraphObject eventObj = OpenGraphObject.Factory.createForPost("aneventbook:event");
			eventObj.setTitle(event.getName());
			eventObj.setProperty("image", event.getPicture());
			eventObj.setUrl("https://facebook.com/events/" + event.getId());
			eventObj.setDescription(TimeFrame.getEventDisplayTime(event.getStart_time()));

			OpenGraphAction action = GraphObject.Factory.create(OpenGraphAction.class);
			action.setType("aneventbook:share");
			action.setExplicitlyShared(true);
			action.setProperty("event", eventObj);

			FacebookDialog.OpenGraphActionDialogBuilder shareDialogBuilder = new FacebookDialog.OpenGraphActionDialogBuilder(activity, action, "event");
			if (code != -1) shareDialogBuilder.setRequestCode(code);
			FacebookDialog shareDialog = shareDialogBuilder.build();

			uiHelper.trackPendingDialogCall(shareDialog.present());
		} else {
			Bundle params = new Bundle();
			params.putString("name", event.getName());
			params.putString("caption", TimeFrame.getEventDisplayTime(event.getStart_time()));
			params.putString("description", event.getLocation());
			params.putString("link", "https://facebook.com/event" + event.getId());
			params.putString("picture", event.getPicture());

			// Invoke the dialog
			FeedDialogBuilder feedDialogBuilder = new WebDialog.FeedDialogBuilder(activity, Session.getActiveSession(), params);
			feedDialogBuilder.setOnCompleteListener(new OnCompleteListener() {

				@Override
				public void onComplete(Bundle values, FacebookException error) {
					if (error == null) {
						final String postId = values.getString("post_id");
						if (postId != null) {
							ShareDataSource shareDataSource = new ShareDataSource(mActivity);
							shareDataSource.addEventShared(eid);
							Toast.makeText(mActivity,"Event shared successfully.", Toast.LENGTH_SHORT).show();
						}
					} else if (!(error instanceof FacebookOperationCanceledException)) 
						Toast.makeText(mActivity, "Error sharing event.", Toast.LENGTH_SHORT).show();
				}

			});

			WebDialog feedDialog = feedDialogBuilder.build();
			feedDialog.show();
		}
	}


	/**
	 * get the session
	 * @param activity
	 * @return Session
	 */
	public static Session getSession(Activity activity) {
		Session session = Session.getActiveSession();
		if (session == null) session = Session.openActiveSessionFromCache(activity);
		return session;
	}


}
