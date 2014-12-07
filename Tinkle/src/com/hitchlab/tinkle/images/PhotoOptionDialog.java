package com.hitchlab.tinkle.images;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;

public class PhotoOptionDialog {
	Activity activity;
	Context context;
	Uri imageUri;
	AlertDialog alertDialog;
	
	public PhotoOptionDialog(Activity activity, Context context) {
		this.activity = activity;
		this.context = context;
		preparePhotoOptionDialog();
	}
	
	
	/**
	 * @return the imageUri
	 */
	public Uri getImageUri() {
		return imageUri;
	}


	/**
	 * @param imageUri the imageUri to set
	 */
	public void setImageUri(Uri imageUri) {
		this.imageUri = imageUri;
	}


	/**
	 * Show alert dialog
	 */
	public void showDialog() {
		alertDialog.show();
	}
	
	/**
	 * Dismiss alert dialog
	 */
	public void dismissDialog() {
		alertDialog.dismiss();
	}
	
	/**
	 * Hide alert dialog
	 */
	public void hideDialog() {
		alertDialog.hide();
	}
	
	/**
	 * Cancel alert dialog
	 */
	public void cancelDialog() {
		alertDialog.cancel();
	}
	
	/**
	 * Prepare the dialog that give the option of uploading the picture
	 * directly from the gallery, or from the camera
	 */
	private void preparePhotoOptionDialog() {
		final String [] items        = new String [] {"Take from camera", "Select from Gallery"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String> (context, android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder  = new AlertDialog.Builder(context);

		builder.setTitle("Select Image");
		builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialog, int item ) { 
				if (item == 0) pickPhotoFromCamera();
				else pickPhotoFromGallery();
			}
		} );
		
		alertDialog = builder.create();
	}
	
	/**
	 * Pick photo from the camera
	 */
	private void pickPhotoFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
				"tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);

		try {
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
			intent.putExtra("return-data", true);
			activity.startActivityForResult(intent, PostImageResultConstant.PICK_FROM_CAMERA);
			cancelDialog();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Pick photo from gallery
	 */
	private void pickPhotoFromGallery() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		activity.startActivityForResult(Intent.createChooser(intent, "Select photo from"), PostImageResultConstant.PICK_FROM_FILE);
	}
}
