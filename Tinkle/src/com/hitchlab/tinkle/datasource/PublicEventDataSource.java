package com.hitchlab.tinkle.datasource;

import java.util.ArrayList;

import com.hitchlab.tinkle.objects.FbEvent;
import com.hitchlab.tinkle.supports.TimeFrame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class PublicEventDataSource {
	private SQLTablesHelper tableHelper;
	
	private String[] columns = {SQLTablesHelper.PUBLIC_EVENT_EID,
			SQLTablesHelper.PUBLIC_EVENT_NAME,
			SQLTablesHelper.PUBLIC_EVENT_PICTURE,
			SQLTablesHelper.PUBLIC_EVENT_START_TIME, 
			SQLTablesHelper.PUBLIC_EVENT_END_TIME,
			SQLTablesHelper.PUBLIC_EVENT_LOCATION,
			SQLTablesHelper.PUBLIC_EVENT_LONGITUDE,
			SQLTablesHelper.PUBLIC_EVENT_LATITUDE,
			SQLTablesHelper.PUBLIC_EVENT_HOST};
	
	public PublicEventDataSource(Context context) {
		this.tableHelper = new SQLTablesHelper(context);
	}
	
	/**
	 * Prepare the ContentValues of an FbEvent
	 * @param FbEvent
	 */
	private ContentValues prepareContentValues(FbEvent event) {
		ContentValues contents = new ContentValues();
		contents.put(SQLTablesHelper.PUBLIC_EVENT_EID, event.getId());
		contents.put(SQLTablesHelper.PUBLIC_EVENT_NAME, event.getName());
		contents.put(SQLTablesHelper.PUBLIC_EVENT_PICTURE, event.getPicture());
		contents.put(SQLTablesHelper.PUBLIC_EVENT_START_TIME, event.getStart_time());
		contents.put(SQLTablesHelper.PUBLIC_EVENT_END_TIME, event.getEnd_time());
		contents.put(SQLTablesHelper.PUBLIC_EVENT_LOCATION, event.getLocation());
		contents.put(SQLTablesHelper.PUBLIC_EVENT_LONGITUDE, event.getVenueLongitude());
		contents.put(SQLTablesHelper.PUBLIC_EVENT_LATITUDE, event.getVenueLatitude());
		contents.put(SQLTablesHelper.PUBLIC_EVENT_HOST, event.getHost());
		return contents;
	}
	
	/**
	 * Add an event to the db
	 * @param fbevent
	 */
	public void addEvent(FbEvent event) throws SQLException{
		SQLiteDatabase db = tableHelper.getWritableDatabase();
		try {
			ContentValues contents = prepareContentValues(event);
			db.insert(SQLTablesHelper.PUBLIC_EVENT_TABLE_NAME, null, contents);
		} finally {
			db.close();
		}
	}
	
	/** Add a list of events to the db
	 * @param ArrayList<FbEvent>
	 * @throws SQLException
	 */
	public void addEvents(ArrayList<FbEvent> events) throws SQLException{
		SQLiteDatabase db = tableHelper.getWritableDatabase();
		try {
			for (FbEvent event : events) {
				ContentValues contents = prepareContentValues(event);
				db.insert(SQLTablesHelper.PUBLIC_EVENT_TABLE_NAME, null, contents);
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
			Cursor cursor = db.query(SQLTablesHelper.PUBLIC_EVENT_TABLE_NAME, columns,
					SQLTablesHelper.PUBLIC_EVENT_EID + " =?",
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
	 * get all the future events
	 * @return ArrayList<FbEvent>
	 * @throws SQLException
	 */
	public ArrayList<FbEvent> getFutureEvents() {
		ArrayList<FbEvent> events = new ArrayList<FbEvent>();
		SQLiteDatabase db = tableHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(SQLTablesHelper.PUBLIC_EVENT_TABLE_NAME, columns, 
					SQLTablesHelper.PUBLIC_EVENT_START_TIME + " >= ?",
					new String[] {String.valueOf(TimeFrame.getUnixTime(TimeFrame.getTodayDate()))},
					null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				FbEvent event = cursorToEvent(cursor);
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
	 * Get all the future events
	 * @return ArrayList<FbEvent>
	 * @throws SQLException
	 */
	public ArrayList<FbEvent> getFutureEvents(double minLng, double maxLng, double minLat, double maxLat) throws SQLException {
		ArrayList<FbEvent> events = new ArrayList<FbEvent>();
		SQLiteDatabase db = tableHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(SQLTablesHelper.PUBLIC_EVENT_TABLE_NAME, columns, 
					SQLTablesHelper.PUBLIC_EVENT_START_TIME + " >= ? AND "
					+ SQLTablesHelper.PUBLIC_EVENT_LONGITUDE + " > ?  AND "
					+ SQLTablesHelper.PUBLIC_EVENT_LONGITUDE + " < ? AND "
					+ SQLTablesHelper.PUBLIC_EVENT_LATITUDE + " > ? AND "
					+ SQLTablesHelper.PUBLIC_EVENT_LATITUDE + " < ?", 
					new String[] {
						String.valueOf(TimeFrame.getUnixTime(TimeFrame.getTodayDate())),
						String.valueOf(minLng),
						String.valueOf(maxLng),
						String.valueOf(minLat),
						String.valueOf(maxLat)}, 
					null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				FbEvent event = cursorToEvent(cursor);
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
			db.delete(SQLTablesHelper.PUBLIC_EVENT_TABLE_NAME, null, null);
		} finally {
			db.close();
		}
	}

	
	/**
	 * Replace the events
	 * @param uid
	 */

	/**
	 * transform the cursor result to event
	 * @param cursor
	 * @return event
	 */
	private FbEvent cursorToEvent(Cursor cursor) {
		FbEvent event = new FbEvent();
		event.setId(cursor.getString(0));
		event.setName(cursor.getString(1));
		event.setPicture(cursor.getString(2));
		event.setStart_time(cursor.getLong(3));
		event.setEnd_time(cursor.getLong(4));
		event.setLocation(cursor.getString(5));
		event.setVenueLongitude(cursor.getDouble(6));
		event.setVenueLatitude(cursor.getDouble(7));
		event.setHost(cursor.getString(8));
		return event;
	}
	
}
