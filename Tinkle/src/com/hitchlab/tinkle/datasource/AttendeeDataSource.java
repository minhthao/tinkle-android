package com.hitchlab.tinkle.datasource;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class AttendeeDataSource {
	private SQLTablesHelper tableHelper;

	private String[] columns = {SQLTablesHelper.ATTENDEE_EID};

	public AttendeeDataSource(Context context) {
		this.tableHelper = new SQLTablesHelper(context);
	}

	/**
	 * Prepare the ContentValues of an FbEvent
	 * @param FbEvent
	 * @param timeQueried
	 */
	private ContentValues prepareContentValues(String uid, String eid) {
		ContentValues contents = new ContentValues();
		contents.put(SQLTablesHelper.ATTENDEE_UID, uid);
		contents.put(SQLTablesHelper.ATTENDEE_EID, eid);
		return contents;
	}

	/**
	 * Check if the user event pair already exist in the db. If not
	 * then add it in.
	 * @param uid
	 * @param eid
	 * @param rsvp
	 * @throws SQLException
	 */
	public void addUserEventPair(String uid, String eid) throws SQLException {
		SQLiteDatabase db = tableHelper.getWritableDatabase();
		try {
			ContentValues contents = prepareContentValues(uid, eid);
			db.insert(SQLTablesHelper.ATTENDEE_TABLE_NAME, null, contents);
		} finally {
			db.close();
		}
	}

	/**
	 * Check whether a user/event pair exist
	 * @param uid
	 * @param eid
	 * @return boolean
	 * @throws SQLException
	 */
	public boolean isUserEventPairExist(String uid, String eid) throws SQLException {
		boolean exist = false;
		SQLiteDatabase db = tableHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(SQLTablesHelper.ATTENDEE_TABLE_NAME, columns,
					SQLTablesHelper.ATTENDEE_UID + " =? AND " +  SQLTablesHelper.ATTENDEE_EID + " =?",
					new String[] {uid, eid}, 
					null, null, null);
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) exist = true;
			cursor.close();
		} finally {
			db.close();
		}
		return exist;
	}



	/**
	 * Get all the events the user is attended
	 * @param uid
	 * @return 
	 * @return ArrayList<String> eids
	 */
	public ArrayList<String> getInterestedEvents(String uid) {
		SQLiteDatabase db = tableHelper.getReadableDatabase(); 
		ArrayList<String> eids = new ArrayList<String>();
		try {
			Cursor cursor = db.query(SQLTablesHelper.ATTENDEE_TABLE_NAME, columns, 
					null, null, null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) eids.add(cursor.getString(0));
			cursor.close();
		} finally {
			db.close();
		}
		return eids;
	}

	/**
	 * remove all the events
	 * @param uid
	 */
	public void clear() throws SQLException{
		SQLiteDatabase db = tableHelper.getWritableDatabase(); 
		try {
			db.delete(SQLTablesHelper.ATTENDEE_TABLE_NAME, null, null);
		} finally {
			db.close();
		}
	}
}
