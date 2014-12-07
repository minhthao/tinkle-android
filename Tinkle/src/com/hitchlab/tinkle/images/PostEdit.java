package com.hitchlab.tinkle.images;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

public class PostEdit {
	
	public static final int IMAGE_MAX_WIDTH = 1080;
	
	/**
	 * Rotate the bitmap to correct orientation
	 * @param originalBitmap
	 * @param filePath
	 * @return rotated and scaled bitmap
	 */
	public static Bitmap getPostPhotoBitmap(Bitmap originalBitmap, String filePath) {
		Matrix matrix = new Matrix();
		ExifInterface exifReader;
		Bitmap bitmap = getScaledPhotoBitmap(originalBitmap, filePath);
		try {
			exifReader = new ExifInterface(filePath);
			int orientation = exifReader.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
			int exifInDegree = Exif.exifToDegrees(orientation);
			if (exifInDegree != 0) {
				matrix.postRotate(exifInDegree);
				return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			}
		} catch (IOException e) {
			Log.i("Exif", "no exif information at com.ebeam.eventbook.images.PostEdit");
		}
		return bitmap;
	}
	
	/**
	 * get the edited photo bitmap
	 * @param originalBitmap
	 * @param filePath
	 * @return scaled bitmap
	 */
	public static Bitmap getScaledPhotoBitmap(Bitmap originalBitmap, String filePath) {
		if (originalBitmap.getWidth() <= IMAGE_MAX_WIDTH) return originalBitmap;
		else {
			//scale the bitmap if it is too big. also make it easy to upload as well
			int targetHeight = (int) (originalBitmap.getHeight() * IMAGE_MAX_WIDTH / originalBitmap.getWidth());
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, IMAGE_MAX_WIDTH, targetHeight, true);
			return scaledBitmap;
		}
	}
}
