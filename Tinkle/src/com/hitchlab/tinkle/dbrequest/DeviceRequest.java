package com.hitchlab.tinkle.dbrequest;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.provider.Settings.Secure;

public class DeviceRequest {
	
	/**
	 * add the device to the db
	 * @param deviceId
	 * @param regId
	 * @param uid
	 * @throws JSONException
	 */
	public static void addDevice(String deviceId, String regId, String uid) throws JSONException {
		ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_TYPE, PublicDbConstants.REQUEST_TYPE_POST));
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA_TYPE, PublicDbConstants.REQUEST_DATA_TYPE_DEVICE));
		JSONObject obj = new JSONObject();
		obj.put(Table.DEVICE_ID, deviceId);
		obj.put(Table.DEVICE_REG_ID, regId);
		obj.put(Table.DEVICE_UID, uid);
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA, obj.toString()));
		try {
			AppHttpClient.executeHttpPost(AppHttpClient.url, postParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the device unique id
	 * @param Context
	 * @return Device Id String
	 */
	public static String getDeviceID(Context context) {
		return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	}
}
