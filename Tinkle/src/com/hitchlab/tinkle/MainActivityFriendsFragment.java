package com.hitchlab.tinkle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.hitchlab.tinkle.R;
import com.hitchlab.tinkle.datasource.FriendDataSource;
import com.hitchlab.tinkle.objects.Friend;
import com.hitchlab.tinkle.preference.Preference;
import com.hitchlab.tinkle.preference.SharedPreference;
import com.hitchlab.tinkle.service.RefreshFriends;
import com.hitchlab.tinkle.service.ServiceStarter;
import com.hitchlab.tinkle.template.FriendAdapter;

public class MainActivityFriendsFragment extends Fragment {
	private Context context; 
	private EditText searchView;
	private ImageView searchRestart;
	private ListView friendsList;
	private FriendAdapter adapter;
	
	private FriendDataSource friendDataSource;
	private ServiceStarter serviceStarter;

	/**
	 * receiver for my events service
	 */
	private BroadcastReceiver friendsReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			ArrayList<Friend> friends = intent.getParcelableArrayListExtra("data");
			setFriends(friends);
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.context = getActivity();
		this.friendDataSource = new FriendDataSource(context);
		this.adapter = new FriendAdapter(context);
		
		this.serviceStarter = new ServiceStarter(context) {
			@Override
			public void noInternet() {
				Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
			}

			@Override
			public void sessionClosed() {
				Toast.makeText(context, "Authentication Error. Please re-login.", Toast.LENGTH_LONG).show();
			}
		};
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_view_friends, container, false);
		this.searchView = (EditText) view.findViewById(R.id.main_view_friends_fragment_search);
		this.searchRestart = (ImageView) view.findViewById(R.id.main_view_friends_fragment_search_restart);
		this.friendsList = (ListView) view.findViewById(R.id.main_view_friends_fragment_list);
		initViewComponentActions();
		
		if (SharedPreference.getPrefBooleanValue(context, Preference.DID_FRIENDS_INIT))
			setFriends(friendDataSource.getFriends());
		else serviceStarter.updateFriends();
		
		return view;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(friendsReceiver);
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(friendsReceiver, new IntentFilter(RefreshFriends.NOTIFICATION));
	
		if (SharedPreference.getPrefBooleanValue(context, Preference.DID_FRIENDS_INIT))
			setFriends(friendDataSource.getFriends());
		else serviceStarter.updateMyEvents();
	}
	
	/**
	 * Set the friend list. But sort the list base on the number of events
	 * @param friends
	 */
	public void setFriends(ArrayList<Friend> friends) {
		Collections.sort(friends, new Comparator<Friend>() {
			@Override
			public int compare(Friend lhs, Friend rhs) {
				if (lhs.getNumOngoingEvents() == rhs.getNumOngoingEvents()) 
					return lhs.getName().compareTo(rhs.getName());
				return rhs.getNumOngoingEvents() - lhs.getNumOngoingEvents();
			}
		});
		adapter.setDataSet(friends);
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * Init the menu components
	 */
	private void initViewComponentActions() {
		searchRestart.setVisibility(View.GONE);
		searchView.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				String text = searchView.getText().toString().toLowerCase(Locale.getDefault());
				adapter.filter(text);
				if (text.equals("")) searchRestart.setVisibility(View.GONE);
				else searchRestart.setVisibility(View.VISIBLE);
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
		
		searchRestart.setOnClickListener(new ImageView.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				searchView.setText("");
			}
		});
		
		friendsList.setAdapter(adapter);
		friendsList.setDivider(new ColorDrawable(0xffc2c2c2));
		friendsList.setDividerHeight(1);
		friendsList.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				ActivityTransition.displayUserInfoPage(adapter.getUserItem(position).getUid(), getActivity());
				searchView.setText("");
			}	
		});
	}
}
