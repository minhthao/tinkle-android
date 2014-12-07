package com.hitchlab.tinkle.appevent;

import java.util.ArrayList;

import com.hitchlab.tinkle.R;
import com.facebook.Session;
import com.hitchlab.tinkle.LoginActivity;
import com.hitchlab.tinkle.images.PhotoOptionDialog;
import com.hitchlab.tinkle.images.UploadPhoto;
import com.hitchlab.tinkle.objects.Photo;
import com.hitchlab.tinkle.service.event.QueryEventPhoto;
import com.hitchlab.tinkle.supports.ImageLoading;
import com.hitchlab.tinkle.supports.ScreenStat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class EventPhotoFragment extends Fragment{
	private Context context;
	
	private View emptyView;
	private TableLayout tableLayout;
	private ImageLoading imageLoading;

	private ArrayList<Photo> eventPhotos;

	private PhotoOptionDialog photoOptionDialog;
	private UploadPhoto uploadPhoto;
	private String eid;
	
	private boolean canPost;

	private BroadcastReceiver eventPhotoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int resultType = intent.getIntExtra("result_type", -1);
			if (resultType == QueryEventPhoto.RESULT_INVALID) {
				Toast.makeText(context, "Invalid session. Please re-login.", Toast.LENGTH_SHORT).show();
			} else if (resultType == QueryEventPhoto.RESULT_NO_INTERNET) {
				Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show();
			} else if (resultType == QueryEventPhoto.RESULT_OK) {
				ArrayList<Photo> photos = intent.getParcelableArrayListExtra("data");
				tableLayout.removeAllViews();
				addPhotosToTable(photos);
				emptyView.setVisibility(View.GONE);
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = getActivity();
		this.imageLoading = new ImageLoading(context);
		this.photoOptionDialog = new PhotoOptionDialog(getActivity(), context);
		this.uploadPhoto = new UploadPhoto(context) {
			@Override
			public void notifyImageUploaded() {
				Toast.makeText(context, "Image uploaded", Toast.LENGTH_SHORT).show();
				eventPhotos = null;
				queryEventPhotos();
			}
		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.event_photo_fragment, container, false);
		this.emptyView = view.findViewById(R.id.event_full_photo_empty_view);
		this.tableLayout = (TableLayout)view.findViewById(R.id.event_photo_table);

		return view;
	}
	
	@Override
	public void onPause() {
		getActivity().unregisterReceiver(eventPhotoReceiver);
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(eventPhotoReceiver, new IntentFilter(QueryEventPhoto.NOTIFICATION));
	}
	
	/**
	 * Set can post
	 * @param canPost
	 */
	public void setCanPost(boolean canPost) {
		this.canPost = canPost;
	}

	/**
	 * Set the eid
	 * @param eid
	 */
	public void setEid(String eid) {
		this.eid = eid;
	}

	/**
	 * make a query to the photo and display it
	 */
	public void queryEventPhotos() {
		if (eventPhotos == null) {
			Intent intent = new Intent(context, QueryEventPhoto.class);
			intent.putExtra("eid", eid);
			context.startService(intent);
		}
	}

	/**
	 * Add photos to table
	 * @param photos
	 */
	private void addPhotosToTable(ArrayList<Photo> photos) {		
		photos.add(0, null);
		eventPhotos = photos;
		int numRow = photos.size()/3;
		int leftOverPhotos = photos.size() % 3;
		for (int i = 0; i < numRow; i++) {
			addPhotoRowToTable(photos.get(i*3), photos.get(i*3+1), photos.get(i*3+2), i);
		}
		if (leftOverPhotos == 1) {
			addPhotoRowToTable(photos.get(photos.size()-1), null, null, numRow);
		} else if (leftOverPhotos == 2) {
			addPhotoRowToTable(photos.get(photos.size()-2), photos.get(photos.size()-1), null, numRow);
		}
	}

	/**
	 * Add photo to the table, 3 per row
	 * @param photo1
	 * @param photo2
	 * @param photo3
	 */
	private void addPhotoRowToTable(Photo photo1, Photo photo2, Photo photo3, int rowIndex) {
		TableRow tableRow = new TableRow(getActivity());
		tableLayout.addView(tableRow);
		if (photo1 != null) addPhoto(tableRow, photo1, rowIndex*3);
		else addAddingOption(tableRow);
		if (photo2 != null) addPhoto(tableRow, photo2, rowIndex*3+1);
		if (photo3 != null) addPhoto(tableRow, photo3, rowIndex*3+2);
	}

	/**
	 * Add an adding cell to table
	 */
	private void addAddingOption(TableRow tableRow) {
		int size = ScreenStat.getViewWidth(context, 5, 5)/3;
		TextView addText = new TextView(getActivity());
		tableRow.addView(addText);
		addText.setWidth(size);
		addText.setHeight(size);
		addText.setBackgroundResource(R.drawable.image);
		addText.setText("new photo");
		addText.setGravity(Gravity.CENTER);
		addText.setClickable(true);
		addText.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (canPost) {
					Session session = Session.getActiveSession();
					if (!LoginActivity.hasPermission(session, LoginActivity.PUBLISH_ACTION)) 
						LoginActivity.requestPublishPermissions(getActivity(), session, LoginActivity.PERMISSION_PUBLISH_ACTION);
					else photoOptionDialog.showDialog();
				} else Toast.makeText(context, "Have to make rsvp before posting", Toast.LENGTH_SHORT).show();
			}	
		});
	}

	/**
	 * set the basic size for the image view component, and add the image in
	 * @param table row
	 * @param photo
	 */
	private void addPhoto(TableRow tableRow, Photo photo, int rowIndex) {
		int size = ScreenStat.getViewWidth(context, 5, 5)/3;
		ImageView img = new ImageView(getActivity());
		tableRow.addView(img);
		img.setAdjustViewBounds(false);
		img.setScaleType(ImageView.ScaleType.FIT_CENTER);
		img.setMinimumHeight(size);
		img.setMinimumWidth(size);
		img.setMaxHeight(size);
		img.setMaxWidth(size);
		img.setPadding(8, 8, 8, 8);
		img.setBackgroundResource(R.drawable.image);
		img.setClickable(true);
		imageLoading.displayImage(photo.getPhotoUrl(), img);

		final int index = rowIndex;
		img.setOnClickListener(new ImageView.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, AlbumActivity.class);
				intent.putExtra("index", index - 1);
				Bundle bundle = new Bundle();
				bundle.putSerializable("photos_array", eventPhotos);
				intent.putExtra("array_bundle", bundle);
				startActivity(intent);
			}

		});
	}

	/**
	 * Get the unedited post image url capture from the camera
	 */
	public Uri getUneditedPostImageUri() {
		return photoOptionDialog.getImageUri();
	}

	public void uploadImage(Bitmap photo) {
		uploadPhoto.uploadImage(photo, eid);
	}

}
