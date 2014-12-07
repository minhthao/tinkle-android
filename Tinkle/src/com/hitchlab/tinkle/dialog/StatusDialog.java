package com.hitchlab.tinkle.dialog;

import org.json.JSONObject;

import com.hitchlab.tinkle.R;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public abstract class StatusDialog {
	private Context context;
	private EditText status;
	private TextView postButton;
	private String eventId;

	private Dialog statusDialog;

	public StatusDialog(Context context) {
		this.context = context;
		this.statusDialog = new Dialog(context, android.R.style.Theme_Translucent);
		statusDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	/**
	 * Show the dialog
	 */
	public void show(String eventId) {
		this.eventId = eventId;
		statusDialog.setContentView(prepareView());
		statusDialog.show();
	}

	/**
	 * Prepare the view to be put in the dialog
	 * @return the view
	 */
	@SuppressLint("InflateParams") 
	private View prepareView() {
		LayoutInflater factory = LayoutInflater.from(context);
		View view = factory.inflate(R.layout.post_status_dialog, null);
		status = (EditText) view.findViewById(R.id.post_status_content);
		postButton = (TextView) view.findViewById(R.id.post_status_submit);
		postButton.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				String message = status.getText().toString();
				if (message != null && !message.equals("")) {
					Session session = Session.getActiveSession();
					if (session == null) session = Session.openActiveSessionFromCache(context);
					if (session != null && session.isOpened()) {
						String queryString = "/" + eventId + "/feed";

						Bundle params = new Bundle();
						params.putString("message", message);

						Request request = new Request(session, queryString, params, HttpMethod.POST, new Request.Callback() {

							@Override
							public void onCompleted(Response response) {
								try {
									GraphObject graphObject = response.getGraphObject();
									JSONObject jsonObject = graphObject.getInnerJSONObject();
									String postId = jsonObject.getString("id");
									if (!postId.equals("") && !postId.equals("null") && postId != null) 
										onPostStatusSucceed();
									else Toast.makeText(context, "unable to upload post", Toast.LENGTH_LONG).show();

								} catch (Exception e) {
									Toast.makeText(context, "error uploading post", Toast.LENGTH_LONG).show();
									e.printStackTrace();
								}
							}
						});
						request.setVersion("v1.0");
						request.executeAsync();
						if (statusDialog.isShowing()) statusDialog.dismiss();
					}
				}
			}	
		});
		return view;
	}

	public abstract void onPostStatusSucceed();

}
