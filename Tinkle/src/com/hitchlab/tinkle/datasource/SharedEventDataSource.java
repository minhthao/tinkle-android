package com.hitchlab.tinkle.datasource;

import java.util.ArrayList;

import com.hitchlab.tinkle.objects.SharedEvent;
import com.hitchlab.tinkle.supports.TimeFrame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SharedEventDataSource {
	private SQLTablesHelper tableHelper;

	private String[] columns = { SQLTablesHelper.SHARED_EVENT_FROM_UID,
			SQLTablesHelper.SHARED_EVENT_FROM_NAME,
			SQLTablesHelper.SHARED_EVENT_TIME_POST,
			SQLTablesHelper.SHARED_EVENT_EID,
			SQLTablesHelper.SHARED_EVENT_NAME,
			SQLTablesHelper.SHARED_EVENT_PICTURE,
			SQLTablesHelper.SHARED_EVENT_START_TIME,
			SQLTablesHelper.SHARED_EVENT_END_TIME,
			SQLTablesHelper.SHARED_EVENT_LOCATION,
			SQLTablesHelper.SHARED_EVENT_LONGITUDE,
			SQLTablesHelper.SHARED_EVENT_LATITUDE,
			SQLTablesHelper.SHARED_EVENT_HOST};

	public SharedEventDataSource(Context context) {
		this.tableHelper = new SQLTablesHelper(context);
	}

	/**
	 * Prepare the ContentValues of an FbEvent
	 * @param SharedEvent
	 * @param timeQueried
	 */
	private ContentValues prepareContentValues(SharedEvent event) {
		ContentValues contents = new ContentValues();
		contents.put(SQLTablesHelper.SHARED_EVENT_FROM_UID, event.getFromUid());
		contents.put(SQLTablesHelper.SHARED_EVENT_FROM_NAME, event.getFromName());
		contents.put(SQLTablesHelper.SHARED_EVENT_TIME_POST, event.getTimePost());
		contents.put(SQLTablesHelper.SHARED_EVENT_EID, event.getEid());
		contents.put(SQLTablesHelper.SHARED_EVENT_NAME, event.getName());
		contents.put(SQLTablesHelper.SHARED_EVENT_PICTURE, event.getPicture());
		contents.put(SQLTablesHelper.SHARED_EVENT_START_TIME, event.getStartTime());
		contents.put(SQLTablesHelper.SHARED_EVENT_END_TIME, event.getEndTime());
		contents.put(SQLTablesHelper.SHARED_EVENT_LOCATION, event.getLocation());
		contents.put(SQLTablesHelper.SHARED_EVENT_LONGITUDE, event.getLongitude());
		contents.put(SQLTablesHelper.SHARED_EVENT_LATITUDE, event.getLatitude());
		contents.put(SQLTablesHelper.SHARED_EVENT_HOST, event.getHost());
		return contents;
	}

	/**
	 * Add an event to the db
	 * @param SharedEvent
	 * @throws SQLException
	 */
	public void addEvent(SharedEvent event) throws SQLException{
		SQLiteDatabase db = tableHelper.getWritableDatabase();
		try {
			ContentValues contents = prepareContentValues(event);
			db.insert(SQLTablesHelper.SHARED_EVENT_TABLE_NAME, null, contents);
		} finally {
			db.close();
		}
	}
	
	/**
	 * Add a list of events to the db
	 * @param ArrayList<FbEvent>
	 * @throws SQLException
	 */
	public void addEvents(ArrayList<SharedEvent> events) throws SQLException{
		SQLiteDatabase db = tableHelper.getWritableDatabase();
		try {
			for (SharedEvent event : events) {
				ContentValues contents = prepareContentValues(event);
				db.insert(SQLTablesHelper.SHARED_EVENT_TABLE_NAME, null, contents);
			}
		} finally {
			db.close();
		}
	}

	/**
	 * Check whether event with a given eid exist
	 * @param eid
	 * @return boolean
	 * @throws SQLException
	 */
	public boolean isEventExist(String eid) throws SQLException {
		boolean exist = false;
		SQLiteDatabase db = tableHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(SQLTablesHelper.SHARED_EVENT_TABLE_NAME, columns,
					SQLTablesHelper.SHARED_EVENT_EID + " =?",
					new String[] {eid}, 
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
	 * Get the event from the db with eid
	 * @param eid
	 * @return SharedEvent
	 */
	public SharedEvent getEvent(String eid) throws SQLException{
		SharedEvent event = null;
		SQLiteDatabase db = tableHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(SQLTablesHelper.SHARED_EVENT_TABLE_NAME, columns,
					SQLTablesHelper.SHARED_EVENT_EID + " =?",
					new String[] {eid}, 
					null, null, null);
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) event = cursorToEvent(cursor);
			cursor.close();
		} finally {
			db.close();
		}
		return event;
	}

	/**
	 * Get all the ongoing events
	 * @return ArrayList<SharedEvent>
	 * @throws SQLException
	 */
	public ArrayList<SharedEvent> getOngoingEvents() throws SQLException {
		ArrayList<SharedEvent> events = new ArrayList<SharedEvent>();
		SQLiteDatabase db = tableHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(SQLTablesHelper.SHARED_EVENT_TABLE_NAME, columns,
					SQLTablesHelper.SHARED_EVENT_START_TIME + " >= ?",
					new String[] {String.valueOf(TimeFrame.getUnixTime(TimeFrame.getTodayDate()))}, 
					null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				SharedEvent event = cursorToEvent(cursor);
				events.add(event);
				cursor.moveToNext();
			}
			cursor.close();
		} finally {
			db.close();
		}
		return events;
	}

	/**
	 * Get all the past events
	 * @return ArrayList<SharedEvent>
	 * @throws SQLException
	 */
	public ArrayList<SharedEvent> getPastEvents() throws SQLException {
		ArrayList<SharedEvent> events = new ArrayList<SharedEvent>();
		SQLiteDatabase db = tableHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(SQLTablesHelper.SHARED_EVENT_TABLE_NAME, columns,
					SQLTablesHelper.SHARED_EVENT_START_TIME + " < ?",
					new String[] {String.valueOf(TimeFrame.getUnixTime(TimeFrame.getTodayDate()))}, 
					null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				SharedEvent event = cursorToEvent(cursor);
				events.add(event);
				cursor.moveToNext();
			}
			cursor.close();
		} finally {
			db.close();
		}
		return events;
	}

	/**
	 * remove all the events
	 * @param uid
	 */
	public void removeEvents() throws SQLException{
		SQLiteDatabase db = tableHelper.getWritableDatabase(); 
		try {
			db.delete(SQLTablesHelper.SHARED_EVENT_TABLE_NAME, null, null);
		} finally {
			db.close();
		}
	}

	/**
	 * Remove the event with specific eid
	 * @param eid
	 */
	public void removeEvent(String eid) {
		SQLiteDatabase db = tableHelper.getWritableDatabase(); 
		try {
		db.delete(SQLTablesHelper.SHARED_EVENT_TABLE_NAME, SQLTablesHelper.SHARED_EVENT_EID + " =?",
				new String[] {eid});
		} finally {
			db.close();
		}
	}

	/**
	 * transform the cursor result to event
	 * @param cursor
	 * @return SharedEvent
	 */
	private SharedEvent cursorToEvent(Cursor cursor) {
		SharedEvent event = new SharedEvent();
		event.setFromUid(cursor.getString(0));
		event.setFromName(cursor.getString(1));
		event.setTimePost(cursor.getString(2));
		event.setEid(cursor.getString(3));
		event.setName(cursor.getString(4));
		event.setPicture(cursor.getString(5));
		event.setStartTime(cursor.getLong(6));
		event.setEndTime(cursor.getLong(7));
		event.setLocation(cursor.getString(8));
		event.setLongitude(cursor.getDouble(9));
		event.setLatitude(cursor.getDouble(10));
		event.setHost(cursor.getString(11));
		return event;
	}
}
