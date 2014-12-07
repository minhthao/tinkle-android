package com.hitchlab.tinkle.dialog;

import java.util.ArrayList;

import com.hitchlab.tinkle.R;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.hitchlab.tinkle.objects.Feed;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;
import com.hitchlab.tinkle.supports.ImageLoading;
import com.hitchlab.tinkle.template.event.CommentListViewAdapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("InflateParams") 
public abstract class CommentDialog {
	private View container;
	private Dialog commentDialog;
	private CommentListViewAdapter adapter;
	private Context context;
	private TextView postButton;
	private EditText comment;
	private ListView commentsList;
	private String feedId;
	private ImageLoading imageLoading;

	public CommentDialog(Context contxt, ImageLoading imageLoading) {
		this.context = contxt;
		this.imageLoading = imageLoading;
		commentDialog = new Dialog(context, android.R.style.Theme_Translucent);
		commentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		LayoutInflater factory = LayoutInflater.from(context);
		container = factory.inflate(R.layout.comment_list, null);
		postButton = (TextView) container.findViewById(R.id.comment_list_button);
		comment = (EditText) container.findViewById(R.id.comment_list_comment);
		commentsList = (ListView) container.findViewById(R.id.comment_list);
		postButton.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final String mComment = comment.getText().toString();
				if (!mComment.equals("")) {
					Session session = Session.getActiveSession();
					if (session == null) session = Session.openActiveSessionFromCache(context);
					if (session != null && session.isOpened()) {
						String queryString = "/" + feedId + "/comments";

						Bundle params = new Bundle();
						params.putString("message", mComment);

						Request request = new Request(session, queryString, params, HttpMethod.POST, new Request.Callback() {

							@Override
							public void onCompleted(Response response) {
								//TODO to check the completed
								Feed feed = new Feed();
								feed.setOwnerId(SharedPreference.getPrefStringValue(context, Preference.UID));
								feed.setOwnerName(SharedPreference.getPrefStringValue(context, Preference.USERNAME));
								feed.setMessage(mComment);
								addComment(feed);
//								try {
//									GraphObject graphObject = response.getGraphObject();
//									JSONObject jsonObject = graphObject.getInnerJSONObject();
//									String postId = jsonObject.getString("id");
//									if (!postId.equals("") && !postId.equals("null") && postId != null) {
//										commentAdded(postId);
//									} else Toast.makeText(context, "unable to upload comment", Toast.LENGTH_LONG).show();
//
//								} catch (Exception e) {
//									Toast.makeText(context, "error uploading comment", Toast.LENGTH_LONG).show();
//									e.printStackTrace();
//								}
							}
						});
						request.setVersion("v1.0");
						request.executeAsync();
					}
				}
			}
		});
	}

	/**
	 * set the adapter and display the comment
	 * @param comments
	 * @param feedId
	 */
	public void displayComments(ArrayList<Feed> comments, String feedId) {
		this.feedId = feedId;
		adapter= new CommentListViewAdapter(context, comments, imageLoading);
		commentsList.setAdapter(adapter);
		commentsList.setDivider(new ColorDrawable(0xffc2c2c2));
		commentsList.setDividerHeight(1);
		commentsList.setSelection(adapter.getCount() - 1);
		commentDialog.setContentView(container);
		commentDialog.show();
	}

	/**
	 * add the comment and notified that the data set changed
	 * @param comment
	 */
	public void addComment(Feed comment) {
		adapter.addFeeds(comment);
		adapter.notifyDataSetChanged();
		commentsList.smoothScrollToPosition(adapter.getCount() - 1);
	}

	public abstract void commentAdded(String id);
}
