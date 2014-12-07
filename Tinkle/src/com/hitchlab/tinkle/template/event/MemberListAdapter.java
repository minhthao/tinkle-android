package com.hitchlab.tinkle.template.event;

import java.util.ArrayList;

import com.hitchlab.tinkle.R;
import com.hitchlab.tinkle.objects.RecommendUser;
import com.hitchlab.tinkle.supports.ImageLoading;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MemberListAdapter extends BaseAdapter{
	private LayoutInflater layoutInflater;
	private ImageLoading imageLoading;
	private ArrayList<RecommendUser> members;
	
	public MemberListAdapter(Context context) {
		this.imageLoading = new ImageLoading(context);
		this.members = new ArrayList<RecommendUser>();
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	/**
	 * Set the data
	 * @param ArrayList<RecommendUser>
	 */
	public void setMembers(ArrayList<RecommendUser> members) {
		this.members = members;
	}
	
	@Override
	public int getCount() {
		return members.size();
	}

	@Override
	public Object getItem(int position) {
		return members.get(position);
	}
	
	/**
	 * get the selected item uid
	 * @param position
	 */
	public String getItemUid(int position) {
		return ((RecommendUser) getItem(position)).getUid();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams") 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		RecommendUser member = members.get(position);
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.member_activity_member_item, null);
			holder = new ViewHolder();
			holder.profilePic = (ImageView) convertView.findViewById(R.id.member_activity_list_item_pic);
			holder.username = (TextView) convertView.findViewById(R.id.member_activity_list_item_name);
			holder.isFriend = (ImageView) convertView.findViewById(R.id.member_activity_list_item_friendship);
			convertView.setTag(holder);
		} else holder = (ViewHolder) convertView.getTag();
		
		holder.username.setText(member.getUsername());
		imageLoading.displayImage("http://graph.facebook.com/" + member.getUid() + "/picture?width=100&height=100", holder.profilePic);
		if (member.isFriend()) holder.isFriend.setVisibility(View.VISIBLE);
		else holder.isFriend.setVisibility(View.GONE);
		
		return convertView;
	}

	//view holder
	private class ViewHolder {
		ImageView profilePic;
		TextView username;
		ImageView isFriend;
	}
}
