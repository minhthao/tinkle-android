package com.hitchlab.tinkle.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class RedirectDialog {
	
	public static final int REDIRECT_TO_PROFILE = 0;
	public static final int REDIRECT_TO_MESSAGE = 1;

	public static void showRedirectDialog(Activity act, String userId, int dialogType) {
		final Activity activity = act; 
		final String uid = userId;
		final int type = dialogType;
		
		boolean hasMessage = true;
		try {activity.getPackageManager().getPackageInfo("com.facebook.katana", 0);} 
		catch (NameNotFoundException e) {hasMessage = false;}
		
		if (type == REDIRECT_TO_MESSAGE && hasMessage) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://messaging/compose/" + uid));
			activity.startActivity(intent);
			return;
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setMessage("You are about to navigate away from Frenvent. Are you sure?");
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					String uri = "fb://profile/" + uid;
					if (type == REDIRECT_TO_MESSAGE) uri = "fb://messaging/compose/" + uid;
					try {
						activity.getPackageManager().getPackageInfo("com.facebook.katana", 0);
					} catch (NameNotFoundException e) {
						uri = "https://facebook.com/" + uid;
						if (type == REDIRECT_TO_MESSAGE) uri = "https://facebook.com/messages/" + uid;
					}
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
					activity.startActivity(intent);
				}
			});
	
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();              
		}
	}
	
	/**
	 * send the add friend request
	 * @param activity
	 * @param uid
	 */
	public static void sendRequestDialog(Activity activity, String uid) {
	    String requestUri = "https://www.facebook.com/dialog/friends/?id="+
	         uid+"&app_id=166467296867875"+
	         "&redirect_uri=http://www.facebook.com";
	    WebView webView = new WebView(activity);
	    webView.getSettings().setUserAgentString("Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20100101 Firefox/10.0");
	    webView.setWebViewClient(new WebViewClient(){
	        public boolean shouldOverrideUrlLoading(WebView view, String url){
	            return false;
	        }
	    });
	    webView.loadUrl(requestUri);
	    AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
	    dialog.setView(webView);
	    dialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {

	                dialog.dismiss();
	            }
	        });
	    dialog.show();
	}

}
