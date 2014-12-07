package com.hitchlab.tinkle.supports;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.facebook.Session;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public abstract class Internet {
	
	/**
	 * Get the facebook session if it is exist
	 */
	public static Session getFacebookSession(Context context) {
		Session session = Session.getActiveSession();
		if (session == null || session.isClosed()) 
			session = Session.openActiveSessionFromCache(context);
		if (session != null && session.isOpened()) return session;
		return null;
	}
	
	/**
	 * Check if the user can connect to the internet
	 * @param context
	 * @return true if connection
	 */
	public static boolean hasActiveInternetConnection(Context context) {
	    if (isNetworkAvailable(context)) {
	        try {
	            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
	            urlc.setRequestProperty("User-Agent", "Test");
	            urlc.setRequestProperty("Connection", "close");
	            urlc.setConnectTimeout(2000); 
	            urlc.connect();
	            return (urlc.getResponseCode() == 200);
	        } catch (IOException e) {
	            Log.e("internet", "Error checking internet connection", e);
	        }
	    } else {
	        Log.d("internet", "No network available!");
	    }
	    return false;
	}
	
	/**
	 * @param context
	 * @return whether there is a available network
	 */
	private static boolean isNetworkAvailable(Context context) {
	    ConnectivityManager connectivityManager 
	         = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null;
	}
}
