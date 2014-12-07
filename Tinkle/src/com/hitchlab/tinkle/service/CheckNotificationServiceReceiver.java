package com.hitchlab.tinkle.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CheckNotificationServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent service = new Intent(context, CheckNotificationService.class);
		context.startService(service);
	}
}
