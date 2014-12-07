package com.hitchlab.tinkle.images;

import java.io.ByteArrayOutputStream;

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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public abstract class UploadPhoto {
	Context context;
	
	ImageView uploadImage;
	EditText imageDescription;
	TextView uploadImageButton;
	
	Bitmap mPhoto;
	String mEventId;
	
	Dialog uploadPhotoDialog;
	
	public UploadPhoto(Context context) {
		this.context = context;
		uploadPhotoDialog = new Dialog(context, android.R.style.Theme_Translucent);
		uploadPhotoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	/**
	 * Call to notify that the imaged has successfully uploaded
	 */
	public abstract void notifyImageUploaded();

	/**
	 * Upload a photo image to the facebook event wall
	 * @param bitmap photo
	 * @param event id
	 */
	@SuppressLint("InflateParams") 
	public void uploadImage(Bitmap photo, String eventId) {
		mPhoto = photo;
		mEventId = eventId;
		LayoutInflater factory = LayoutInflater.from(context);
		View uploadPhotoViewContainer = factory.inflate(R.layout.upload_photo, null);
		uploadImage = (ImageView) uploadPhotoViewContainer.findViewById(R.id.upload_photo_image);
		uploadImage.setImageBitmap(photo);
		imageDescription = (EditText) uploadPhotoViewContainer.findViewById(R.id.upload_photo_description);
		uploadImageButton = (TextView) uploadPhotoViewContainer.findViewById(R.id.upload_photo_button);
		setUploadButtonClickListener();
		uploadPhotoDialog.setContentView(uploadPhotoViewContainer);
		uploadPhotoDialog.show();
	}
	
	/**
	 * set the listener for the upload image button
	 */
	private void setUploadButtonClickListener() {
		uploadImageButton.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Session session = Session.getActiveSession();
				if (session == null) session = Session.openActiveSessionFromCache(context);
				if (session != null && session.isOpened()) {
					String queryString = "/" + mEventId + "/photos";
					Bundle params = new Bundle();				
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					mPhoto.compress(Bitmap.CompressFormat.PNG, 100, stream);
					byte[] byteArray = stream.toByteArray();
					params.putByteArray("source", byteArray);
					params.putString("message", imageDescription.getText().toString());
					
					Request request = new Request(session, queryString, params, HttpMethod.POST, new Request.Callback() {
						@Override
						public void onCompleted(Response response) {
							try {
								GraphObject graphObject = response.getGraphObject();
								JSONObject jsonObject = graphObject.getInnerJSONObject();
								String postId = jsonObject.getString("post_id");
								if (postId != null && !postId.equals("") && !postId.equals("null")) notifyImageUploaded();
								else Toast.makeText(context, "unable to upload photo", Toast.LENGTH_LONG).show();
							} catch (Exception e) {
								Toast.makeText(context, "error uploading photo", Toast.LENGTH_LONG).show();
							}
						}
					});
					request.setVersion("v1.0");
					request.executeAsync();
				}
				if (uploadPhotoDialog.isShowing()) uploadPhotoDialog.dismiss();
			}	
		});
	}
}
