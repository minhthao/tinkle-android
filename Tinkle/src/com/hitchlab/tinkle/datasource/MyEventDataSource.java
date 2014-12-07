package com.hitchlab.tinkle.datasource;

import java.util.ArrayList;

import com.hitchlab.tinkle.objects.Attendee;
import com.hitchlab.tinkle.objects.FbEvent;
import com.hitchlab.tinkle.supports.TimeFrame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MyEventDataSource {
	private SQLTablesHelper tableHelper;

	private String[] columns = {SQLTablesHelper.MY_EVENT_EID,
			SQLTablesHelper.MY_EVENT_NAME,
			SQLTablesHelper.MY_EVENT_PICTURE,
			SQLTablesHelper.MY_EVENT_RSVP,
			SQLTablesHelper.MY_EVENT_START_TIME,
			SQLTablesHelper.MY_EVENT_END_TIME,
			SQLTablesHelper.MY_EVENT_LOCATION,
			SQLTablesHelper.MY_EVENT_LONGITUDE,
			SQLTablesHelper.MY_EVENT_LATITUDE,
			SQLTablesHelper.MY_EVENT_FRIENDS_ATTENDING};

	public MyEventDataSource(Context context) {
		this.tableHelper = new SQLTablesHelper(context);
	}

	/**
	 * Prepare the ContentValues of an FbEvent
	 * @param FbEvent
	 * @return ContentValues
	 */
	private ContentValues prepareContentValues(FbEvent event) {
		ContentValues contents = new ContentValues();
		contents.put(SQLTablesHelper.MY_EVENT_EID, event.getId());
		contents.put(SQLTablesHelper.MY_EVENT_NAME, event.getName());
		contents.put(SQLTablesHelper.MY_EVENT_PICTURE, event.getPicture());
		contents.put(SQLTablesHelper.MY_EVENT_RSVP, event.getRsvp_status());
		contents.put(SQLTablesHelper.MY_EVENT_START_TIME, event.getStart_time());
		contents.put(SQLTablesHelper.MY_EVENT_END_TIME, event.getEnd_time());
		contents.put(SQLTablesHelper.MY_EVENT_LOCATION, event.getLocation());
		contents.put(SQLTablesHelper.MY_EVENT_LONGITUDE, event.getVenueLongitude());
		contents.put(SQLTablesHelper.MY_EVENT_LATITUDE, event.getVenueLatitude());
		contents.put(SQLTablesHelper.MY_EVENT_FRIENDS_ATTENDING, Attendee.getAttendeesStrFromList(event.getFriendsAttending()));
		return contents;
	}

	/**
	 * Add an event to the db
	 * @param fbevent
	 * @throws SQLException
	 */
	public void addEvent(FbEvent event) throws SQLException{
		SQLiteDatabase db = tableHelper.getWritableDatabase();
		try {
			ContentValues contents = prepareContentValues(event);
			db.insert(SQLTablesHelper.MY_EVENT_TABLE_NAME, null, contents);
		} finally {
			db.close();
		}
	}
	
	/**
	 * Add a list of events to the db
	 * @param ArrayList<FbEvent>
	 * @throws SQLException
	 */
	public void addEvents(ArrayList<FbEvent> events) throws SQLException{
		SQLiteDatabase db = tableHelper.getWritableDatabase();
		try {
			for (FbEvent event : events) {
				ContentValues contents = prepareContentValues(event);
				db.insert(SQLTablesHelper.MY_EVENT_TABLE_NAME, null, contents);
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
			Cursor cursor = db.query(SQLTablesHelper.MY_EVENT_TABLE_NAME, columns,
					SQLTablesHelper.MY_EVENT_EID + " =?",
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
	 * @return FbEvent
	 */
	public FbEvent getEvent(String eid) throws SQLException{
		FbEvent event = null;
		SQLiteDatabase db = tableHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(SQLTablesHelper.MY_EVENT_TABLE_NAME, columns,
					SQLTablesHelper.MY_EVENT_EID + " =?",
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
	 * @return ArrayList<FbEvent>
	 * @throws SQLException
	 */
	public ArrayList<FbEvent> getOngoingEvents() throws SQLException {
		ArrayList<FbEvent> events = new ArrayList<FbEvent>();
		SQLiteDatabase db = tableHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(SQLTablesHelper.MY_EVENT_TABLE_NAME, columns,
					SQLTablesHelper.MY_EVENT_START_TIME + " >= ?",
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
	 * Get all the past events
	 * @return ArrayList<FbEvent>
	 * @throws SQLException
	 */
	public ArrayList<FbEvent> getPastEvents() throws SQLException {
		ArrayList<FbEvent> events = new ArrayList<FbEvent>();
		SQLiteDatabase db = tableHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(SQLTablesHelper.MY_EVENT_TABLE_NAME, columns,
					SQLTablesHelper.MY_EVENT_START_TIME + " < ?",
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
	 * remove all the events
	 * @param uid
	 */
	public void removeEvents() throws SQLException{
		SQLiteDatabase db = tableHelper.getWritableDatabase(); 
		try {
			db.delete(SQLTablesHelper.MY_EVENT_TABLE_NAME, null, null);
		} finally {
			db.close();
		}
	}

	/**
	 * Update the event rsvp status
	 * @param eid
	 * @param new rsvp status
	 */
	public void updateEventRsvp(String eid, String rsvp) throws SQLException{
		SQLiteDatabase db = tableHelper.getWritableDatabase(); 
		try {
			ContentValues content = new ContentValues();
			content.put(SQLTablesHelper.MY_EVENT_RSVP, rsvp);
			db.update(SQLTablesHelper.MY_EVENT_TABLE_NAME, content, 
					SQLTablesHelper.MY_EVENT_EID + " =?",
					new String[] {eid});
		} finally {
			db.close();
		}
	}

	/**
	 * Update the friend attendings
	 * @param eid
	 * @param String form of attending people
	 */
	public void updateAttendees(String eid, String attendees) throws SQLException{
		SQLiteDatabase db = tableHelper.getWritableDatabase(); 
		try {
			ContentValues content = new ContentValues();
			content.put(SQLTablesHelper.MY_EVENT_FRIENDS_ATTENDING, attendees);
			db.update(SQLTablesHelper.MY_EVENT_TABLE_NAME, content, 
					SQLTablesHelper.MY_EVENT_EID + " =?",
					new String[] {eid});
		} finally {
			db.close();
		}
	}

	/**
	 * Update events info
	 * @param eid
	 * @param FbEvent
	 */
	public void updateEvent(FbEvent event) throws SQLException {
		SQLiteDatabase db = tableHelper.getWritableDatabase(); 
		try {
			ContentValues content = prepareContentValues(event);
			db.update(SQLTablesHelper.MY_EVENT_TABLE_NAME, content, 
					SQLTablesHelper.MY_EVENT_EID + " =?",
					new String[] {event.getId()});
		} finally {
			db.close();
		}
	}

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
		event.setRsvp_status(cursor.getString(3));
		event.setStart_time(cursor.getLong(4));
		event.setEnd_time(cursor.getLong(5));
		event.setLocation(cursor.getString(6));
		event.setVenueLongitude(cursor.getDouble(7));
		event.setVenueLatitude(cursor.getDouble(8));
		String friendsAttending = cursor.getString(9);
		event.setFriendsAttending(Attendee.getAttendeesListFromStr(friendsAttending));
		return event;
	}
}
