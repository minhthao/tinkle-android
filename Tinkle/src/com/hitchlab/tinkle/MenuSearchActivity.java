package com.hitchlab.tinkle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.hitchlab.tinkle.R;
import com.hitchlab.tinkle.appevent.EventFullActivity;
import com.hitchlab.tinkle.datasource.FriendEventDataSource;
import com.hitchlab.tinkle.datasource.PublicEventDataSource;
import com.hitchlab.tinkle.map.Coordinate;
import com.hitchlab.tinkle.map.MapLocationManager;
import com.hitchlab.tinkle.objects.FbEvent;
import com.hitchlab.tinkle.supports.ImageLoading;
import com.hitchlab.tinkle.template.SearchListAdapter;

import eu.erikw.PullToRefreshListView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

public class MenuSearchActivity extends Activity {

	private Context context;
	private ImageLoading imageLoading;

	private View mainView;
	private EditText searchView;
	private ImageView searchRestart;
	private ListView list;
	private View shadowView;
	private SearchListAdapter adapter;

	private MapLocationManager locationManager;
	private Location lastKnown;
	private String provider;

	private ArrayList<FbEvent> allEvents; 

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_view_search);
		this.context = this; 
		this.imageLoading = new ImageLoading(context);
		this.adapter = new SearchListAdapter(context, imageLoading);

		this.locationManager = new MapLocationManager(context);
		this.provider = locationManager.getProvider();
		if (provider != null) this.lastKnown = locationManager.getLastKnownLocation();

		this.searchView = (EditText) findViewById(R.id.main_view_search_activity_search);
		this.searchRestart = (ImageView) findViewById(R.id.main_view_search_activity_search_restart);
		this.list = (ListView) findViewById(R.id.main_view_search_activity_list);
		this.shadowView = findViewById(R.id.main_view_search_activity_shadow); 

		this.allEvents = new ArrayList<FbEvent>();
		FriendEventDataSource friendEvents = new FriendEventDataSource(context);
		allEvents.addAll(friendEvents.getOngoingEvents());

		ArrayList<FbEvent> nearbyEvents = new ArrayList<FbEvent>();
		PublicEventDataSource publicEvents = new PublicEventDataSource(context);
		nearbyEvents = publicEvents.getFutureEvents();

		Set<String> eidSet = new HashSet<String>();
		for (FbEvent event : allEvents) {
			findEventDistance(event);
			eidSet.add(event.getId());
		}
		for (FbEvent event : nearbyEvents) {
			if (!eidSet.contains(event.getId())) {
				eidSet.add(event.getId());
				findEventDistance(event);
				allEvents.add(event);
			}
		}

		initList();
		initViewsListener();
	}

	/**
	 * Init the list and its item click action
	 */
	private void initList() {
		list.setAdapter(adapter);
		list.setDivider(new ColorDrawable(0x00c2c2c2));
		list.setDividerHeight(0);

		list.setOnItemClickListener(new PullToRefreshListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				if (adapter.isEnabled(position)) {
					Intent intent = new Intent(context, EventFullActivity.class);
					intent.putExtra("eid", adapter.getEventItem(position).getId());
					context.startActivity(intent);
				}
			}	
		});

		adapter.setEvents(allEvents);
		adapter.notifyDataSetChanged();
	}

	/**
	 * init other view click/modified components
	 */
	private void initViewsListener() {
		searchRestart.setVisibility(View.GONE);
		searchRestart.setOnClickListener(new ImageView.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				searchView.setText("");
			}
		});

		searchView.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				String text = searchView.getText().toString().toLowerCase(Locale.getDefault());
				if (text.equals("")) {
					adapter.setEvents(allEvents);
					adapter.notifyDataSetChanged();
					searchRestart.setVisibility(View.GONE);
				} else {
					ArrayList<FbEvent> searchedEvent = new ArrayList<FbEvent>();
					for (FbEvent event : allEvents) {
						if (event.getName().toLowerCase(Locale.getDefault()).contains(text)) 
							searchedEvent.add(event);
					}
					adapter.setEvents(searchedEvent);
					adapter.notifyDataSetChanged();

					searchRestart.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});

		this.mainView = findViewById(R.id.main_view_search_main_view);
		mainView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				int heightDiff = mainView.getRootView().getHeight() - mainView.getHeight();
				if (heightDiff > 100) shadowView.setVisibility(View.VISIBLE);
				else shadowView.setVisibility(View.GONE);
			}
		});
	}

	/**
	 * Find the event distance if possible
	 * @param FbEvent
	 */
	private void findEventDistance(FbEvent event) {
		if (provider != null) {
			Location eventLocation = new Location(provider);
			if (event.getVenueLatitude() == 0 && event.getVenueLongitude() == 0) {
				event.setDistance(-1);
			} else {
				eventLocation.setLatitude(event.getVenueLatitude());
				eventLocation.setLongitude(event.getVenueLongitude());
				double distance = lastKnown.distanceTo(eventLocation)/Coordinate.METERS_IN_MILE;
				event.setDistance(distance);
			}
		}
	}

}
