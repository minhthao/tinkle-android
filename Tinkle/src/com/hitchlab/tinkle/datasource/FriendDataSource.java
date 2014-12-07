package com.hitchlab.tinkle.datasource;

import java.util.ArrayList;
import java.util.HashMap;

import com.hitchlab.tinkle.objects.Friend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class FriendDataSource {
	private SQLTablesHelper tableHelper;
	
	public FriendDataSource(Context context) {
		this.tableHelper = new SQLTablesHelper(context);
	}

	private String[] columns = {SQLTablesHelper.FRIEND_UID,
			SQLTablesHelper.FRIEND_NAME,
			SQLTablesHelper.FRIEND_NUM_EVENTS};

	/**
	 * prepare the ContentValues for a Friend
	 * @param Friend
	 * @return ContentValues;
	 */
	private ContentValues prepareContentValues(Friend friend) {
		ContentValues contents = new ContentValues();
		contents.put(SQLTablesHelper.FRIEND_UID, friend.getUid());
		contents.put(SQLTablesHelper.FRIEND_NAME, friend.getName());
		contents.put(SQLTablesHelper.FRIEND_NUM_EVENTS, friend.getNumOngoingEvents());
		return contents;
	}
	
	/**
	 * add a friend to the DB
	 * @param friend
	 */
	public void addFriend(Friend friend) throws SQLException{	
		SQLiteDatabase db = tableHelper.getWritableDatabase();
		try {	
			ContentValues contents = prepareContentValues(friend);
			db.insert(SQLTablesHelper.FRIEND_TABLE_NAME, null, contents);
		} finally {
			db.close();
		}
	}
	
	/**
	 * Add a list of friends to the database
	 * @param ArrayList<Friend>
	 */
	public void addFriends(ArrayList<Friend> friends) throws SQLException{
		SQLiteDatabase db = tableHelper.getWritableDatabase();
		try {
			for (Friend friend : friends) {
				ContentValues contents = prepareContentValues(friend);
				db.insert(SQLTablesHelper.FRIEND_TABLE_NAME, null, contents);
			}
		} finally {
			db.close();
		}
	}
	
	/**
	 * Update the list of friends
	 * @param ArrayList<Friend>
	 */
	public void updateFriendsList(ArrayList<Friend> friends) throws SQLException {
		SQLiteDatabase db = tableHelper.getWritableDatabase();
		try {
			for (Friend friend : friends) {
				Cursor cursor = db.query(SQLTablesHelper.FRIEND_TABLE_NAME, columns, 
						SQLTablesHelper.FRIEND_UID + " =?",
						new String[] {friend.getUid()}, 
						null, null, null);
				cursor.moveToFirst();
				if (cursor.isAfterLast()) {
					ContentValues contents = prepareContentValues(friend);
					db.insert(SQLTablesHelper.FRIEND_TABLE_NAME, null, contents);
				}
				cursor.close();
			}
		} finally {
			db.close();
		}
	}
	
	/**
	 * Get the list of all user friends
	 * @return ArrayList<Friend>
	 */
	public ArrayList<Friend> getFriends() throws SQLException{
		SQLiteDatabase db = tableHelper.getReadableDatabase(); 
		ArrayList<Friend> friends = new ArrayList<Friend>();
		try {
			Cursor cursor = db.query(SQLTablesHelper.FRIEND_TABLE_NAME, columns, 
					null, null, null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Friend friend = cursorToFriend(cursor);
				friends.add(friend);
				cursor.moveToNext();
			}
			cursor.close();
		} finally {
			db.close();
		}
		return friends;
	}
	
	/**
	 * Get friend with specific id
	 * @return Friend
	 */
	public Friend getFriendWithUid(String uid) throws SQLException{
		SQLiteDatabase db = tableHelper.getReadableDatabase(); 
		Friend friend = null;
		try {
			Cursor cursor = db.query(SQLTablesHelper.FRIEND_TABLE_NAME, columns, 
					SQLTablesHelper.FRIEND_UID + " =?",
					new String[] {uid}, 
					null, null, null);
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) friend = cursorToFriend(cursor);
			cursor.close();
		} finally {
			db.close();
		}
		return friend;
	}
	
	/**
	 * Update the friends number of events
	 * @param friend uid
	 * @param number of events
	 */
	public void updateNumberOfEvents(String uid, int numEvents) throws SQLException{
		SQLiteDatabase db = tableHelper.getWritableDatabase(); 
		ContentValues content = new ContentValues();
		content.put(SQLTablesHelper.FRIEND_NUM_EVENTS, numEvents);
		try {
			db.update(SQLTablesHelper.FRIEND_TABLE_NAME, content, SQLTablesHelper.FRIEND_UID + " =? ", new String[] {uid});
		} finally {
			db.close();
		}
	}
	
	/**
	 * Update the friends number of events
	 * @param friend uid
	 * @param number of events
	 */
	public void updateNumberOfEvents(HashMap<String, Integer> uidToNumEvents) throws SQLException{
		SQLiteDatabase db = tableHelper.getWritableDatabase(); 
		try {
			for (String uid : uidToNumEvents.keySet()) {
				ContentValues content = new ContentValues();
				content.put(SQLTablesHelper.FRIEND_NUM_EVENTS, uidToNumEvents.get(uid));
				db.update(SQLTablesHelper.FRIEND_TABLE_NAME, content, SQLTablesHelper.FRIEND_UID + " =? ", new String[] {uid});
			}
		} finally {
			db.close();
		}
	}
	
	/**
	 * remove all friends
	 * @param timeQueried
	 */
	public void removeFriends() throws SQLException{
		SQLiteDatabase db = tableHelper.getWritableDatabase(); 
		try {
			db.delete(SQLTablesHelper.FRIEND_TABLE_NAME, null, null);
		} finally {
			db.close();
		}
	}
	
	/**
	 * remove friend with specify uid
	 * @param uid
	 */
	public void removeFriend(String uid) {
		SQLiteDatabase db = tableHelper.getWritableDatabase(); 
		try {
			db.delete(SQLTablesHelper.FRIEND_TABLE_NAME, SQLTablesHelper.FRIEND_UID + " =?", new String[] {uid});
		} finally {
			db.close();
		}
	}
	
	/**
	 * transform the cursor result to friend
	 * @param cursor
	 * @return friend
	 */
	private Friend cursorToFriend(Cursor cursor) {
		Friend friend = new Friend();
		friend.setUid(cursor.getString(0));
		friend.setName(cursor.getString(1));
		friend.setNumOngoingEvents(cursor.getInt(2));
		return friend;
	}
}
