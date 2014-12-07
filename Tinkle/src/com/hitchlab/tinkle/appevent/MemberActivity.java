package com.hitchlab.tinkle.appevent;

import java.util.ArrayList;

import com.hitchlab.tinkle.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.hitchlab.tinkle.ActivityTransition;
import com.hitchlab.tinkle.objects.RecommendUser;
import com.hitchlab.tinkle.service.event.QueryEventMember;
import com.hitchlab.tinkle.template.event.MemberListAdapter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MemberActivity extends Activity{
	public static final String TYPE = "type";
	public static final int TYPE_ATTENDING = 0;
	public static final int TYPE_MAYBE = 1;
	public static final int TYPE_INVITED = 2;
	
	private Context context;
	
	private TextView header;
	private View loadingView;
	private ListView memberList;
	private MemberListAdapter adapter;
	
	private BroadcastReceiver eventMembersReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int resultType = intent.getIntExtra("result_type", -1);
			if (resultType == QueryEventMember.RESULT_INVALID) {
				Toast.makeText(context, "Invalid session. Please re-login.", Toast.LENGTH_SHORT).show();
				finish();
			} else if (resultType == QueryEventMember.RESULT_NO_INTERNET) {
				Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show();
				finish();
			} else if (resultType == QueryEventMember.RESULT_OK) {
				ArrayList<RecommendUser> members = intent.getParcelableArrayListExtra("data");
				adapter.setMembers(members);
				adapter.notifyDataSetChanged();
				loadingView.setVisibility(View.GONE);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.member_activity);
		this.context = this;
		this.header = (TextView) findViewById(R.id.member_activity_back_button);
		header.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}	
		});
		
		this.loadingView = findViewById(R.id.member_activity_loading_view);
		this.adapter = new MemberListAdapter(context);
		this.memberList = (ListView) findViewById(R.id.member_activity_member_list);
		memberList.setAdapter(adapter);
		memberList.setDivider(new ColorDrawable(0xffc2c2c2));
		memberList.setDividerHeight(1);
		memberList.setEmptyView(findViewById(R.id.member_activity_member_empty_list));
		memberList.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				ActivityTransition.displayUserInfoPage(adapter.getItemUid(position), context);
			}	
		});
		
		int memberType = getIntent().getIntExtra(TYPE, -1);
		if (memberType != -1) {
			Intent intent = new Intent(context, QueryEventMember.class);
			intent.putExtra(TYPE, memberType);
			intent.putExtra("eid", getIntent().getStringExtra("eid"));
			context.startService(intent);
			if (memberType == TYPE_ATTENDING) header.setText("JOINED");
			else if (memberType == TYPE_MAYBE) header.setText("UNSURE");
			else if (memberType == TYPE_INVITED) header.setText("INVITED");
		} else loadingView.setVisibility(View.GONE);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(eventMembersReceiver, new IntentFilter(QueryEventMember.NOTIFICATION));
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(eventMembersReceiver);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	}
}
