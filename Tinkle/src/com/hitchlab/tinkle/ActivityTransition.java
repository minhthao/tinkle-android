package com.hitchlab.tinkle;

import android.content.Context;
import android.content.Intent;

public class ActivityTransition {

	/**
	 * Start a new activity that display the user info
	 * @param uid
	 * @param context
	 */
	public static void displayUserInfoPage(String userId, Context contxt) {	
		final Context context = contxt;
		final String uid = userId;
		
		Intent intent = new Intent(context, PersonalActivity.class);
		intent.putExtra("uid", uid);
		context.startActivity(intent);
	}
}
