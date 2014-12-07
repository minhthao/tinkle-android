package com.hitchlab.tinkle.dbrequest;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class FbuserRequest {
	
	/**
	 * add the fbuser to the db
	 * @param uid
	 * @param name
	 * @param info
	 * @throws JSONException
	 */
	public static void addFbuser(String uid, String name, String info) throws JSONException {
		ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_TYPE, PublicDbConstants.REQUEST_TYPE_POST));
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA_TYPE, PublicDbConstants.REQUEST_DATA_TYPE_FBUSER));
		JSONObject obj = new JSONObject();
		obj.put(Table.FBUSER_UID, uid);
		obj.put(Table.FBUSER_NAME, name);
		obj.put(Table.FBUSER_INFO, info);
		postParams.add(new BasicNameValuePair(PublicDbConstants.REQUEST_DATA, obj.toString()));
		try {
			AppHttpClient.executeHttpPost(AppHttpClient.url, postParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
