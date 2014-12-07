/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hitchlab.tinkle;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import bolts.AppLinks;

import java.util.Timer;
import java.util.TimerTask;

import com.hitchlab.tinkle.R;
import com.facebook.Session;
import com.facebook.Settings;
import com.hitchlab.tinkle.appevent.EventFullActivity;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;
import com.hitchlab.tinkle.service.LogOutService;
import com.hitchlab.tinkle.supports.Internet;

public class DeepLinkSplashActivity extends Activity {
	private long splashDelay = 1500;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		Settings.setPlatformCompatibilityEnabled(true);

		if (SharedPreference.containKey(this, Preference.UID)) {
			//if (SharedPreference.containKey(this, Preference.REG_ID)) {
				Uri targetUrl = AppLinks.getTargetUrl(getIntent());
				if (targetUrl != null) {
					String profileName = targetUrl.getLastPathSegment();
					//Log.e("url", targetUrl.toString());
					if (profileName != null && !profileName.equals("null")) {

						String eid = targetUrl.getQueryParameter("eid");
						String uid = targetUrl.getQueryParameter("uid");
						if (eid != null) {
							finish();
							Intent intent = new Intent(this, EventFullActivity.class);
							intent.putExtra("eid", eid);
							startActivity(intent);
						} else if (uid != null) {
							finish();
							ActivityTransition.displayUserInfoPage(uid, this);
						} else {
							Toast.makeText(this, "Link did not exist", Toast.LENGTH_SHORT).show();
							startEventbook();
						}
					} else {
						Toast.makeText(this, "Link did not exist", Toast.LENGTH_SHORT).show();
						startEventbook();
					}
//				} else {
//					Toast.makeText(this, "Link did not exist", Toast.LENGTH_SHORT).show();
//					startEventbook();
//				}
			} else {
				Intent logOutService = new Intent(this, LogOutService.class);
				this.startService(logOutService);

				Session session = Internet.getFacebookSession(this);
				if (session != null && session.isOpened())
					session.closeAndClearTokenInformation();

				//Toast.makeText(this, "Update require user re-logging", Toast.LENGTH_LONG).show();
				startLoginActivity();
			}
		} else startLoginActivity();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * Go to the eventbook main activity
	 */
	private void startEventbook() {
		finish();
		Intent eventbookActivity = new Intent(this, MainActivity.class);
		startActivity(eventbookActivity);
	}

	/**
	 * start the login activity
	 */
	private void startLoginActivity() {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				finish();
				Intent mainIntent = new Intent().setClass(DeepLinkSplashActivity.this, LoginActivity.class);
				startActivity(mainIntent);
			}
		};

		Timer timer = new Timer();
		timer.schedule(task, splashDelay);
	}
}
