package com.hitchlab.tinkle.supports;

import com.hitchlab.tinkle.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("InlinedApi")
public class LoadingSpinner extends Dialog{
	private ImageView iv;
	
	public LoadingSpinner(Context context, String... message) {
		super(context, R.style.TransparentProgressDialog);
        	WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        	wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        	getWindow().setAttributes(wlmp);
		setTitle(null);
		setCancelable(false);
		setOnCancelListener(null);
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		iv = new ImageView(context);
		iv.setImageResource(R.drawable.spinner_icon);
		layout.addView(iv, params);
		
		if (message != null && message.length >0) {
			TextView msg = new TextView(context);
			msg.setText(message[0]);
			layout.addView(msg, params);
		}
		addContentView(layout, params);
	}
		
	@Override
	public void show() {
		super.show();
		RotateAnimation anim = new RotateAnimation(0.0f, 360.0f , Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
		anim.setInterpolator(new LinearInterpolator());
		anim.setRepeatCount(Animation.INFINITE);
		anim.setDuration(3000);
		iv.setAnimation(anim);
		iv.startAnimation(anim);
	}
	
	
}
