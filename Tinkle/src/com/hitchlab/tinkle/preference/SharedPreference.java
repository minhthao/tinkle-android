package com.hitchlab.tinkle.preference;

import java.util.Set;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference {
	
	public static void updateSharedPref(Context context, String key, int value) {
		updateSharedPref(context, Preference.PREF_ID, key, value);
	}
	
	public static void updateSharedPref(Context context, String key, String value) {
		updateSharedPref(context, Preference.PREF_ID, key, value);
	}
	
	public static void updateSharedPref(Context context, String key, boolean value) {
		updateSharedPref(context, Preference.PREF_ID, key, value);
	}
	
	public static void updateSharedPref(Context context, String key, float value) {
		updateSharedPref(context, Preference.PREF_ID, key, value);
	}
	
	public static void updateSharedPref(Context context, String key, long value) {
		updateSharedPref(context, Preference.PREF_ID, key, value);
	}
	
	public static void updateSharedPref(Context context, String key, Set<String> values) {
		updateSharedPref(context, Preference.PREF_ID, key, values);
	}
	
	public static void removeKey(Context context, String key) {
		removeKey(context, Preference.PREF_ID, key);
	}
	
	public static boolean containKey(Context context, String key) {
		return containKey(context, Preference.PREF_ID, key);
	}
	
	public static int getPrefIntValue(Context context, String key) {
		return getPrefIntValue(context, Preference.PREF_ID, key);
	}
	
	public static boolean getPrefBooleanValue(Context context, String key) {
		return getPrefBooleanValue(context, Preference.PREF_ID, key);
	}
	
	public static long getPrefLongValue(Context context, String key) {
		return getPrefLongValue(context, Preference.PREF_ID, key);
	}
	
	public static float getPrefFloatValue(Context context, String key) {
		return getPrefFloatValue(context, Preference.PREF_ID, key);
	}
	
	public static String getPrefStringValue(Context context, String key) {
		return getPrefStringValue(context, Preference.PREF_ID, key);
	}
	
	public static Set<String> getPrefStringSetValue(Context context, String key) {
		return getPrefStringSetValue(context, Preference.PREF_ID, key);
	}
	
	public static void clear(Context context) {
		SharedPreferences settings = context.getSharedPreferences(Preference.PREF_ID, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();
	}
	
	
	//below is for the one with specific pref Id

	public static void updateSharedPref(Context context, String prefId, String key, int value) {
		SharedPreferences settings = context.getSharedPreferences(prefId, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	public static void updateSharedPref(Context context, String prefId, String key, String value) {
		SharedPreferences settings = context.getSharedPreferences(prefId, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static void updateSharedPref(Context context, String prefId, String key, boolean value) {
		SharedPreferences settings = context.getSharedPreferences(prefId, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public static void updateSharedPref(Context context, String prefId, String key, float value) {
		SharedPreferences settings = context.getSharedPreferences(prefId, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putFloat(key, value);
		editor.commit();
	}
	
	public static void updateSharedPref(Context context, String prefId, String key, long value) {
		SharedPreferences settings = context.getSharedPreferences(prefId, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
	public static void updateSharedPref(Context context, String prefId, String key, Set<String> values) {
		SharedPreferences settings = context.getSharedPreferences(prefId, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putStringSet(key, values);
		editor.commit();
	}
	
	public static void removeKey(Context context, String prefId, String key) {
		SharedPreferences settings = context.getSharedPreferences(prefId, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove(key);
		editor.commit();
	}
	
	public static void clear(Context context, String prefId) {
		SharedPreferences settings = context.getSharedPreferences(prefId, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();
	}
	
	public static boolean containKey(Context context, String prefId, String key) {
		SharedPreferences settings = context.getSharedPreferences(prefId, 0);
		return settings.contains(key);
	}
	
	public static int getPrefIntValue(Context context, String prefId, String key) {
		SharedPreferences settings = context.getSharedPreferences(prefId, 0);
		return settings.getInt(key, -1);
	}
	
	public static boolean getPrefBooleanValue(Context context, String prefId, String key) {
		SharedPreferences settings = context.getSharedPreferences(prefId, 0);
		return settings.getBoolean(key, false);
	}
	
	public static long getPrefLongValue(Context context, String prefId, String key) {
		SharedPreferences settings = context.getSharedPreferences(prefId, 0);
		return settings.getLong(key, -1);
	}
	
	public static float getPrefFloatValue(Context context, String prefId, String key) {
		SharedPreferences settings = context.getSharedPreferences(prefId, 0);
		return settings.getFloat(key, -1);
	}
	
	public static String getPrefStringValue(Context context, String prefId, String key) {
		SharedPreferences settings = context.getSharedPreferences(prefId, 0);
		return settings.getString(key, "");
	}
	
	public static Set<String> getPrefStringSetValue(Context context, String prefId, String key) {
		SharedPreferences settings = context.getSharedPreferences(prefId, 0);
		return settings.getStringSet(key, null);
	}
}
