package com.hitchlab.tinkle.map;

import com.hitchlab.tinkle.R;
import com.hitchlab.tinkle.objects.FbEvent;
import com.hitchlab.tinkle.supports.TimeFrame;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class InfoWindow {
	View infoWindow;
	TextView title;
	TextView time;
	ImageView icon;
	
	public InfoWindow(Context context) {
		this.infoWindow = (ViewGroup) View.inflate(context, R.layout.map_info_window_item, null);
		this.title = (TextView) infoWindow.findViewById(R.id.info_window_item_title);
		this.time = (TextView) infoWindow.findViewById(R.id.info_window_item_time);
		this.icon = (ImageView) infoWindow.findViewById(R.id.info_window_item_icon);
	}
	
	/**
	 * Get info content of the fb event
	 * @param event
	 * @return view display event info
	 */
	public View getInfoContents(FbEvent event, Bitmap iconBitmap) {
		title.setText(event.getName());
		time.setText(TimeFrame.getEventDisplayTime(event.getStart_time()));
		icon.setImageBitmap(iconBitmap);
		
		return infoWindow;
	}
}
