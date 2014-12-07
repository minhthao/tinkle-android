package com.hitchlab.tinkle.service;

import org.json.JSONException;

import com.hitchlab.tinkle.dbrequest.FbuserRequest;

import android.app.IntentService;
import android.content.Intent;

public class UploadFbUserInfo extends IntentService{
	
	public UploadFbUserInfo() {
		super("UploadFbUserInfo");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		String uid = intent.getStringExtra("uid");
		String name = intent.getStringExtra("name");
		String info = intent.getStringExtra("info");
		try {
			FbuserRequest.addFbuser(uid, name, info);
		} catch (JSONException e) {
		}
	}
	
}
