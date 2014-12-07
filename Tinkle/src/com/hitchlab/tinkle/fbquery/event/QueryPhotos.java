package com.hitchlab.tinkle.fbquery.event;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.hitchlab.tinkle.objects.Photo;

public abstract class QueryPhotos {
	public QueryPhotos() {

	}

	/**
	 * Call back function when the photos request is called and loaded
	 * @param photos
	 */
	protected abstract void onPhotosLoaded(ArrayList<Photo> photo);

	/**
	 * Query all the photos
	 */
	public void queryAllPhotos(Session session, String eid) {
		String path = "/" + eid + "/feed";
		Bundle params = new Bundle();
		params.putString("fields", "message,picture");
		Request request = new Request(session, path, params, HttpMethod.GET, new Request.Callback() {			
			@Override
			public void onCompleted(Response response) {
				ArrayList<Photo> photos = new ArrayList<Photo>();
				try { 
					GraphObject graphObject = response.getGraphObject();

					JSONObject jsonObject = graphObject.getInnerJSONObject();
					JSONArray data = jsonObject.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {
						Photo photo = new Photo();
						JSONObject photoObj = data.getJSONObject(i);
						fetchPhotoMessage(photoObj, photo);
						fetchPhotoUrl(photoObj, photo);
						if (!photo.getPhotoUrl().equals("")) photos.add(photo);
					}
				} catch (JSONException e) {
					Log.e("Photo", "Error getting photo in com.ebeam.eventbook.query.queryfeeds");
				}
				onPhotosLoaded(photos);
			}
		});
		request.setVersion("v1.0");
		request.executeAndWait();
	}


	/**
	 * fetch the photo message
	 * @param photoObj
	 * @throws JSONException
	 */
	private void fetchPhotoMessage(JSONObject photoObj, Photo photo) {
		try {
			photo.setComment(photoObj.getString("message"));
		} catch (JSONException e) {}
	}

	/**
	 * fetch the photo url
	 * @param photoObj
	 * @throws JSONException
	 */
	private void fetchPhotoUrl(JSONObject photoObj, Photo photo) {
		try {
			photo.setPhotoUrl(photoObj.getString("picture"));
		} catch (JSONException e) {}
	}

}
