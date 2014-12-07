package com.hitchlab.tinkle.appevent;

import java.util.ArrayList;

import com.hitchlab.tinkle.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.hitchlab.tinkle.objects.Photo;
import com.hitchlab.tinkle.supports.ImageLoading;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class AlbumActivity extends Activity implements OnTouchListener{

	Context context;
	FrameLayout albumContainer;
	ImageView image;
	TextView imageIndex;
	TextView imageDescription;
	
	ArrayList<Photo> eventPhotos;
	int currentIndex;
	
	ImageLoading imageLoading;

	private static final int MIN_SWIPE_DETECTABLE_DISTANCE = 150;
	private float downX, upX;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.album_activity); 
		context = this;
		imageLoading  = new ImageLoading(context);
		initContents();
		getIntentData();
		displayImage(currentIndex);
	}
	
	@Override
	  public void onStart() {
	    super.onStart();
	    EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	  }

	  @Override
	  public void onStop() {
	    super.onStop();
	    EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	  }

	/**
	 * Init the views components
	 */
	private void initContents() {
		albumContainer = (FrameLayout) findViewById(R.id.event_photo_album_container);
		albumContainer.setOnTouchListener(this);
		imageIndex = (TextView) findViewById(R.id.event_photo_album_index);
		imageDescription = (TextView) findViewById(R.id.event_photo_album_comment);
		imageDescription.setMovementMethod(new ScrollingMovementMethod());
		image = (ImageView) findViewById(R.id.event_photo_album_image);
	}
	
	/**
	 * Get the intent data pass from previous activity
	 */
	@SuppressWarnings("unchecked")
	private void getIntentData() {
		Intent intent = getIntent();
		currentIndex = intent.getIntExtra("index", 0);
		Bundle bundle = intent.getBundleExtra("array_bundle");
		eventPhotos = (ArrayList<Photo>) bundle.getSerializable("photos_array");
		if (eventPhotos.get(0) == null)
			eventPhotos.remove(0);
	}
	
	/**
	 * Display image of a given index
	 * @param index in the array 
	 */
	private void displayImage(int index) {
		currentIndex = index;
		Photo photo = eventPhotos.get(currentIndex);
		String smallPhotoUrl = photo.getPhotoUrl();
		String originalPhotoUrl = smallPhotoUrl.replace("s.jpg", "o.jpg");
		imageLoading.displayImage(originalPhotoUrl, image);
		imageDescription.setText(photo.getComment());
		int displayIndex = index + 1;
		imageIndex.setText(displayIndex + " of " + eventPhotos.size());
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (albumContainer == v) {
			Log.i("click", "touch");
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					downX = event.getX();
					return true;
					
				case MotionEvent.ACTION_UP:
					return checkAndDoSwipeAction(event);
				
			}
		}
		return false;
	}
	
	/**
	 * Check and do the swipe action
	 * @param event
	 * @return whether the action is a valid swipe
	 */
	private boolean checkAndDoSwipeAction(MotionEvent event) {
		upX = event.getX();  
		float deltaX = downX - upX;
		// horizontal swipe detection
		if (Math.abs(deltaX) > MIN_SWIPE_DETECTABLE_DISTANCE) {
			if (deltaX < 0) swipeLeftToRight();
			else swipeRightToLeft();
			return true;
		}
		return false;
	}
	
	/**
	 * Handle the action of swipe from right to left
	 */
	private void swipeRightToLeft() {
		if (currentIndex == eventPhotos.size() - 1) displayImage(0);
		else displayImage(currentIndex + 1);
	}
	
	/**
	 * Handle the action of swipe from left to right
	 */
	private void swipeLeftToRight() {
		if (currentIndex == 0) displayImage(eventPhotos.size() - 1);
		else displayImage(currentIndex - 1);
	}
}