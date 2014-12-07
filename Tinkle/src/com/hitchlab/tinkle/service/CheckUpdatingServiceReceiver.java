package com.hitchlab.tinkle.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CheckUpdatingServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent service = new Intent(context, CheckUpdatingService.class);
		context.startService(service);
	}

}
