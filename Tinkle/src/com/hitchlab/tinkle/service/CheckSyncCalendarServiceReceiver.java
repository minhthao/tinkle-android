package com.hitchlab.tinkle.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CheckSyncCalendarServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent service = new Intent(context, CheckSyncCalendarService.class);
		service.putExtra(CheckSyncCalendarService.TYPE, CheckSyncCalendarService.TYPE_REGULAR_SERVICE);
		context.startService(service);
	}

}
