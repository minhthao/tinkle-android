package com.hitchlab.tinkle.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public abstract class SyncDialog {

	public abstract void onSyncDialogButtonSelect(boolean positive); 
	
	public SyncDialog(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Calendar Sync");
		builder.setMessage("Would your like to sync your Facebook events calendar with your device's calendar?");
		builder.setPositiveButton("Sync", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				onSyncDialogButtonSelect(true);
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				onSyncDialogButtonSelect(false);
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();              
	}
}
