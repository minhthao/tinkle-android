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
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import com.hitchlab.tinkle.R;
import com.facebook.Settings;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;

public class SplashActivity extends Activity {
	private long splashDelay = 1500;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		Settings.setPlatformCompatibilityEnabled(true);

		if (SharedPreference.containKey(this, Preference.UID)) {
			startEventbook();
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
				Intent mainIntent = new Intent().setClass(SplashActivity.this, LoginActivity.class);
				startActivity(mainIntent);
			}
		};

		Timer timer = new Timer();
		timer.schedule(task, splashDelay);
	}
}